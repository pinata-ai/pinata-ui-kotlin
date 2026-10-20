package com.pinata.ui

import android.content.Context
import android.util.AttributeSet

/**
 * Marketplace Preview in event mode: rows of offer cards. Card clicks reach
 * [onMarketplaceOpenListener] with the clicked item (`detailsId`/`detailsType`);
 * "View all" sends the same event without an item.
 */
class PinataMarketplacePreviewView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
) : PinataComponentView(context, attrs) {

  override val componentTag = "pinata-marketplace-preview"
}
