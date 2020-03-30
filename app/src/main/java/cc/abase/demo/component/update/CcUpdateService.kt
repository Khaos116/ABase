package cc.abase.demo.component.update

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import cc.abase.demo.R
import cc.abase.demo.constants.EventKeys
import cc.abase.demo.constants.IntConstants
import cc.abase.demo.constants.StringConstants
import com.blankj.utilcode.util.*
import com.github.kittinunf.fuel.Fuel
import com.jeremyliao.liveeventbus.LiveEventBus
import io.reactivex.android.schedulers.AndroidSchedulers
import rxhttp.wrapper.param.RxHttp
import java.io.File
import java.util.*


/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/30 9:45
 */
open class CcUpdateService : IntentService("UpdateService") {
  companion object {
    private const val DOWNLOAD_PATH = "download_path"
    private const val DOWNLOAD_VERSION_NAME = "download_version_name"
    private const val DOWNLOAD_SHOW_NOTIFICATION = "download_show_notification"
    private var isDownloading = false//是否正在下载
    fun startIntent(
      path: String,
      version_name: String = "",
      showNotification: Boolean = false
    ) {
      if (isDownloading) return
      val intent = Intent(Utils.getApp(), CcUpdateService::class.java)
      intent.putExtra(DOWNLOAD_PATH, path)
      intent.putExtra(DOWNLOAD_VERSION_NAME, version_name)
      intent.putExtra(DOWNLOAD_SHOW_NOTIFICATION, showNotification)
      Utils.getApp()
          .startService(intent)
    }
  }

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
  //显示下载更新的版本名称
  private var mVerName: String? = null
  //文件下载保存的文件夹
  private val mFileDir = PathUtils.getExternalAppFilesPath()
  //是否显示通知栏
  private var needShowNotification = false
  //总大小
  private var mTtotalSize = 0L
  //apk下载地址
  private var mApkUrl = ""
  //apk下载的版本
  private var mApkVersion = ""
  //app名称
  private val appName = AppUtils.getAppName()
  //渠道id 安卓8.0 https://blog.csdn.net/MakerCloud/article/details/82079498
  private val UPDATE_CHANNEL_ID = AppUtils.getAppPackageName() + ".update.channel.id"
  private val UPDATE_CHANNEL_NAME = AppUtils.getAppPackageName() + ".update.channel.name"

