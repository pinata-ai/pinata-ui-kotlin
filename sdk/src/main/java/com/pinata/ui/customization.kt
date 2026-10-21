package com.pinata.ui

import org.json.JSONObject

/**
 * Typed `customization` for each component, mirroring the web contract (see the W6 docs in
 * renter-website/docs). Every field is optional: only what you set is sent, and the web
 * component applies its own defaults to the rest. Plain mutable properties keep the classes
 * usable from Java (`c.setWidgetTitle(...)`).
 */

/** How values are shown and reported. Some companies have an allowed list on Pinata's side. */
enum class PinataCurrency(internal val jsonValue: String) {
  PINATA("pinata"),
  POINTS("points"),
  DOLLAR("dollar"),
}

/** Hex colors like "#RRGGBB". Each component reads the keys it supports. */
class ColorScheme {
  var primaryColor: String? = null
  var secondaryColor: String? = null
  var white: String? = null
  var dark: String? = null

  /** Wallet only: the points balance and icon. It does not follow [primaryColor]; set both. */
  var walletColor: String? = null

  /** Preview and Full Shopping only. */
  var fontColor: String? = null

  internal fun toJson() = json(
    "primaryColor" to primaryColor,
    "secondaryColor" to secondaryColor,
    "white" to white,
    "dark" to dark,
    "walletColor" to walletColor,
    "fontColor" to fontColor,
  )
}

/** Renter attributes for offer targeting. */
class RenterAttributes {
  var zipCode: String? = null
  var leaseStartDate: String? = null
  var leaseEndDate: String? = null
  var rentAmount: String? = null
  var gender: String? = null
  var loyaltyLevel: String? = null

  internal fun toJson() = json(
    "zipCode" to zipCode,
    "leaseStartDate" to leaseStartDate,
    "leaseEndDate" to leaseEndDate,
    "rentAmount" to rentAmount,
    "gender" to gender,
    "loyaltyLevel" to loyaltyLevel,
  )
}

class SectionLabels {
  var title: String? = null
  var viewAllCta: String? = null

  internal fun toJson() = json("title" to title, "viewAllCta" to viewAllCta)
}

class WalletCustomization {
  var widgetTitle: String? = null
  var ctaText: String? = null
  var disabledCta: Boolean? = null
  var helperText: String? = null

  /** Text link under the header; both are needed for it to show. */
  var descriptionCopy: String? = null
  var descriptionUrl: String? = null

  var companyName: String? = null
  var currentPageUrl: String? = null
  var colorScheme: ColorScheme = ColorScheme()

  internal fun toJson() = json(
    "widgetTitle" to widgetTitle,
    "ctaText" to ctaText,
    "disabledCta" to disabledCta,
    "helperText" to helperText,
    "description" to json("copy" to descriptionCopy, "url" to descriptionUrl),
    "companyName" to companyName,
    "currentPageUrl" to currentPageUrl,
    "colorScheme" to colorScheme.toJson(),
  )
}

class PreviewLabels {
  var featured: SectionLabels = SectionLabels()
  var browseMarketplace: SectionLabels = SectionLabels()

  internal fun toJson() = json(
    "featured" to featured.toJson(),
    "browseMarketplace" to browseMarketplace.toJson(),
  )
}

class PreviewCustomization {
  var labels: PreviewLabels = PreviewLabels()
  var currency: PinataCurrency? = null
  var companyName: String? = null
  var companyCustomizationKey: String? = null
  var currentPageUrl: String? = null
  var useCustomFont: Boolean? = null
  var attributes: RenterAttributes = RenterAttributes()
  var colorScheme: ColorScheme = ColorScheme()

  internal fun toJson() = json(
    "labels" to labels.toJson(),
    "currency" to currency,
    "companyName" to companyName,
    "companyCustomizationKey" to companyCustomizationKey,
    "currentPageUrl" to currentPageUrl,
    "useCustomFont" to useCustomFont,
    "attributes" to attributes.toJson(),
    "colorScheme" to colorScheme.toJson(),
  )
}

