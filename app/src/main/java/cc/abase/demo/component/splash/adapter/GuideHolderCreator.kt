package cc.abase.demo.component.splash.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import cc.ab.base.ext.*
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.databinding.LayoutGuideBinding
import cc.abase.demo.utils.MMkvUtils
import com.blankj.utilcode.util.ScreenUtils

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/14 14:30
 */
class GuideHolderCreator : DiscreteHolderCreator<String, LayoutGuideBinding>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun loadViewBinding(inflater: LayoutInflater, parent: ViewGroup) = LayoutGuideBinding.inflate(inflater, parent, false)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="创建Holder+数据填充">
  override fun createHolder(binding: LayoutGuideBinding) = object : DiscreteHolder<String, LayoutGuideBinding>(binding) {
    //填充数据
    override fun updateUI(data: String?, binding: LayoutGuideBinding, position: Int, count: Int) {
      val height = (itemView.context as? Activity)?.mContentView?.height ?: ScreenUtils.getScreenHeight()
      val width = ScreenUtils.getScreenWidth()
      binding.guideKIV.loadImgVertical(data, holderRatio = width * 1f / height)
      binding.guideGo.visibleGone(position == count - 1)
      binding.guideGo.let { view ->
        view.visibleGone(position == count - 1)
        view.pressEffectAlpha()
        view.click {
          MMkvUtils.setNeedGuide(false)
          LoginActivity.startActivity(it.context)
        }
      }
    }
  }
  //</editor-fold>
}