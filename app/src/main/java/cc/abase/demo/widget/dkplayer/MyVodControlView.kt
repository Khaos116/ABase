package cc.abase.demo.widget.dkplayer

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import cc.ab.base.ext.dp2px
import cc.ab.base.ext.getMyParents
import cc.abase.demo.constants.StringConstants
import xyz.doikki.videocontroller.R
import xyz.doikki.videocontroller.component.VodControlView
import xyz.doikki.videoplayer.player.VideoView
import xyz.doikki.videoplayer.player.VideoViewManager
import xyz.doikki.videoplayer.util.PlayerUtils

/**
 * 解决竖向视频全屏问题
 * Author:Khaos
 * Date:2020/12/24
 * Time:17:50
 */
class MyVodControlView @kotlin.jvm.JvmOverloads constructor(c: Context, a: AttributeSet? = null, d: Int = 0) : VodControlView(c, a, d) {
  //<editor-fold defaultstate="collapsed" desc="重写">
  //重写点击事件，解决竖向全屏问题
  override fun onClick(v: View) {
    val id = v.id
    if (id == R.id.fullscreen) {
      toggleFullScreen2()
    } else if (id == R.id.iv_play) {
      mControlWrapper.togglePlay()
    }
  }

  //增加播放完成退出全屏
  override fun onPlayStateChanged(playState: Int) {
    super.onPlayStateChanged(playState)
    //播放完成，如果是全屏则退出全屏
    if (playState == VideoView.STATE_PLAYBACK_COMPLETED && mControlWrapper.isFullScreen) {
      toggleFullScreen2()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="增加竖向全屏的适配">
  private fun toggleFullScreen2() {
    val size = mControlWrapper.videoSize
    if (size[0] > 0 && size[1] > 0 && size[0] > size[1]) {
      val activity = this.getMyParents().lastOrNull { v -> v.context is Activity }?.let { f -> f.context as Activity }
        ?: VideoViewManager.instance().get(StringConstants.Tag.FLOAT_PLAY).getMyParents().lastOrNull { v -> v.context is Activity }
          ?.let { f -> f.context as Activity } ?: PlayerUtils.scanForActivity(context)
      mControlWrapper.toggleFullScreen(activity)
    } else {
      mControlWrapper.toggleFullScreen()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="设置全屏按钮的显示状态">
  fun setFullShow(visible: Int) {
    findViewById<View>(R.id.fullscreen)?.let { iv -> //全屏按钮处理
      iv.visibility = visible
      findViewById<View>(R.id.total_time)?.let { tv -> //时间间距处理
        (tv.layoutParams as? MarginLayoutParams)?.marginEnd = if (visible == View.GONE) 10.dp2px() else 0
      }
    }
  }
  //</editor-fold>
}