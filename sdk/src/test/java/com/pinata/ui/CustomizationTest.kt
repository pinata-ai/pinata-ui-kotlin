package com.pinata.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomizationTest {

  @Test
  fun `only set keys are sent`() {
    val json = WalletCustomization().apply {
      ctaText = "Open the marketplace"
      colorScheme.primaryColor = "#0055FF"
    }.toJson()!!

    assertEquals("Open the marketplace", json.getString("ctaText"))
    assertEquals("#0055FF", json.getJSONObject("colorScheme").getString("primaryColor"))
    assertFalse(json.has("widgetTitle"))
    assertFalse(json.has("description"))
    assertFalse(json.getJSONObject("colorScheme").has("walletColor"))
  }

  @Test
  fun `empty customization serializes to null`() {
    assertNull(WalletCustomization().toJson())
    assertNull(PreviewCustomization().toJson())
    assertEquals(0, FullShoppingCustomization().toJson().length())
  }

  @Test
  fun `wallet description needs its own object`() {
    val json = WalletCustomization().apply {
      descriptionCopy = "How do points work?"
      descriptionUrl = "example.com/faq"
    }.toJson()!!

    assertEquals("How do points work?", json.getJSONObject("description").getString("copy"))
    assertEquals("example.com/faq", json.getJSONObject("description").getString("url"))
  }

  @Test
  fun `currency uses the web contract value`() {
    val json = PreviewCustomization().apply { currency = PinataCurrency.DOLLAR }.toJson()!!
    assertEquals("dollar", json.getString("currency"))
  }

  @Test
  fun `full shopping details colors are flattened to the root`() {
    val json = FullShoppingCustomization().apply {
      widgetTitle = "Shop"
      detailsColors.detailsActionColor = "#111111"
    }.toJson()

    assertEquals("#111111", json.getString("detailsActionColor"))
    assertFalse(json.has("detailsColors"))
  }

  @Test
  fun `nested labels serialize per section`() {
    val json = FullShoppingCustomization().apply {
      labels.walletCard.accessAccountCta = "Go to Pinata"
      labels.browseMarketplace.searchPlaceholder = "Search deals"
    }.toJson()

    val labels = json.getJSONObject("labels")
    assertEquals("Go to Pinata", labels.getJSONObject("walletCard").getString("accessAccountCta"))
    assertEquals("Search deals", labels.getJSONObject("browseMarketplace").getString("searchPlaceholder"))
    assertFalse(labels.has("featured"))
  }

  @Test
  fun `renter attributes serialize under attributes`() {
    val json = PreviewCustomization().apply { attributes.zipCode = "33130" }.toJson()!!
    assertEquals("33130", json.getJSONObject("attributes").getString("zipCode"))
    assertTrue(json.length() == 1)
  }
}
