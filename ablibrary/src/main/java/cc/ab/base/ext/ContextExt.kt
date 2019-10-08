package cc.ab.base.ext

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.*
import android.widget.Toast
import androidx.annotation.*
import androidx.core.content.ContextCompat
import cc.ab.base.widget.toast.AmToast
import com.blankj.utilcode.util.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/24 9:44
 */
//屏幕宽度
val Context.mScreenWidth: Int
  get() {
    return ScreenUtils.getScreenWidth()
  }

//屏幕高度
val Context.mScreenHeight: Int
  get() {
    return ScreenUtils.getScreenHeight()
  }

//状态栏高度
val Context.mStatusBarHeight: Int
  get() {
    return BarUtils.getStatusBarHeight()
  }

//dp转px
fun Context.dp2px(dp: Float): Int {
  return SizeUtils.dp2px(dp)
}

//dp转px
fun Context.dp2px(dp: Int): Int {
  return SizeUtils.dp2px(dp.toFloat())
}

//Toast 文字
fun Context.toast(msg: String?, duration: Int = Toast.LENGTH_SHORT) =
  if (!msg.isNullOrBlank()) {
    AmToast.showCenterToast(this, msg, duration)
  } else {
    LogUtils.e("toast内容为空")
  }

//Toast 文字资源
fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) =
  AmToast.showCenterToast(this, getString(resId), duration)

//获取颜色
fun Context.getColorRes(@ColorRes resId: Int): Int =
  ContextCompat.getColor(this, resId)

//获取Drawable
fun Context.getDrawableRes(@DrawableRes resId: Int): Drawable? =
  ContextCompat.getDrawable(this, resId)

//XML的layout转换为View
fun Context.inflate(
  @LayoutRes layoutResource: Int,
  parent: ViewGroup? = null,
  attachToRoot: Boolean = false
): View {
  return LayoutInflater.from(this).inflate(layoutResource, parent, attachToRoot)
}