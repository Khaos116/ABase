package cc.abase.demo.config

import cc.ab.base.ext.logE
import cc.abase.demo.utils.MMkvUtils

/**
 * @Description
 * @Author：Khaos
 * @Date：2021/1/12
 * @Time：16:58
 */
object UserManager {
  //<editor-fold defaultstate="collapsed" desc="变量">
  private var uid: Long = 0
  private var token: String? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="判断是否登录">
  fun isLogin(): Boolean {
    if (uid == 0L) getUid()
    if (token.isNullOrBlank()) getToken()
    return uid > 0 && !token.isNullOrBlank()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Uid操作">
  fun getUid(): Long {
    if (uid == 0L) uid = MMkvUtils.getUid()
    return uid
  }

  fun setUid(uid: Long) {
    this.uid = uid
    MMkvUtils.setUid(uid)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Token操作">
  fun getToken(): String? {
    if (token.isNullOrBlank()) token = MMkvUtils.getToken()
    return token
  }

  //更新Token
  fun setToken(token: String) {
    if (token.isNotBlank() && token != this.token) {
     "更新Token为:${token}".logE()
      this.token = token
      MMkvUtils.setToken(token)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="退出清理数据">
  fun clearUserInfo() {
    uid = 0
    token = null
    MMkvUtils.clearUserInfo()
  }
  //</editor-fold>
}