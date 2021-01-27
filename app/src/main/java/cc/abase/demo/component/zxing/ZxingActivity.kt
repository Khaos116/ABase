package cc.abase.demo.component.zxing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cc.ab.base.ext.*
import cc.ab.base.widget.engine.ImageEngine
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.widget.MyZxingView
import com.blankj.utilcode.util.ClickUtils
import com.hjq.permissions.*
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.activity_zxing.*
import kotlinx.coroutines.*
import me.devilsen.czxing.code.*
import me.devilsen.czxing.util.BitmapUtil
import me.devilsen.czxing.view.ScanListener

/**
 * @Description
 * @Author：CASE
 * @Date：2021/1/26
 * @Time：16:01
 */
class ZxingActivity : CommTitleActivity() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      context.startActivity(Intent(context, ZxingActivity::class.java))
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //扫码控件
  private var mScanView: MyZxingView? = null

  //子线程解析二维码
  private var mJob: Job? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResContentId() = R.layout.activity_zxing
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initContentView() {
    setTitleText(R.string.title_zxing.xmlToString())
    ClickUtils.applyPressedViewAlpha(zxingCreate, zxingParse, zxingScan)
    zxingCreate.click {
      val code = zxingEdit.text.toString().trim()
      val size = zxingContainer.width
      if (code.isBlank()) {
        R.string.empty_zxing.xmlToast()
      } else {
        BarcodeWriter()
            .write(code, size / 2, R.color.cyan_40E0D0.xmlToColor(), BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            ?.let { bit ->
              val iv = ImageView(mContext)
              iv.setImageBitmap(bit)
              zxingContainer.removeAllViews()
              zxingContainer.addView(iv, ViewGroup.LayoutParams(size, size))
            }
      }
    }
    zxingParse.click { go2SelImg() }
    zxingScan.click { checkCameraPermission() }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun initData() {
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="摄像头权限">
  private fun checkCameraPermission() {
    XXPermissions.with(mActivity)
        .permission(Permission.CAMERA)
        .request(object : OnPermissionCallback {
          override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
            if (all) openScan() else R.string.zxing_camera_permission.xmlToast()
          }

          override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
            super.onDenied(permissions, never)
            if (never) {
              R.string.zxing_camera_permission.xmlToast()
              XXPermissions.startPermissionActivity(mContext, permissions)
            }
          }
        })
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="打开扫码">
  private fun openScan() {
    zxingContainer.removeAllViews()
    if (mScanView == null) {
      mScanView = MyZxingView(mContext).also { zx ->
        zx.mScanListener = object : ScanListener {
          override fun onScanSuccess(result: String?, format: BarcodeFormat?) = showResult(result ?: "扫码失败")
          override fun onOpenCameraError() = showResult("扫码失败")
        }
      }
    }
    mScanView?.let {
      val size = zxingContainer.width
      zxingContainer.removeAllViews()
      zxingContainer.addView(it, ViewGroup.LayoutParams(size, size))
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="图库选择二维码图片">
  private fun go2SelImg() {
    zxingContainer.removeAllViews()
    //https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-Api%E8%AF%B4%E6%98%8E
    PictureSelector.create(this)
        .openGallery(PictureMimeType.ofImage())
        .imageEngine(ImageEngine())
        .isGif(false)
        .isCamera(false)
        .isPageStrategy(true, PictureConfig.MAX_PAGE_SIZE, true) //过滤掉已损坏的
        .maxSelectNum(1)
        .queryMaxFileSize(5f)
        .isPreviewVideo(true)
        .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        .forResult(PictureConfig.CHOOSE_REQUEST)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="图片选择回调">
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    data?.let {
      if (requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == Activity.RESULT_OK) {
        // 图片、视频、音频选择结果回调
        PictureSelector.obtainMultipleResult(data)
            ?.let { medias ->
              if (medias.isNotEmpty()) {
                mJob?.cancel()
                mJob = GlobalScope.launchError {
                  withContext(Dispatchers.IO) {
                    BitmapUtil.getDecodeAbleBitmap(medias.first().realPath)?.let { bit ->
                      // 这个方法因为要做bitmap的变换，所以比较耗时，推荐放到子线程执行
                      BarcodeReader.getInstance().read(bit)
                    }
                  }.let { result ->
                    if (isActive) {
                      showResult(result?.text ?: "解析失败")
                    }
                  }
                }
              }
            }
      } else {
        "onActivityResult:other".logE()
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="展示结果">
  private fun showResult(msg: String) {
    GlobalScope.launchError {
      val tv = TextView(mContext)
      tv.setTextColor(R.color.magenta.xmlToColor())
      tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
      tv.text = msg
      zxingContainer.removeAllViews()
      zxingContainer.addView(tv, ViewGroup.LayoutParams(-1, -2))
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="生命周期">
  override fun onDestroy() {
    super.onDestroy()
    mScanView?.onDestroy()
    mJob?.cancel()
  }
  //</editor-fold>
}