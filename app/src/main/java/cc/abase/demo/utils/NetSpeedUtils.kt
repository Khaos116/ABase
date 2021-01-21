package cc.abase.demo.utils

import android.net.TrafficStats
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.lifecycle.*
import com.blankj.utilcode.util.Utils
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

/**
 * static long getMobileRxBytes() //获取通过Mobile连接收到的字节总数，不包含WiFi
 * static long getMobileRxPackets() //获取Mobile连接收到的数据包总数
 * static long getMobileTxBytes() //Mobile发送的总字节数
 * static long getMobileTxPackets() //Mobile发送的总数据包数
 * static long getTotalRxBytes() //获取总的接受字节数，包含Mobile和WiFi等
 * static long getTotalRxPackets() //总的接受数据包数，包含Mobile和WiFi等
 * static long getTotalTxBytes() //总的发送字节数，包含Mobile和WiFi等
 * static long getTotalTxPackets() //发送的总数据包数，包含Mobile和WiFi等
 * static long getUidRxBytes(int uid) //获取某个网络UID的接受字节数，某一个进程的总接收量
 * static long getUidTxBytes(int uid) //获取某个网络UID的发送字节数，某一个进程的总发送量
 *
 * Description:实时显示网速
 * @author: CASE
 * @date: 2020/2/21 17:14
 */
object NetSpeedUtils : LifecycleObserver {
  private var mJob: Job? = null
  private var mLifecycle: WeakReference<Lifecycle>? = null
  private var mTv: WeakReference<TextView>? = null
  private var mReceive: Boolean = true

  //显示网速
  fun showNetSpeed(tv: TextView?, receive: Boolean = true, /*接收速度还是发送速度*/      @NonNull owner: LifecycleOwner) {
    mJob?.cancel()
    mLifecycle?.get()?.removeObserver(this)
    mLifecycle = WeakReference(owner.lifecycle)
    mLifecycle?.get()?.addObserver(this)
    startJob(tv, receive)
  }

  private fun startJob(tv: TextView?, receive: Boolean = true) { //接收速度还是发送速度
    mTv = WeakReference(tv)
    mReceive = receive
    mJob?.cancel()
    mJob = GlobalScope.launch(Dispatchers.Main) {
      getNetSpeed()
      while (isActive) {
        delay(1000)
        val speed = getNetSpeed(receive = receive)
        tv?.text = if (speed > 1024) {
          "${String.format("%.1f", speed / 1024f)} MB/s"
        } else {
          "$speed KB/s"
        }
      }
    }
  }

  //添加的代码:12
  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  fun onResumeNetSpeed() {
    startJob(mTv?.get(), mReceive)
  }

  //添加的代码:13
  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  fun onPauseNetSpeed() {
    mJob?.cancel()
  }

  //添加的代码:14
  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  fun onDestroyNetSpeed() {
    mLifecycle?.get()?.removeObserver(this)
    mLifecycle = null
    mJob = null
    mTv = null
    mReceive = true
  }

  private var lastTotalBytes: Long = 0
  private var lastTimeStamp: Long = 0

  private suspend fun getNetSpeed(uid: Int = Utils.getApp().applicationInfo.uid, receive: Boolean = true): Long {
    val nowTotalBytes = Math.max(0, getTotalRxBytes(uid, receive))
    val nowTime = System.currentTimeMillis()
    val speed = Math.max(0, (nowTotalBytes - lastTotalBytes) * 1000 / (nowTime - lastTimeStamp)) //毫秒转换
    lastTimeStamp = nowTime
    lastTotalBytes = nowTotalBytes
    return speed
  }

  //getApplicationInfo().uid
  private suspend fun getTotalRxBytes(uid: Int, receive: Boolean = true /*是接收还是发送*/): Long {
    return when {
      TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED.toLong() -> 0
      receive -> TrafficStats.getTotalRxBytes() / 1024 //转为KB
      else -> TrafficStats.getTotalTxBytes() / 1024 //转为KB
    }
  }
}