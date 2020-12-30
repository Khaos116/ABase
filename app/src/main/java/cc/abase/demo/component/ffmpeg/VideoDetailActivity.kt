package cc.abase.demo.component.ffmpeg

import android.content.Context
import android.content.Intent
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.android.synthetic.main.activity_video_detail.*

/**
 * Description:
 * @author: CASE
 * @date: 2019/11/2 9:51
 */
class VideoDetailActivity : CommActivity() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    private const val INTENT_KEY_VIDEO_URL = "INTENT_KEY_VIDEO_URL"
    fun startActivity(context: Context, videoUrl: String?) {
      val intent = Intent(context, VideoDetailActivity::class.java)
      val defaultUrl = "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4"
      intent.putExtra(INTENT_KEY_VIDEO_URL, if (videoUrl.isNullOrBlank()) defaultUrl else videoUrl)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="是否默认填充到状态栏">
  override fun fillStatus() = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="状态栏颜色设置">
  override fun initStatus() {
    immersionBar { statusBarDarkFont(false) }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.activity_video_detail
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initView() {
    videoDetailBack.pressEffectAlpha()
    videoDetailBack.click { onBackPressed() }
    videoDetailStatus.layoutParams.height = mStatusBarHeight
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun initData() {
    val url = intent.getStringExtra(INTENT_KEY_VIDEO_URL)
    url?.let { videoDetailVideoView.setPlayUrl(it) }
  }
  //</editor-fold>
}