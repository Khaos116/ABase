package cc.abase.demo.component.recyclerpage

import android.app.Activity
import android.view.View
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
  override fun getLayoutId() = R.layout.item_vertical_page_parent

  override fun createHolder(itemView: View) = object : DiscreteHolder<VerticalPageBean>(itemView) {
    override fun updateUI(data: VerticalPageBean?, position: Int, count: Int) {
      val h = (itemView.itemRecyclePagerContainer?.context as? Activity)?.mContentView?.height ?: ScreenUtils.getScreenHeight()
      val height = h - BarUtils.getStatusBarHeight() - 49.dp2Px()
      data?.cover?.let {
        if (it.isVideoUrl()) {
          itemView.itemRecyclePagerCover?.loadNetVideoCover(it, ScreenUtils.getScreenWidth() * 1f / height, hasHolder = false)
        } else {
          itemView.itemRecyclePagerCover?.loadImgVertical(it, ScreenUtils.getScreenWidth() * 1f / height, hasHolder = false)
        }
      }
    }
  }
}