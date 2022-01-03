package cc.abase.demo.widget.biometric

import android.app.Application
import android.os.Build
import androidx.fragment.app.FragmentActivity
import cc.ab.base.ext.xmlToString
import cc.abase.demo.R
import javax.crypto.Cipher

/**
 * 生物识别功能提供者，实现 [BiometricInterface] 接口，实际功能由 [BiometricQ] [BiometricM] [BiometricUn] 代理
 *
 * - 创建时间：2021/1/11
 *
 * @author 王杰
 */
class BiometricProvider(private val app: Application, supportQ: Boolean = true) : BiometricInterface by when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && supportQ -> BiometricQ(app)
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> BiometricM(app)
    else -> BiometricUn()
}

/** 尝试进行生物识别认证，成功回调 [onSuccess] 回传 [Cipher] 对象，失败回调 [onError] 回传错误码 [Int] 错误信息 [String] */
fun BiometricInterface.tryAuthenticate(activity: FragmentActivity, onSuccess: ((Cipher) -> Unit)? = null, onError: ((Int, String) -> Unit)? = null) {
    when (val resultCode = checkBiometric()) {
        BiometricInterface.ERROR_HW_UNAVAILABLE -> {
            // 不支持
            onError?.invoke(resultCode, R.string.当前设备不支持指纹识别.xmlToString())
        }
        BiometricInterface.ERROR_NO_BIOMETRICS -> {
            // 没有有效指纹
            onError?.invoke(resultCode, R.string.请添加至少一个有效指纹.xmlToString())
        }
        BiometricInterface.ERROR_NO_DEVICE_CREDENTIAL -> {
            // 没有设置锁屏
            onError?.invoke(resultCode, R.string.请先设置锁屏.xmlToString())
        }
        else -> {
            // 支持
            authenticate(activity, onSuccess, onError)
        }
    }
}