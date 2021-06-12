package cc.abase.demo.widget.dkplayer

import android.content.Context
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.widget.*
import cc.abase.demo.R
import com.blankj.utilcode.util.BarUtils
import xyz.doikki.videocontroller.component.TitleView
import xyz.doikki.videoplayer.controller.ControlWrapper
import xyz.doikki.videoplayer.player.VideoView
import xyz.doikki.videoplayer.util.PlayerUtils

/**
 * 1.解决竖向全屏适配状态栏
 * 2.解决非列表中不显示电量和时间
 * Author:Khaos
 * Date:2020/12/24
 * Time:18:41
 */
class MyTitleView @kotlin.jvm.JvmOverloads constructor(c: Context, a: AttributeSet? = null, d: Int = 0) : TitleView(c, a, d) {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //进入全屏前返回按钮的显示状态
  var noFullBackVisibility = View.VISIBLE

  //是否在列表中
  private var isInList = false

  //全屏判断工具
  private var mControlWrapper: ControlWrapper? = null

  //标题
  private var mBack: ImageView = findViewById(R.id.back)

  //标题
  private var mTitle: TextView = findViewById(R.id.title)

  //系统时间
  private var mSysTime: TextView = findViewById(R.id.sys_time)

  //电池电量
  private var batteryLevel: ImageView = findViewById(R.id.iv_battery)

  //标题容器
  private var mTitleContainer: LinearLayout = findViewById(R.id.title_container)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="重写">
  //获取视频相关工具
  override fun attach(controlWrapper: ControlWrapper) {
    super.attach(controlWrapper)
    mControlWrapper = controlWrapper
  }

  //解决非全屏不显示电量和时间的问题
  override fun onVisibilityChanged(isVisible: Boolean, anim: Animation?) {
    //由于判断全屏
    val wrapper = mControlWrapper
    if (isInList || wrapper == null) { //如果在列表中，则不修改
      super.onVisibilityChanged(isVisible, anim)
    } else { //如果不在列表中
      if (wrapper.isFullScreen) { //全屏才显示电量和时间
        mSysTime.visibility = View.VISIBLE
        batteryLevel.visibility = View.VISIBLE
      } else { //非全屏不显示电量和时间
        mSysTime.visibility = View.GONE
        batteryLevel.visibility = View.GONE
      }
      if (isVisible) {
        if (visibility == GONE) {
          mSysTime.text = PlayerUtils.getCurrentSystemTime()
          visibility = VISIBLE
          if (!mTitle.isSelected) mTitle.isSelected = true //为了让跑马灯跑起来
          anim?.let { startAnimation(it) }
        }
      } else {
        if (visibility == VISIBLE) {
          visibility = GONE
          anim?.let { startAnimation(it) }
        }
      }
    }
  }

  //兼容竖屏全屏显示
  override fun onPlayerStateChanged(playerState: Int) {
    super.onPlayerStateChanged(playerState)
    //处理全屏状态
    if (playerState == VideoView.PLAYER_FULL_SCREEN) { //进入全屏，记录之前的显示状态，并强制显示返回
      noFullBackVisibility = mBack.visibility
      mBack.visibility = View.VISIBLE
    } else { //退出全屏，回复显示状态
      mBack.visibility = noFullBackVisibility
    }
    mControlWrapper?.let { cw ->
      //处于全屏+需要适配刘海+是竖屏视频
      if (cw.isFullScreen && cw.hasCutout() && cw.videoSize[0] < cw.videoSize[1]) {
        PlayerUtils.scanForActivity(context)?.let { ac ->
          //是竖屏全屏状态
          if (ac.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            mTitleContainer.setPadding(0, cw.cutoutHeight, 0, 0)
          }
        }
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部设置">
  //设置是否在列表中
  fun setInList(inList: Boolean) {
    isInList = inList
    if (isInList) visibility = GONE //在列表中直接不显示
  }

  //强制适配状态栏
  fun forceFitWindow(fit: Boolean) {
    (mTitleContainer.layoutParams as? MarginLayoutParams)?.topMargin = if (fit) BarUtils.getStatusBarHeight() else 0
  }
  //</editor-fold>
}