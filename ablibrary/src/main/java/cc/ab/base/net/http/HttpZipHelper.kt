//package cc.ab.base.net.http
//
//import java.io.*
//import java.util.ArrayList
//import java.util.zip.*
//
///**
// * Description:
// * @author: CASE
// * @date: 2019/9/22 18:23
// */
//object HttpZipHelper {
//
//  /**
//   * zlib decompress 2 String
//   */
//  fun decompressToStringForZlib(bytesToDecompress: ByteArray): String? {
//    val bytesDecompressed = decompressForZlib(bytesToDecompress)
//    var returnValue: String? = null
//    bytesDecompressed?.let { bdp ->
//      try {
//        returnValue = String(
//          bdp,
//          0,
//          bdp.size,
//          charset("UTF-8")
//        )
//      } catch (uee: UnsupportedEncodingException) {
//        uee.printStackTrace()
//      }
//    }
//    return returnValue
//  }
//
//  /**
//   * zlib decompress 2 byte
//   */
//  private fun decompressForZlib(bytesToDecompress: ByteArray): ByteArray? {
//    var returnValues: ByteArray? = null
//
//    val inflater = Inflater()
//
//    val numberOfBytesToDecompress = bytesToDecompress.size
//
//    inflater.setInput(
//      bytesToDecompress,
//      0,
//      numberOfBytesToDecompress
//    )
//
//    var numberOfBytesDecompressedSoFar = 0
//    val bytesDecompressedSoFar = ArrayList<Byte>()
//
//    try {
//      while (!inflater.needsInput()) {
//        val bytesDecompressedBuffer = ByteArray(numberOfBytesToDecompress)
//
//        val numberOfBytesDecompressedThisTime = inflater.inflate(
//          bytesDecompressedBuffer
//        )
//
//        numberOfBytesDecompressedSoFar += numberOfBytesDecompressedThisTime
//
//        for (b in 0 until numberOfBytesDecompressedThisTime) {
//          bytesDecompressedSoFar.add(bytesDecompressedBuffer[b])
//        }
//      }
//
//      returnValues = ByteArray(bytesDecompressedSoFar.size)
//      for (b in returnValues.indices) {
//        returnValues[b] = bytesDecompressedSoFar[b]
//      }
//    } catch (dfe: DataFormatException) {
//      dfe.printStackTrace()
//    }
//
//    inflater.end()
//
//    return returnValues
//  }
//
//  /**
//   * zlib compress 2 byte
//   */
//  private fun compressForZlib(bytesToCompress: ByteArray): ByteArray {
//    val defl = Deflater()
//    defl.setInput(bytesToCompress)
//    defl.finish()
//
//    val bytesCompressed = ByteArray(Int.MAX_VALUE)
//
//    val numberOfBytesAfterCompression = defl.deflate(bytesCompressed)
//
//    val returnValues = ByteArray(numberOfBytesAfterCompression)
//
//    System.arraycopy(
//      bytesCompressed,
//      0,
//      returnValues,
//      0,
//      numberOfBytesAfterCompression
//    )
//
//    return returnValues
//  }
//
//  /**
//   * zlib compress 2 byte
//   */
//  private fun compressForZlib(stringToCompress: String): ByteArray? {
//    var returnValues: ByteArray? = null
//
//    try {
//
//      returnValues = compressForZlib(
//        stringToCompress.toByteArray(charset("UTF-8"))
//      )
//    } catch (uee: UnsupportedEncodingException) {
//      uee.printStackTrace()
//    }
//
//    return returnValues
//  }
//
//  /**
//   * gzip compress 2 byte
//   */
//  private fun compressForGzip(string: String): ByteArray? {
//    var os: ByteArrayOutputStream? = null
//    var gos: GZIPOutputStream? = null
//    try {
//      os = ByteArrayOutputStream(string.length)
//      gos = GZIPOutputStream(os)
//      gos.write(string.toByteArray(charset("UTF-8")))
//      return os.toByteArray()
//    } catch (e: IOException) {
//      e.printStackTrace()
//    } finally {
//      gos?.closeQuietly()
//      os?.closeQuietly()
//    }
//    return null
//  }
//
//  /**
//   * gzip decompress 2 string
//   */
//  fun decompressForGzip(compressed: ByteArray): String? {
//
//    val BUFFER_SIZE = compressed.size
//    var gis: GZIPInputStream? = null
//    var input: ByteArrayInputStream? = null
//    try {
//      input = ByteArrayInputStream(compressed)
//      gis = GZIPInputStream(input, BUFFER_SIZE)
//      val string = StringBuilder()
//      val data = ByteArray(BUFFER_SIZE)
//      var bytesRead: Int
//      while (((gis.read(data)).also { bytesRead = it }) != -1) {
//        string.append(String(data, 0, bytesRead, charset("UTF-8")))
//      }
//      return string.toString()
//    } catch (e: IOException) {
//      e.printStackTrace()
//    } finally {
//      gis?.closeQuietly()
//      input?.closeQuietly()
//    }
//    return null
//  }
//}