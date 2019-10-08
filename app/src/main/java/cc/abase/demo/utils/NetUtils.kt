package cc.abase.demo.utils

import cc.ab.base.ext.toast
import cc.abase.demo.R
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.Utils

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/1 16:52
 */
class NetUtils private constructor() {
  private object SingletonHolder {
    val holder = NetUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //无网络则吐司
  fun checkToast(): Boolean {
    if (!NetworkUtils.isConnected()) {
      Utils.getApp()
          .toast(R.string.no_network)
      return false
    }
    return true
  }
}