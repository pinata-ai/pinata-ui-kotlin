package com.pinata.ui

import org.json.JSONObject

/**
 * The `pinata-marketplace-open` event: the renter asked to open the marketplace.
 * `detailsId`/`detailsType` (and `entryPoint`) are present when a specific item was clicked;
 * pass them to [PinataFullShoppingView] to land on that item.
 */
data class MarketplaceOpenEvent(
  val source: String,
  val authenticated: Boolean,
  val detailsId: String?,
  val detailsType: String?,
  val entryPoint: String?,
) {
  internal companion object {
    fun fromJson(json: JSONObject) = MarketplaceOpenEvent(
      source = json.optString("source"),
      authenticated = json.optBoolean("authenticated"),
      detailsId = json.optString("detailsId").ifEmpty { null },
      detailsType = json.optString("detailsType").ifEmpty { null },
      entryPoint = json.optString("entryPoint").ifEmpty { null },
    )
  }
}

/** SAM listeners so Java callers can pass lambdas. */
fun interface OnMarketplaceOpenListener {
  fun onMarketplaceOpen(event: MarketplaceOpenEvent)
}

fun interface OnPinataErrorListener {
  fun onError(message: String)
}

/** Full Shopping consumed a deep link (or the renter declined the consent holding it). */
fun interface OnDetailsConsumedListener {
  fun onDetailsConsumed()
}
