package com.pinata.ui

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MarketplaceOpenEventTest {

  @Test
  fun `parses the full payload`() {
    val event = MarketplaceOpenEvent.fromJson(
      JSONObject(
        """{"type":"pinata-marketplace-open","source":"preview","authenticated":true,
           "detailsId":"01ABC","detailsType":"deal","entryPoint":"winner_banner"}""",
      ),
    )

    assertEquals("preview", event.source)
    assertTrue(event.authenticated)
    assertEquals("01ABC", event.detailsId)
    assertEquals("deal", event.detailsType)
    assertEquals("winner_banner", event.entryPoint)
  }

  @Test
  fun `missing optionals come back null`() {
    val event = MarketplaceOpenEvent.fromJson(
      JSONObject("""{"type":"pinata-marketplace-open","source":"wallet","authenticated":false}"""),
    )

    assertEquals("wallet", event.source)
    assertNull(event.detailsId)
    assertNull(event.detailsType)
    assertNull(event.entryPoint)
  }
}
