package cc.abase.demo.component.sticky.adapter

import cc.abase.demo.R
import cc.abase.demo.bean.local.UserStickyBean
import cc.abase.demo.component.sticky.widget.HasStickyHeader
import cc.abase.demo.epoxy.base.DividerItem_
import cc.abase.demo.epoxy.item.Sticky2BottomItem_
import cc.abase.demo.epoxy.item.Sticky2TopItem_
import com.airbnb.epoxy.EpoxyAdapter

/**
 * Showcases [EpoxyAdapter] with sticky header support
 */
class StickyHeaderAdapter2(list: MutableList<UserStickyBean>) : EpoxyAdapter(), HasStickyHeader {

  var mData: MutableList<UserStickyBean> = list
    set(value) {
      val fresh = !field.isNullOrEmpty()
      field = value
      if (fresh) refresh()
    }

  init {
    enableDiffing()
    refresh()
  }

  private fun refresh() {
    removeAllModels()
    mData.forEachIndexed { index, bean ->
      //标题
      if (bean.title) addModel(Sticky2TopItem_().apply { id("topTitle") })
      //成绩
      else addModel(
          Sticky2BottomItem_().apply {
            id(bean.name + index.toString())
            bean(bean.score)
          })
      //标题有横线了，所以标题下面不加横线
      if (!bean.title) addModel(
          DividerItem_().apply {
            id(if (bean.title) "title_line" else "${bean.name + index.toString()}_line")
            bgColorRes(R.color.gray)
            heightPx(1)
          })
    }
    notifyModelsChanged()
  }

  override fun isStickyHeader(position: Int) = models[position] is Sticky2TopItem_
}