package cc.ab.base.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import cc.ab.base.databinding.BaseActivityBinding
import cc.ab.base.ext.visibleGone
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.coroutines.*

/**
 * 参考：
 * 1.https://blog.csdn.net/choimroc/article/details/104756365
 * 2.https://blog.csdn.net/u010976213/article/details/104501830
 * @Description 使用ViewBinding的基类
 * @Author：CASE
 * @Date：2021/3/15
 * @Time：16:20
 */
abstract class BaseBindActivity<T : ViewBinding> : AppCompatActivity() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //如果要操作状态栏，则需要使用到
  protected lateinit var baseBinding: BaseActivityBinding

  //除状态栏意外的XML
  protected lateinit var viewBinding: T
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="创建">
  override fun onCreate(savedInstanceState: Bundle?) {
    this.onCreateBefore()
    super.onCreate(savedInstanceState)
    //异步加载布局，可以实现快速打开页面
    GlobalScope.launch(context = Dispatchers.Main + CoroutineExceptionHandler { _, _ -> }) {
      withContext(Dispatchers.IO) { BaseActivityBinding.inflate(layoutInflater) }.let { b ->
        baseBinding = b
        setContentView(baseBinding.root)
        baseBinding.baseStatusView.visibleGone(fillStatus())
        viewBinding = loadViewBinding(layoutInflater)
        baseBinding.root.addView(viewBinding.root, ViewGroup.LayoutParams(-1, -1))
        initStatus()
        initView()
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="可重写的方法">

  //onCreateBefore之前需要处理的重写这个
  protected open fun onCreateBefore() {}

  //状态栏处理(默认白底，黑字)
  protected open fun initStatus() {
    immersionBar {
      statusBarDarkFont(true)
      statusBarView(baseBinding.baseStatusView)
    }
  }

  //是否需要默认填充状态栏,默认填充为白色view
  protected open fun fillStatus() = true

  //解决Android Q内存泄漏，如果重写记得把这个逻辑抄下去
  override fun onBackPressed() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //Android Q的bug https://blog.csdn.net/oLengYueZa/article/details/109207492
      finishAfterTransition()
    } else {
      super.onBackPressed()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类必须重新的方法">
  //获取XML
  protected abstract fun loadViewBinding(inflater: LayoutInflater): T

  //执行初始化
  protected abstract fun initView()
  //</editor-fold>
}