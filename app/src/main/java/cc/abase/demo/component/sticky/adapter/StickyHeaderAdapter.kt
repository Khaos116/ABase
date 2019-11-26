package cc.abase.demo.component.sticky.adapter

import android.content.Context

import android.widget.Toast
import cc.abase.demo.component.sticky.widget.HasStickyHeader
import cc.abase.demo.epoxy.item.*
import com.airbnb.epoxy.EpoxyAdapter

/**
 * Showcases [EpoxyAdapter] with sticky header support
 */
class StickyHeaderAdapter(
  private val context: Context
) : EpoxyAdapter(), HasStickyHeader {

  init {
    enableDiffing()
    for (i in 0 until 100) {
      addModel(when {
        i % 5 == 0 -> StickyTopItem_().apply {
          id("sticky-header $i")
          text("Sticky header $i")
          onItemClick {
            Toast.makeText(context, "clicked", Toast.LENGTH_LONG).show()
          }
        }
        else -> StickyNormalItem_().apply {
          id("view holder $i")
          text("this is a View Holder item")
          onItemClick {
            Toast.makeText(context, "clicked", Toast.LENGTH_LONG)
                .show()
          }
        }
      })
    }
    notifyModelsChanged()
  }

  override fun isStickyHeader(position: Int) = models[position] is StickyTopItem
}