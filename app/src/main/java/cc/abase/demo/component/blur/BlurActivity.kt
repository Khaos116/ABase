package cc.abase.demo.component.blur

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.constants.ImageUrls
import cc.abase.demo.databinding.ActivityBlurBinding

/**
 * @Description
 * @Author：Khaos
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

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  @SuppressLint("LogNotTimber")
  override fun initContentView() {
    setTitleText(R.string.高斯模糊.xmlToString())
    val url = ImageUrls.image_1125x642
    viewBinding.blurIv1.loadCoilImg(url = url, holderRatio = 1125f / 642, blurRadius = 5f)
    viewBinding.blurIv2.loadCoilImg(url = url, holderRatio = 1125f / 642, blurRadius = 10f, blackWhite = true)
    viewBinding.blurIv3.loadCoilImg(url = url, holderRatio = 1125f / 642, blurRadius = 15f)
    viewBinding.blurIv1.click { v ->
      Log.e("BlurTransformation", "高斯模糊控件Size:w=${v.width},h=${v.height}")
    }
  }
  //</editor-fold>
}