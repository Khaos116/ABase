package cc.abase.demo.component.recyclerpage

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import cc.ab.base.ext.*
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import cc.abase.demo.R
import cc.abase.demo.bean.local.VerticalPageBean
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ScreenUtils
import kotlinx.android.synthetic.main.item_vertical_page_parent.view.itemRecyclePagerContainer
import kotlinx.android.synthetic.main.item_vertical_page_parent.view.itemRecyclePagerCover

/**
 * Description:
 * @author: CASE
 * @date: 2020/5/13 9:38
 */
class RecyclerPagerHolderCreator : DiscreteHolderCreator {
  override fun createHolder(itemView: View) = VerticalPageHolderView(itemView)

  override fun getLayoutId() = R.layout.item_vertical_page_parent
}

class VerticalPageHolderView(view: View) : DiscreteHolder<VerticalPageBean>(view) {
  var container: FrameLayout? = null
  var cover: ImageView? = null
  override fun updateUI(data: VerticalPageBean, position: Int, count: Int) {
    val h = (container?.context as? Activity)?.mContentView?.height ?: ScreenUtils.getScreenHeight()
    val height = h - BarUtils.getStatusBarHeight() - 49.dp2Px()
    data.cover?.let {
      if (it.isVideoUrl()) {
        cover?.loadNetVideoCover(it, ScreenUtils.getScreenWidth() * 1f / height, hasHolder = false)
      } else {
        cover?.loadImgVertical(it, ScreenUtils.getScreenWidth() * 1f / height, hasHolder = false)
      }
    }
  }

  override fun initView(view: View) {
    container = view.itemRecyclePagerContainer
    cover = view.itemRecyclePagerCover
  }
}