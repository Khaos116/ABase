package cc.abase.demo.utils

import cc.abase.demo.component.gallery.GalleryActivity
import com.blankj.utilcode.util.ActivityUtils

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/28 12:06
 */
class BrowserUtils private constructor() {

  fun show(
    urls: ArrayList<String>,
    position: Int = 0
  ) {
    ActivityUtils.getTopActivity()
        ?.let {
          GalleryActivity.startActivity(it, urls, position)
        }
  }

  private object SingletonHolder {
    val holder = BrowserUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

}