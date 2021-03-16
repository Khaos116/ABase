package cc.abase.demo.component.video

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import cc.ab.base.ext.*
import cc.abase.demo.component.comm.CommBindActivity
import cc.abase.demo.constants.StringConstants
import cc.abase.demo.databinding.ActivityVideoDetailBinding
import cc.abase.demo.widget.dkplayer.MyVideoView
import cc.abase.demo.widget.dkplayer.pipfloat.PIPManager
import com.dueeeke.videoplayer.player.VideoViewManager
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.permissions.*

/**
 * Description:
 * @author: CASE
 * @date: 2019/11/2 9:51
 */
class VideoDetailActivity : CommBindActivity<ActivityVideoDetailBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    //视频地址
    private val moveUrls = mutableListOf(
        "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4",
        "https://meinv.jingyu-zuida.com/20200917/13581_c98cc8fd/1000k/hls/index.m3u8",
    )
    private const val INTENT_KEY_VIDEO_URL = "INTENT_KEY_VIDEO_URL"
    fun startActivity(context: Context, videoUrl: String?) {
      val intent = Intent(context, VideoDetailActivity::class.java)
      val defaultUrl = moveUrls[(System.currentTimeMillis() % moveUrls.size).toInt()]
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
  override fun loadViewBinding(inflater: LayoutInflater) = ActivityVideoDetailBinding.inflate(inflater)
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
      url?.let { videoDetailVideoView.setPlayUrl(it) }
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