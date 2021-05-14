package cn.leizy.shell

import java.io.File
import java.io.RandomAccessFile

/**
 * @author Created by wulei
 * @date 2021/2/22, 022
 * @description
 */
object Utils {
    fun getBytes(file: File): ByteArray {
        val fis = RandomAccessFile(file, "r")
        val buffer = ByteArray(fis.length().toInt())
        fis.readFully(buffer)
        fis.close()
        return buffer
    }
}