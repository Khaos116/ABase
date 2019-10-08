package cc.ab.base.ext

import android.graphics.Color

/**
 * description : 产生随机颜色,使用时需要创建Color对象再用，如Color().random()
 *
 * @author : case
 * @date : 2019/3/8 9:43
 */

/*产生随机颜色(不含透明度)*/
fun Color.randomNormal(): Int {
  val r = (Math.random() * 256).toInt()
  val g = (Math.random() * 256).toInt()
  val b = (Math.random() * 256).toInt()
  return Color.rgb(r, g, b)
}

/*产生随机颜色(含透明度)*/
fun Color.randomAlpha(): Int {
  val a = (Math.random() * 256).toInt()
  val r = (Math.random() * 256).toInt()
  val g = (Math.random() * 256).toInt()
  val b = (Math.random() * 256).toInt()
  return Color.argb(a, r, g, b)
}