package cc.ab.base.ext

import android.graphics.Color
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Author:case
 * Date:2020/8/11
 * Time:17:46
 */
//停止惯性滚动(SmartRefresh下拉会自动停止滚动，所以不需要调用；如果使用Swipe下拉刷新，请一定在请求接口前调用)
fun RecyclerView.stopInertiaRolling() {
  try {
    //如果是Support的RecyclerView则需要使用"cancelTouch"
    val field = this.javaClass.getDeclaredMethod("cancelScroll")
    field.isAccessible = true
    field.invoke(this)
  } catch (e: Exception) {
    e.printStackTrace()
    "RecyclerView惯性滚动停止失败:${e.message}".logI()
  }
}

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

/**需要明确的一点是，通过 async 启动的协程出现未捕获的异常时会忽略
 * CoroutineExceptionHandler，这与 launch 的设计思路是不同的。*/
inline fun GlobalScope.launchError(
    context: CoroutineContext = Dispatchers.Main,
    crossinline handler: (CoroutineContext, Throwable) -> Unit = { _, e -> e.message.logE() },
    start: CoroutineStart = CoroutineStart.DEFAULT,
    noinline block: suspend CoroutineScope.() -> Unit) {
  GlobalScope.launch(context + CoroutineExceptionHandler(handler), start, block)
}

//打印异常
fun Throwable?.logE() {
  this?.message?.logE()
}

//找到所有子fragment
fun Fragment.getAllChildFragments(): MutableList<Fragment> {
  val list = mutableListOf<Fragment>()
  val fragments = this.childFragmentManager.fragments
  if (fragments.isNotEmpty()) {
    list.addAll(fragments)
    fragments.forEach { f -> list.addAll(f.getAllChildFragments()) }
  }
  return list
}