package com.pinata.ui

import android.content.Context
import android.util.AttributeSet

/**
 * Marketplace Wallet in event mode: points balance, offers and a CTA. Every CTA click
 * (and the giveaway claim banner) reaches [onMarketplaceOpenListener]; the view never
 * navigates on its own.
 */
class PinataMarketplaceWalletView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
) : PinataComponentView(context, attrs) {

  override val componentTag = "pinata-marketplace-wallet"

  var customization: WalletCustomization = WalletCustomization()

  override fun componentCustomization() = customization.toJson()
}
