package cc.ab.base.ext

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.SystemClock
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Author:Khaos
 * Date:2020/8/11
 * Time:17:46
 */
//停止惯性滚动(SmartRefresh下拉会自动停止滚动，所以不需要调用；如果使用Swipe下拉刷新，请一定在请求接口前调用)
fun RecyclerView.stopInertiaRolling() {
  try {
    this.stopScroll()
    //如果是Support的RecyclerView则需要使用"cancelTouch"
    val field = this.javaClass.getDeclaredMethod("cancelScroll")
    field.isAccessible = true
    field.invoke(this)
  } catch (e: Exception) {
    e.printStackTrace()
    "RecyclerView惯性滚动停止失败:${e.message}".logI()
  }
}

//RecyclerView点击事件
@SuppressLint("ClickableViewAccessibility")
inline fun RecyclerView.onClick(crossinline function: (view: View) -> Unit) {
  val recyclerView = this
  recyclerView.setOnTouchListener { _, event ->
    GestureDetector(context, object : SimpleOnGestureListener() {
      override fun onSingleTapUp(e: MotionEvent?): Boolean {
        function.invoke(recyclerView)
        return true
      }
    }).onTouchEvent(event)
  }
}

//点击事件传递给父控件
@SuppressLint("ClickableViewAccessibility")
inline fun View.click2Parent(parentView: View? = null) {
  (parentView ?: (this.parent as? View))?.let { p ->
    this.setOnTouchListener { _, event -> p.onTouchEvent(event) }
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
inline fun launchError(
  context: CoroutineContext = Dispatchers.Main,//如果使用了IO线程，则异常的时候需要注意也在IO线程
  crossinline handler: (CoroutineContext, Throwable) -> Unit = { _, e -> e.message.logE() },
  start: CoroutineStart = CoroutineStart.DEFAULT,
  noinline block: suspend CoroutineScope.() -> Unit
): Job {
  return CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    .launch(context + CoroutineExceptionHandler(handler), start, block)
}

//打印异常
fun Throwable?.logE() {
  this?.message?.logE()
}

//吐司异常
fun Throwable?.toast() {
  this?.message?.toast()
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

//设置没有更多的显示
fun SmartRefreshLayout?.noMoreData() {
  this?.setEnableLoadMore(true)
  this?.setNoMoreData(false) //防止下一个设置无效，所以先重置
  this?.setNoMoreData(true)
}

//设置还有更多的显示
fun SmartRefreshLayout?.hasMoreData() {
  this?.setEnableLoadMore(true)
  this?.setNoMoreData(true) //防止下一个设置无效，所以先重置
  this?.setNoMoreData(false)
}

//开机时间
fun Long.getOpenSysTime(): Long {
  return System.currentTimeMillis() - SystemClock.elapsedRealtime()
}

/**
 * 判断手机号(国家编码，中国->CN)
 * @see com.google.i18n.phonenumbers.CountryCodeToRegionCodeMap
 */
fun String?.isPhoneNumber(countryCode: String = "CN"): Boolean {
  if (this.isNullOrBlank()) return false
  val phoneUtil = PhoneNumberUtil.getInstance()
  return try {
    val numberProto: Phonenumber.PhoneNumber = phoneUtil.parse(this, countryCode.uppercase())
    phoneUtil.isValidNumber(numberProto)
  } catch (e: Exception) {
    e.printStackTrace()
    false
  }
}