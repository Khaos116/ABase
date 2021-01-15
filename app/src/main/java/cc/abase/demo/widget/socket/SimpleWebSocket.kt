package cc.abase.demo.widget.socket

import cc.ab.base.ext.*
import com.blankj.utilcode.util.NetworkUtils
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import okhttp3.*
import rxhttp.wrapper.ssl.HttpsUtils
import java.util.concurrent.TimeUnit

/**
 * @Description https://github.com/fomin-zhu/websocket/blob/master/app/src/main/java/com/fomin/websocket/WebSocketManager.java
 * @Author：CASE
 * @Date：2021/1/13
 * @Time：17:16
 */
class SimpleWebSocket {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //OkHttpClient对象
  private val mOkHttpClient: OkHttpClient

  //WebSocket用于发送消息
  private var mWebSocket: WebSocket? = null

  //是否已经连接
  private var isConnected = false

  //是否已经释放
  private var isRelease = false

  //WebSocket地址
  private var mUrlWebSocket: String = ""

  //重连次数
  private var mCountRetry: Int = 0

  //重连倒计时
  private var mJobRetry: Job? = null

  //消息处理
  private var mJobDealMsg: Job? = null

  //轮播倒计时
  private var mJobCountDown: Job? = null

  //重连时间间隔
  var mTimeRetry: Long = 10 * 1000

  //最大重连次数
  var mCountRetryMax: Int = Int.MAX_VALUE
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    val sslParams = HttpsUtils.getSslSocketFactory()
    val builder = OkHttpClient.Builder()
        .pingInterval(10, TimeUnit.SECONDS)
        .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager) //添加信任证书
        .hostnameVerifier { _, _ -> true }
        .retryOnConnectionFailure(true)
    mOkHttpClient = builder.build()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="WebSocket回调">
  private val mWebSocketListener = object : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
      super.onOpen(webSocket, response)
      "WebSocket连接成功:${mUrlWebSocket}".logI()
      mWebSocket = webSocket
      isConnected = response.code == 101
      if (!isConnected) reConnect()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
      super.onMessage(webSocket, text)
      if (isRelease) releaseSocketConnect() else dealReceiveMessage(text)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
      super.onClosing(webSocket, code, reason)
      mWebSocket = null
      isConnected = false
      if (code == 1001) "主动关闭WebSocket".logE()
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
      super.onClosed(webSocket, code, reason)
      mWebSocket = null
      isConnected = false
      if (code == 1001) "主动关闭WebSocket".logE()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
      super.onFailure(webSocket, t, response)
      isConnected = false
      "WebSocket连接失败:${t.message ?: "null"}".logE()
      reConnect()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部调用">
  private fun reConnect() {
    if (isRelease) return
    if (mCountRetry < mCountRetryMax) {
      mWebSocket = null
      mOkHttpClient.dispatcher.cancelAll()
      mJobRetry = GlobalScope.launchError(Dispatchers.IO) {
        delay(mTimeRetry)
        if (NetworkUtils.isConnected()) { //有网络直接重试
          mCountRetry++
          if (isActive) mOkHttpClient.newWebSocket(Request.Builder().url(mUrlWebSocket).build(), mWebSocketListener)
        } else { //没有网络直接下一次重试
          reConnect()
          "没有网络直接下一次重试".logE()
        }
      }
    } else {
      "超过最大重连次数".logE()
    }
  }

  //列表类解析
  private val msgType = object : TypeToken<MutableList<String>>() {}.type

  //接收消息后通过LiveData回调出去
  private fun dealReceiveMessage(msg: String) {
    mJobDealMsg = GlobalScope.launchError(Dispatchers.Main) {
      withContext(Dispatchers.IO) { //异步线程进行数据转换
        "WebSocket消息:$msg".logI()
        val temp = mutableListOf<String>()
        temp.add(msg)
        temp
      }.let { list ->
        if (list.isNotEmpty()) {
          val needStart = msgList.isEmpty() && msgListOld.isEmpty()
          msgList.addAll(list)
          if (needStart) startLoop(0)
        }
      } //发送到主线程
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="30秒轮询">
  private val msgList = mutableListOf<String>() //未读消息
  private val msgListOld = mutableListOf<String>() //已读消息

  //开始每30秒读取一次消息
  @Synchronized private fun startLoop(delay: Long = 30 * 1000) {
    stopLoop()
    mJobCountDown = GlobalScope.launchError(handler = { _, _ -> startLoop() }) {
      withContext(Dispatchers.IO) { delay(delay) }.let {
        if (isActive) {
          when {
            msgList.isNotEmpty() -> { //有新数据读新数据
              "30s读取公告新消息".logI()
              val msg = msgList[0]
              msgList.removeAt(0)
              msgListOld.add(msg)
              //旧消息只保留20条
              if (msgListOld.size > 20) {
                val temp = msgListOld.takeLast(20)
                msgListOld.clear()
                msgListOld.addAll(temp)
              }
              //LiveDataConfig.noticeLiveData.value = msg
            }
            msgListOld.isNotEmpty() -> { //没有新数据读旧数据
              "30s读取公告旧消息".logI()
              val msg = msgListOld[0]
              msgListOld.removeAt(0)
              msgListOld.add(msg)
              //LiveDataConfig.noticeLiveData.value = msg
            }
            else -> "30s没有读取到消息".logI()
          }
          startLoop()
        }
      }
    }
  }

  //停止轮询
  private fun stopLoop() {
    mJobCountDown?.cancel()
    mJobCountDown = null
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用">
  //开始连接
  fun startSocketConnect(url: String) {
    if (url.isBlank() || mUrlWebSocket == url) return
    mUrlWebSocket = url
    releaseSocketConnect()
    isRelease = false
    isConnected = false
    mOkHttpClient.dispatcher.cancelAll()
    mOkHttpClient.newWebSocket(Request.Builder().url(url).build(), mWebSocketListener)
  }

  //释放Socket
  fun releaseSocketConnect() {
    isRelease = true
    mUrlWebSocket = ""
    if (isConnected) {
      mWebSocket?.cancel()
      mWebSocket?.close(1001, "客户端主动关闭连接") //range [1000,5000)
    }
    mWebSocket = null
    mJobRetry?.cancel()
    mJobDealMsg?.cancel()
    mJobRetry = null
    mJobDealMsg = null
    stopLoop()
  }

  //发送消息
  fun sendMessage(msg: String): Boolean {
    if (!isConnected || isRelease) return false
    return mWebSocket?.send(msg) ?: false
  }
  //</editor-fold>
}