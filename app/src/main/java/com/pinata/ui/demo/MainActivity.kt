package com.pinata.ui.demo

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import com.pinata.ui.MarketplaceOpenEvent
import com.pinata.ui.OnMarketplaceOpenListener
import com.pinata.ui.OnPinataErrorListener
import com.pinata.ui.PinataComponentView
import com.pinata.ui.PinataEnvironment
import com.pinata.ui.PinataFullShoppingView
import com.pinata.ui.PinataMarketplacePreviewView
import com.pinata.ui.PinataMarketplaceWalletView

/**
 * Demo of the V2 flow: Wallet or Preview emits `pinata-marketplace-open`, the app reacts
 * and shows Full Shopping on the clicked item. Token and environment come from
 * `-PdemoToken` / `-PdemoEnv` (see gradle.properties).
 */
class MainActivity : ComponentActivity() {

  private val environment = PinataEnvironment.valueOf(BuildConfig.DEMO_ENV)
  private val token = BuildConfig.DEMO_TOKEN.ifEmpty { null }

  private lateinit var container: FrameLayout
  private var showSelected: () -> Unit = {}
  private var onFullShopping = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    container = FrameLayout(this)

    val buildAnonWallet = {
      PinataMarketplaceWalletView(this).apply {
        // Exercises the typed customization end to end.
        customization.ctaText = "Open the marketplace"
        customization.colorScheme.primaryColor = "#0A7E5C"
      }.configured(null)
    }

    val menu = LinearLayout(this).apply {
      addButton("Wallet anonymous", buildAnonWallet)
      addButton("Wallet logged in") { PinataMarketplaceWalletView(this@MainActivity).configured(token) }
      addButton("Preview logged in") { PinataMarketplacePreviewView(this@MainActivity).configured(token) }
    }

    setContentView(LinearLayout(this).apply {
      orientation = LinearLayout.VERTICAL
      fitsSystemWindows = true
      addView(menu, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
      addView(container, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))
    })

    onBackPressedDispatcher.addCallback(this) {
      if (onFullShopping) showSelected() else finish()
    }

    showSelected = { show(buildAnonWallet()) }
    showSelected()
  }

  private fun LinearLayout.addButton(label: String, build: () -> PinataComponentView) {
    addView(
      Button(context).apply {
        text = label
        setOnClickListener {
          showSelected = { show(build()) }
          showSelected()
        }
      },
      LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f),
    )
  }

  private fun show(view: PinataComponentView) {
    onFullShopping = view is PinataFullShoppingView
    container.removeAllViews()
    container.addView(view)
    view.load()
  }

  private fun PinataComponentView.configured(userToken: String?) = apply {
    environment = this@MainActivity.environment
    this.userToken = userToken
    companyName = "your-company"
    onMarketplaceOpenListener = OnMarketplaceOpenListener { event ->
      runOnUiThread { openFullShopping(event) }
    }
    onErrorListener = OnPinataErrorListener { message ->
      runOnUiThread { toast("pinata-error: $message") }
    }
  }

  private fun openFullShopping(event: MarketplaceOpenEvent) {
    toast("Event received: $event")
    if (token == null) {
      // A real app would onboard the user here (authenticated=false) before opening the marketplace.
      toast("No demoToken set; cannot open Full Shopping")
      return
    }
    show(
      PinataFullShoppingView(this).apply {
        detailsId = event.detailsId
        detailsType = event.detailsType
      }.configured(token),
    )
  }

  private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}
