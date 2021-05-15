package cn.leizy.shell

import java.io.File
import java.io.FileOutputStream

class Main {
  companion object {
    //需要修改的地方
    val 签名文件地址 = File("source/jsk").path + File.separator + "com_ab.jks"
    const val 签名密码 = "com_cc"
    const val 签名别名 = "com_cc"
    const val 签名别名密码 = "com_cc"
    const val 需要加固的APK名称 = "APP.apk"

    @JvmStatic
    fun main(args: Array<String>) {
      println("加固开始")
      //初始化相应目录，或删除之前的文件。
      val tempFileApk = File("source/apk/temp")
      if (tempFileApk.exists()) {
        delDir(tempFileApk)
      } else {
        tempFileApk.mkdirs()
      }
      val tempFileAar = File("source/aar/temp")
      if (tempFileAar.exists()) {
        delDir(tempFileAar)
      } else {
        tempFileAar.mkdirs()
      }

      /**
       * 1.处理原始apk 加密dex
       */
      AES.init(AES.DEFAULT_PWD)
      //解压apk
      val apkFile = File("source/apk/$需要加固的APK名称")
      val newApkFile = File(apkFile.parent + File.separator + "temp")
      if (!newApkFile.exists()) newApkFile.mkdirs()
      val mainDexFile = AES.encryptAPKFile(apkFile, newApkFile)
      if (newApkFile.isDirectory) {
        newApkFile.listFiles()?.forEach {
          if (it.isFile) {
            if (it.name.endsWith(".dex")) {
              val name = it.name
              println("重命名前:$name")
              val cursor = name.indexOf(".dex")
              val newName = it.parent + File.separator + name.substring(0, cursor) + "_.dex"
              println("重命名后:$newName")
              it.renameTo(File(newName))
            }
          }
        }
      }

      /**
       * 2.处理aar 获得壳dex
       */
      val aarFile = File("source/aar/shelllib-debug.aar")
      val aarDex = Dx.jar2Dex(aarFile)
      val tempMainDex = File(newApkFile.path + File.separator + "classes.dex")
      if (!tempMainDex.exists()) {
        tempMainDex.createNewFile()
      }
      val fos = FileOutputStream(tempMainDex)
      val fbytes = Utils.getBytes(aarDex)
      fos.write(fbytes)
      fos.flush()
      fos.close()

      /**
       * 3.打包签名
       */
      val unsignedApk = File("APK/JiaGu/apk-unsigned.apk")
      unsignedApk.parentFile.mkdirs()
      Zip.zip(newApkFile, unsignedApk)
      val signedApk = File("APK/JiaGu/apk-signed.apk")
      Signature.signature(unsignedApk, signedApk, 签名文件地址)
      delDir(tempFileApk)
      delDir(tempFileAar)
      unsignedApk.delete()
    }

    private fun delDir(f: File) {
      f.listFiles()?.forEach {
        it?.run {
          if (isFile) {
            delete()
          }
        }
      }
    }
  }
}