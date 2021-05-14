package cn.leizy.shell

import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.Charset
import kotlin.RuntimeException

/**
 * @author Created by wulei
 * @date 2021/2/22, 022
 * @description
 */
object Dx {
  fun jar2Dex(aarFile: File): File {
    val fakeDex = File(aarFile.parent + File.separator + "temp")
    println("jar2Dex:" + aarFile.parent)
    Zip.unzip(aarFile, fakeDex)
    val files = fakeDex.listFiles { _, s -> s.equals("classes.jar") }
    if (files == null || files.isEmpty()) {
      throw RuntimeException("the aar is error.")
    }
    val classes_jar = files[0]
    val aarDex = File(classes_jar.parent, "classes.dex")
    dxCommand(aarDex, classes_jar)
    return aarDex
  }

  private fun dxCommand(aarDex: File, classesJar: File) {
    val runtime = Runtime.getRuntime()
    val exe = "I:\\01StudioAndEclipseSdk\\build-tools\\30.0.3\\cmd.exe"
    val dx = "I:\\01StudioAndEclipseSdk\\build-tools\\30.0.3\\dx.bat"
    val process =
      runtime.exec(exe + " /C " + dx + " --dex --output=" + aarDex.absolutePath + " " + classesJar.absolutePath)
    try {
      process.waitFor()
    } catch (e: InterruptedException) {
      e.printStackTrace()
      throw e
    }
    if (process.exitValue() != 0) {
      val inputStream = process.errorStream
      var len: Int
      val buffer = ByteArray(2048)
      val bos = ByteArrayOutputStream()
      while (inputStream.read(buffer).also { len = it } != -1) {
        bos.write(buffer, 0, len)
      }
      println(String(bos.toByteArray(), Charset.forName("GBK")))
      throw RuntimeException("dx run failed")
    }
    process.destroy()
  }
}