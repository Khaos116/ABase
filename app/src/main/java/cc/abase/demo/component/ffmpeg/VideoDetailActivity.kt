package cc.abase.demo.component.ffmpeg

import android.content.Context
import android.content.Intent
import android.util.Log
import cc.ab.base.ext.*
import cc.ab.base.utils.MediaUtils
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.utils.VideoUtils
import cc.abase.demo.widget.video.controller.StandardVideoController
import cc.abase.demo.widget.video.controller.VodControlView
import cc.abase.demo.widget.video.controller.VodControlView.VerticalFullListener
import com.dueeeke.videocontroller.component.*
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.android.synthetic.main.activity_video_detail.*
import kotlinx.android.synthetic.main.dkplayer_layout_prepare_view.view.thumb
import java.io.File

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/11/2 9:51
 */
class VideoDetailActivity : CommActivity() {
  companion object {
    private const val INTENT_KEY_VIDEO_URL = "INTENT_KEY_VIDEO_URL"
    fun startActivity(
      context: Context,
      videoUrl: String?
    ) {
      val intent = Intent(context, VideoDetailActivity::class.java)
      intent.putExtra(
          INTENT_KEY_VIDEO_URL, if (videoUrl.isNullOrBlank()) {
        "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4"
      } else videoUrl
      )
      context.startActivity(intent)
    }
  }

  //控制器
  private var controller: StandardVideoController? = null
  //准备播放页
  private var prepareView: PrepareView? = null
  //每次设置资源后的第一次播放
  private var isFirstPlay = true

  override fun fillStatus() = false

  override fun initStatus() {
    immersionBar { statusBarDarkFont(false) }
  }

  override fun layoutResId() = R.layout.activity_video_detail

  override fun initView() {
    videoDetailBack.pressEffectAlpha()
    videoDetailBack.click { onBackPressed() }
    videoDetailStatus.layoutParams.height = mStatusBarHeight
    //控制器
    controller = StandardVideoController(this)
    prepareView = PrepareView(mContext)
    controller?.let {
      it.addControlComponent(prepareView)//播放前预览封面
      it.addControlComponent(CompleteView(this)) //自动完成播放界面
      it.addControlComponent(ErrorView(this)) //错误界面
      val titleView = TitleView(this) //标题栏
      it.addControlComponent(titleView)
      val vodControlView = VodControlView(this) //点播控制条
      //是否显示底部进度条,默认显示
      vodControlView.showBottomProgress(true)
      vodControlView.setVerticalFullListener(object : VerticalFullListener {
        override fun isVerticalVideo(): Boolean {
          val videoSize = videoDetailVideoView.videoSize
          return if (videoSize != null) {
            videoSize[0] < videoSize[1]//纵向视频
          } else {
            false
          }
        }

        override fun isStopOutFull(): Boolean {
          return false
        }
      })
      it.addControlComponent(vodControlView)
    }
    controller?.setEnableOrientation(true)
    videoDetailVideoView.setVideoSizeChangeListener { videoWidth, videoHeight ->
      //全屏时跟随屏幕旋转
      controller?.setEnableOrientation(videoWidth > videoHeight)
    }
//    controller?.isNeedNoFullShowBack = true
    //设置控制器
    videoDetailVideoView.setVideoController(controller)
    videoDetailVideoView.setLooping(false)
    //内部处理生命周期
    videoDetailVideoView.setLifecycleOwner(this)
  }

  override fun initData() {
    val url = intent.getStringExtra(INTENT_KEY_VIDEO_URL)
    url?.let {
      videoDetailVideoView.setUrl(it)
      controller?.thumb?.click {
        when {
          isFirstPlay -> {
            videoDetailVideoView.start()
            isFirstPlay = false
          }
          videoDetailVideoView.isPlaying -> {
            videoDetailVideoView.pause()
          }
          else -> {
            videoDetailVideoView.resume()
          }
        }
      }
      if (it.startsWith("http", true)) {
        VideoUtils.instance.getNetVideoFistFrame(it) { bit ->
          if (bit != null) {
            controller?.thumb?.setImageBitmap(bit)
          } else {
            controller?.thumb?.setImageResource(R.drawable.svg_placeholder_fail)
          }
        }
      } else if (File(it).exists()) {
        if (MediaUtils.instance.isVideoFile(it)) {
          VideoUtils.instance.getFirstFrame(File(it)) { suc, info ->
            if (suc) controller?.thumb?.load(File(info))
            else Log.e("CASE", "视频文件封面获取失败:$it")
          }
        } else Log.e("CASE", "非视频文件:$it")
      } else Log.e("CASE", "未知视频播放源:$it")
    }
  }

  override fun onBackPressed() {
    if (videoDetailVideoView?.onBackPressed() == false) super.onBackPressed()
  }
}