package cc.abase.demo.component.splash.adapter

import android.app.Activity
import android.view.View
import cc.ab.base.ext.*
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import cc.abase.demo.R
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.utils.MMkvUtils
import com.blankj.utilcode.util.ScreenUtils
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
      val height = (itemView.context as? Activity)?.mContentView?.height ?: ScreenUtils.getScreenHeight()
      val width = ScreenUtils.getScreenWidth()
      itemView.guideKIV?.loadImgVertical(data, holderRatio = width * 1f / height)
      itemView.guideGo?.visibleGone(position == count - 1)
      itemView.guideGo?.let { view ->
        view.visibleGone(position == count - 1)
        view.pressEffectAlpha()
        view.click {
          MMkvUtils.setNeedGuide(false)
          LoginActivity.startActivity(it.context)
        }
      }
    }
  }
}