package cc.ab.base.ext

import cc.ab.base.app.BaseApplication
import com.blankj.utilcode.util.Utils
import com.blankj.utilcode.util.Utils.getApp

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/22 18:15
 */
fun Utils.getApplication(): BaseApplication {
  return getApp() as BaseApplication
}