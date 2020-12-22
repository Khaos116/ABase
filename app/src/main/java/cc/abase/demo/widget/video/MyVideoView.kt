package cc.abase.demo.widget.video

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import cc.ab.base.ext.load
import cc.ab.base.utils.MediaUtils
import cc.abase.demo.R
import cc.abase.demo.constants.PathConstants
import cc.abase.demo.utils.VideoUtils
import com.blankj.utilcode.util.*
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import me.panpf.sketch.SketchImageView
import java.io.File

/**
 * Author:CASE
 * Date:2020/12/19
 * Time:16:07
 */
class MyVideoView : StandardGSYVideoPlayer, LifecycleObserver {
  //<editor-fold defaultstate="collapsed" desc="构造方法">
  constructor(context: Context) : super(context) //一定要写单Context构造，否则全屏会异常展示
  constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
  constructor(context: Context, fullFlag: Boolean) : super(context, fullFlag)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="重写">
  //准备播放过程中依然显示封面
  override fun changeUiToPreparingShow() {
    super.changeUiToPreparingShow()
    setViewShowState(mThumbImageViewLayout, VISIBLE)
  }

  //重写点击事件
  override fun onClick(v: View) {
    super.onClick(v)
    when (v.id) {
      com.shuyu.gsyvideoplayer.R.id.back -> (context as? Activity)?.onBackPressed()
      else -> {
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //是否在列表中播放
  private var isInList = false

  //是否要适配状态栏
  private var fitSystemWindow = false

  //全屏工具
  private var orientationUtils: OrientationUtils? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    val activity = context as AppCompatActivity
    setLifecycleOwner(activity)
    //封面
    thumbImageView = SketchImageView(activity).also { iv -> iv.scaleType = ImageView.ScaleType.FIT_CENTER }
    //旋转工具
    orientationUtils = OrientationUtils(activity, this).also { ou ->
      //不要重力感应
      ou.releaseListener()
      ou.isEnable = false
    }
    //关闭自动旋转
    isRotateViewAuto = false
    //不要旋转动画
    isShowFullAnimation = false
    //自动判断竖屏全屏还是横屏全屏
    mAutoFullWithSize = true
    //是否跟随系统设置
    mRotateWithSystem = false
    //旋转时仅处理横屏
    isOnlyRotateLand = false
    //设置全屏点击
    setVideoAllCallBack(object : GSYSampleCallBack() {
      override fun onQuitFullscreen(url: String, vararg objects: Any) {
        super.onQuitFullscreen(url, *objects)
        orientationUtils?.backToProtVideo()
      }
    })
    fullscreenButton.setOnClickListener {
      orientationUtils?.isLand = 0 //回复原来的值，因为全屏后内部采用的是GSYBaseVideoPlayer新建的mOrientationUtils
      orientationUtils?.resolveByClick() //直接横屏
      //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
      startWindowFullscreen(activity, false, true)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部设置">
  //设置是否处于列表中播放
  fun setInList(inList: Boolean): MyVideoView {
    isInList = inList
    return this
  }

  //设置状态栏适配
  fun setFitSystemWindow(fit: Boolean): MyVideoView {
    fitSystemWindow = fit
    (mTopContainer?.layoutParams as? MarginLayoutParams)?.topMargin = if (fitSystemWindow) BarUtils.getStatusBarHeight() else 0
    return this
  }

  //设置播放信息
  fun setPlayUrl(url: String, title: String = "", cover: String = "", autoPlay: Boolean = false): MyVideoView {
    //设置播放url和缓存地址
    setUp(url, true, File(PathConstants.videoCacheDir, EncryptUtils.encryptMD5ToString(url)), if (title.isBlank()) url else title)
    //清空上一个封面
    (thumbImageView as? ImageView)?.setImageDrawable(null)
    //加载封面
    if (cover.isBlank()) {
      if (url.startsWith("http", true)) {
        VideoUtils.instance.getNetVideoFistFrame(url) { bit ->
          if (bit != null) {
            (thumbImageView as? ImageView)?.setImageBitmap(bit)
          } else {
            (thumbImageView as? ImageView)?.setImageResource(R.drawable.svg_placeholder_fail)
          }
        }
      } else if (File(url).exists()) {
        if (MediaUtils.instance.isVideoFile(url)) {
          VideoUtils.instance.getFirstFrame(File(url)) { suc, info ->
            if (suc) (thumbImageView as? SketchImageView)?.load(File(info))
            else LogUtils.e("CASE:视频文件封面获取失败:$url")
          }
        } else LogUtils.e("CASE:非视频文件:$url")
      }
    } else {
      (thumbImageView as? SketchImageView)?.load(cover)
    }
    //自动播放
    if (autoPlay) startPlayLogic()
    return this
  }

  //重力感应旋转时调用
  fun onConfigChanged(newConfig: Configuration) {
    onConfigurationChanged(context as Activity, newConfig, orientationUtils, true, true)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Lifecycle生命周期">
  private var mLifecycle: Lifecycle? = null

  //通过Lifecycle内部自动管理暂停和播放(如果不需要后台播放)
  fun setLifecycleOwner(owner: LifecycleOwner?) {
    if (owner == null) {
      mLifecycle?.removeObserver(this)
      mLifecycle = null
    } else {
      mLifecycle?.removeObserver(this)
      mLifecycle = owner.lifecycle
      mLifecycle?.addObserver(this)
    }
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  private fun onResumeVideo() {
    getCurPlay().onVideoResume(false)
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  private fun onPauseVideo() {
    getCurPlay().onVideoPause()
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  private fun onDestroyVideo() {
    getCurPlay().release()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="获取当前播放器">
  private fun getCurPlay(): GSYVideoPlayer {
    return fullWindowPlayer ?: this
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="处理返回">
  fun onBackPress(): Boolean {
    orientationUtils?.backToProtVideo()
    val c = context
    return if (c is Activity) GSYVideoManager.backFromWindowFull(c) else false
  }
  //</editor-fold>
}