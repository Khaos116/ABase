package cc.abase.demo.component.splash

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import cc.ab.base.widget.discretescrollview.DSVOrientation
import cc.abase.demo.component.comm.CommBindActivity
import cc.abase.demo.component.splash.adapter.GuideHolderCreator
import cc.abase.demo.constants.ImageUrls
import cc.abase.demo.databinding.ActivityGuideBinding
import com.gyf.immersionbar.ktx.immersionBar

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/11 15:11
 */
class GuideActivity : CommBindActivity<ActivityGuideBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, GuideActivity::class.java)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //引导页图片，如果需要，可以在启动页进行预加载
  private val mList = ImageUrls.imgs.take(4)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="状态栏">
  //不默认填充状态栏
  override fun fillStatus() = false

  //状态栏透明
  override fun initStatus() {
    immersionBar { statusBarDarkFont(true) }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun loadViewBinding(inflater: LayoutInflater) = ActivityGuideBinding.inflate(inflater)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initView() {
    viewBinding.guideBanner
        .setOrientation(DSVOrientation.VERTICAL)
        .apply { getIndicator()?.needSpecial = true }
        .setPages(GuideHolderCreator(), mList)
  }
  //</editor-fold>
}