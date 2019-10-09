package cc.abase.demo.component.main

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log
import androidx.annotation.IntRange
import androidx.lifecycle.Observer
import cc.ab.base.ext.getColorRes
import cc.ab.base.ext.mContext
import cc.ab.base.ext.toast
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.component.comm.CommFragment
import cc.abase.demo.constants.EventKeys
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.FragmentUtils
import com.blankj.utilcode.util.NetworkUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : CommActivity() {
  //页面
  private lateinit var homeFragment: CommFragment
  private lateinit var dynFragment: CommFragment
  private lateinit var mineFragment: CommFragment
  //当前页面
  private var currentFragment: CommFragment? = null
  //子列表合集，方便外部调用选中那个
  private var fragmentList = mutableListOf<CommFragment>()

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, MainActivity::class.java)
      context.startActivity(intent)
    }
  }

  override fun layoutResId() = R.layout.activity_main

  override fun initView() {
    //初始化
    homeFragment = HomeFragment.newInstance()
    dynFragment = DynFragment.newInstance()
    mineFragment = MineFragment.newInstance()
    //添加
    fragmentList = mutableListOf(homeFragment, dynFragment, mineFragment)
    //设置选中
    selectFragment(0)
    //切换
    mainNavigation.setOnNavigationItemSelectedListener {
      when (it.itemId) {
        R.id.menu_main_home -> selectFragment(0)
        R.id.menu_main_dyn -> selectFragment(1)
        R.id.menu_main_mine -> selectFragment(2)
      }
      true//返回true让其默认选中点击的选项
    }
  }

  override fun initData() {
    if (NetworkUtils.isWifiConnected()) {
      (applicationContext.getSystemService(
          Context.WIFI_SERVICE
      ) as? WifiManager)?.connectionInfo?.let {
        Log.e("CASE", "wifiInfo=${it}")
      }
    }
    LiveEventBus.get(EventKeys.WEB_URL, String::class.java).observe(this,
      Observer<String> { Log.e("CASE", "Web打开的url=${it}") })
  }

  //设置选中的fragment
  private fun selectFragment(@IntRange(from = 0, to = 2) index: Int) {
    //需要显示的fragment
    val fragment = fragmentList[index]
    //和当前选中的一样，则不再处理
    if (currentFragment == fragment) return
    //先关闭之前显示的
    currentFragment?.let { FragmentUtils.hide(it) }
    //设置现在需要显示的
    currentFragment = fragment
    if (!fragment.isAdded) { //没有添加，则添加并显示
      val tag = fragment::class.java.simpleName
      FragmentUtils.add(
          supportFragmentManager, fragment, R.id.mainContainer, tag, false
      )
    } else { //添加了就直接显示
      FragmentUtils.show(fragment)
    }
    //修改状态栏颜色
    immersionBar {
      mStatusView?.setBackgroundColor(
          mContext.getColorRes(if (index == 1) R.color.transparent else R.color.colorAccent)
      )
      statusBarDarkFont(index != 0)
      statusBarView(mStatusView)
    }
  }

  //外部调用选中哪一个tab
  fun setSelectIndex(@IntRange(from = 0, to = 2) index: Int) {
    val selectId = when (index) {
      1 -> R.id.menu_main_dyn
      2 -> R.id.menu_main_mine
      else -> R.id.menu_main_home
    }
    if (mainNavigation.selectedItemId != selectId) mainNavigation.selectedItemId = selectId
  }

  private var touchTime = 0L
  private val waitTime = 2000L
  override fun onBackPressed() {
    val currentTime = System.currentTimeMillis()
    if (currentTime - touchTime >= waitTime) {
      //让Toast的显示时间和等待时间相同
      toast(R.string.double_exit)
      touchTime = currentTime
    } else {
      AppUtils.exitApp()
//      super.onBackPressed()
    }
  }
}
