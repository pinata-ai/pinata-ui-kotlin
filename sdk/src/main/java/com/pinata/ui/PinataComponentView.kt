package com.pinata.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Message
import android.util.AttributeSet
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.browser.customtabs.CustomTabsIntent
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import org.json.JSONObject

/**
 * Base of every Pinata component view: a WebView around a generated host page with the
 * pinata-ui script and one web component. The host page forwards every `message` from the
 * component's iframes to native (registered once, here), and `window.open` targets
 * (deals, full Pinata account) open in Chrome Custom Tabs.
 *
 * Configure the properties, then call [load]. The component reads them on load only;
 * to change them afterwards, set and call [load] again. The view is single-use: once
 * detached from the window it destroys its WebView, so create a new instance to show it again.
 */
abstract class PinataComponentView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

  protected abstract val componentTag: String
  /** Wallet and Preview get `navigationMode: "events"`; Full Shopping is the destination. */
  protected open val usesEventMode = true

  var environment: PinataEnvironment = PinataEnvironment.PRODUCTION
  var userToken: String? = null
  var companyName: String? = null
  /** Extra `customization` keys, merged over what the view sets (view wins on conflicts). */
  var customization: JSONObject = JSONObject()
  var onMarketplaceOpenListener: OnMarketplaceOpenListener? = null
  var onErrorListener: OnPinataErrorListener? = null

  protected val webView = WebView(context)
  private var bridgeSupported = true
  private var destroyed = false

  init {
    context.theme.obtainStyledAttributes(attrs, R.styleable.PinataComponentView, 0, 0).apply {
      getString(R.styleable.PinataComponentView_pinataCompanyName)?.let { companyName = it }
      environment = PinataEnvironment.entries[
        getInt(R.styleable.PinataComponentView_pinataEnvironment, environment.ordinal)
      ]
      recycle()
    }
    configureWebView()
    addView(webView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun configureWebView() = with(webView) {
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.setSupportMultipleWindows(true)
    webChromeClient = PopupsToCustomTabs()
    webViewClient = object : WebViewClient() {
      override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        if (!request.isForMainFrame) return false
        CustomTabsIntent.Builder().build().launchUrl(view.context, request.url)
        return true
      }
    }
    if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) {
      WebViewCompat.addWebMessageListener(this, "PinataBridge", setOf("*")) { _, message, _, _, _ ->
        onBridgeMessage(message.data ?: return@addWebMessageListener)
      }
    } else {
      bridgeSupported = false
    }
  }

  private fun onBridgeMessage(data: String) {
    if (!data.startsWith("{")) return
    val event = try { JSONObject(data) } catch (_: Exception) { return }
    when (event.optString("type")) {
      "pinata-marketplace-open" ->
        onMarketplaceOpenListener?.onMarketplaceOpen(MarketplaceOpenEvent.fromJson(event))
      // `detail` is whatever pinata-ui put in the CustomEvent: a string or an object (kept as JSON).
      "pinata-error" ->
        onErrorListener?.onError(
          event.opt("detail")?.takeIf { it !== JSONObject.NULL }?.toString() ?: "",
        )
    }
  }

  fun load() {
    if (destroyed) {
      onErrorListener?.onError("This view was detached and destroyed; create a new instance.")
      return
    }
    if (!bridgeSupported) {
      onErrorListener?.onError(
        "This device's WebView does not support web message listeners; Pinata events will not reach the app.",
      )
    }
    webView.loadDataWithBaseURL(environment.baseUrl, buildHostPage(), "text/html", "utf-8", null)
  }

  // The view is single-use, like the web component: once detached, create a new instance.
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    destroyed = true
    removeView(webView)
    webView.destroy()
  }

  protected open fun buildCustomization(): JSONObject {
    val json = JSONObject(customization.toString())
    if (usesEventMode) json.put("navigationMode", "events")
    companyName?.let { if (!json.has("companyName")) json.put("companyName", it) }
    return json
  }

  protected open fun elementAttributes(): String {
    val attributes = StringBuilder()
    environment.elementEnvironment?.let { attributes.append(" environment=\"$it\"") }
    userToken?.let { attributes.append(" usertoken=\"${escapeAttribute(it)}\"") }
    attributes.append(" customization='${escapeAttribute(buildCustomization().toString())}'")
    return attributes.toString()
  }

  private fun buildHostPage(): String = """
    <!doctype html>
    <html>
    <head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style>html, body { margin: 0; height: 100%; }</style>
    <script type="module" src="${environment.scriptUrl}"></script>
    <script>
      addEventListener("message", e => PinataBridge.postMessage(JSON.stringify(e.data)));
      addEventListener("DOMContentLoaded", () =>
        document.body.firstElementChild.addEventListener("pinata-error", e =>
          PinataBridge.postMessage(JSON.stringify({type: "pinata-error", detail: e.detail == null ? "" : e.detail}))));
    </script>
    </head>
    <body>
    <$componentTag style="display:block;height:100vh"${elementAttributes()}></$componentTag>
    </body>
    </html>
  """.trimIndent()

  private fun escapeAttribute(value: String) =
    value.replace("&", "&amp;").replace("'", "&#39;").replace("\"", "&quot;")
}

/** Opens every window.open() target in Chrome Custom Tabs instead of a second WebView. */
private class PopupsToCustomTabs : WebChromeClient() {

  override fun onCreateWindow(
    view: WebView,
    isDialog: Boolean,
    isUserGesture: Boolean,
    resultMsg: Message,
  ): Boolean {
    val popup = WebView(view.context)
    popup.webViewClient = object : WebViewClient() {
      override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        CustomTabsIntent.Builder().build().launchUrl(view.context, request.url)
        view.post { view.destroy() }
        return true
      }
    }
    (resultMsg.obj as WebView.WebViewTransport).webView = popup
    resultMsg.sendToTarget()
    return true
  }
}
