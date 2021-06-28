package cc.ab.base.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.location.LocationManager
import android.media.*
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import cc.ab.base.ext.logE
import com.blankj.utilcode.util.*
import java.io.File

/**
 * Description:
 * @author: Khaos
 * @date: 2019/11/11 21:40
 */
object PermissionUtils {
  //是否有SD卡读取权限
  @SuppressLint("MissingPermission")
  @Synchronized
  fun hasSDPermission(): Boolean {
    val parent = File(PathUtils.getExternalDownloadsPath())
    val file = File(parent, "Khaos_TEST.txt")
    return try {
      if (!parent.exists()) parent.mkdirs()
      if (!file.exists()) file.createNewFile()
      FileUtils.delete(file)
    } catch (e: Exception) {
      e.printStackTrace()
      "SD卡权限异常:${e.message}".logE()
      false
    }
  }

  //是否有拍照权限
  @SuppressLint("MissingPermission")
  @Synchronized
  fun hasCameraPermission(): Boolean {
    var camera: Camera? = null
    return try {
      //targetSdkVersion低于23，使用异常捕捉相机权限
      camera = Camera.open()
      // setParameters 是针对魅族MX5 做的。MX5 通过Camera.open() 拿到的Camera
      // 对象不为null
      val mParameters = camera.parameters
      camera.parameters = mParameters
      camera.release()
      true
    } catch (e: Exception) {
      camera?.release()
      e.printStackTrace()
      "拍照权限异常:${e.message}".logE()
      false
    }
  }

  //是否有录音权限
  @SuppressLint("MissingPermission")
  @Synchronized
  fun hasRecordPermission(): Boolean {
    val minBufferSize = AudioRecord.getMinBufferSize(
      44100,
      AudioFormat.CHANNEL_IN_STEREO,
      AudioFormat.ENCODING_PCM_16BIT
    )
    var audioRecord: AudioRecord? = null
    try {
      audioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC, 44100,
        AudioFormat.CHANNEL_IN_STEREO,
        AudioFormat.ENCODING_PCM_16BIT, minBufferSize
      )
    } catch (e: Exception) {
      e.printStackTrace()
      "录音权限异常:${e.message}".logE()
    }
    if (audioRecord == null) {
      return false
    }
    try {
      // 开始录音
      audioRecord.startRecording()
    } catch (e: Exception) {
      e.printStackTrace()
      //可能情况一
      audioRecord.release()
      "录音权限异常:${e.message}".logE()
    }

    // 检测是否在录音中,6.0以下会返回此状态
    if (audioRecord.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
      //可能情况二
      try {
        audioRecord.stop()
        audioRecord.release()
      } catch (e: Exception) {
        e.printStackTrace()
        "录音权限异常:${e.message}".logE()
      }
      return false
    }
    val bufferSizeInBytes = 1024
    val audioData = ByteArray(bufferSizeInBytes)
    var readSize = 0
    // 正在录音
    readSize = audioRecord.read(audioData, 0, bufferSizeInBytes)
    if (readSize == AudioRecord.ERROR_INVALID_OPERATION || readSize <= 0) {
      return false
    }
    try {
      audioRecord.stop()
      audioRecord.release()
    } catch (e: Exception) {
      e.printStackTrace()
      "录音权限异常:${e.message}".logE()
    }
    return true
  }

  //是否有定位权限
  @SuppressLint("MissingPermission")
  @Synchronized
  fun hasLocationPermission(): Boolean {
    val c = Utils.getApp()
    val permission =
      ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION)
    if (permission < 0) {
      if (permission == -1) "定位权限被拒绝".logE()
      if (permission == -2) "定位权限被永久拒绝".logE()
      return false
    }
    val mLocationManager = c.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return try {
      mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        ?.let {
          "getLastKnownLocation=$it".logE()
        }
      true
    } catch (e: Exception) {
      e.printStackTrace()
      "定位权限异常:${e.message}".logE()
      false
    }
  }

  //定位是否可用
  @SuppressLint("MissingPermission")
  fun locationEnable(): Boolean {
    val c = Utils.getApp()
    val permission =
      ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION)
    if (permission < 0) {
      if (permission == -1) "定位权限被拒绝".logE()
      if (permission == -2) "定位权限被永久拒绝".logE()
      return false
    }
    val mLocationManager = c.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      return mLocationManager.isLocationEnabled
    }
    val provider = if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      LocationManager.GPS_PROVIDER
    } else if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
      LocationManager.NETWORK_PROVIDER
    } else {
      null
    }
    if (provider.isNullOrBlank()) return false
    return try {
      mLocationManager.getLastKnownLocation(provider)
        ?.let {
          "getLastKnownLocation=$it".logE()
        }
      true
    } catch (e: Exception) {
      e.printStackTrace()
      "定位权限异常:${e.message}".logE()
      false
    }
  }

  //打开APP权限页
  fun startPermissionSetting(context: Context) {
    val intent = Intent()
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (Build.VERSION.SDK_INT >= 9) {
      intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
      intent.data = Uri.fromParts("package", context.packageName, null)
    } else if (Build.VERSION.SDK_INT <= 8) {
      intent.action = Intent.ACTION_VIEW
      intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
      intent.putExtra("com.android.settings.ApplicationPkgName", context.packageName)
    }
    context.startActivity(intent)
  }
}