package cc.abase.demo.component.splash.adapter

import android.view.View
import cc.ab.base.ext.*
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import cc.abase.demo.R
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.utils.MMkvUtils
import kotlinx.android.synthetic.main.layout_guide.view.guideGo
import kotlinx.android.synthetic.main.layout_guide.view.guideKIV

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/14 14:30
 */
class GuideHolderCreator : DiscreteHolderCreator {
  override fun getLayoutId() = R.layout.layout_guide

  override fun createHolder(itemView: View) = object : DiscreteHolder<String>(itemView) {
    override fun updateUI(data: String?, position: Int, count: Int) {
      itemView.guideKIV?.load(data)
      itemView.guideGo?.visibleGone(position == count - 1)
      itemView.guideGo?.let { view ->
        view.visibleGone(position == count - 1)
        view.pressEffectAlpha()
        view.click {
          MMkvUtils.instance.setNeedGuide(false)
          LoginActivity.startActivity(it.context)
        }
      }
    }
  }
}