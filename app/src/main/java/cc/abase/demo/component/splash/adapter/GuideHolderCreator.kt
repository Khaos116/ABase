package cc.abase.demo.component.splash.adapter

import android.view.View
import android.widget.TextView
import cc.ab.base.ext.*
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import cc.abase.demo.R
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.utils.MMkvUtils
import kotlinx.android.synthetic.main.layout_guide.view.guideGo
import kotlinx.android.synthetic.main.layout_guide.view.guideKIV
import me.panpf.sketch.SketchImageView

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/14 14:30
 */
class GuideHolderCreator : DiscreteHolderCreator {
  override fun createHolder(itemView: View): DiscreteHolder<String> = GuideHolder(itemView)

  override fun getLayoutId() = R.layout.layout_guide
}

class GuideHolder(view: View) : DiscreteHolder<String>(view) {
  private var imageView: SketchImageView? = null
  private var textView: TextView? = null
  override fun initView(itemView: View) {
    imageView = itemView.guideKIV
    textView = itemView.guideGo
  }

  override fun updateUI(
    data: String?,
    position: Int,
    count: Int
  ) {
    this.imageView?.load(data)
    this.textView?.visibleGone(position == count - 1)
    this.textView?.let { view ->
      view.visibleGone(position == count - 1)
      view.pressEffectAlpha()
      view.click {
        MMkvUtils.instance.setNeedGuide(false)
        LoginActivity.startActivity(it.context)
      }
    }
  }
}