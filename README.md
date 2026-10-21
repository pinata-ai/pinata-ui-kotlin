# pinata-ui-kotlin

Android SDK for the Pinata marketplace components. One native `View` per component wraps the
[pinata-ui](https://github.com/pinata-ai/pinata-ui) web components in a `WebView`, turns on event
mode (`navigationMode: "events"`) and bridges the component's events to typed native callbacks.
Written in Kotlin with a Java-callable API (see `spike-native-webview/java-support-mini-spike.md`).

## Modules

| Module | What it is |
| --- | --- |
| `sdk` | The library. Public surface: `PinataMarketplaceWalletView`, `PinataMarketplacePreviewView`, `PinataFullShoppingView`, `PinataEnvironment`, `MarketplaceOpenEvent` and the two SAM listeners |
| `app` | Kotlin demo: Wallet and Preview emit `pinata-marketplace-open`, the app opens Full Shopping on the clicked item (the V2 flow) |
| `demo-java` | Minimal pure Java app proving the SDK's Java interop |

## Usage

```kotlin
val wallet = PinataMarketplaceWalletView(context).apply {
  environment = PinataEnvironment.SANDBOX
  userToken = token            // omit for anonymous users
  customization.companyName = "your-company"
  customization.ctaText = "Browse marketplace"
  customization.colorScheme.primaryColor = "#0055FF"
  onMarketplaceOpenListener = OnMarketplaceOpenListener { event ->
    // event.authenticated, event.detailsId, event.detailsType
    openFullShopping(event)
  }
  load()
}
```

```kotlin
val fullShopping = PinataFullShoppingView(context).apply {
  environment = PinataEnvironment.SANDBOX
  userToken = token            // required: Full Shopping has no anonymous mode
  detailsId = event.detailsId  // optional deep link, straight from the event
  detailsType = event.detailsType
  load()
}
```

Notes:

- Properties are read on `load()` only. To change anything afterwards (a new `detailsId`, a new
  token), set it and call `load()` again.
- `window.open` targets (deals, "Access your full Pinata account") open in Chrome Custom Tabs.
- Wallet and Preview never navigate: every click arrives as a `MarketplaceOpenEvent`. Full
  Shopping is the destination and emits no navigation event.
- `customization` is typed per component (`WalletCustomization`, `PreviewCustomization`,
  `FullShoppingCustomization`), mirroring the web contract: only what you set is sent. Keys the
  SDK does not type yet go in `extraCustomization` (a `JSONObject`); typed values win.
- Full Shopping also reports `onDetailsConsumedListener` when a deep link is consumed.
- Jetpack Compose: the views are plain `View`s, use them through `AndroidView { ... }`.

## Run the demos

Requirements: Android Studio (or JDK 17+ and the Android SDK with platform 36) and a device or
emulator.

```bash
./gradlew :app:installDebug -PdemoToken=<handoff user token> -PdemoEnv=DEV
./gradlew :demo-java:installDebug -PdemoToken=<handoff user token> -PdemoEnv=DEV
```

Without `demoToken` the demos run anonymous (Full Shopping won't open). `demoEnv` accepts
`PRODUCTION`, `SANDBOX` and `DEV`. Debug builds are inspectable from `chrome://inspect`.
