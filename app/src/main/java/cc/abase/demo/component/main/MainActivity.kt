package cc.abase.demo.component.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.lifecycle.Lifecycle
import cc.ab.base.ext.*
import cc.ab.base.ui.fragment.BaseBindFragment
import cc.ab.base.utils.ping.*
import cc.ab.base.widget.DragFloatView
import cc.ab.base.widget.livedata.MyObserver
import cc.abase.demo.BuildConfig
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindActivity
import cc.abase.demo.component.comm.CommBindFragment
import cc.abase.demo.component.main.fragment.*
import cc.abase.demo.config.AppLiveData
import cc.abase.demo.databinding.ActivityMainBinding
import cc.abase.demo.widget.dialog.commAlertDialog
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.*
import com.hjq.permissions.*

class MainActivity : CommBindActivity<ActivityMainBinding>() {
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
  private var currentFragment: CommBindFragment<*>? = null

  //子列表合集，方便外部调用选中那个
  private val fragmentList = mutableListOf<CommBindFragment<*>>()
  private var floatView: DragFloatView? = null

  //ping
  private var mSocketPing: PingBySocket = PingBySocket()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initView() {
    //添加子页面
    fragmentList.clear()
    fragmentList.add(WanFragment.newInstance())
    fragmentList.add(ReadhubFragment.newInstance())
    fragmentList.add(MineFragment.newInstance())
    //设置选中
    selectFragment(0)
    setSelectIndex(0)
    //切换
    viewBinding.mainNavigation.setOnItemSelectedListener {
      when (it.itemId) {
        R.id.menu_main_home -> selectFragment(0)
        R.id.menu_main_dyn -> selectFragment(1)
        R.id.menu_main_mine -> selectFragment(2)
      }
      true //返回true让其默认选中点击的选项
    }
    //取消长按显示+过度绘制
    viewBinding.mainNavigation.findViewById<View>(R.id.menu_main_home).setOnLongClickListener { true }
    viewBinding.mainNavigation.findViewById<View>(R.id.menu_main_dyn).setOnLongClickListener { true }
    viewBinding.mainNavigation.findViewById<View>(R.id.menu_main_mine).setOnLongClickListener { true }
    //添加悬浮球
    floatView = DragFloatView(mContext)
    floatView?.click { R.string.点击悬浮球.xmlToast() }
    //随机位置
    val params = FrameLayout.LayoutParams(45.dp2px(), 45.dp2px())
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
    if (BuildConfig.DEBUG && !XXPermissions.isGranted(mContext, Permission.NOTIFICATION_SERVICE)) {
      commAlertDialog(supportFragmentManager, cancelable = false, outside = false) {
        title = R.string.温馨提示.xmlToString()
        content = R.string.APP调试需要通知权限是否去打开.xmlToString()
        confirmText = R.string.去打开.xmlToString()
        confirmTextColor = cc.ab.base.R.color.style_Accent.xmlToColor()
        confirmCallback = { openNoticePermission() }
      }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //安卓Q版本以上需要ACCESS_BACKGROUND_LOCATION
      val permissions = mutableListOf(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_BACKGROUND_LOCATION)
      if (XXPermissions.isGranted(this, permissions)) {
        getLocation()
      } else {
        XXPermissions.with(mActivity)
          .permission(permissions)
          .request(object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, all: Boolean) {
              if (all) getLocation() else "获取到部分权限:${permissions.toString()}".logE()
            }

            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
              "被拒绝的权限:${permissions.toString()};never=$never".logE()
              permissions?.let { p ->
                if (!never && p.size == 1 && p.first() == Permission.ACCESS_BACKGROUND_LOCATION) {
                  getLocation()
                }
              }
            }
          })
      }
    } else { //安卓Q版本以前
      val permissions = mutableListOf(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION)
      if (XXPermissions.isGranted(this, permissions)) {
        getLocation()
      } else {
        ActivityUtils.getTopActivity()?.let { activity ->
          XXPermissions.with(activity)
            .permission(permissions)
            .request(object : OnPermissionCallback {
              override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                if (all) getLocation() else "获取到部分权限:${permissions.toString()}".logE()
              }

              override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                "被拒绝的权限:${permissions.toString()};never=$never".logE()
              }
            })
        }
      }
    }
    //关闭其他所有页面
    ActivityUtils.finishOtherActivities(javaClass)
    pingTest()
    //PinYinConstants.testTransTiny()//测试多音字
    val ipTv = TextView(mContext)
    ipTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f)
    ipTv.setTextColor(Color.BLUE)
    ipTv.gravity = Gravity.CENTER
    val padding = 3.dp2px()
    ipTv.setPadding(padding, padding, padding, padding)
    AppLiveData.ipLiveData.observe(this, MyObserver { bean ->
      bean?.let { b ->
        val sb = StringBuilder()
        sb.append(b.country).append("\n")
          .append(b.city).append("\n")
          .append(b.query)
        ipTv.text = sb.toString()
        ipTv.removeParent()
        floatView?.addView(ipTv, ViewGroup.LayoutParams(-1, -1))
      }
    })
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="测试ping">
  private fun pingTest() {
    //测试ping的速度
    if (NetworkUtils.isConnected()) {
      val sb1 = StringBuilder("\n")
      val sb2 = StringBuilder("\n")
      val host = "www.baidu.com"
      Ping.onAddress(host)
        .setTimeOutMillis(1 * TimeConstants.SEC)
        .setTimes(5)
        .doPing(object : Ping.PingListener {
          override fun onResult(pingResult: PingResult) {
            if (pingResult.isReachable) {
              sb1.append("Ping耗时=${String.format("%.0f ms", pingResult.getTimeTaken())}").append("\n")
            } else {
              sb1.append("Ping失败:${pingResult.error}").append("\n")
            }
          }

          override fun onFinished(pingStats: PingStats) {
            sb1.append("Ping的次数=${pingStats.noPings};丢包=${pingStats.packetsLost}").append("\n")
            sb1.append(
              "Ping完成${String.format("Min/Avg/Max Time: %.0f/%.0f/%.0f ms", pingStats.minTimeTaken, pingStats.averageTimeTaken, pingStats.maxTimeTaken)}"
            ).append("\n")
            sb1.toString().logE()
          }

          override fun onError(e: Exception) {
            "Ping异常:${e.message}".logE()
          }
        })
      mSocketPing.onAddress(host)
        .setTimeOutMillis(1 * TimeConstants.SEC)
        .setTimes(5)
        .doPing(object : PingBySocket.PingListener {
          override fun onResult(pingResult: PingResult) {
            if (pingResult.isReachable) {
              sb2.append("SocketPing耗时=${String.format("%.0f ms", pingResult.getTimeTaken())}").append("\n")
            } else {
              sb2.append("SocketPing失败:${pingResult.error}").append("\n")
            }
          }

          override fun onFinished(pingStats: PingStats) {
            sb2.append("SocketPing的次数=${pingStats.noPings};丢包=${pingStats.packetsLost}").append("\n")
            sb2.append(
              "SocketPing完成${String.format("Min/Avg/Max Time: %.0f/%.0f/%.0f ms", pingStats.minTimeTaken, pingStats.averageTimeTaken, pingStats.maxTimeTaken)}"
            ).append("\n")
            sb2.toString().logE()
          }

          override fun onError(e: Exception) {
            "SocketPing异常:${e.message}".logE()
          }
        })
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="获取到地理位置权限后的操作">
  private fun getLocation() {
    //获取到位置后才能获取到WIFI的BSSID
    "已获取位置权限,可以进行定位操作".logE()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="请求通知权限">
  private fun openNoticePermission() {
    XXPermissions.with(mContext)
      .permission(Permission.NOTIFICATION_SERVICE)
      .request(object : OnPermissionCallback {
        override fun onGranted(permissions: MutableList<String>, all: Boolean) {}

        override fun onDenied(permissions: MutableList<String>, never: Boolean) {}
      })
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
      FragmentUtils.add(supportFragmentManager, fragment, viewBinding.mainContainer.id, tag, false)
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
    viewBinding.mainNavigation.post {
      if (viewBinding.mainNavigation.selectedItemId != selectId) viewBinding.mainNavigation.selectedItemId = selectId
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="按两次退出APP">
  private var touchTime = 0L
  private val waitTime = 2000L

  @Deprecated("Deprecated in Java")
  override fun onBackPressed() {
    val childDeal = (getCurrentFragment() as? BaseBindFragment<*>)?.onBackPress() ?: false
    if (childDeal) return
    val currentTime = System.currentTimeMillis()
    if (currentTime - touchTime >= waitTime) {
      //让Toast的显示时间和等待时间相同
      toast(R.string.再按一次退出)
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
    mSocketPing.release()
    floatView?.release()
    floatView = null
  }

  override fun onDestroy() {
    mSocketPing.release()
    floatView?.release()
    floatView = null
    super.onDestroy()
  }
  //</editor-fold>
}
