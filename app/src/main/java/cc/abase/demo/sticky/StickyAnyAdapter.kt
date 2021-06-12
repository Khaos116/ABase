package cc.abase.demo.sticky

import android.graphics.Color
import android.view.View
import com.drakeet.multitype.*

/**
 * Author:Khaos
 * Date:2020/8/31
 * Time:16:00
 */
abstract class StickyAnyAdapter(
    private val stickyBgColor: Int = Color.TRANSPARENT,
    private val noStickyBgColor: Int = Color.TRANSPARENT,
    val i: List<Any> = emptyList(),
    val c: Int = 0,
    val t: Types = MutableTypes(c)
) : MultiTypeAdapter(i, c, t), StickyHeaderCallbacks {
  override fun setupStickyHeaderView(stickyHeader: View) {
    super.setupStickyHeaderView(stickyHeader)
    stickyHeader.setBackgroundColor(stickyBgColor)
  }

  override fun teardownStickyHeaderView(stickyHeader: View) {
    super.teardownStickyHeaderView(stickyHeader)
    stickyHeader.setBackgroundColor(noStickyBgColor)
  }
}