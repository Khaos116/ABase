package cc.abase.demo.widget

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.*
import cc.ab.base.ext.getMyLifecycleOwner
import me.devilsen.czxing.code.BarcodeFormat
import me.devilsen.czxing.util.SoundPoolUtil
import me.devilsen.czxing.view.ScanListener
import me.devilsen.czxing.view.ScanView

/**
 * @Description
 * @Author：Khaos
 * @Date：2021/1/26
 * @Time：17:15
 */
class MyZxingView : ScanView, LifecycleObserver {
  //<editor-fold defaultstate="collapsed" desc="多构造">
  constructor(c: Context) : super(c, null, 0)
  constructor(c: Context, a: AttributeSet) : super(c, a, 0)
  constructor(c: Context, a: AttributeSet?, d: Int) : super(c, a, d)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //Lifecycle生命周期
  private var mLifecycle: Lifecycle? = null

  //扫码成功的声音
  private var mSoundPoolUtil: SoundPoolUtil? = SoundPoolUtil()

  //扫码回调
  var mScanListener: ScanListener? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    mSoundPoolUtil?.loadDefault(context)
    this.onFlashLightClick()
    this.setScanMode(SCAN_MODE_TINY)
    this.setBarcodeFormat(BarcodeFormat.QR_CODE, BarcodeFormat.CODABAR, BarcodeFormat.CODE_128, BarcodeFormat.EAN_13, BarcodeFormat.UPC_A)
    this.setScanListener(object : ScanListener {
      override fun onScanSuccess(result: String?, format: BarcodeFormat?) {
        mSoundPoolUtil?.play()
        mScanListener?.onScanSuccess(result, format)
      }

      override fun onOpenCameraError() {
        mScanListener?.onOpenCameraError()
      }
    })
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Lifecycle生命周期">
  //通过Lifecycle内部自动管理暂停和播放(如果不需要后台播放)
  private fun setLifecycleOwner(owner: LifecycleOwner?) {
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
  private fun onResumeScan() {
    this.openCamera()
    this.startScan()
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  private fun onPauseScan() {
    this.stopScan()
    this.closeCamera()
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  private fun onDestroyScan() {
    this.onDestroy()
    mSoundPoolUtil?.release()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="自感应生命周期">
  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    setLifecycleOwner(getMyLifecycleOwner())
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    setLifecycleOwner(null)
    onPauseScan()
  }
  //</editor-fold>
}