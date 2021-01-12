package cc.abase.demo.component.splash

import android.content.Context
import android.content.Intent
import cc.ab.base.widget.discretescrollview.DSVOrientation
import cc.ab.base.widget.discretescrollview.DiscreteBanner
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.component.splash.adapter.GuideHolderCreator
import cc.abase.demo.constants.ImageUrls
import com.gyf.immersionbar.ktx.immersionBar

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/11 15:11
 */
class GuideActivity : CommActivity() {
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
  override fun layoutResId() = R.layout.activity_guide
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initView() {
    findViewById<DiscreteBanner<String>>(R.id.guideBanner)
        .setOrientation(DSVOrientation.VERTICAL)
        .apply { getIndicator()?.needSpecial = true }
        .setPages(GuideHolderCreator(), mList)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun initData() {
  }
  //</editor-fold>
}