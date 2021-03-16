package cc.abase.demo.widget.biometric

import android.app.Application
import android.app.KeyguardManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import androidx.fragment.app.FragmentActivity
import cc.ab.base.ext.logE
import cc.ab.base.ext.xmlToString
import cc.abase.demo.R
import cc.abase.demo.widget.dialog.*
import javax.crypto.Cipher

/**
 * [Build.VERSION_CODES.M] 及以上版本实现
 *
 * - 创建时间：2021/1/11
 *
 * @author 王杰
 */
@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.M)
class BiometricM(val app: Application) : BiometricInterface {

  /** [Build.VERSION_CODES.M] 以上指纹管理对象 */
  private val fingerprintManager: FingerprintManagerCompat by lazy {
    FingerprintManagerCompat.from(app)
  }

  override var encrypt = true
  override var keyAlias = "DEFAULT_KEY_NAME"
  override var ivBytes: ByteArray? = null
  override var title = "验证指纹"
  override var subTitle = ""
  override var hint = "请按压指纹感应区验证指纹"
  override var negative = "取消"

  private var mDialog: CommAlertDialog? = null

  override fun checkBiometric(): Int {
    //键盘锁管理者
    val km = app.getSystemService(KeyguardManager::class.java)
    return when {
      !fingerprintManager.isHardwareDetected -> {
        // 不支持指纹
        BiometricInterface.ERROR_HW_UNAVAILABLE
      }
      !km.isKeyguardSecure -> {
        // 未设置锁屏
        BiometricInterface.ERROR_NO_DEVICE_CREDENTIAL
      }
      !fingerprintManager.hasEnrolledFingerprints() -> {
        // 未注册有效指纹
        BiometricInterface.ERROR_NO_BIOMETRICS
      }
      else -> {
        // 支持指纹识别
        BiometricInterface.HW_AVAILABLE
      }
    }
  }

  private var mSuccessCall: ((Cipher) -> Unit)? = null
  private var mErrorCall: ((Int, String) -> Unit)? = null

  override fun release() {
    mSuccessCall = null
    mErrorCall = null
  }

  override fun authenticate(activity: FragmentActivity, onSuccess: ((Cipher) -> Unit)?, onError: ((Int, String) -> Unit)?) {
    mSuccessCall = onSuccess
    mErrorCall = onError
    val cancellationSignal = CancellationSignal()
    cancellationSignal.setOnCancelListener {
      mErrorCall?.invoke(5, "用户取消")
    }
    mDialog?.dismiss()
    mDialog = null
    commAlertDialog(activity.supportFragmentManager) {
      type = AlertDialogType.SINGLE_BUTTON
      mDialog = this
      content = hint
      confirmText = R.string.cancel.xmlToString()
    }
    val loadCipher = try {
      loadCipher(encrypt, keyAlias, ivBytes)
    } catch (throwable: Throwable) {
      null
    }
    if (null == loadCipher) {
      mErrorCall?.invoke(BiometricInterface.ERROR_FAILED, "指纹验证失败")
      return
    }
    fingerprintManager.authenticate(FingerprintManagerCompat.CryptoObject(loadCipher), 0, cancellationSignal,
        object : FingerprintManagerCompat.AuthenticationCallback() {
          override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
            try {
              val cipher = result?.cryptoObject?.cipher ?: throw RuntimeException("cipher is null!")
              mSuccessCall?.invoke(cipher)
            } catch (throwable: Throwable) {
              throwable.logE()
              mErrorCall?.invoke(BiometricInterface.ERROR_FAILED, "指纹验证失败")
            } finally {
              mDialog?.dismiss()
            }
          }

          override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
            mDialog?.viewBinding?.commAlertContent?.text = (helpString.toString())
            mErrorCall?.invoke(helpCode, helpString.toString())
          }

          override fun onAuthenticationFailed() {
            mDialog?.dismiss()
            mErrorCall?.invoke(BiometricInterface.ERROR_FAILED, "指纹验证失败")
          }

          override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            mDialog?.dismiss()
            mErrorCall?.invoke(errorCode, errString.toString())
          }
        },
        null)
  }
}