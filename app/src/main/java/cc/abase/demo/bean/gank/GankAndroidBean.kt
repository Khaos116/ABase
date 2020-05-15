package cc.abase.demo.bean.gank

import cc.ab.base.net.http.response.PicBean
import cc.ab.base.widget.nineimageview.ImageData

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/30 20:28
 */
data class GankAndroidBean(
    val _id: String = "",//"5e9e807b808d6d2fe6b56ee8",
    val author: String? = null,//"Amit Shekhar",
    val category: String? = null,//"GanHuo",
    val createdAt: String? = null,//"2020-04-21 13:11:23",
    val desc: String? = null,//"This project will help in getting started with Kotlin Coroutines for Android Development and mastering it.",
    val images: MutableList<String?>? = null,//["https://gank.io/images/b4bdf5edd058417d8e78d1a2aa75fd95"],
    val likeCounts: Int = 0,//0,
    val publishedAt: String? = null,//"2020-04-21 13:11:23",
    val stars: Int = 0,//1,
    val title: String? = null,//"Kotlin-Coroutines-Android-Examples",
    val type: String? = null,//"Android",
    val url: String? = null,//"https://github.com/MindorksOpenSource/Kotlin-Coroutines-Android-Examples",
    val views: String? = null//115
) {

  //获取非空图片地址
  fun imagesNoNull(): MutableList<String> {
    return images?.filterNotNull()?.toMutableList() ?: mutableListOf()
  }

  //获取图片地址
  var urlImgs = mutableListOf<PicBean>()
    get() {
      if (field.isNullOrEmpty() && !images.isNullOrEmpty()) {
        field = mutableListOf()
        images.forEach { url ->
          if (!url.isNullOrBlank()) field.add(PicBean(url = url))
        }
      }
      return field
    }

  //获取图片地址2
  var urlImgs2 = mutableListOf<ImageData>()
    get() {
      if (field.isNullOrEmpty() && !images.isNullOrEmpty()) {
        field = mutableListOf()
        images.forEach { url ->
          if (!url.isNullOrBlank()) field.add(ImageData(url))
        }
      }
      return field
    }
}