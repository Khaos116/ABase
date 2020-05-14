package cc.abase.demo.component.sticky.adapter

import cc.abase.demo.R
import cc.abase.demo.bean.local.UserStickyBean
import cc.abase.demo.component.sticky.widget.HasStickyHeader
import cc.abase.demo.epoxy.base.DividerItem_
import cc.abase.demo.epoxy.item.Sticky2BottomItem_
import com.airbnb.epoxy.EpoxyAdapter

/**
 * Showcases [EpoxyAdapter] with sticky header support
 */
class StickyHeaderAdapter2(list: MutableList<UserStickyBean>) : EpoxyAdapter(), HasStickyHeader {
  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    enableDiffing()
    refresh(list, true)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量区">
  /**
   * 如果要全部重新刷新，则更新该变量。如果只是添加数据，请调用addMoreData
   * @see cc.abase.demo.component.sticky.adapter.StickyHeaderAdapter2.addMoreData
   */
  var mData: MutableList<UserStickyBean> = list
    set(value) {
      field = value
      refresh(value, true)
    }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部添加item">

  /**
   * @data 系统添加的item数据
   * @refresh 是否需要全部清空重新添加data
   */
  private fun refresh(data: List<UserStickyBean>, refresh: Boolean) {
    if (refresh) removeAllModels()
    data.forEachIndexed { index, bean ->
      //标题
      // if (bean.title) addModel(Sticky2TopItem_().apply { id("topTitle") })//旧版本的滑动效果3
      // else
      //成绩
      addModel(
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
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="加载更多时添加数据">
  fun addMoreData(list: List<UserStickyBean>) {
    mData.addAll(list)
    refresh(list, false)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="固定在顶部的View">
  override fun isStickyHeader(position: Int) = false//models[position] is Sticky2TopItem_//旧版本的滑动效果4
  //</editor-fold>
}