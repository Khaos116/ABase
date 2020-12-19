package cc.ab.base.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import cc.ab.base.ext.*
import cc.ab.base.utils.CleanLeakUtils
import com.airbnb.mvrx.MvRxView
import com.airbnb.mvrx.MvRxViewId
import com.gyf.immersionbar.ktx.immersionBar
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle
import com.trello.rxlifecycle3.LifecycleProvider
import kotlinx.android.synthetic.main.base_activity.baseLLRoot
import kotlinx.android.synthetic.main.base_activity.baseStatusView

/**
 * Description:
 * @author: CASE
 * @date: 2019/9/20 16:37
 */
abstract class BaseActivity : AppCompatActivity(), MvRxView {
  //MvRxView
  private val mvrxViewIdProperty = MvRxViewId()
  final override val mvrxViewId: String by mvrxViewIdProperty
  //状态栏填充的view,当设置fillStatus返回为true时才生效
  protected var mStatusView: View? = null
  //防止内存泄漏
  lateinit var lifecycleProvider: LifecycleProvider<Lifecycle.Event>

  override fun onCreate(savedInstanceState: Bundle?) {
    lifecycleProvider = AndroidLifecycle.createLifecycleProvider(this)
    mvrxViewIdProperty.restoreFrom(savedInstanceState)
    this.onCreateBefore()
    this.initStatus()
    super.onCreate(savedInstanceState)
    if (fillStatus()) {
      setContentView(cc.ab.base.R.layout.base_activity)
      mStatusView = baseStatusView
      mStatusView?.layoutParams = LinearLayout.LayoutParams(-1, mStatusBarHeight)
      //不需要填充白色view状态栏
      if (!fillStatus()) baseLLRoot.removeAllViews()
      if (layoutResId() > 0) mContext.inflate(layoutResId(), baseLLRoot, true)
    } else if (layoutResId() > 0) {
      setContentView(layoutResId())
    }
    initView()
    initData()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    mvrxViewIdProperty.saveTo(outState)
  }

  protected open fun onCreateBefore() {}

  //状态栏处理(默认白底，黑字)
  protected open fun initStatus() {
    immersionBar {
      statusBarDarkFont(true)
      mStatusView?.let { statusBarView(it) }
    }
  }

  //是否需要默认填充状态栏,默认填充为白色view
  protected open fun fillStatus(): Boolean {
    return true
  }

  override fun invalidate() {
  }

  override fun onDestroy() {
    CleanLeakUtils.instance.fixInputMethodManagerLeak(this)
    super.onDestroy()
  }

  //-----------------------需要重写-----------------------//
  //xml布局
  protected abstract fun layoutResId(): Int

  //初始化View
  protected abstract fun initView()

  //初始化数据
  protected abstract fun initData()
}