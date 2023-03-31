package cc.ab.base.utils.ping

import cc.ab.base.ext.launchError
import cc.ab.base.ext.toFloatMy
import kotlinx.coroutines.*
import java.io.IOException
import java.net.*

/**
 * 通过Socket连接测试实现ping效果，以解决开启VPN抓包导致无法ping通的bug
 * Author:Khaos
 * Date:2021-07-08
 * Time:16:08
 */
class PingBySocket {
  private val mPort = 80 //Socket端口号
  private var mConnTimes = 5 //尝试连接的次数
  private var mTimeOut = 1000 // 设置每次连接的timeout时间
  private val mRttTimes = LongArray(mConnTimes) // 用于存储测试中每次的RTT值
  private var mHost: String? = "" //需要ping的host
  private var mJob: Job? = null

  fun onAddress(host: String): PingBySocket {
    mHost = host
    return this
  }

  fun setTimeOutMillis(timeOut: Int): PingBySocket {
    mTimeOut = timeOut
    return this
  }

  fun setTimes(times: Int): PingBySocket {
    mConnTimes = times
    return this
  }

  fun release() {
    mJob?.cancel()
  }

  //返回某个Host进行5次connect的最终结果
  fun doPing(listener: PingListener?) {
    if (mHost.isNullOrBlank()) {
      listener?.onError(Exception("Host is empty"))
      return
    }
    mJob?.cancel()
    launchError(context = Dispatchers.IO) {
      val inetAddress: InetAddress? = try {
        InetAddress.getByName(mHost)
      } catch (mE: Exception) {
        mE.printStackTrace()
        withContext(Dispatchers.Main) { if(isActive)listener?.onError(mE) }
        return@launchError
      }
      val socketAddress: InetSocketAddress
      var failCount = mConnTimes
      var maxTime: Long = 0
      var minTime: Long = 999999
      if (inetAddress != null) {
        socketAddress = InetSocketAddress(inetAddress, mPort)
        var flag = 0
        for (i in 0 until mConnTimes) {
          execSocket(socketAddress, mTimeOut, i)
          if (mRttTimes[i] == -1L) { // 一旦发生timeOut,则尝试加长连接时间
            if (i > 0 && mRttTimes[i - 1] == -1L) { // 连续两次连接超时,停止后续测试
              flag = -1
              break
            }
          } else if (mRttTimes[i] == -2L) {
            if (i > 0 && mRttTimes[i - 1] == -2L) { // 连续两次出现IO异常,停止后续测试
              flag = -2
              break
            }
          } else {
            failCount--
            val mResult = PingResult(inetAddress)
            mResult.isReachable = true
            mResult.timeTaken = mRttTimes[i].toFloatMy()
            withContext(Dispatchers.Main) { if(isActive)listener?.onResult(mResult) }
          }
        }
        var time: Long = 0
        var count = 0
        if (flag == -1) {
          withContext(Dispatchers.Main) { if(isActive)listener?.onError(Exception("Time Out")) }
        } else if (flag == -2) {
          withContext(Dispatchers.Main) { if(isActive)listener?.onError(IOException("IO Exception")) }
        } else {
          for (i in 0 until mConnTimes) {
            if (mRttTimes[i] > 0) {
              val t = mRttTimes[i]
              if (t < minTime) minTime = t
              if (t > maxTime) maxTime = t
              time += t
              count++
            }
          }
          if (count > 0) {
            val mStats = PingStats(inetAddress, count.toLong(), failCount.toLong(), time.toFloatMy(), minTime.toFloatMy(), maxTime.toFloatMy())
            withContext(Dispatchers.Main) { if(isActive)listener?.onFinished(mStats) }
          }
        }
      }
    }
  }

  //针对某个IP第index次connect
  private fun execSocket(socketAddress: InetSocketAddress, timeOut: Int, index: Int) {
    var socket: Socket? = null
    val start: Long
    val end: Long
    try {
      socket = Socket()
      start = System.currentTimeMillis()
      socket.connect(socketAddress, timeOut)
      end = System.currentTimeMillis()
      mRttTimes[index] = end - start
    } catch (e: SocketTimeoutException) {
      mRttTimes[index] = -1 // 作为TIMEOUT标识
      e.printStackTrace()
    } catch (e: Exception) {
      mRttTimes[index] = -2 // 作为IO异常标识
      e.printStackTrace()
    } finally {
      if (socket != null) {
        try {
          socket.close()
        } catch (e: IOException) {
          e.printStackTrace()
        }
      }
    }
  }

  interface PingListener {
    fun onResult(pingResult: PingResult)
    fun onFinished(pingStats: PingStats)
    fun onError(e: Exception)
  }
}