class BrowseMarketplaceLabels {
  var title: String? = null
  var searchPlaceholder: String? = null

  internal fun toJson() = json("title" to title, "searchPlaceholder" to searchPlaceholder)
}

class WalletCardLabels {
  var accessAccountCta: String? = null

  internal fun toJson() = json("accessAccountCta" to accessAccountCta)
}

class FullShoppingLabels {
  var featured: SectionLabels = SectionLabels()
  var winThisMonth: SectionLabels = SectionLabels()
  var lastChance: SectionLabels = SectionLabels()
  var browseMarketplace: BrowseMarketplaceLabels = BrowseMarketplaceLabels()
  var walletCard: WalletCardLabels = WalletCardLabels()

  internal fun toJson() = json(
    "featured" to featured.toJson(),
    "winThisMonth" to winThisMonth.toJson(),
    "lastChance" to lastChance.toJson(),
    "browseMarketplace" to browseMarketplace.toJson(),
    "walletCard" to walletCard.toJson(),
  )
}

/** Colors of an item's details view. These live at the root of `customization` on the web. */
class DetailsColors {
  var detailsCtaContainerColor: String? = null
  var detailsCtaContainerFontColor: String? = null
  var detailsPointsBadgeColor: String? = null
  var detailsPointsBadgeFontColor: String? = null
  var detailsTagFontColor: String? = null
  var detailsTextFontColor: String? = null
  var detailsActionColor: String? = null
  var detailsActionFontColor: String? = null

  internal fun toJson() = json(
    "detailsCtaContainerColor" to detailsCtaContainerColor,
    "detailsCtaContainerFontColor" to detailsCtaContainerFontColor,
    "detailsPointsBadgeColor" to detailsPointsBadgeColor,
    "detailsPointsBadgeFontColor" to detailsPointsBadgeFontColor,
    "detailsTagFontColor" to detailsTagFontColor,
    "detailsTextFontColor" to detailsTextFontColor,
    "detailsActionColor" to detailsActionColor,
    "detailsActionFontColor" to detailsActionFontColor,
  )
}

class FullShoppingCustomization {
  var widgetTitle: String? = null
  var widgetDescription: String? = null
  var companyName: String? = null
  var companyCustomizationKey: String? = null
  var currency: PinataCurrency? = null
  var currentPageUrl: String? = null
  var useCustomFont: Boolean? = null
  var walletSectionEnabled: Boolean? = null
  var transactionHistoryEnabled: Boolean? = null
  var attributes: RenterAttributes = RenterAttributes()
  var labels: FullShoppingLabels = FullShoppingLabels()
  var colorScheme: ColorScheme = ColorScheme()
  var detailsColors: DetailsColors = DetailsColors()

  internal fun toJson(): JSONObject {
    val obj = json(
      "widgetTitle" to widgetTitle,
      "widgetDescription" to widgetDescription,
      "companyName" to companyName,
      "companyCustomizationKey" to companyCustomizationKey,
      "currency" to currency,
      "currentPageUrl" to currentPageUrl,
      "useCustomFont" to useCustomFont,
      "walletSectionEnabled" to walletSectionEnabled,
      "transactionHistoryEnabled" to transactionHistoryEnabled,
      "attributes" to attributes.toJson(),
      "labels" to labels.toJson(),
      "colorScheme" to colorScheme.toJson(),
    ) ?: JSONObject()
    obj.mergeFlat(detailsColors.toJson())
    return obj
  }
}

/** Builds a JSONObject with the non-null pairs only; null when nothing was set. */
internal fun json(vararg pairs: Pair<String, Any?>): JSONObject? {
  val obj = JSONObject()
  pairs.forEach { (key, value) ->
    when (value) {
      null -> {}
      is PinataCurrency -> obj.put(key, value.jsonValue)
      is JSONObject -> if (value.length() > 0) obj.put(key, value)
      else -> obj.put(key, value)
    }
  }
  return if (obj.length() == 0) null else obj
}

internal fun JSONObject.mergeFlat(other: JSONObject?) {
  other?.keys()?.forEach { put(it, other.get(it)) }
}
