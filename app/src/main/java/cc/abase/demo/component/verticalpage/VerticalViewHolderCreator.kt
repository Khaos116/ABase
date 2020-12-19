package cc.abase.demo.component.verticalpage

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import cc.ab.base.ext.load
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import cc.abase.demo.R
import cc.abase.demo.bean.local.VerticalPageBean
import kotlinx.android.synthetic.main.item_vertical_page.view.itemVerticalThumb
import kotlinx.android.synthetic.main.item_vertical_page.view.itemVerticalTitle
import kotlinx.android.synthetic.main.item_vertical_page_parent.view.itemVerticalContainer
import kotlinx.android.synthetic.main.item_vertical_page_parent.view.itemVerticalPIV
import me.panpf.sketch.SketchImageView

/**
 * Description:
 * @author: CASE
 * @date: 2020/5/13 9:38
 */
class VerticalPageHolderCreator : DiscreteHolderCreator {
  override fun createHolder(itemView: View) = VerticalPageHolderView(itemView)

  override fun getLayoutId() = R.layout.item_vertical_page_parent
}

class VerticalPageHolderView(view: View) : DiscreteHolder<VerticalPageBean>(view) {

  lateinit var tvDes: TextView
  lateinit var ivCover: SketchImageView
  lateinit var parentVideo: FrameLayout
  lateinit var piv: VerticalPagerItemView

  override fun updateUI(data: VerticalPageBean, position: Int, count: Int) {
    tvDes.text = data.description
    ivCover.load(data.cover)
    ivCover.clearAnimation()
    if (piv.parent == null) parentVideo.addView(piv)
  }

  override fun initView(view: View) {
    this.tvDes = view.itemVerticalTitle
    this.ivCover = view.itemVerticalThumb
    this.parentVideo = view.itemVerticalContainer
    this.piv = view.itemVerticalPIV
  }
}