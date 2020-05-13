package cc.abase.demo.component.verticalpage

import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import cc.ab.base.ext.load
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import cc.abase.demo.R
import cc.abase.demo.bean.local.VerticalPageBean
import kotlinx.android.synthetic.main.item_vertical_page.view.*
import me.panpf.sketch.SketchImageView

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/5/13 9:38
 */
class VerticalPageHolderCreator : DiscreteHolderCreator {
  override fun createHolder(itemView: View) = VerticalPageHolderView(itemView)

  override fun getLayoutId() = R.layout.item_vertical_page
}

class VerticalPageHolderView(view: View) : DiscreteHolder<VerticalPageBean>(view) {

  lateinit var tvDes: TextView
  lateinit var ivCover: SketchImageView
  lateinit var parentVideo: FrameLayout

  override fun updateUI(data: VerticalPageBean, position: Int, count: Int) {
    tvDes.setBackgroundColor(if (position % 2 == 0) Color.MAGENTA else Color.CYAN)
    tvDes.text = data.description
    ivCover.load(data.cover)
    ivCover.clearAnimation()
    ivCover.alpha = 1f
  }

  override fun initView(view: View) {
    this.tvDes = view.itemVerticalDes
    this.ivCover = view.itemVerticalCover
    this.parentVideo = view.itemVerticalVideoParent
  }
}