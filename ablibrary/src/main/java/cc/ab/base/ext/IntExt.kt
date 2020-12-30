package cc.ab.base.ext

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.*

/*将xml定义的颜色变为 计算后的颜色值*/
fun Int.xmlToColor(): Int {
  return if (this == 0) this else ColorUtils.getColor(this)
}

/*将xml定义的字符串引用，获取真实String值*/
fun Int.xmlToString(): String {
  return StringUtils.getString(this)
}

//XML吐司
fun Int.xmlToast() {
  StringUtils.getString(this).toast()
}

fun Number.dp2Px(): Int {
  return SizeUtils.dp2px(this.toFloat())
}

fun Number.sp2Px(): Int {
  return SizeUtils.sp2px(this.toFloat())
}

fun Int.toDrawable(mContext: Context): Drawable? {
  return if (this <= 0) null else ContextCompat.getDrawable(mContext, this)
}