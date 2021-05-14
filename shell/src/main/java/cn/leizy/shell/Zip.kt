package cn.leizy.shell

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.*


/**
 * @author Created by wulei
 * @date 2021/2/22, 022
 * @description
 */
object Zip {
    fun unzip(zip: File, dir: File) {
        try {
            dir.delete()
            val zipFile = ZipFile(zip)
            val entries = zipFile.entries()
            while (entries.hasMoreElements()) {
                val zipEntry = entries.nextElement()
                val name = zipEntry.name
                if (name.equals("META-INF/CERT.RSA")
                    || name.equals("META-INF/CERT.SF")
                    || name.equals("META-INF/MANIFEST.MF")
                ) continue
                if (!zipEntry.isDirectory) {
                    val file = File(dir, name)
                    if (!file.parentFile.exists()) file.parentFile.mkdirs()
                    val fos = FileOutputStream(file)
                    val inputStream = zipFile.getInputStream(zipEntry)
                    val buffer = ByteArray(1024)
                    var len: Int
                    while (inputStream.read(buffer).also { len = it } != -1) {
                        fos.write(buffer, 0, len)
                    }
                    inputStream.close()
                    fos.close()
                }
            }
            zipFile.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun zip(dir: File, zip: File) {
        zip.delete()
        //CRC32校验
        val cos = CheckedOutputStream(FileOutputStream(zip), CRC32())
        val zos = ZipOutputStream(cos)
        compress(dir, zos, "")
        zos.flush()
        zos.close()
    }

    private fun compress(srcFile: File, zos: ZipOutputStream, basePath: String) {
        if (srcFile.isDirectory) {
            compressDir(srcFile, zos, basePath)
        } else {
            compressFile(srcFile, zos, basePath)
        }
    }

    private fun compressDir(dir: File, zos: ZipOutputStream, basePath: String) {
        val files = dir.listFiles()
        if (files!!.isEmpty()) {
            val entry = ZipEntry(basePath + dir.name + "/")
            zos.putNextEntry(entry)
            zos.closeEntry()
        }
        files.forEach {
            compress(it, zos, basePath + dir.name + "/")
        }
    }

    private fun compressFile(file: File, zos: ZipOutputStream, dir: String) {
        val dirName = dir + file.name
//        println("compressfile $dirName")
        val dirNameNew = dirName.split("/")
        val buffer = StringBuffer()
        if (dirNameNew.size > 1) {
            for (i in 1 until dirNameNew.size) {
                buffer.append("/")
                buffer.append(dirNameNew[i])
            }
            /*dirNameNew.forEach {
                buffer.append("/")
                buffer.append(it)
            }*/
        } else {
            buffer.append("/")
        }

        val entry = ZipEntry(buffer.toString().substring(1))
        zos.putNextEntry(entry)
        val bis = BufferedInputStream(FileInputStream(file))
        var count: Int
        val data = ByteArray(1024)
        while (bis.read(data, 0, 1024).also { count = it } != -1) {
            zos.write(data, 0, count)
        }
        bis.close()
        zos.closeEntry()
    }

}