package cc.abase.demo.utils

import cc.abase.demo.BuildConfig
import cc.abase.demo.constants.api.ApiUrl
import com.tencent.mmkv.MMKV

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/10 15:14
 */
object MMkvUtils {
  //<editor-fold defaultstate="collapsed" desc="MMKV对象">
  //普通存储
  private var mMMKVDefault: MMKV? = null
    get() {
      if (field == null) field = MMKV.defaultMMKV()
      return field
    }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="域名地址(解决动态替换域名问题)">
  private const val APP_BASE_URL = "KKMV_KEY_APP_BASE_URL"
  fun getBaseUrl(): String {
    return mMMKVDefault?.decodeString(APP_BASE_URL) ?: if (BuildConfig.APP_IS_RELEASE) ApiUrl.baseUrlRelease else ApiUrl.baseUrlDebug
  }

  fun setBaseUrl(url: String?) {
    ApiUrl.appBaseUrl = ""
    if (url.isNullOrBlank()) {
      mMMKVDefault?.removeValueForKey(APP_BASE_URL)
    } else {
      mMMKVDefault?.encode(APP_BASE_URL, url)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="引导页">
  private const val GUIDE_SPLASH = "KKMV_KEY_GUIDE_SPLASH"
  fun getNeedGuide(): Boolean {
    return mMMKVDefault?.decodeBool(GUIDE_SPLASH, true) ?: true
  }

  fun setNeedGuide(need: Boolean = true) {
    mMMKVDefault?.encode(GUIDE_SPLASH, need)
  }
  //</editor-fold>.

  //<editor-fold defaultstate="collapsed" desc="UID">
  private const val USER_UID = "KKMV_KEY_USER_UID"
  fun getUid(): Long {
    return mMMKVDefault?.decodeLong(USER_UID, 0L) ?: 0L
  }

  fun setUid(uid: Long) {
    mMMKVDefault?.encode(USER_UID, uid)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Token">
  private const val USER_TOKEN = "KKMV_KEY_USER_TOKEN"
  fun getToken(): String? {
    return mMMKVDefault?.decodeString(USER_TOKEN)
  }

  fun setToken(token: String) {
    mMMKVDefault?.encode(USER_TOKEN, token)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="账号">
  private const val USER_ACCOUNT = "USER_ACCOUNT"
  fun getAccount(): String {
    return mMMKVDefault?.decodeString(USER_ACCOUNT, "") ?: ""
  }

  fun setAccount(account: String) {
    mMMKVDefault?.encode(USER_ACCOUNT, account)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="密码">
  private const val USER_PWD = "USER_PWD"
  fun getPassword(): String {
    return mMMKVDefault?.decodeString(USER_PWD, "") ?: ""
  }

  fun setPassword(pwd: String) {
    mMMKVDefault?.encode(USER_PWD, pwd)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="退出时需要清理的内容">
  fun clearUserInfo() {
    mMMKVDefault?.removeValueForKey(USER_UID)
    mMMKVDefault?.removeValueForKey(USER_TOKEN)
  }
  //</editor-fold>
}