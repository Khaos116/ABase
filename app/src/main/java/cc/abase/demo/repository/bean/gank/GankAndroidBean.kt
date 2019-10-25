package cc.abase.demo.repository.bean.gank

import cc.ab.base.net.http.response.PicBean
import cc.ab.base.widget.nineimageview.ImageData
import cc.abase.demo.utils.TimeUtils

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/30 20:28
 */
data class GankAndroidBean(
  var _id: String,// "5bbb01af9d21226111b86f0d",
  var createdAt: String? = null,// "2018-10-08T07:05:19.297Z",
  var desc: String? = null,// "适用于Android的灵活，强大且轻量级的插件框架【爱奇艺】",
  var images: List<String?>?,
  var publishedAt: String? = null,// "2019-04-10T00:00:00.0Z",
  var source: String? = null,// "chrome",
  var type: String? = null,// "Android",
  var url: String? = null,// "https://github.com/iqiyi/Neptune",
  var used: String? = null,// true,
  var who: String? = null// "潇湘剑雨"
) {
  //防止在列表中每次都要计算
  var publishTime: String? = null
    get() {
      if (field.isNullOrBlank() && !publishedAt.isNullOrEmpty()) {
        field = TimeUtils.instance.utc2Local(publishedAt ?: "")
      }
      return field
    }

  //获取图片地址
  var urlImgs = mutableListOf<PicBean>()
    get() {
      if (field.isNullOrEmpty() && !images.isNullOrEmpty()) {
        field = mutableListOf()
        images?.forEach { url ->
          if (!url.isNullOrBlank()) field.add(PicBean(mediaUrl = url))
        }
      }
      return field
    }

  //获取图片地址2
  var urlImgs2 = mutableListOf<ImageData>()
    get() {
      if (field.isNullOrEmpty() && !images.isNullOrEmpty()) {
        field = mutableListOf()
        images?.forEach { url ->
          if (!url.isNullOrBlank()) field.add(ImageData(url))
        }
      }
      return field
    }
}