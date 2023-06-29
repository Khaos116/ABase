//package cc.abase.demo.widget
//
//import android.content.Context
//import android.util.AttributeSet
//import androidx.lifecycle.*
//import cc.ab.base.ext.getMyLifecycleOwner
//import me.devilsen.czxing.code.CodeResult
//import me.devilsen.czxing.util.SoundPoolUtil
//import me.devilsen.czxing.view.scanview.*
//
//
///**
// * CZXing 1.2.0版本需要修改的内容，但是1.2.0暂时不适合使用，先备注注释掉
// * @Description
// * @Author：Khaos
// * @Date：2021/1/26
// * @Time：17:15
// */
//class MyZxingView : ScanLayout, LifecycleObserver {
//  //<editor-fold defaultstate="collapsed" desc="多构造">
//  constructor(c: Context) : super(c, null, 0)
//  constructor(c: Context, a: AttributeSet) : super(c, a, 0)
//  constructor(c: Context, a: AttributeSet?, d: Int) : super(c, a, d)
//  //</editor-fold>
//
//  //<editor-fold defaultstate="collapsed" desc="变量">
//  //Lifecycle生命周期
//  private var mLifecycle: Lifecycle? = null
//
//  //扫码成功的声音
//  private var mSoundPoolUtil: SoundPoolUtil = SoundPoolUtil()
//
//  //扫码回调
//  var mScanListener: ScanListener? = null
//  //</editor-fold>
//
//  //<editor-fold defaultstate="collapsed" desc="初始化">
//  init {
//    mSoundPoolUtil.loadDefault(context)
//    //this.onFlashLightClick()
//    //this.setScanMode(SCAN_MODE_TINY)
//    //this.setBarcodeFormat(BarcodeFormat.QR_CODE, BarcodeFormat.CODABAR, BarcodeFormat.CODE_128, BarcodeFormat.EAN_13, BarcodeFormat.UPC_A)
//    this.setOnScanListener(object : ScanListener {
//
//      override fun onScanSuccess(resultList: MutableList<CodeResult>) {
//        mSoundPoolUtil.play()
//        mScanListener?.onScanSuccess(resultList)
//      }
//
//      override fun onClickResult(result: CodeResult?) {
//        mScanListener?.onClickResult(result)
//      }
//
//      override fun onOpenCameraError() {
//        mScanListener?.onOpenCameraError()
//      }
//    })
//  }
//  //</editor-fold>
//
//  //<editor-fold defaultstate="collapsed" desc="Lifecycle生命周期">
//  //通过Lifecycle内部自动管理暂停和播放(如果不需要后台播放)
//  private fun setLifecycleOwner(owner: LifecycleOwner?) {
//    if (owner == null) {
//      mLifecycle?.removeObserver(this)
//      mLifecycle = null
//    } else {
//      mLifecycle?.removeObserver(this)
//      mLifecycle = owner.lifecycle
//      mLifecycle?.addObserver(this)
//    }
//  }
//
//  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//  private fun onResumeScan() {
//    this.openCamera()
//    this.startDetect()
//  }
//
//  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//  private fun onPauseScan() {
//    this.stopDetect()
//    this.closeCamera()
//  }
//
//  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//  private fun onDestroyScan() {
//    this.onDestroy()
//    mSoundPoolUtil?.release()
//  }
//  //</editor-fold>
//
//  //<editor-fold defaultstate="collapsed" desc="自感应生命周期">
//  override fun onAttachedToWindow() {
//    super.onAttachedToWindow()
//    setLifecycleOwner(getMyLifecycleOwner())
//  }
//
//  override fun onDetachedFromWindow() {
//    super.onDetachedFromWindow()
//    setLifecycleOwner(null)
//    onPauseScan()
//  }
//  //</editor-fold>
//}