  override fun onHandleIntent(intent: Intent?) {
    if (mNotificationManager == null) {
      mNotificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            UPDATE_CHANNEL_ID,
            UPDATE_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW//等级太高会一直响
        )
        channel.setSound(null, null)
        mNotificationManager?.createNotificationChannel(channel)
      }
    }
    if (isDownloading) return
    intent?.let {
      isDownloading = true
      mApkUrl = it.getStringExtra(DOWNLOAD_PATH) ?: ""
      val downloadUrl: String = mApkUrl
      mApkVersion = it.getStringExtra(DOWNLOAD_VERSION_NAME) ?: ""
      val versionName = mApkVersion
      needShowNotification = it.getBooleanExtra(DOWNLOAD_SHOW_NOTIFICATION, false)
      val downLoadName = EncryptUtils.encryptMD5ToString(downloadUrl)
      val apkName = "$downLoadName.apk"
      mVerName = if (versionName.isEmpty() || versionName == "最新版本") "最新版本" else versionName
      if (File(mFileDir, apkName).exists()) {
        showSuccess(File(mFileDir, apkName).path)
        AppUtils.installApp(File(mFileDir, apkName).path)
        return
      }
      showNotification()
      //Fuel下载
      Fuel.download(downloadUrl)
          .fileDestination { response, request ->
            File(mFileDir, downLoadName)
          }
          .progress { readBytes, totalBytes ->
            updateProgress(readBytes, totalBytes)
          }
          .response { req, res, result ->
            isDownloading = false
            if (result.component2() == null) {
              FileUtils.rename(File(mFileDir, downLoadName), apkName)
              showSuccess(File(mFileDir, apkName).path)
              AppUtils.installApp(File(mFileDir, apkName).path)
            } else {
              showFail(downloadUrl)
            }
          }
      //RxHttp 下载
//      val tempFile = File(mFileDir, downLoadName)
//      RxHttp.get(downloadUrl)
//        .setRangeHeader(if (tempFile.exists()) tempFile.length() else 0L)//设置开始下载位置，结束位置默认为文件末尾,如果需要衔接上次的下载进度，则需要传入上次已下载的字节数length
//        .asDownload(tempFile.path, { progress ->
//          //下载进度回调,0-100，仅在进度有更新时才会回调
//          val currentProgress = progress.progress //当前进度 0-100
//          val currentSize = progress.currentSize //当前已下载的字节大小
//          val totalSize = progress.totalSize //要下载的总字节大小
//          updateProgress(currentSize, totalSize)
//        }, AndroidSchedulers.mainThread())//指定回调(进度/成功/失败)线程,不指定,默认在请求所在线程回调
//        .subscribe({
//          //下载成功，处理相关逻辑
//          FileUtils.rename(tempFile, apkName)
//          showSuccess(File(mFileDir, apkName).path)
//          AppUtils.installApp(File(mFileDir, apkName).path)
//        }, {
//          //下载失败，处理相关逻辑
//          showFail(downloadUrl)
//        }, {
//          isDownloading = false
//        })
    }
  }

  //显示下载通知
  private fun showNotification() {
    //发送进度
    LiveEventBus.get(EventKeys.UPDATE_PROGRESS)
        .post(Pair(UpdateEnum.START, 0f))
    if (needShowNotification) {
      initRemoteViews()
      mRemoteViews?.let {
        it.setTextViewText(
            R.id.notice_update_title,
            "正在下载$appName$mVerName"
        )
        it.setTextViewText(R.id.notice_update_percent, "0%")
        it.setProgressBar(
            R.id.notice_update_progress,
            100,
            0,
            false
        )
        it.setTextViewText(R.id.notice_update_speed, "")
        it.setTextViewText(R.id.notice_update_size, "")
        initBuilder(it)
        mNotificationManager?.notify(IntConstants.Notification.UPDATE_ID, mBuilder?.build())
      }
    }
  }

  //每次刷新的时间间隔
  private val interval = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) 500 else 250

  //更新下载进度
  private fun updateProgress(
    offsetSize: Long,
    totalSize: Long
  ) {
    mTtotalSize = totalSize
    mPercent = offsetSize * 100f / totalSize
    //发送进度
    LiveEventBus.get(EventKeys.UPDATE_PROGRESS)
        .post(Pair(UpdateEnum.DOWNLOADING, Math.min(99.9f, mPercent)))
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
        it.setProgressBar(
            R.id.notice_update_progress, 100,
            mPercent.toInt(), false
        )
        val progress = String.format(Locale.getDefault(), "%.1f", mPercent) + "%"
        it.setTextViewText(R.id.notice_update_percent, progress)
        val speedStr = byte2FitMemorySize(mSpeed) + "/s"
        it.setTextViewText(R.id.notice_update_speed, speedStr)
        val curSizeStr = byte2FitMemorySize(offsetSize)
        val totalSizeStr = byte2FitMemorySize(totalSize)
        it.setTextViewText(R.id.notice_update_size, "$curSizeStr/$totalSizeStr")
        mNotificationManager?.notify(IntConstants.Notification.UPDATE_ID, mBuilder?.build())
      }
    }
  }

  //下载成功
  private fun showSuccess(filePath: String) {
    //发送进度
    LiveEventBus.get(EventKeys.UPDATE_PROGRESS)
        .post(Pair(UpdateEnum.SUCCESS, 100f))
    if (needShowNotification) {
      initRemoteViews()
      mRemoteViews?.let {
        if (mTtotalSize == 0L) mTtotalSize = File(filePath).length()
        it.setTextViewText(
            R.id.notice_update_title,
            "$appName${mVerName}下载完成，点击安装"
        )
        it.setProgressBar(
            R.id.notice_update_progress,
            100,
            100,
            false
        )
        it.setTextViewText(R.id.notice_update_percent, "100%")
        val totalSizeStr = byte2FitMemorySize(mTtotalSize)
        it.setTextViewText(
            R.id.notice_update_size,
            "$totalSizeStr/$totalSizeStr"
        )
        val intentInstall =
          Intent(Utils.getApp(), NotificationBroadcastReceiver::class.java)
        intentInstall.action = StringConstants.Update.INTENT_KEY_INSTALL_APP
        intentInstall.putExtra(StringConstants.Update.INTENT_KEY_INSTALL_PATH, filePath)
        it.setOnClickPendingIntent(
            R.id.notice_update_layout,
            PendingIntent.getBroadcast(
                Utils.getApp(), 0, intentInstall,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        )
        initBuilder(it)
        mBuilder?.setOngoing(false)
        mNotificationManager?.notify(
            IntConstants.Notification.UPDATE_ID,
            mBuilder?.build()
        )
      }
    }
  }

  //更新失败
  private fun showFail(path: String) {
    //发送进度
    LiveEventBus.get(EventKeys.UPDATE_PROGRESS)
        .post(Pair(UpdateEnum.FAIL, -1))
    if (needShowNotification) {
      initRemoteViews()
      mRemoteViews?.let {
        it.setTextViewText(R.id.notice_update_title, "$appName${mVerName}下载失败，点击重试")
        val intentInstall =
          Intent(Utils.getApp(), NotificationBroadcastReceiver::class.java)
        intentInstall.action = StringConstants.Update.INTENT_KEY_APK_DOWNLOAD_ERROR
        intentInstall.putExtra(StringConstants.Update.INTENT_KEY_RETRY_PATH, mApkUrl)
        intentInstall.putExtra(StringConstants.Update.INTENT_KEY_RETRY_NAME, mApkVersion)
        it.setOnClickPendingIntent(
            R.id.notice_update_layout,
            PendingIntent.getBroadcast(
                Utils.getApp(), 0, intentInstall,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        )
        initBuilder(it)
        mBuilder?.setOngoing(false)
        mNotificationManager?.notify(IntConstants.Notification.UPDATE_ID, mBuilder?.build())
      }
    }
  }

  //初始化RemoteViews
  private fun initRemoteViews() {
    if (mRemoteViews == null) { //防止直接走失败
      mRemoteViews =
        RemoteViews(packageName, R.layout.notification_update)
      mRemoteViews?.setImageViewResource(
          R.id.notice_update_icon,
          R.mipmap.ic_launcher
      )
    }
  }

  //初始化Builder
  private fun initBuilder(remoteViews: RemoteViews) {
    if (mBuilder == null) {
      mBuilder = NotificationCompat.Builder(Utils.getApp(), UPDATE_CHANNEL_ID)
          .setWhen(System.currentTimeMillis())
          .setDefaults(Notification.FLAG_AUTO_CANCEL)
          .setSound(null)
          .setOngoing(true)
          .setAutoCancel(true)
          .setContent(remoteViews)
          .setSmallIcon(R.drawable.ic_notification)
    }
  }

  private fun byte2FitMemorySize(byteNum: Long): String {
    return when {
      byteNum < 0 -> "0KB"
      byteNum < 1024 -> String.format(Locale.getDefault(), "%.2fB", byteNum.toDouble())
      byteNum < 1048576 -> String.format(Locale.getDefault(), "%.2fKB", byteNum.toDouble() / 1024)
      byteNum < 1073741824 -> String.format(
          Locale.getDefault(), "%.2fMB", byteNum.toDouble() / 1048576
      )
      else -> String.format(Locale.getDefault(), "%.32GB", byteNum.toDouble() / 1073741824)
    }
  }
}