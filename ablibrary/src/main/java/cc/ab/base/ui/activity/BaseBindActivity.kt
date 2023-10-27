package cc.ab.base.ui.activity

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import cc.ab.base.databinding.BaseActivityBinding
import cc.ab.base.ext.*
import cc.ab.base.ui.fragment.BaseBindFragment
import cc.ab.base.utils.FixResources
import com.blankj.utilcode.util.LanguageUtils
import com.dylanc.viewbinding.base.ViewBindingUtil
import com.dylanc.viewbinding.inflateBinding
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.coroutines.*
import me.jessyan.autosize.AutoSize
import java.util.*

/**
 * 参考：
 * 1.https://blog.csdn.net/choimroc/article/details/104756365
 * 2.https://blog.csdn.net/u010976213/article/details/104501830
 * 注:目前发现MyVideoView和DLSideBar使用ViewBinding在XML加载存在问题，所以改为代码动态加载
 * @Description 由于进行了异步XML加载，所以如果重写了onResume、onPause等生命周期方法，需要谨慎使用viewBinding
 * @Author：Khaos
 * @Date：2021/3/15
 * @Time：16:20
 */
abstract class BaseBindActivity<T : ViewBinding> : AppCompatActivity() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //如果要操作状态栏，则需要使用到
  protected lateinit var baseBinding: BaseActivityBinding

  //除状态栏以外的XML
  protected var _binding: T? = null //由于是异步加载，所以如果重写生命周期用到View先判断它是否为空
  protected val viewBinding get() = _binding!!

  //XML加载
  private var mJobLoading: Job? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="创建">
  override fun onCreate(savedInstanceState: Bundle?) {
    FixResources.fixDp2Px(mContext)
    if (isOpenAgainFromHome()) {
      "回到桌面二次打开PP，不需要走加载流程，直接关闭".logI()
      super.onCreate(savedInstanceState)
      finish()
      return
    }
    baseBinding = inflateBinding(layoutInflater)
    baseBinding.root.post { FixResources.fixDp2Px(mContext) }
    this.onCreateBefore()
    this.initStatus()
    super.onCreate(savedInstanceState)
    setContentView(baseBinding.root)
    FixResources.fixDp2Px(mContext)
    baseBinding.baseStatusView.visibleGone(showHolderStatusView())
    //异步加载布局，可以实现快速打开页面
    mJobLoading = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch(context = Dispatchers.Main + CoroutineExceptionHandler { _, e -> e.logE() }) {
      withContext(Dispatchers.IO) { initBinding() }.let {
        baseBinding.root.addView(viewBinding.root, ViewGroup.LayoutParams(-1, -1))
        initView()
      }
    }
    //onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
    //  override fun handleOnBackPressed() {
    //    onBackPressed()
    //  }
    //})
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="修改旋转屏幕后语种和字体大小变化问题">
  override fun onConfigurationChanged(newConfig: Configuration) {
    val locale = LanguageUtils.getAppliedLanguage() ?: Locale.CHINA
    setAcLocale(locale)
    super.onConfigurationChanged(newConfig)
    AutoSize.autoConvertDensityOfGlobal(this)
  }

  private fun setAcLocale(locale: Locale) {
    Locale.setDefault(locale)
    val configuration = Configuration()
    configuration.locale = locale
    baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)
    resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="由于要读取泛型，所以必须要放到泛型类下面调用，不能放到协程中">
  private fun initBinding() {
    _binding = ViewBindingUtil.inflateWithGeneric(this, layoutInflater)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="可重写的方法">

  //onCreateBefore之前需要处理的重写这个
  protected open fun onCreateBefore() {}

  //状态栏处理(默认白底，黑字)
  protected open fun initStatus() {
    immersionBar {
      keyboardEnable(needAdjustResizeAndNoFitsSystemWindows())
      statusBarDarkFont(true)
      statusBarView(baseBinding.baseStatusView)
    }
  }

  //是否显示默认状态栏占位
  protected open fun showHolderStatusView() = true

  //需要使用adjustResize但是又没有使用fitsSystemWindows=true,则需要返回true
  protected open fun needAdjustResizeAndNoFitsSystemWindows() = false

  //专门处理从桌面重新打开APP的BUG
  open fun isOpenAgainFromHome(): Boolean = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="释放异步耗时">
  override fun finish() {
    super.finish()
    mJobLoading?.cancel()
  }

  override fun onDestroy() {
    super.onDestroy()
    mJobLoading?.cancel()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="判断View是否加载">
  fun hasLoadedXML() = _binding != null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类必须重新的方法">
  //执行初始化
  protected abstract fun initView()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="触摸传递">
  //解决Android Q内存泄漏，如果重写记得把这个逻辑抄下去
  @Deprecated("Deprecated in Java")
  override fun onBackPressed() {
    val childDeal = (getCurrentFragment() as? BaseBindFragment<*>)?.onBackPress() ?: false
    if (childDeal) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //Android Q的bug https://blog.csdn.net/oLengYueZa/article/details/109207492
      finishAfterTransition()
    } else {
      super.onBackPressed()
    }
  }

  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    val childDeal = (getCurrentFragment() as? BaseBindFragment<*>)?.dispatchChildTouchEvent(ev) ?: false
    if (childDeal) return true
    return super.dispatchTouchEvent(ev)
  }

  open fun getCurrentFragment(): Fragment? {
    return supportFragmentManager.fragments.firstOrNull { f -> f.isAdded && f.isVisible && f.isResumed }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="是否点击到View的外面了">
  // Return whether touch the view.
  protected fun isTouchViewOut(v: View, event: MotionEvent): Boolean {
    val l = intArrayOf(0, 0)
    v.getLocationOnScreen(l)
    val left = l[0]
    val top = l[1]
    val bottom = top + v.height
    val right = left + v.width
    return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
  }
  //</editor-fold>
}