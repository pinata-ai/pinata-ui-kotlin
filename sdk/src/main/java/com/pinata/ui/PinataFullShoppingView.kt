package com.pinata.ui

import android.content.Context
import android.util.AttributeSet

/**
 * Full Shopping: the complete marketplace, the destination of every [MarketplaceOpenEvent].
 * Requires [userToken]. Set [detailsId]/[detailsType] (straight from the event) to land on
 * that item instead of home.
 */
class PinataFullShoppingView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
) : PinataComponentView(context, attrs) {

  override val componentTag = "pinata-marketplace-full-shopping"
  override val usesEventMode = false

  var customization: FullShoppingCustomization = FullShoppingCustomization()
  var detailsId: String? = null
  var detailsType: String? = null

  init {
    context.theme.obtainStyledAttributes(attrs, R.styleable.PinataComponentView, 0, 0).apply {
      getString(R.styleable.PinataComponentView_pinataDetailsId)?.let { detailsId = it }
      getString(R.styleable.PinataComponentView_pinataDetailsType)?.let { detailsType = it }
      recycle()
    }
  }

  override fun componentCustomization() = customization.toJson()

  override fun buildCustomization() = super.buildCustomization().apply {
    detailsId?.let { put("detailsId", it) }
    detailsType?.let { put("detailsType", it) }
  }

  override fun elementAttributes() = " useFixedHeight=\"false\"" + super.elementAttributes()
}
