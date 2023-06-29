package cc.abase.demo.widget

import android.Manifest
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleObserver
import cc.ab.base.ext.dp2px
import com.google.zxing.Result
import com.king.zxing.*
import com.king.zxing.analyze.MultiFormatAnalyzer
import com.king.zxing.util.PermissionUtils


/**
 * @Description
 * @Author：Khaos
 * @Date：2021/1/26
 * @Time：17:15
 */
class MyZxingView : ConstraintLayout, LifecycleObserver {
  //<editor-fold defaultstate="collapsed" desc="多构造">
  constructor(c: Context) : super(c, null, 0)
  constructor(c: Context, a: AttributeSet) : super(c, a, 0)
  constructor(c: Context, a: AttributeSet?, d: Int) : super(c, a, d)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //扫码成功的声音
  private var mSoundPoolUtil: SoundPoolUtil? = null

  //扫码回调
  var mScanListener: CameraScan.OnScanResultCallback? = null

  //扫码
  private var mCameraScan: CameraScan? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    removeAllViews()
    mSoundPoolUtil = SoundPoolUtil()
    mSoundPoolUtil?.loadDefault(context, com.king.zxing.R.raw.zxl_beep)

    val previewView = PreviewView(context)
    this.addView(previewView, LayoutParams(-1, -1))

    val viewfinderView = ViewfinderView(context)
    this.addView(viewfinderView, LayoutParams(-1, -1))

    val ivFlashlight = ImageView(context)
    ivFlashlight.setImageResource(com.king.zxing.R.drawable.zxl_flashlight_selector)
    val lpIv = LayoutParams(-2, -2)
    this.addView(ivFlashlight, lpIv)
    // 设置按钮的约束
    lpIv.startToStart = LayoutParams.PARENT_ID
    lpIv.endToEnd = LayoutParams.PARENT_ID
    lpIv.bottomToBottom = LayoutParams.PARENT_ID
    lpIv.bottomMargin = 10.dp2px()
    ivFlashlight.setOnClickListener { v -> toggleTorchState(v) }

    initCameraScan(previewView, ivFlashlight)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部方法">
  private fun initCameraScan(pv: PreviewView, iv: ImageView) {
    isStartCamera = false
    mCameraScan = DefaultCameraScan(context as FragmentActivity, pv)
    //初始化解码配置
    val decodeConfig = DecodeConfig()
      .setHints(DecodeFormatManager.ALL_HINTS) ////设置解码
      .setSupportVerticalCode(true) //设置是否支持扫垂直的条码 （增强识别率，相应的也会增加性能消耗）
      .setSupportLuminanceInvert(true) //设置是否支持识别反色码，黑白颜色反转（增强识别率，相应的也会增加性能消耗）
      .setAreaRectRatio(0.8f)//设置识别区域比例，默认0.8，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别
      //.setAreaRectVerticalOffset(0)//设置识别区域垂直方向偏移量，默认为0，为0表示居中，可以为负数
      //.setAreaRectHorizontalOffset(0)//设置识别区域水平方向偏移量，默认为0，为0表示居中，可以为负数
      .setFullAreaScan(false)//设置是否全区域识别，默认false
    //获取CameraScan，里面有扫码相关的配置设置。CameraScan里面包含部分支持链式调用的方法，即调用返回是CameraScan本身的一些配置建议在startCamera之前调用。
    mCameraScan?.also { cs ->
      cs.setPlayBeep(false) //设置是否播放音效，默认为false
        .setVibrate(false) //设置是否震动，默认为false
        //.setCameraConfig(new CameraConfig())//设置相机配置信息，CameraConfig可覆写options方法自定义配置
        //.setCameraConfig(new ResolutionCameraConfig(this))//设置CameraConfig，可以根据自己的需求去自定义配置
        .setNeedAutoZoom(false) //二维码太小时可自动缩放，默认为false
        .setNeedTouchZoom(true) //支持多指触摸捏合缩放，默认为true
        .setDarkLightLux(45f) //设置光线足够暗的阈值（单位：lux），需要通过{@link #bindFlashlightView(View)}绑定手电筒才有效
        .setBrightLightLux(100f) //设置光线足够明亮的阈值（单位：lux），需要通过{@link #bindFlashlightView(View)}绑定手电筒才有效
        .bindFlashlightView(iv) //绑定手电筒，绑定后可根据光线传感器，动态显示或隐藏手电筒按钮
        .setOnScanResultCallback(object : CameraScan.OnScanResultCallback {
          override fun onScanResultCallback(result: Result?): Boolean {
            if (result == null || result.text.isNullOrBlank()) return false//没有识别出结果就继续扫码
            mSoundPoolUtil?.play()
            mScanListener?.onScanResultCallback(result)
            return true//返回true表示拦截，将不自动执行后续逻辑，为false表示不拦截，默认不拦截
          }

          override fun onScanResultFailure() {
            super.onScanResultFailure()
            if (!PermissionUtils.checkPermission(context, Manifest.permission.CAMERA)) mScanListener?.onScanResultFailure()
          }
        })
        .setAnalyzer(MultiFormatAnalyzer(decodeConfig)) //设置分析器,DecodeConfig可以配置一些解码时的配置信息，如果内置的不满足您的需求，你也可以自定义实现，
        .setAnalyzeImage(true) //设置是否分析图片，默认为true。如果设置为false，相当于关闭了扫码识别功能
    }
  }

  private fun toggleTorchState(v: View) {
    mCameraScan?.let { cs ->
      val isTorch = cs.isTorchEnabled
      cs.enableTorch(!isTorch)
      v.isSelected = !isTorch
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用">
  private var isStartCamera = false
  fun startCamera() {
    if (!isStartCamera && PermissionUtils.checkPermission(context, Manifest.permission.CAMERA)) {
      isStartCamera = true
      mCameraScan?.startCamera()
    }
  }

  fun releaseCamera() {
    mCameraScan?.release()
    mSoundPoolUtil?.release()
    mCameraScan = null
    mSoundPoolUtil = null
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内置的播放无效，所以自己写一个">
  inner class SoundPoolUtil {
    private var mSoundPool: SoundPool? = SoundPool.Builder()
      .setMaxStreams(1)//设置最大同时播放流的数量
      .setAudioAttributes(
        AudioAttributes.Builder()
          .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
          .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
          .build()
      )//设置音频属性
      .build()

    private var mSoundId = -1
    internal fun loadDefault(c: Context, resId: Int) {
      mSoundId = mSoundPool?.load(c, resId, 1) ?: -1
    }

    internal fun play() {
      mSoundPool?.play(mSoundId, 1f, 1f, 1, 0, 1f)
    }

    internal fun release() {
      mSoundPool?.release()
      mSoundPool = null
    }
  }
  //</editor-fold>
}