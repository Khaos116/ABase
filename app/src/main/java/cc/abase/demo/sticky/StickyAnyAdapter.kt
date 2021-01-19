package cc.abase.demo.sticky

import android.graphics.Color
import android.view.View
import com.drakeet.multitype.MultiTypeAdapter
import com.drakeet.multitype.MutableTypes
import com.drakeet.multitype.Types

/**
 * Author:case
 * Date:2020/8/31
 * Time:16:00
 */
abstract class StickyAnyAdapter(
    private val bgColor: Int = Color.TRANSPARENT,
    val i: List<Any> = emptyList(),
    val c: Int = 0,
    val t: Types = MutableTypes(c)
) : MultiTypeAdapter(i, c, t), StickyHeaderCallbacks {
    override fun setupStickyHeaderView(stickyHeader: View) {
        super.setupStickyHeaderView(stickyHeader)
        if (bgColor != 0) stickyHeader.setBackgroundColor(bgColor)
    }

    override fun teardownStickyHeaderView(stickyHeader: View) {
        super.teardownStickyHeaderView(stickyHeader)
        stickyHeader.setBackgroundColor(Color.TRANSPARENT)
    }
}