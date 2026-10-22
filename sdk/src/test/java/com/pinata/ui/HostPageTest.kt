package com.pinata.ui

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HostPageTest {

  @Test
  fun `event mode adds the navigationMode flag`() {
    val json = buildCustomizationJson(JSONObject(), null, true, null)
    assertEquals("events", json.getString("navigationMode"))
  }

  @Test
  fun `full shopping does not get the flag`() {
    assertFalse(buildCustomizationJson(JSONObject(), null, false, null).has("navigationMode"))
  }

  @Test
  fun `typed customization wins over the extra escape hatch`() {
    val extra = JSONObject().put("ctaText", "from extra").put("untypedKey", 1)
    val typed = JSONObject().put("ctaText", "from typed")
    val json = buildCustomizationJson(extra, typed, true, null)

    assertEquals("from typed", json.getString("ctaText"))
    assertEquals(1, json.getInt("untypedKey"))
  }

  @Test
  fun `companyName property fills only when customization misses it`() {
    val fromTyped = buildCustomizationJson(JSONObject(), JSONObject().put("companyName", "typed"), true, "prop")
    assertEquals("typed", fromTyped.getString("companyName"))

    val fromProp = buildCustomizationJson(JSONObject(), null, true, "prop")
    assertEquals("prop", fromProp.getString("companyName"))
  }

  @Test
  fun `dev environment sets the element attribute, production does not`() {
    assertTrue(buildElementAttributes(PinataEnvironment.DEV, null, JSONObject()).contains(" environment=\"dev\""))
    assertFalse(buildElementAttributes(PinataEnvironment.PRODUCTION, null, JSONObject()).contains("environment="))
  }

  @Test
  fun `user token is escaped for the attribute`() {
    val attributes = buildElementAttributes(PinataEnvironment.PRODUCTION, "a\"b'c&d", JSONObject())
    assertTrue(attributes.contains("usertoken=\"a&quot;b&#39;c&amp;d\""))
  }

  @Test
  fun `customization json is escaped inside single quotes`() {
    val attributes = buildElementAttributes(
      PinataEnvironment.PRODUCTION, null, JSONObject().put("ctaText", "Gabriel's deals"),
    )
    assertTrue(attributes.contains("customization='{&quot;ctaText&quot;:&quot;Gabriel&#39;s deals&quot;}'"))
  }

  @Test
  fun `escape handles the ampersand first`() {
    assertEquals("&amp;&#39;&quot;", escapeAttribute("&'\""))
  }

  @Test
  fun `host page wires the script, the component and the bridge`() {
    val page = buildHostPage("https://static.pinata.ai/x.js", "pinata-marketplace-wallet", " usertoken=\"t\"")

    assertTrue(page.contains("<script type=\"module\" src=\"https://static.pinata.ai/x.js\"></script>"))
    assertTrue(page.contains("<pinata-marketplace-wallet style=\"display:block;height:100vh\" usertoken=\"t\"></pinata-marketplace-wallet>"))
    assertTrue(page.contains("addEventListener(\"message\", e => PinataBridge.postMessage(JSON.stringify(e.data)))"))
    assertTrue(page.contains("pinata-error"))
  }
}
