package cc.abase.demo.component.comm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import cc.ab.base.ui.fragment.BaseBindFragment
import cc.abase.demo.databinding.ActivityFragmentParentBinding
import com.blankj.utilcode.util.ActivityUtils

/**
 * @Description fragment父容器，解决每次都要注册Activity的问题，需要处理关闭页面时判断fragment的问题(会重复打开多个，需要处理快速点击；需要fragment处理状态栏)
 * @Author：Khaos
 * @Date：2021-07-22
 * @Time：12:36
 */
class FragmentParentActivity : CommBindActivity<ActivityFragmentParentBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    private const val INTENT_KEY_CLASS = "INTENT_KEY_CLASS"
    private var lastOpenTime: Long = 0

    //bundle为Fragment内部需要接受的参数信息
    fun startFragment(content: Context? = null, clazz: Class<out Fragment>, bundle: Bundle = Bundle()) {
      (content ?: ActivityUtils.getTopActivity())?.let { ac ->
        if (System.currentTimeMillis() - lastOpenTime < 500) return@let //防止太快跳转
        lastOpenTime = System.currentTimeMillis()
        ac.startActivity(Intent(ac, FragmentParentActivity::class.java).putExtras(bundle.apply {
          putSerializable(INTENT_KEY_CLASS, clazz)
        }))
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //内嵌Fragment的名字(全路径)，可以根据他来关闭Activity
  var mFragmentName: String = ""
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  @Suppress("UNCHECKED_CAST")
  override fun initView() {
    (intent.getSerializableExtra(INTENT_KEY_CLASS) as? Class<out Fragment>)?.let { c ->
      mFragmentName = c.name ?: ""
      supportFragmentManager.beginTransaction().replace(viewBinding.container.id, c, intent.extras).commitNowAllowingStateLoss()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="获取fragment">
  fun getCurrentFragment(): Fragment? {
    return supportFragmentManager.fragments.firstOrNull()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="返回键单独处理">
  override fun onBackPressed() {
    val fragment = supportFragmentManager.fragments.firstOrNull()
    if (fragment is BaseBindFragment<*> && fragment.onBackPress()) return
    super.onBackPressed()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="交给子类处理点击事件">
  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    val childDeal = (getCurrentFragment() as? CommBindFragment<*>)?.dispatchTouchEvent(ev) ?: false
    if (childDeal) return true
    return super.dispatchTouchEvent(ev)
  }
  //</editor-fold>
}