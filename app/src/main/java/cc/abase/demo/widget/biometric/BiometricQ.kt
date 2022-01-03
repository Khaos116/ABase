package cc.abase.demo.widget.biometric

import android.app.Application
import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import cc.ab.base.ext.logE
import cc.ab.base.ext.xmlToString
import cc.abase.demo.R
import javax.crypto.Cipher

/**
 * [Build.VERSION_CODES.Q] 及以上版本实现
 *
 * - 创建时间：2021/1/11
 *
 * @author 王杰
 */
@RequiresApi(Build.VERSION_CODES.Q)
class BiometricQ(val app: Application) : BiometricInterface by BiometricM(app) {
    override var encrypt = true
    override var keyAlias = "DEFAULT_KEY_NAME"
    override var ivBytes: ByteArray? = null
    override var title = R.string.验证指纹.xmlToString()
    override var subTitle = ""
    override var hint = R.string.请按压指纹感应区验证指纹.xmlToString()
    override var negative = R.string.取消.xmlToString()

    private var mSuccessCall: ((Cipher) -> Unit)? = null
    private var mErrorCall: ((Int, String) -> Unit)? = null
    private var mListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, _ ->
        dialog?.dismiss()
        mCancellationSignal?.cancel()
    }
    private var mCancellationSignal: CancellationSignal? = null
    override fun release() {
        mSuccessCall = null
        mErrorCall = null
        mCancellationSignal = null
    }

    override fun authenticate(activity: FragmentActivity, onSuccess: ((Cipher) -> Unit)?, onError: ((Int, String) -> Unit)?) {
        mSuccessCall = onSuccess
        mErrorCall = onError
        val cancellationSignal = android.os.CancellationSignal()
        mCancellationSignal = cancellationSignal
        mCancellationSignal?.setOnCancelListener { mErrorCall?.invoke(5, "用户取消") }
        val prompt = with(BiometricPrompt.Builder(activity.applicationContext)) {
            if (title.isNotBlank()) {
                setTitle(title)
            }
            if (subTitle.isNotBlank()) {
                setSubtitle(subTitle)
            }
            if (hint.isNotBlank()) {
                setDescription(hint)
            }
            setNegativeButton(negative, activity.mainExecutor, mListener)
            build()
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
        prompt.authenticate(BiometricPrompt.CryptoObject(loadCipher), cancellationSignal, activity.mainExecutor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    try {
                        val cipher = result?.cryptoObject?.cipher ?: throw RuntimeException("cipher is null!")
                        mSuccessCall?.invoke(cipher)
                    } catch (throwable: Throwable) {
                        throwable.logE()
                        mErrorCall?.invoke(BiometricInterface.ERROR_FAILED, "指纹验证失败")
                    }
                }

                override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                    mErrorCall?.invoke(helpCode, helpString.toString())
                }

                override fun onAuthenticationFailed() {
                    mErrorCall?.invoke(BiometricInterface.ERROR_FAILED, "指纹验证失败")
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    mErrorCall?.invoke(errorCode, errString.toString())
                }
            })
    }
}