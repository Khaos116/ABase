package cc.abase.demo.component.recyclerpage

import android.view.View
import android.widget.FrameLayout
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import cc.abase.demo.R
import cc.abase.demo.bean.local.VerticalPageBean
import kotlinx.android.synthetic.main.item_vertical_page_parent.view.itemVerticalContainer

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
  var container: FrameLayout? = null
  override fun updateUI(data: VerticalPageBean, position: Int, count: Int) {
  }

  override fun initView(view: View) {
    container = view.itemVerticalContainer
  }
}