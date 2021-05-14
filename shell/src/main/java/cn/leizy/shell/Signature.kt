package cn.leizy.shell

import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.Charset

/**
 * @author Created by wulei
 * @date 2021/2/22, 022
 * @description
 */
object Signature {
    fun signature(unsignedApk: File, signedApk: File, keyPath: String) {
        val cmd = arrayOf(
            "cmd.exe", "/C ", "jarsigner", "-sigalg", "MD5withRSA",
            "-digestalg", "SHA1",
            "-keystore", keyPath,
            "-storepass", Main.签名别名密码,//密码
            "-keypass", Main.签名密码,//密码
            "-signedjar", signedApk.absolutePath,
            unsignedApk.absolutePath,
            Main.签名别名//别名
        )
        cmd.forEach {
            print(it+" ")
        }
        println()
        val process = Runtime.getRuntime().exec(cmd)
        println("begin sign $process")
        try {
            val waitFor = process.waitFor()
            println("waitResult $waitFor")
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        if (process.exitValue() != 0) {
            val errorStream = process.errorStream
            var len: Int
            val buffer = ByteArray(2048)
            val bos = ByteArrayOutputStream()
            while (errorStream.read(buffer).also { len = it } != -1) {
                bos.write(buffer, 0, len)
            }
            println(String(bos.toByteArray(), Charset.forName("GBK")))
        }
        println("finish sign")
        process.destroy()
    }
}