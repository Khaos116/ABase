package cc.abase.demo.utils

import android.text.SpannableStringBuilder
import cc.abase.demo.BuildConfig
import cc.abase.demo.R
import cc.abase.demo.constants.api.ApiUrl
import com.blankj.utilcode.util.*
import com.snail.antifake.deviceid.macaddress.MacAddressUtils
import com.snail.antifake.jni.EmulatorDetectUtil
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Description:
 * @author: CASE
 * @date: 2019/11/6 17:29
 */
object AppInfoUtils {
  //获取APP版本信息
  fun getAppInfo(): CharSequence {
    val builder = SpannableStringBuilder()
    builder.append("APP信息：").append("\n")
        .append("=====================================\n")
        .append("Emulator：").append(EmulatorDetectUtil.isEmulator(Utils.getApp()).toString()).append("\n")
        .append("Release：").append(BuildConfig.APP_IS_RELEASE.toString()).append("\n")
        .append("BuildTime：").append(StringUtils.getString(R.string.build_time)).append("\n")
        .append("VersionName：").append(AppUtils.getAppVersionName()).append("\n")
        .append("VersionCode：").append(AppUtils.getAppVersionCode().toString()).append("\n")
        .append("BaseUrl：").append(ApiUrl.appBaseUrl).append("\n")
        .append("CPU：").append(getDeviceCPU()).append("\n")
        .append("渠道号").append(MySpanUtils.getSpanSimple("[测试服永远-1]")).append("：").append(WalleUtils.getChannel()).append("\n")
        .append("Wifi-BSSID：").append(MacAddressUtils.getConnectedWifiMacAddress(Utils.getApp()) ?: "null").append("\n") //需要地理位置权限
        .append("=====================================\n")
        .append("签名SHA1：").append("\n")
        .append(AppUtils.getAppSignaturesSHA1().firstOrNull()).append("\n")
        .append("签名SHA256：").append("\n")
        .append(AppUtils.getAppSignaturesSHA256().firstOrNull()).append("\n")
        .append("签名MD5：").append("\n")
        .append(AppUtils.getAppSignaturesMD5().firstOrNull()).append("\n")
    return builder
  }

  //获取CPU架构
  private fun getDeviceCPU(): String {
    return try {
      BufferedReader(
          InputStreamReader(
              Runtime.getRuntime().exec("getprop ro.product.cpu.abi").inputStream
          )
      ).readLine()
    } catch (e: Exception) {
      ""
    }
  }
}