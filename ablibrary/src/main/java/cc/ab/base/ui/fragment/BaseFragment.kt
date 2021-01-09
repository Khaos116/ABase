package cc.ab.base.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import cc.ab.base.ext.logI
import cc.ab.base.ext.removeParent
import cc.ab.base.utils.CleanLeakUtils

/**
 * Author:case
 * Date:2020/8/11
 * Time:18:01
 */
abstract class BaseFragment : Fragment() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //是否已经懒加载
  private var isLoaded = false

  //页面基础信息
  lateinit var mContext: Activity
  lateinit var mActivity: Activity
  protected var mRootView: FrameLayout? = null

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="上下文">
  override fun onAttach(context: Context) {
    super.onAttach(context)
    mContext = context as Activity
    mActivity = context
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="创建View(重用)">
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    //第一次的时候加载xml
    if (contentXmlId > 0 && mRootView == null) {
      val contentView = inflater.inflate(contentXmlId, null)
      if (contentView is FrameLayout) {
        contentView.layoutParams = ViewGroup.LayoutParams(-1, -1)
        mRootView = contentView
      } else {
        mRootView = FrameLayout(mContext)
        mRootView?.layoutParams = ViewGroup.LayoutParams(-1, -1)
        mRootView?.addView(contentView, ViewGroup.LayoutParams(-1, -1))
      }
    } else {
      //防止重新create时还存在
      mRootView?.removeParent()
    }
    return mRootView
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
    //isLoaded = false
    "Fragment:UI销毁：${this.javaClass.simpleName}${this.hashCode()}".logI()
  }

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="页面销毁释放输入法">
  override fun onDestroy() {
    CleanLeakUtils.fixInputMethodManagerLeak(mActivity)
    "Fragment完全销毁，释放输入法：${this.javaClass.simpleName}${this.hashCode()}".logI()
    super.onDestroy()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类需要重新的方法">
  //xml布局
  protected abstract val contentXmlId: Int

  //懒加载初始化
  protected abstract fun lazyInit()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类公共方法">
  open fun scroll2Top() {}
  //</editor-fold>
}