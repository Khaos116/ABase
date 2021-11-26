package cc.abase.demo.component.update

import android.app.*
import android.content.*
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Build
import android.text.*
import android.text.style.ForegroundColorSpan
import android.widget.RemoteViews
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import cc.ab.base.config.PathConfig
import cc.abase.demo.R
import cc.abase.demo.constants.EventKeys
import cc.abase.demo.constants.StringConstants
import cc.abase.demo.rxhttp.config.RxHttpConfig
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.*
import com.jeremyliao.liveeventbus.LiveEventBus
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import rxhttp.RxHttp
import java.io.File
import java.util.Locale

/**
 * Description:
 * 1.https://blog.csdn.net/houson_c/article/details/78461751
 * 2.https://www.cnblogs.com/yongfengnice/p/10945591.html
 * @author: Khaos
 * @date: 2019/10/30 9:45
 */
open class CcUpdateService : JobIntentService() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    private const val JOB_ID = 1000
    private const val DOWNLOAD_PATH = "download_path"
    private const val DOWNLOAD_VERSION = "download_version"
    private const val DOWNLOAD_APK_NAME = "DOWNLOAD_APK_NAME"
    private const val DOWNLOAD_SHOW_NOTIFICATION = "download_show_notification"
    private var downIDs: MutableList<Int> = mutableListOf() //正在下载的通知id
    fun startIntent(path: String, apk_name: String = "", version: String = "", showNotification: Boolean = false) {
      val intent = Intent(Utils.getApp(), CcUpdateService::class.java)
      intent.putExtra(DOWNLOAD_PATH, path)
      intent.putExtra(DOWNLOAD_VERSION, version)
      intent.putExtra(DOWNLOAD_APK_NAME, apk_name)
      intent.putExtra(DOWNLOAD_SHOW_NOTIFICATION, showNotification)
      enqueueWork(Utils.getApp(), CcUpdateService::class.java, JOB_ID, intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //渠道id 安卓8.0 https://blog.csdn.net/MakerCloud/article/details/82079498
  private val updateChannelId = AppUtils.getAppPackageName() + ".update.channel.id"
  private val updateChannelName = AppUtils.getAppPackageName() + ".update.channel.name"

  //下载的百分比
  private var mPercent = 0f

  //通知管理
  private var mNotificationManager: NotificationManager? = null

  //通知创建类
  private var mBuilder: NotificationCompat.Builder? = null

  //上次的下载量(方便计算下载速度)
  private var mLastBytes: Long = 0

  //上次的时间
  private var mLastTime: Long = 0

  //下载速度
  private var mSpeed: Long = 0

  //通知栏数据设置
  private var mRemoteViews: RemoteViews? = null

  //文件下载保存的文件夹
  private val mFileDir = PathConfig.DOWNLOAD_DIR

  //是否显示通知栏
  private var needShowNotification = false

  //总大小
  private var mTotalSize = 0L

  //apk下载地址
  private var mApkUrl = ""

  //防止服务器返回同样的下载地址，就需要版本号来区别缓存文件
  private var mApkVersion = ""

  //app名称
  private var appName = AppUtils.getAppName()

  //通知id
  private var notificationID = 0
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="处理打开Service的命令等">
  override fun onHandleWork(intent: Intent) {
    if (mNotificationManager == null) {
      mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(updateChannelId, updateChannelName, NotificationManager.IMPORTANCE_LOW) //等级太高会一直响
        channel.setSound(null, null)
        mNotificationManager?.createNotificationChannel(channel)
      }
    }
    intent.let {
      mApkUrl = it.getStringExtra(DOWNLOAD_PATH) ?: ""
      mApkVersion = it.getStringExtra(DOWNLOAD_VERSION) ?: ""
      if (downIDs.contains(mApkUrl.hashCode())) return
      notificationID = mApkUrl.hashCode()
      downIDs.add(mApkUrl.hashCode())
      val downloadUrl: String = mApkUrl
      val downloadVersion: String = mApkVersion
      it.getStringExtra(DOWNLOAD_APK_NAME)?.let { name -> if (name.isNotBlank()) appName = name }
      needShowNotification = it.getBooleanExtra(DOWNLOAD_SHOW_NOTIFICATION, false)
      val downLoadName = EncryptUtils.encryptMD5ToString("$downloadUrl-$downloadVersion")
      val apkName = "$downLoadName.apk"
      if (File(mFileDir, apkName).exists()) {
        showSuccess(File(mFileDir, apkName).path)
        AppUtils.installApp(File(mFileDir, apkName).path)
        NotificationUtils.setNotificationBarVisibility(false)
        return
      }
      showNotification()
      //RxHttp 下载
      val tempFile = File(mFileDir, downLoadName)
      val downSize = if (tempFile.exists()) tempFile.length() else 0L
      RxHttp.get(downloadUrl)
        .setOkClient(RxHttpConfig.getOkHttpClient().build())
        .setRangeHeader(downSize) //设置开始下载位置，结束位置默认为文件末尾,如果需要衔接上次的下载进度，则需要传入上次已下载的字节数length
        .asDownload(tempFile.path, AndroidSchedulers.mainThread()) { progress ->
          //下载进度回调,0-100，仅在进度有更新时才会回调
          val currentProgress = progress.progress //当前进度 0-100
          val currentSize = progress.currentSize //当前已下载的字节大小
          val totalSize = progress.totalSize //要下载的总字节大小
          updateProgress(currentSize + downSize, totalSize + downSize)
        } //指定回调(进度/成功/失败)线程,不指定,默认在请求所在线程回调
        .subscribe({
          //下载成功，处理相关逻辑
          FileUtils.rename(tempFile, apkName)
          showSuccess(File(mFileDir, apkName).path)
          AppUtils.installApp(File(mFileDir, apkName).path)
          NotificationUtils.setNotificationBarVisibility(false)
        }, {
          //下载失败，处理相关逻辑
          showFail(downloadUrl)
        })
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="触发下载显示下载的内容">
  //显示下载通知
  private fun showNotification() {
    //发送进度
    LiveEventBus.get(EventKeys.UPDATE_PROGRESS, Triple::class.java).post(Triple(UpdateEnum.START, 0f, mApkUrl))
    if (needShowNotification) {
      initRemoteViews()
      mRemoteViews?.let {
        val sb = SpannableStringBuilder()
        sb.append("正在下载\"")
        val color = ForegroundColorSpan(ColorUtils.getColor(R.color.magenta))
        val title = SpannableString(appName)
        title.setSpan(color, 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        sb.append(title)
        sb.append("\"新版本")
        it.setTextViewText(R.id.notice_update_title, sb)
        it.setTextViewText(R.id.notice_update_percent, "0%")
        it.setProgressBar(R.id.notice_update_progress, 100, 0, false)
        it.setTextViewText(R.id.notice_update_speed, "")
        it.setTextViewText(R.id.notice_update_size, "")
        initBuilder(it)
        mNotificationManager?.notify(notificationID, mBuilder?.build())
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="下载中更新进度">
  //每次刷新的时间间隔
  private val interval = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) 500 else 250

  //更新下载进度
  private fun updateProgress(offsetSize: Long, totalSize: Long) {
    mTotalSize = totalSize
    mPercent = offsetSize * 100f / totalSize
    //发送进度
    LiveEventBus.get(EventKeys.UPDATE_PROGRESS, Triple::class.java).post(Triple(UpdateEnum.DOWNLOADING, 99.9f.coerceAtMost(mPercent), mApkUrl))
    if (mLastTime == 0L || mLastBytes == 0L) {
      mSpeed = offsetSize / 1000
      mLastTime = System.currentTimeMillis()
      mLastBytes = offsetSize
    } else if (System.currentTimeMillis() - mLastTime >= interval || offsetSize > totalSize) {
      mSpeed = (offsetSize - mLastBytes) / (System.currentTimeMillis() - mLastTime) * 1000
      mLastTime = System.currentTimeMillis()
      mLastBytes = offsetSize
    } else {
      return
    }
    if (needShowNotification) {
      mRemoteViews?.let {
        it.setProgressBar(R.id.notice_update_progress, 100, mPercent.toInt(), false)
        val progress = String.format(Locale.getDefault(), "%.1f", mPercent) + "%"
        it.setTextViewText(R.id.notice_update_percent, progress)
        val speedStr = byte2FitMemorySize(mSpeed) + "/s"
        it.setTextViewText(R.id.notice_update_speed, speedStr)
        val curSizeStr = byte2FitMemorySize(offsetSize)
        val totalSizeStr = byte2FitMemorySize(totalSize)
        it.setTextViewText(R.id.notice_update_size, "$curSizeStr/$totalSizeStr")
        mNotificationManager?.notify(notificationID, mBuilder?.build())
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="下载成功的显示">
  //下载成功后读取APK文件的包名，安装成功后对比是不是安装的刚下载的包名
  private var downApkPackageName: String = ""

  //下载成功
  private fun showSuccess(filePath: String) {
    downApkPackageName = AppUtils.getApkInfo(filePath)?.packageName ?: ""
    downIDs.remove(mApkUrl.hashCode())
    //发送进度
    LiveEventBus.get(EventKeys.UPDATE_PROGRESS, Triple::class.java).post(Triple(UpdateEnum.SUCCESS, 100f, mApkUrl))
    if (needShowNotification) {
      initRemoteViews()
      mRemoteViews?.let {
        val sb = SpannableStringBuilder()
        sb.append("\"")
        val color = ForegroundColorSpan(ColorUtils.getColor(R.color.magenta))
        val title = SpannableString(appName)
        title.setSpan(color, 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        sb.append(title)
        sb.append("\"下载完成,点击安装")
        it.setTextViewText(R.id.notice_update_title, sb)
        it.setProgressBar(R.id.notice_update_progress, 100, 100, false)
        it.setTextViewText(R.id.notice_update_percent, "100%")
        if (mTotalSize == 0L) mTotalSize = File(filePath).length()
        val totalSizeStr = byte2FitMemorySize(mTotalSize)
        it.setTextViewText(R.id.notice_update_size, "$totalSizeStr/$totalSizeStr")
        val intentInstall = Intent(Utils.getApp(), NotificationBroadcastReceiver::class.java)
        intentInstall.action = StringConstants.Update.INTENT_KEY_INSTALL_APP
        intentInstall.putExtra(StringConstants.Update.INTENT_KEY_INSTALL_PATH, filePath)
        intentInstall.putExtra(StringConstants.Update.INTENT_KEY_UPDATE_ID, notificationID)
        intentInstall.putExtra(StringConstants.Update.INTENT_KEY_RETRY_NAME, appName)
        intentInstall.putExtra(StringConstants.Update.INTENT_KEY_RETRY_PATH, mApkUrl)
        intentInstall.putExtra(StringConstants.Update.INTENT_KEY_RETRY_VERSION, mApkVersion)
        val intent = PendingIntent.getBroadcast(Utils.getApp(), notificationID, intentInstall, PendingIntent.FLAG_CANCEL_CURRENT)
        it.setOnClickPendingIntent(R.id.notice_update_layout, intent)
        initBuilder(it)
        mNotificationManager?.notify(notificationID, mBuilder?.build())
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="下载失败的显示">
  //更新失败
  private fun showFail(path: String) {
    downIDs.remove(mApkUrl.hashCode())
    //发送进度
    LiveEventBus.get(EventKeys.UPDATE_PROGRESS, Triple::class.java).post(Triple(UpdateEnum.FAIL, -1, mApkUrl))
    if (needShowNotification) {
      initRemoteViews()
      mRemoteViews?.let {
        val sb = SpannableStringBuilder()
        sb.append("\"")
        val color = ForegroundColorSpan(ColorUtils.getColor(R.color.magenta))
        val title = SpannableString(appName)
        title.setSpan(color, 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        sb.append(title)
        sb.append("\"下载失败,点击重试")
        it.setTextViewText(R.id.notice_update_title, sb)
        val intentInstall = Intent(Utils.getApp(), NotificationBroadcastReceiver::class.java)
        intentInstall.action = StringConstants.Update.INTENT_KEY_APK_DOWNLOAD_ERROR
        intentInstall.putExtra(StringConstants.Update.INTENT_KEY_RETRY_NAME, appName)
        intentInstall.putExtra(StringConstants.Update.INTENT_KEY_RETRY_PATH, mApkUrl)
        intentInstall.putExtra(StringConstants.Update.INTENT_KEY_RETRY_VERSION, mApkVersion)
        val intent = PendingIntent.getBroadcast(Utils.getApp(), notificationID, intentInstall, PendingIntent.FLAG_CANCEL_CURRENT)
        it.setOnClickPendingIntent(R.id.notice_update_layout, intent)
        initBuilder(it)
        mNotificationManager?.notify(notificationID, mBuilder?.build())
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="通知栏显示初始化">
  //初始化RemoteViews
  private fun initRemoteViews() {
    if (mRemoteViews == null) { //防止直接走失败
      mRemoteViews = RemoteViews(packageName, R.layout.notification_update)
      mRemoteViews?.setImageViewResource(R.id.notice_update_icon, R.mipmap.ic_launcher)
    }
  }

  //初始化Builder
  private fun initBuilder(remoteViews: RemoteViews) {
    if (mBuilder == null) {
      mBuilder = NotificationCompat.Builder(Utils.getApp(), updateChannelId)
        .setWhen(System.currentTimeMillis())
        .setDefaults(Notification.FLAG_AUTO_CANCEL)
        .setSound(null)
        .setOngoing(true) //将Ongoing设为true 那么notification将不能滑动删除
        .setAutoCancel(false) //将AutoCancel设为true后，当你点击通知栏的notification后，它会自动被取消消失
        .setContent(remoteViews)
        .setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_notification))
        .setSmallIcon(R.drawable.ic_notification)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="文件大小格式化显示">
  private fun byte2FitMemorySize(byteNum: Long): String {
    return when {
      byteNum < 0 -> "0B"
      byteNum < MemoryConstants.KB -> String.format(Locale.getDefault(), "%.2fB", byteNum.toDouble())
      byteNum < MemoryConstants.MB -> String.format(Locale.getDefault(), "%.2fKB", byteNum.toDouble() / 1024)
      byteNum < MemoryConstants.GB -> String.format(Locale.getDefault(), "%.2fMB", byteNum.toDouble() / 1048576)
      else -> String.format(Locale.getDefault(), "%.3fGB", byteNum.toDouble() / 1073741824)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="打开监听和关闭监听">
  override fun onCreate() {
    super.onCreate()
    registerAppInstall()
  }

  override fun onTaskRemoved(rootIntent: Intent?) {
    unregisterAppInstall()
    super.onTaskRemoved(rootIntent)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="监听安装">
  private var hasRegister = false
  private fun registerAppInstall() {
    if (!hasRegister) {
      hasRegister = true
      val intentFilter = IntentFilter()
      intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
      intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED)
      intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
      intentFilter.addDataScheme("package")
      Utils.getApp().registerReceiver(mInstallAppBroadcastReceiver, intentFilter)
    }
  }

  private fun unregisterAppInstall() {
    mNotificationManager?.cancel(notificationID)
    downIDs.remove(mApkUrl.hashCode())
    if (hasRegister) {
      hasRegister = false
      Utils.getApp().unregisterReceiver(mInstallAppBroadcastReceiver)
    }
  }

  private val mInstallAppBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
      if (intent != null && TextUtils.equals(Intent.ACTION_PACKAGE_ADDED, intent.action)) {
        intent.data?.schemeSpecificPart?.let { packageName ->
          if (packageName == AppUtils.getAppPackageName()) {
            unregisterAppInstall()
          } else if (packageName == downApkPackageName) { //兼容正式和测试包名不一样
            unregisterAppInstall()
            AppUtils.exitApp()
          }
        }
      }
    }
  }
  //</editor-fold>
}