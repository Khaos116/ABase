package cc.abase.demo.component.ffmpeg

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.util.Log
import cc.ab.base.ext.load
import cc.ab.base.ext.mStatusBarHeight
import cc.ab.base.utils.RxUtils
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.utils.MediaUtils
import cc.abase.demo.utils.VideoUtils
import cc.abase.demo.widget.video.controller.StandardVideoController
import cc.abase.demo.widget.video.player.CustomExoMediaPlayer
import com.dueeeke.videoplayer.player.VideoView
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.android.synthetic.main.activity_video_detail.videoDetailStatus
import kotlinx.android.synthetic.main.activity_video_detail.videoDetailVideoView
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
      videoUrl: String
    ) {
      val intent = Intent(context, VideoDetailActivity::class.java)
      intent.putExtra(
          INTENT_KEY_VIDEO_URL, "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4"
      )
      context.startActivity(intent)
    }
  }

  //控制器
  private var controller: StandardVideoController<VideoView<CustomExoMediaPlayer>>? = null

  override fun fillStatus() = false

  override fun initStatus() {
    immersionBar { statusBarDarkFont(false) }
  }

  override fun layoutResId() = cc.abase.demo.R.layout.activity_video_detail

  override fun initView() {
    videoDetailStatus.layoutParams.height = mStatusBarHeight
    //控制器
    controller = StandardVideoController(this)
    //全屏时跟随屏幕旋转
    controller?.setEnableOrientation(true)
    controller?.layoutMode
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
      if (it.startsWith("http", true)) {
        io.reactivex.Observable.just(it)
            .flatMap {
              val retriever =MediaMetadataRetriever()
              retriever.setDataSource(it, HashMap())
              val bitmap = retriever.getFrameAtTime()
              io.reactivex.Observable.just(bitmap)
            }
            .compose(RxUtils.instance.rx2SchedulerHelperO())
            .subscribe({ bit ->
              controller?.thumb?.setImageBitmap(bit)
            }, { controller?.thumb?.setImageResource(R.drawable.svg_placeholder_fail) })

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