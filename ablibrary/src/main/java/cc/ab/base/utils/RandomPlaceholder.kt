package cc.ab.base.utils

import cc.ab.base.R
import kotlin.math.abs

/**
 *description: 根据图片地址获取对应纯色占位图
 */
class RandomPlaceholder private constructor() {
  private object SingletonHolder {
    val holder = RandomPlaceholder()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  private val placeholderResIdList = mutableListOf(
    R.drawable.img_placeholder_ffbcc6cc,
    R.drawable.img_placeholder_ffc6d1c0,
    R.drawable.img_placeholder_ffc7c9d6,
    R.drawable.img_placeholder_ffc9becf,
    R.drawable.img_placeholder_ffc9e2f0,
    R.drawable.img_placeholder_ffcad1db,
    R.drawable.img_placeholder_ffcad2e0,
    R.drawable.img_placeholder_ffcad8e8,
    R.drawable.img_placeholder_ffcadee3,
    R.drawable.img_placeholder_ffccd9e3,
    R.drawable.img_placeholder_ffccded2,
    R.drawable.img_placeholder_ffcce3da,
    R.drawable.img_placeholder_ffcfcabe,
    R.drawable.img_placeholder_ffcfd4e6,
    R.drawable.img_placeholder_ffd9d3eb,
    R.drawable.img_placeholder_ffddd3eb,
    R.drawable.img_placeholder_ffe6cfde,
    R.drawable.img_placeholder_ffe6e6cf
  )

  //根据需要加载的图片地址，寻找对于的占位图
  fun getPlaceHolder(url: String?): Int {
    val index = if (url.isNullOrEmpty()) {
      0
    } else {
      abs(url.hashCode()) % placeholderResIdList.size
    }
    return placeholderResIdList[index]
  }
}