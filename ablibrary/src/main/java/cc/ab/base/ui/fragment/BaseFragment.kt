package cc.ab.base.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import cc.ab.base.ext.removeParent
import com.airbnb.mvrx.BaseMvRxFragment

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/24 11:24
 */
abstract class BaseFragment : BaseMvRxFragment() {
  //懒加载相关
  private var isFragmentVisible = true
  private var isPrepared = false
  private var isFirst = true
  private var isInViewPager = false
  //页面基础信息
  lateinit var mContext: Activity
  protected var rootView: FrameLayout? = null

  override fun onAttach(context: Context) {
    super.onAttach(context)
    mContext = context as Activity
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    initBeforeCreateView(savedInstanceState)
    //第一次的时候加载xml
    if (contentLayout > 0 && rootView == null) {
      val contentView = inflater.inflate(contentLayout, null)
      if (contentView is FrameLayout) {
        contentView.layoutParams = ViewGroup.LayoutParams(-1, -1)
        rootView = contentView
      } else {
        rootView = FrameLayout(mContext)
        rootView?.layoutParams = ViewGroup.LayoutParams(-1, -1)
        rootView?.addView(contentView, ViewGroup.LayoutParams(-1, -1))
      }
    } else {
      //防止重新create时还存在
      rootView?.removeParent()
    }
    return rootView
  }

  //处理懒加载
  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    initView(view)
    isPrepared = true
    lazyLoad()
  }

  override fun setUserVisibleHint(isVisibleToUser: Boolean) {
    super.setUserVisibleHint(isVisibleToUser)
    isFragmentVisible = isVisibleToUser
    isInViewPager = true
    lazyLoad()
  }

  //懒加载
  private fun lazyLoad() {
    if (!isInViewPager) {
      isFirst = false
      initData()
      return
    }
    if (!isPrepared || !isFragmentVisible || !isFirst) {
      return
    }
    isFirst = false
    initData()
  }

  //初始化前的处理
  protected open fun initBeforeCreateView(savedInstanceState: Bundle?) {}

  //-----------------------需要重写-----------------------//
  //xml布局
  protected abstract val contentLayout: Int

  //初始化View
  protected abstract fun initView(root: View?)

  //初始化数据
  protected abstract fun initData()
}