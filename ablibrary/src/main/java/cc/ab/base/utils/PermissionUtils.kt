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
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.blankj.utilcode.util.*
import java.io.File

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/11/11 21:40
 */
class PermissionUtils private constructor() {
  private object SingletonHolder {
    val holder = PermissionUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //是否有SD卡读取权限
  @SuppressLint("MissingPermission")
  fun hasSDPermission(): Boolean {
    val parent = File(PathUtils.getExternalAppDataPath())
    val file: File = File(parent, System.currentTimeMillis().toString())
    return try {
      if (parent.exists()) {
        parent.mkdirs()
      } else {
        file.createNewFile()
        FileUtils.delete(file)
      }
      true
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }

  //是否有拍照权限
  @SuppressLint("MissingPermission")
  fun hasCameraPermission(): Boolean {
    return try {
      //targetSdkVersion低于23，使用异常捕捉相机权限
      val camera = Camera.open()
      // setParameters 是针对魅族MX5 做的。MX5 通过Camera.open() 拿到的Camera
      // 对象不为null
      val mParameters = camera.parameters
      camera.parameters = mParameters
      camera.release()
      true
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }

  //是否有录音权限
  @SuppressLint("MissingPermission")
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
    }
    if (audioRecord == null) {
      return false
    }
    try {
      // 开始录音
      audioRecord.startRecording()
    } catch (e: Exception) {
      //可能情况一
      audioRecord.release()
      LogUtils.e("MMAudioRecorderPanel hasRecordPermission", e)
    }

    // 检测是否在录音中,6.0以下会返回此状态
    if (audioRecord.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
      //可能情况二
      try {
        audioRecord.stop()
        audioRecord.release()
      } catch (e: Exception) {
        LogUtils.e("MMAudioRecorderPanel hasRecordPermission", e)
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
      LogUtils.e("MMAudioRecorderPanel hasRecordPermission", e)
    }
    return true
  }

  //是否有定位权限
  @SuppressLint("MissingPermission")
  fun hasLocationPermission(): Boolean {
    val c = Utils.getApp()
    val permission = ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION)
    if (permission < 0) {
      if (permission == -1) LogUtils.e("定位权限被拒绝")
      if (permission == -2) LogUtils.e("定位权限被永久拒绝")
      return false
    }
    val mLocationManager = c.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return try {
      mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        ?.let {
          LogUtils.e("getLastKnownLocation=$it")
        }
      true
    } catch (e: Exception) {
      LogUtils.e("LocationUtil:$e")
      false
    }
  }

  //定位是否可用
  fun locationEnable(): Boolean {
    val c = Utils.getApp()
    val permission = ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION)
    if (permission < 0) {
      if (permission == -1) LogUtils.e("定位权限被拒绝")
      if (permission == -2) LogUtils.e("定位权限被永久拒绝")
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
          LogUtils.e("getLastKnownLocation=$it")
        }
      true
    } catch (e: Exception) {
      LogUtils.e("LocationUtil:$e")
      false
    }
  }

  //打开定位设置
  fun startLocationSetting(context: Context) {
    val intent = Intent()
    intent.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
    context.startActivity(intent)
  }

  //打开权限设置
  fun startPermissionSetting(context: Context) {
    try {
      val intent = Intent()
      intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
      val uri = Uri.fromParts("package", AppUtils.getAppPackageName(), null)
      intent.data = uri
      context.startActivity(intent)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}