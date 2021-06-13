package cc.abase.demo.component.video

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import cc.ab.base.ext.*
import cc.abase.demo.component.comm.CommBindActivity
import cc.abase.demo.constants.StringConstants
import cc.abase.demo.databinding.ActivityVideoDetailBinding
import cc.abase.demo.widget.dkplayer.MyVideoView
import cc.abase.demo.widget.dkplayer.pipfloat.PIPManager
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.permissions.*
import xyz.doikki.videoplayer.player.VideoViewManager

/**
 * Description: 视频源 https://miao101.com/
 * @author: Khaos
 * @date: 2019/11/2 9:51
 */
class VideoDetailActivity : CommBindActivity<ActivityVideoDetailBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    //视频地址
    private val moveUrlPairs = mutableListOf(
      Pair("http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4", "叶问预览片"),
      Pair("https://v3.dious.cc/20210429/CnNHtUZs/index.m3u8", "不良人4(第一集)"),
      Pair("https://v3.dious.cc/20210429/euEFOXZ2/index.m3u8", "不良人4(第二集)"),
      Pair("https://v3.dious.cc/20210506/CXr0LMp8/index.m3u8", "不良人4(第三集)"),
      Pair("https://v3.dious.cc/20210513/zodnofSz/index.m3u8", "不良人4(第四集)"),
      Pair("https://v3.dious.cc/20210520/KJjHTrV9/index.m3u8", "不良人4(第五集)"),
      Pair("https://v3.dious.cc/20210527/hq83F1Vq/index.m3u8", "不良人4(第六集)"),
      Pair("https://v3.dious.cc/20210527/S8c0zImb/index.m3u8", "不良人4(第七集)"),
      Pair("https://v3.dious.cc/20210603/a6JxY1mK/index.m3u8", "不良人4(第八集)"),
      Pair("https://v3.dious.cc/20210610/a9B0XUbP/index.m3u8", "不良人4(第九集)"),
    )
    private const val INTENT_KEY_VIDEO_URL = "INTENT_KEY_VIDEO_URL"
    fun startActivity(context: Context, videoUrl: String?) {
      val intent = Intent(context, VideoDetailActivity::class.java)
      val index = (System.currentTimeMillis() % moveUrlPairs.size).toInt()
      val defaultUrl = moveUrlPairs[index].first
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

  //<editor-fold defaultstate="collapsed" desc="变量">
  private lateinit var videoDetailVideoView: MyVideoView
  private var mPIPManager = PIPManager.getInstance()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initView() {
    videoDetailVideoView = VideoViewManager.instance().get(StringConstants.Tag.FLOAT_PLAY) as MyVideoView
    viewBinding.videoDetailBack.pressEffectAlpha()
    viewBinding.videoDetailFloat.pressEffectAlpha()
    viewBinding.videoDetailBack.click { onBackPressed() }
    viewBinding.videoDetailStatus.layoutParams.height = mStatusBarHeight
    viewBinding.videoDetailFloat.click {
      XXPermissions.with(this)
        .permission(Permission.SYSTEM_ALERT_WINDOW)
        .request(object : OnPermissionCallback {
          override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
            if (all) {
              mPIPManager.startFloatWindow()
              finish()
            }
          }

          override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
            // 如果是被永久拒绝就跳转到应用权限系统设置页面
            if (never) XXPermissions.startPermissionActivity(mActivity, permissions)
          }
        })
    }
    if (mPIPManager.isStartFloatWindow) {
      mPIPManager.stopFloatWindow()
      videoDetailVideoView.setVideoController(videoDetailVideoView.getMyController())
      videoDetailVideoView.getMyController().setPlayerState(videoDetailVideoView.currentPlayerState)
      videoDetailVideoView.getMyController().setPlayState(videoDetailVideoView.currentPlayState)
    } else {
      mPIPManager.actClass = VideoDetailActivity::class.java
      val url = intent.getStringExtra(INTENT_KEY_VIDEO_URL)
      url?.let { videoDetailVideoView.setPlayUrl(url = it, title = moveUrlPairs.firstOrNull { p -> p.first == it }?.second) }
    }
    viewBinding.videoDetailVideoViewParent.addView(videoDetailVideoView, ViewGroup.LayoutParams(-1, -1))
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="生命周期">
  override fun onDestroy() {
    mPIPManager.reset()
    super.onDestroy()
  }
  //</editor-fold>
}