package cc.abase.demo.component.zxing

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cc.ab.base.ext.*
import cc.ab.base.widget.engine.CoilEngine
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.databinding.ActivityZxingBinding
import cc.abase.demo.widget.MyZxingView
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import me.devilsen.czxing.code.BarcodeFormat
import me.devilsen.czxing.code.BarcodeReader
import me.devilsen.czxing.code.BarcodeWriter
import me.devilsen.czxing.util.BitmapUtil
import me.devilsen.czxing.view.ScanListener

/**
 * @Description
 * @Author：Khaos
 * @Date：2021/1/26
 * @Time：16:01
 */
class ZxingActivity : CommBindTitleActivity<ActivityZxingBinding>() {
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

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initContentView() {
    setTitleText(R.string.Zxing扫码.xmlToString())
    ClickUtils.applyPressedViewAlpha(viewBinding.zxingCreate, viewBinding.zxingParse, viewBinding.zxingScan)
    viewBinding.zxingCreate.click {
      val code = viewBinding.zxingEdit.text.toString().trim()
      val size = viewBinding.zxingContainer.width
      if (code.isBlank()) {
        R.string.二维码内容不能为空.xmlToast()
      } else {
        KeyboardUtils.hideSoftInput(viewBinding.zxingEdit)
        BarcodeWriter()
          .write(code, size / 2, R.color.cyan_40E0D0.xmlToColor(), BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
          ?.let { bit ->
            val iv = ImageView(mContext)
            iv.setImageBitmap(bit)
            viewBinding.zxingContainer.removeAllViews()
            viewBinding.zxingContainer.addView(iv, ViewGroup.LayoutParams(size, size))
          }
      }
    }
    viewBinding.zxingParse.click { go2SelImg() }
    viewBinding.zxingScan.click { checkCameraPermission() }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="摄像头权限">
  private fun checkCameraPermission() {
    XXPermissions.with(mActivity)
      .permission(Permission.CAMERA)
      .request(object : OnPermissionCallback {
        override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
          if (all) openScan() else R.string.扫码需要摄像头权限.xmlToast()
        }

        override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
          super.onDenied(permissions, never)
          if (never) {
            R.string.扫码需要摄像头权限.xmlToast()
            XXPermissions.startPermissionActivity(mContext, permissions)
          }
        }
      })
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="打开扫码">
  private fun openScan() {
    viewBinding.zxingContainer.removeAllViews()
    if (mScanView == null) {
      mScanView = MyZxingView(mContext).also { zx ->
        zx.mScanListener = object : ScanListener {
          override fun onScanSuccess(result: String?, format: BarcodeFormat?) = showResult(result ?: R.string.扫码失败.xmlToString())
          override fun onOpenCameraError() = showResult(R.string.扫码失败.xmlToString())
        }
      }
    }
    mScanView?.let {
      val size = viewBinding.zxingContainer.width
      viewBinding.zxingContainer.removeAllViews()
      viewBinding.zxingContainer.addView(it, ViewGroup.LayoutParams(size, size))
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="图库选择二维码图片">
  @SuppressLint("SourceLockedOrientationActivity")
  private fun go2SelImg() {
    viewBinding.zxingContainer.removeAllViews()
    //https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E5%8A%9F%E8%83%BDapi%E8%AF%B4%E6%98%8E
    PictureSelector.create(this)
      .openGallery(SelectMimeType.ofImage())
      .setImageEngine(CoilEngine())
      .isGif(false)
      .isDisplayCamera(true)
      .isPageStrategy(true, PictureConfig.MAX_PAGE_SIZE, true) //过滤掉已损坏的
      .setMaxSelectNum(1)
      .setFilterMaxFileSize(5L * MemoryConstants.MB)
      .isPreviewImage(true)
      .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
      .forResult(object : OnResultCallbackListener<LocalMedia> {
        override fun onResult(result: ArrayList<LocalMedia>?) {
          if (!result.isNullOrEmpty()) {
            mJob?.cancel()
            mJob = launchError {
              withContext(Dispatchers.IO) {
                BitmapUtil.getDecodeAbleBitmap(result.first().realPath)?.let { bit ->
                  // 这个方法因为要做bitmap的变换，所以比较耗时，推荐放到子线程执行
                  BarcodeReader.getInstance().read(bit)
                }
              }.let { result ->
                if (isActive) {
                  showResult(result?.text ?: R.string.解析失败.xmlToString())
                }
              }
            }
          }
        }

        override fun onCancel() {
        }
      })
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="展示结果">
  private fun showResult(msg: String) {
    launchError {
      val tv = TextView(mContext)
      tv.setTextColor(R.color.magenta.xmlToColor())
      tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
      tv.text = msg
      viewBinding.zxingContainer.removeAllViews()
      viewBinding.zxingContainer.addView(tv, ViewGroup.LayoutParams(-1, -2))
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