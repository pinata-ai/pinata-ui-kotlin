package com.pinata.ui

import android.app.Activity
import android.widget.FrameLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LifecycleTest {

  private val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
  private val parent = FrameLayout(activity).also { activity.setContentView(it) }
  private val errors = mutableListOf<String>()
  private val wallet = PinataMarketplaceWalletView(activity).apply {
    onErrorListener = OnPinataErrorListener { errors += it }
  }

  @Test
  fun `detaching from the window keeps the component, as a scrolling list does`() {
    parent.addView(wallet)
    parent.removeView(wallet)
    parent.addView(wallet)
    wallet.load()

    assertEquals(1, wallet.childCount)
    assertFalse(errors.any { it.contains("destroyed") })
  }

  @Test
  fun `wallet asks the component to follow its width`() {
    assertTrue(wallet.componentCustomization().getBoolean("observeWidth"))
  }

  @Test
  fun `content height message reaches the listener`() {
    val heights = mutableListOf<Int>()
    wallet.onContentHeightListener = OnContentHeightListener { heights += it }

    wallet.onBridgeMessage("""{"type":"pinata-content-height","height":323}""")
    wallet.onBridgeMessage("""{"type":"pinata-content-height"}""")

    assertEquals(listOf(323), heights)
  }

  @Test
  fun `destroy releases the web view and load reports it`() {
    parent.addView(wallet)
    wallet.destroy()
    wallet.destroy()
    wallet.load()

    assertEquals(0, wallet.childCount)
    assertTrue(errors.single().contains("destroyed"))
  }
}
