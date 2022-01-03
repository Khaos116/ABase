package cc.abase.demo.widget.biometric

import androidx.fragment.app.FragmentActivity
import cc.ab.base.ext.xmlToString
import cc.abase.demo.R
import javax.crypto.Cipher

/**
 * 低版本实现，不支持生物识别
 *
 * - 创建时间：2021/1/11
 *
 * @author 王杰
 */
class BiometricUn : BiometricInterface {

    override var encrypt = true
    override var keyAlias = "DEFAULT_KEY_ALIAS"
    override var ivBytes: ByteArray? = null
    override var title = R.string.验证指纹.xmlToString()
    override var subTitle = ""
    override var hint = ""
    override var negative = R.string.取消.xmlToString()

    override fun checkBiometric(): Int {
        return BiometricInterface.ERROR_HW_UNAVAILABLE
    }

    override fun release() {}

    override fun authenticate(activity: FragmentActivity, onSuccess: ((Cipher) -> Unit)?, onError: ((Int, String) -> Unit)?) {}
}