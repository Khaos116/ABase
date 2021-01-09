package cc.abase.demo.component.blur

import android.content.Context
import android.content.Intent
import cc.ab.base.ext.loadImgHorizontalBlur
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.constants.ImageUrls
import kotlinx.android.synthetic.main.activity_blur.*

/**
 * @Description
 * @Author：CASE
 * @Date：2021/1/9
 * @Time：10:04
 */
class BlurActivity : CommTitleActivity() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      context.startActivity(Intent(context, BlurActivity::class.java))
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResContentId() = R.layout.activity_blur
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initContentView() {
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun initData() {
    val url = ImageUrls.image_1125x642
    blurIv1.loadImgHorizontalBlur(url, holderRatio = 1125f / 642, blurRadius = 5f)
    blurIv2.loadImgHorizontalBlur(url, holderRatio = 1125f / 642, blurRadius = 10f, blackWhite = true)
    blurIv3.loadImgHorizontalBlur(url, holderRatio = 1125f / 642, blurRadius = 15f)
  }
  //</editor-fold>
}