package cc.abase.demo.component.recyclerpage

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import cc.ab.base.ext.*
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import cc.abase.demo.bean.local.VerticalPageBean
import cc.abase.demo.databinding.ItemVerticalPageParentBinding
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ScreenUtils
import kotlinx.android.synthetic.main.item_vertical_page_parent.view.itemRecyclePagerContainer
import kotlinx.android.synthetic.main.item_vertical_page_parent.view.itemRecyclePagerCover

/**
 * Description:
 * @author: CASE
 * @date: 2020/5/13 9:38
 */
class RecyclerPagerHolderCreator : DiscreteHolderCreator<VerticalPageBean, ItemVerticalPageParentBinding>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun loadViewBinding(inflater: LayoutInflater, parent: ViewGroup) = ItemVerticalPageParentBinding.inflate(inflater, parent, false)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="创建Holder+数据填充">
  override fun createHolder(binding: ItemVerticalPageParentBinding) =
      object : DiscreteHolder<VerticalPageBean, ItemVerticalPageParentBinding>(binding) {
        //数据填充
        override fun updateUI(data: VerticalPageBean?, binding: ItemVerticalPageParentBinding, position: Int, count: Int) {
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
  //</editor-fold>
}