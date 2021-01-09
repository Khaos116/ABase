package cc.abase.demo.utils

import cc.abase.demo.component.gallery.GalleryActivity
import com.blankj.utilcode.util.ActivityUtils

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/28 12:06
 */
object BrowserUtils {
  fun show(urls: ArrayList<String>, position: Int = 0) {
    ActivityUtils.getTopActivity()
        ?.let {
          GalleryActivity.startActivity(it, urls, position)
        }
  }
}