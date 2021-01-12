package cc.abase.demo.utils

import cc.abase.demo.BuildConfig
import cc.abase.demo.constants.ApiUrl
import com.tencent.mmkv.MMKV

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/10 15:14
 */
object MMkvUtils {
  //<editor-fold defaultstate="collapsed" desc="域名地址(解决动态替换域名问题)">
  private const val APP_BASE_URL = "KKMV_KEY_APP_BASE_URL"
  fun getBaseUrl(): String {
    return MMKV.defaultMMKV()?.decodeString(APP_BASE_URL) ?: if (BuildConfig.APP_IS_RELEASE) ApiUrl.baseUrlRelease else ApiUrl.baseUrlDebug
  }

  fun setBaseUrl(url: String?) {
    if (url.isNullOrBlank()) {
      MMKV.defaultMMKV()?.removeValueForKey(APP_BASE_URL)
    } else {
      MMKV.defaultMMKV()?.encode(APP_BASE_URL, url)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="引导页">
  private const val GUIDE_SPLASH = "KKMV_KEY_GUIDE_SPLASH"
  fun getNeedGuide(): Boolean {
    return MMKV.defaultMMKV()?.decodeBool(GUIDE_SPLASH, true) ?: true
  }

  fun setNeedGuide(need: Boolean = true) {
    MMKV.defaultMMKV()?.encode(GUIDE_SPLASH, need)
  }
  //</editor-fold>.

  //<editor-fold defaultstate="collapsed" desc="UID">
  private const val USER_UID = "KKMV_KEY_USER_UID"
  fun getUid(): Long {
    return MMKV.defaultMMKV()?.decodeLong(USER_UID, 0L) ?: 0L
  }

  fun setUid(uid: Long) {
    MMKV.defaultMMKV()?.encode(USER_UID, uid)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Token">
  private const val USER_TOKEN = "KKMV_KEY_USER_TOKEN"
  fun getToken(): String? {
    return MMKV.defaultMMKV()?.decodeString(USER_TOKEN)
  }

  fun setToken(token: String) {
    MMKV.defaultMMKV()?.encode(USER_TOKEN, token)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="账号">
  private const val USER_ACCOUNT = "USER_ACCOUNT"
  fun getAccount(): String {
    return MMKV.defaultMMKV()?.decodeString(USER_ACCOUNT, "") ?: ""
  }

  fun setAccount(account: String) {
    MMKV.defaultMMKV()?.encode(USER_ACCOUNT, account)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="密码">
  private const val USER_PWD = "USER_PWD"
  fun getPassword(): String {
    return MMKV.defaultMMKV()?.decodeString(USER_PWD, "") ?: ""
  }

  fun setPassword(pwd: String) {
    MMKV.defaultMMKV()?.encode(USER_PWD, pwd)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="退出时需要清理的内容">
  fun clearUserInfo() {
    MMKV.defaultMMKV()?.removeValueForKey(USER_UID)
    MMKV.defaultMMKV()?.removeValueForKey(USER_TOKEN)
  }
  //</editor-fold>
}