package com.pinata.ui

/** Pinata environment the component runs against. DEV is internal to Pinata. */
enum class PinataEnvironment(
  val scriptUrl: String,
  val baseUrl: String,
  internal val elementEnvironment: String?,
) {
  PRODUCTION("https://static.pinata.ai/static/ui/pinata-ui.latest.js", "https://pinata.ai", null),
  SANDBOX("https://static.pinata.ai/static/ui/pinata-ui.latest-sandbox.js", "https://sandbox.pinata.ai", null),
  DEV("https://static.pinata.ai/static/ui/pinata-ui.latest-alpha-dev.js", "https://dev.pinata.ai", "dev"),
}
