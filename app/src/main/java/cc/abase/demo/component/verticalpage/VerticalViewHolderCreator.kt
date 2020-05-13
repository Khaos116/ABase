package cc.abase.demo.component.verticalpage

import android.graphics.Color
import android.view.View
import android.widget.TextView
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import cc.abase.demo.R
import cc.abase.demo.bean.local.VerticalPageBean
import kotlinx.android.synthetic.main.item_vertical_page.view.itemVerticalDes

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

  override fun updateUI(data: VerticalPageBean, position: Int, count: Int) {
    tvDes.setBackgroundColor(if (position % 2 == 0) Color.MAGENTA else Color.CYAN)
    tvDes.text = data.description
  }

  override fun initView(view: View) {
    this.tvDes = view.itemVerticalDes
  }
}