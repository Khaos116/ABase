package cc.ab.base.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import cc.ab.base.ext.logI
import kotlinx.coroutines.*

/**
 * @Description 由于进行了异步XML加载，所以如果重写了onResume、onPause等生命周期方法，需要谨慎使用viewBinding
 * @Author：CASE
 * @Date：2021/3/15
 * @Time：18:28
 */
abstract class BaseBindFragment<T : ViewBinding> : Fragment() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  protected var _binding: T? = null//由于是异步加载，所以如果重写生命周期用到View先判断它是否为空
  protected val viewBinding: T get() = _binding!!
  protected var mRootFrameLayout: FrameLayout? = null

  //是否已经懒加载
  private var isLoaded = false

  //是否处于OnResume
  private var isOnResume = false

  //页面基础信息
  protected lateinit var mContext: Activity
  protected lateinit var mActivity: Activity

  //XML加载
  private var mJobLoading: Job? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="上下文">
  override fun onAttach(context: Context) {
    super.onAttach(context)
    mContext = context as Activity
    mActivity = context
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="创建View和销毁">
  override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? = FrameLayout(mContext).also { mRootFrameLayout = it }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mRootFrameLayout?.removeAllViews()
    mJobLoading = GlobalScope.launch(context = Dispatchers.Main + CoroutineExceptionHandler { _, _ -> }) {
      withContext(Dispatchers.IO) { loadViewBinding(LayoutInflater.from(mContext), mRootFrameLayout) }.let { b ->
        _binding = b
        mRootFrameLayout?.addView(b.root, ViewGroup.LayoutParams(-1, -1))
        checkFirstLoad()
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="懒加载核心">
  override fun onResume() {
    super.onResume()
    isOnResume = true
    checkFirstLoad()
  }

  override fun onPause() {
    super.onPause()
    isOnResume = false
  }

  //检查是否需要进行懒加载
  private fun checkFirstLoad() {
    if (!isLoaded && isOnResume && !isHidden && _binding != null) {
      isLoaded = true
      lazyInit()
      "Fragment:懒加载：${this.javaClass.simpleName}${this.hashCode()}".logI()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="判断View是否加载">
  fun hasLoadedXML() = _binding != null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="UI销毁-重置懒加载">
  override fun onDestroyView() {
    super.onDestroyView()
    mJobLoading?.cancel()//释放异步耗时
    _binding = null
    isLoaded = false
    "Fragment:UI销毁：${this.javaClass.simpleName}${this.hashCode()}".logI()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类需要重新的方法">
  //获取XML
  protected abstract fun loadViewBinding(inflater: LayoutInflater, container: ViewGroup?): T

  //懒加载初始化
  protected abstract fun lazyInit()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类公共方法">
  open fun scroll2Top() {}
  //</editor-fold>
}