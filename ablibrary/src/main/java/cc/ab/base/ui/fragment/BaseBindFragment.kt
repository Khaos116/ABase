package cc.ab.base.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import cc.ab.base.ext.logI

/**
 * @Description
 * @Author：CASE
 * @Date：2021/3/15
 * @Time：18:28
 */
abstract class BaseBindFragment<T : ViewBinding> : Fragment() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  private var _binding: T? = null
  protected val viewBinding: T get() = _binding!!
  protected var mRootFrameLayout: FrameLayout? = null

  //是否已经懒加载
  private var isLoaded = false

  //页面基础信息
  protected lateinit var mContext: Activity
  protected lateinit var mActivity: Activity
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="上下文">
  override fun onAttach(context: Context) {
    super.onAttach(context)
    mContext = context as Activity
    mActivity = context
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="创建View和销毁">
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    _binding = loadViewBinding(inflater)
    val root = viewBinding.root
    return if (root is FrameLayout) {
      mRootFrameLayout = root
      root
    } else FrameLayout(mContext).also {
      mRootFrameLayout = it
      it.addView(root, ViewGroup.LayoutParams(-1, -1))
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="懒加载核心">
  override fun onResume() {
    super.onResume()
    if (!isLoaded && !isHidden) {
      lazyInit()
      "Fragment:懒加载：${this.javaClass.simpleName}${this.hashCode()}".logI()
      isLoaded = true
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="UI销毁-重置懒加载">
  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
    isLoaded = false
    "Fragment:UI销毁：${this.javaClass.simpleName}${this.hashCode()}".logI()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类需要重新的方法">
  //获取XML
  protected abstract fun loadViewBinding(inflater: LayoutInflater): T

  //懒加载初始化
  protected abstract fun lazyInit()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类公共方法">
  open fun scroll2Top() {}
  //</editor-fold>
}