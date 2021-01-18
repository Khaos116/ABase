package cc.abase.demo.component.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.IntRange
import androidx.lifecycle.Lifecycle
import cc.ab.base.ext.*
import cc.ab.base.widget.DragFloatView
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.component.comm.CommFragment
import cc.abase.demo.component.main.fragment.GankFragment
import cc.abase.demo.component.main.fragment.WanFragment
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.FragmentUtils
import kotlinx.android.synthetic.main.activity_main.mainContainer
import kotlinx.android.synthetic.main.activity_main.mainNavigation

class MainActivity : CommActivity() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, MainActivity::class.java)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //当前页面
  private var currentFragment: CommFragment? = null

  //子列表合集，方便外部调用选中那个
  private val fragmentList = mutableListOf<CommFragment>()
  private var floatView: DragFloatView? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.activity_main
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initView() {
    //添加子页面
    fragmentList.clear()
    fragmentList.add(WanFragment.newInstance())
    fragmentList.add(GankFragment.newInstance())
    fragmentList.add(MineFragment.newInstance())
    //设置选中
    selectFragment(0)
    setSelectIndex(0)
    //切换
    mainNavigation.setOnNavigationItemSelectedListener {
      when (it.itemId) {
        R.id.menu_main_home -> selectFragment(0)
        R.id.menu_main_dyn -> selectFragment(1)
        R.id.menu_main_mine -> selectFragment(2)
      }
      true //返回true让其默认选中点击的选项
    }
    //取消长按显示+过度绘制
    mainNavigation.findViewById<View>(R.id.menu_main_home).let {
      it.setOnLongClickListener { true }
      (it.parent?.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)
    }
    mainNavigation.findViewById<View>(R.id.menu_main_dyn).setOnLongClickListener { true }
    mainNavigation.findViewById<View>(R.id.menu_main_mine).setOnLongClickListener { true }
    //添加悬浮球
    floatView = DragFloatView(mContext)
    floatView?.click { "点击悬浮球".toast() }
    //随机位置
    val params = FrameLayout.LayoutParams(45.dp2Px(), 45.dp2Px())
    val h = (Math.random() * 3 + 1).toInt()
    val v = (Math.random() * 3 + 1).toInt()
    params.gravity = when (h % 3) {
      0 -> Gravity.START
      1 -> Gravity.CENTER_HORIZONTAL
      else -> Gravity.END
    } or when (v % 3) {
      0 -> Gravity.TOP
      1 -> Gravity.CENTER_VERTICAL
      else -> Gravity.BOTTOM
    }
    (mContentView as? FrameLayout)?.addView(floatView, params)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化数据">
  override fun initData() {
    //关闭其他所有页面
    ActivityUtils.finishOtherActivities(javaClass)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="设置选中的fragment">
  //设置选中的fragment
  private fun selectFragment(@IntRange(from = 0, to = 2) index: Int) {
    //需要显示的fragment
    val fragment = fragmentList[index]
    //和当前选中的一样，则不再处理
    if (currentFragment == fragment) return
    //先关闭之前显示的
    currentFragment?.let {
      if (it.isAdded) supportFragmentManager
          .beginTransaction()
          .setMaxLifecycle(it, Lifecycle.State.STARTED)
          .commitAllowingStateLoss() //触发Fragment的onPause
      FragmentUtils.hide(it)
    }
    //设置现在需要显示的
    currentFragment = fragment
    if (!fragment.isAdded) { //没有添加，则添加并显示
      val tag = fragment::class.java.simpleName
      FragmentUtils.add(supportFragmentManager, fragment, mainContainer.id, tag, false)
    } else { //添加了就直接显示
      FragmentUtils.show(fragment)
      if (fragment.isAdded) supportFragmentManager
          .beginTransaction()
          .setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
          .commitAllowingStateLoss() //触发Fragment的onResume
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用选中哪一个tab">
  //外部调用选中哪一个tab
  fun setSelectIndex(@IntRange(from = 0, to = 2) index: Int) {
    val selectId = when (index) {
      1 -> R.id.menu_main_dyn
      2 -> R.id.menu_main_mine
      else -> R.id.menu_main_home
    }
    mainNavigation?.post {
      if (mainNavigation.selectedItemId != selectId) mainNavigation.selectedItemId = selectId
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="按两次退出APP">
  private var touchTime = 0L
  private val waitTime = 2000L
  override fun onBackPressed() {
    val currentTime = System.currentTimeMillis()
    if (currentTime - touchTime >= waitTime) {
      //让Toast的显示时间和等待时间相同
      toast(R.string.double_exit)
      touchTime = currentTime
    } else {
      //AppUtils.exitApp()
      super.onBackPressed()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="生命周期">
  override fun finish() {
    super.finish()
    floatView?.release()
    floatView = null
  }

  override fun onDestroy() {
    floatView?.release()
    floatView = null
    super.onDestroy()
  }
  //</editor-fold>
}
