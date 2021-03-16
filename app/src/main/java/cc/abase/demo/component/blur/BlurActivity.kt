package cc.abase.demo.component.blur

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import cc.ab.base.ext.loadImgHorizontalBlur
import cc.ab.base.ext.xmlToString
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.constants.ImageUrls
import cc.abase.demo.databinding.ActivityBlurBinding

/**
 * @Description
 * @Author：CASE
 * @Date：2021/1/9
 * @Time：10:04
 */
class BlurActivity : CommBindTitleActivity<ActivityBlurBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      context.startActivity(Intent(context, BlurActivity::class.java))
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun loadViewBinding(inflater: LayoutInflater) = ActivityBlurBinding.inflate(inflater)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initContentView() {
    setTitleText(R.string.title_blur.xmlToString())
    val url = ImageUrls.image_1125x642
    viewBinding.blurIv1.loadImgHorizontalBlur(url, holderRatio = 1125f / 642, blurRadius = 5f)
    viewBinding.blurIv2.loadImgHorizontalBlur(url, holderRatio = 1125f / 642, blurRadius = 10f, blackWhite = true)
    viewBinding.blurIv3.loadImgHorizontalBlur(url, holderRatio = 1125f / 642, blurRadius = 15f)
  }
  //</editor-fold>
}