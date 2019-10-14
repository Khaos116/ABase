package cc.abase.demo.component.splash

import android.content.Context
import android.content.Intent
import cc.ab.base.ext.mContext
import cc.ab.base.ext.toast
import cc.ab.base.widget.discretescrollview.DSVOrientation
import cc.ab.base.widget.discretescrollview.DiscreteBanner
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.component.splash.adapter.GuideHolderCreator
import cc.abase.demo.constants.ImageUrls
import com.gyf.immersionbar.ktx.immersionBar

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/11 15:11
 */
class GuideActivity : CommActivity() {
  //引导页图片，如果需要，可以在启动页进行预加载
  private val mList = ImageUrls.instance.imgs.take(4)

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, GuideActivity::class.java)
      context.startActivity(intent)
    }
  }

  //不默认填充状态栏
  override fun fillStatus() = false

  //状态栏透明
  override fun initStatus() {
    immersionBar { statusBarDarkFont(false) }
  }

  override fun layoutResId() = R.layout.activity_guide

  override fun initView() {
    findViewById<DiscreteBanner<String>>(R.id.guideBanner)
        .setOrientation(DSVOrientation.VERTICAL)
        .apply { getIndicator()?.needSpecial = true }
        .setLooper(true)
        .setPages(GuideHolderCreator(), mList)
        .setOnItemClick { position, t -> mContext.toast("$position") }
  }

  override fun initData() {
  }
}