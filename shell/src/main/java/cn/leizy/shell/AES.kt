package cn.leizy.shell

import java.io.File
import java.io.FileOutputStream
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec

/**
 * @author Created by wulei
 * @date 2021/2/22, 022
 * @description
 */
object AES {
    const val DEFAULT_PWD: String = "abcdefghijklmnop"
    private const val algorithmStr = "AES/ECB/PKCS5Padding"
    private lateinit var encryptCipher: Cipher
    private lateinit var decryptCipher: Cipher

    fun init(password: String) {
        try {
            encryptCipher = Cipher.getInstance(algorithmStr)
            decryptCipher = Cipher.getInstance(algorithmStr)
            val keyStr = password.toByteArray()
            val key = SecretKeySpec(keyStr, "AES")
            encryptCipher.init(Cipher.ENCRYPT_MODE, key)
            decryptCipher.init(Cipher.DECRYPT_MODE, key)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        }
    }

    fun encryptAPKFile(srcApkFile: File?, dstApkFile: File): File? {
        if (srcApkFile == null) {
            println("encryptAPKFile : srcApkFile is null")
            return null
        }

        Zip.unzip(srcApkFile, dstApkFile)
        val dexFiles = dstApkFile.listFiles { _, s -> s!!.endsWith(".dex") }
        var mainDexFile: File? = null
        var mainDexData: ByteArray
        dexFiles?.forEach {
            val buffer = Utils.getBytes(it)
            val encryptBytes = encrypt(buffer)
            if (it.name.endsWith("classes.dex")) {
                mainDexData = encryptBytes!!
                mainDexFile = it
            }
            val fos = FileOutputStream(it)
            fos.write(encryptBytes!!)
            fos.flush()
            fos.close()
        }
        return mainDexFile
    }

    private fun encrypt(content: ByteArray): ByteArray? {
        return encryptCipher.doFinal(content)
    }
}