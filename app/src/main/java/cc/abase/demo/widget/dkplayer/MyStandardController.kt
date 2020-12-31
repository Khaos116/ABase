package cc.abase.demo.widget.dkplayer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.TextView
import cc.ab.base.ext.dp2Px
import cc.ab.base.ext.removeParent
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.VibrateUtils
import com.dueeeke.videocontroller.StandardVideoController
import com.dueeeke.videoplayer.player.VideoView

/**
 * 为了监听手指按压和抬起,使用倍速播放
 * Author:CASE
 * Date:2020/12/30
 * Time:10:16
 */
@SuppressLint("SetTextI18n")
class MyStandardController : StandardVideoController {
  //<editor-fold defaultstate="collapsed" desc="多构造">
  constructor(c: Context) : super(c, null, 0)
  constructor(c: Context, a: AttributeSet) : super(c, a, 0)
  constructor(c: Context, a: AttributeSet?, d: Int) : super(c, a, d)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  //倍速显示
  private val speedTv = TextView(context)

  //是否在列表中
  private var isInList = false

  init {
    //倍速
    speedTv.text = "播放速度x3"
    speedTv.setTextColor(Color.WHITE)
    speedTv.setShadowLayer(1f, 1f, 1f, Color.parseColor("#4D000000"))
    speedTv.textSize = 14f
    speedTv.gravity = Gravity.CENTER
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="触发手势操作就取消倍速">
  override fun slideToChangePosition(deltaX: Float) {
    super.slideToChangePosition(deltaX)
    cancelSpeedShow()
  }

  override fun slideToChangeBrightness(deltaY: Float) {
    super.slideToChangeBrightness(deltaY)
    cancelSpeedShow()
  }

  override fun slideToChangeVolume(deltaY: Float) {
    super.slideToChangeVolume(deltaY)
    cancelSpeedShow()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="倍速播放">
  //按压和抬起监听
  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    ev?.let { e ->
      when (e.action and MotionEvent.ACTION_MASK) {
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> cancelSpeedShow(true)
        MotionEvent.ACTION_DOWN -> if (mControlWrapper.isPlaying) postDelayed(runnable, 500)
        else -> {
        }
      }
    }
    return super.dispatchTouchEvent(ev)
  }

  //取消倍速
  private fun cancelSpeedShow(vibrate: Boolean = false) {
    if (mControlWrapper.isShowing) mControlWrapper.startFadeOut()
    if (vibrate && mControlWrapper.speed > 1f) VibrateUtils.vibrate(50)
    mControlWrapper.speed = 1f
    removeCallbacks(runnable)
    speedTv.removeParent()
  }

  //长按触发倍速播放
  private val runnable = Runnable {
    if (mControlWrapper.isPlaying) {
      speedTv.removeParent()
      val params = FrameLayout.LayoutParams(-2, -2)
      val w = mControlWrapper.videoSize[0]
      val h = mControlWrapper.videoSize[1]
      params.gravity = Gravity.CENTER_HORIZONTAL
      params.topMargin = if (mControlWrapper.isFullScreen) { //全屏
        speedTv.textSize = 14f
        35.dp2Px() + if (w < h) mControlWrapper.cutoutHeight else 0 //竖向全屏需要兼容状态栏
      } else { //非全屏
        speedTv.textSize = 12f
        if (fitStatus && fitTitle) (BarUtils.getStatusBarHeight() + 35.dp2Px())
        else if (fitStatus) (BarUtils.getStatusBarHeight() + 5.dp2Px())
        else if (fitTitle) 35.dp2Px()
        else if (isInList) 5.dp2Px()
        else 35.dp2Px()
      }
      addView(speedTv, params)
      if (!mControlWrapper.isShowing) mControlWrapper.show()
      mControlWrapper.stopFadeOut()
      mControlWrapper.speed = 3f
      VibrateUtils.vibrate(50)
    }
  }

  //不处于播放状态要取消倍速展示
  override fun onPlayerStateChanged(playState: Int) {
    super.onPlayerStateChanged(playState)
    when (playState) {
      VideoView.STATE_ERROR, VideoView.STATE_IDLE, VideoView.STATE_PREPARING, VideoView.STATE_PAUSED, VideoView.STATE_START_ABORT, VideoView.STATE_PLAYBACK_COMPLETED -> cancelSpeedShow()
      VideoView.STATE_PLAYING -> mControlWrapper.speed = 1f //防止播放结束和异常没有重置到播放速度
      else -> {
      }
    }
  }

  private var fitStatus = false
  private var fitTitle = false

  //倍速显示位置是否要适配状态栏
  fun fitSpeedStatus(fit: Boolean) {
    fitStatus = fit
  }

  //倍速显示位置是否要适配标题栏
  fun fitSpeedTitle(fit: Boolean) {
    fitTitle = fit
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="重写">
  override fun setEnableInNormal(enableInNormal: Boolean) {
    super.setEnableInNormal(enableInNormal)
    isInList = !enableInNormal
  }
  //</editor-fold>
}