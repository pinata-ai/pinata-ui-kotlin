# pinata-ui-kotlin

Android SDK for the Piñata marketplace components. Each component is a native `View` that renders
the Piñata web component in a `WebView` and delivers its events to your app as typed callbacks.
It is the native counterpart of [pinata-ui-react-native](https://github.com/pinata-ai/pinata-ui-react-native).

[Partner API reference](https://www.pinata.ai/partners/api/v1/redoc/)

## Installation

Requires Android 7.0 (API 24) or higher.

Add the Piñata repository in `settings.gradle.kts`. The `exclusiveContent` filter makes Gradle
look up only `ai.pinata` there, and everything else in your usual repositories:

```kotlin
dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    exclusiveContent {
      forRepository { maven { url = uri("https://static.pinata.ai/static/android/maven") } }
      filter { includeGroup("ai.pinata") }
    }
  }
}
```

Then add the dependency to your app module:

```kotlin
dependencies {
  implementation("ai.pinata:pinata-ui:0.1.0")
}
```

## Components

| View | Web component | `userToken` |
| --- | --- | --- |
| `PinataMarketplaceWalletView` | `pinata-marketplace-wallet` | Optional: leave it out for renters who are not on Piñata yet |
| `PinataMarketplacePreviewView` | `pinata-marketplace-preview` | Optional: leave it out for renters who are not on Piñata yet |
| `PinataFullShoppingView` | `pinata-marketplace-full-shopping` | Required: without it the view stays empty |

Wallet and Preview run in event mode: a click that would open the marketplace reaches your app as
a `MarketplaceOpenEvent` instead of navigating. Your app then opens Full Shopping, the complete
marketplace.

## Usage

Show the Wallet (the Preview works the same way):

```kotlin
val wallet = PinataMarketplaceWalletView(context).apply {
  environment = PinataEnvironment.SANDBOX
  userToken = token // null for renters who are not on Piñata yet
  customization.companyName = "your-company"
  onMarketplaceOpenListener = OnMarketplaceOpenListener { event ->
    if (event.authenticated) openFullShopping(event) else startOnboarding()
  }
  onErrorListener = OnPinataErrorListener { message -> Log.w("Pinata", message) }
}
container.addView(wallet, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
wallet.load()
```

Open Full Shopping, on the clicked item when the event carries one:

```kotlin
fun openFullShopping(event: MarketplaceOpenEvent) {
  val fullShopping = PinataFullShoppingView(context).apply {
    environment = PinataEnvironment.SANDBOX
    userToken = token // required
    detailsId = event.detailsId // null opens the marketplace home
    detailsType = event.detailsType
  }
  container.removeAllViews()
  container.addView(fullShopping, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
  fullShopping.load()
}
```

When `authenticated` is `false`, the renter has no Piñata account yet: create it through your
backend integration (see the Partner API reference) and onboard them, then pass their `userToken`.

## Events

`MarketplaceOpenEvent` has `source`, `authenticated`, `detailsId`, `detailsType` and `entryPoint`.

| Click | `source` | `detailsId` / `detailsType` | `entryPoint` |
| --- | --- | --- | --- |
| Wallet main button | `wallet` | none | none |
| Preview card | `preview` | the item; `detailsType` is `deal`, `reward` or `giveaway` | none |
| Preview "View all" | `preview` | none | `featured` or `browse_marketplace` |
| "Claim your prize" banner (Wallet or Preview, after the renter wins a giveaway) | `wallet` or `preview` | the giveaway, `giveaway` | `winner_banner` |

Pass `detailsId` and `detailsType` to `PinataFullShoppingView` as they are. Other listeners:

| Listener | Called when |
| --- | --- |
| `onDetailsConsumedListener` (Full Shopping) | The item passed in `detailsId` was opened, or the renter declined the consent screen that comes before it |
| `onErrorListener` | The component reports an error while loading (for example an invalid or expired `userToken`), the device's WebView cannot deliver events to the app, or `load()` is called on a destroyed view |
| `onMessageListener` | Every message the component posts, as a raw JSON string, also the ones that have a typed listener |

All listeners run on the main thread.

## Customization

Each view has a typed `customization` (`WalletCustomization`, `PreviewCustomization`,
`FullShoppingCustomization`) whose fields map to the web component's `customization` keys. Only
the fields you set are sent; the component applies its defaults to the rest.

```kotlin
wallet.customization.ctaText = "Browse marketplace"
wallet.customization.colorScheme.primaryColor = "#0055FF"
```

Keys the SDK does not type yet go in `extraCustomization` (a `JSONObject`). Typed fields win.

## Good to know

- **Environments:** `PRODUCTION` (default) and `SANDBOX` for testing. Use tokens from the same
  environment.
- **Size:** the component fills the view. Give the view a height, or let it fill its parent
  (`MATCH_PARENT` or a layout weight).
- **Properties are read by `load()`.** To change them later, set them and call `load()` again.
- **Single use:** when the view is detached from the window it destroys its `WebView`. To show the
  component again (for example after a Fragment recreates its views), create a new instance.
- **Links:** pages that leave the marketplace, such as a deal on the merchant's site, open in
  Chrome Custom Tabs.
- **Java:** the API is Java-friendly (setters such as `setUserToken(...)`, lambdas for listeners).
- **Jetpack Compose:** the views are plain Android `View`s; use them through `AndroidView`.

## Development

The repo has the `sdk` library and two demo apps: `app` (Kotlin: Wallet and Preview open Full
Shopping on the clicked item) and `demo-java` (pure Java). Requirements: JDK 17 and the Android
SDK with platform 36, or Android Studio.

```bash
./gradlew :app:installDebug -PdemoToken=<user token> -PdemoEnv=SANDBOX
./gradlew :demo-java:installDebug -PdemoToken=<user token> -PdemoEnv=SANDBOX
```

Without `demoToken` the demos run anonymous (Full Shopping won't open). Debug builds are
inspectable from `chrome://inspect`. Releases: see [RELEASE.md](RELEASE.md).

## License

MIT
