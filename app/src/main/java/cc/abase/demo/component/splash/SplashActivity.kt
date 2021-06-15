package cc.abase.demo.component.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.view.ViewGroup
import cc.ab.base.ext.*
import cc.ab.base.utils.PermissionUtils
import cc.abase.demo.component.comm.CommBindActivity
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.component.main.MainActivity
import cc.abase.demo.config.UserManager
import cc.abase.demo.databinding.ActivitySplashBinding
import cc.abase.demo.utils.MMkvUtils
import com.airbnb.lottie.*
import com.blankj.utilcode.util.TimeUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.permissions.*
import kotlinx.coroutines.*

/**
 * Description:
 * @author: Khaos
 * @date: 2019/10/8 10:03
 */
class SplashActivity : CommBindActivity<ActivitySplashBinding>() {
  //<editor-fold defaultstate="collapsed" desc="测试静态模块和StartUp的启动顺序">
  companion object {
    val mSysTime = getSysTime()
    private fun getSysTime(): Long {
      "初始化完成后，第一个页面静态模块加载".logE()
      return System.currentTimeMillis()
    }
  }

  override fun onCreateBefore() {
    "初始化完成后，第一个非静态模块加载".logE()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //一个小时变一张图
  private val randomImg = TimeUtils.millis2String(System.currentTimeMillis()).split(" ")[1].split(":")[0].toInt()

  //动画是否结束
  private var animIsFinished = false

  //是否需要关闭页面
  private var hasFinish = false

  //延迟执行动画
  private var mJob: Job? = null

  //PAG动画
  private var mLottieAnimationView: LottieAnimationView? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="状态栏">
  //不设置状态栏填充，即显示全屏
  override fun fillStatus() = false

  //状态栏透明
  override fun initStatus() {
    immersionBar {
      statusBarDarkFont(false)
      fullScreen(true)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  private var isFirstSize = true
  override fun initView() {
    hasFinish = checkReOpenHome()
    if (hasFinish) return
    //页面无缝过渡后重置背景，不然会导致页面显示出现问题。主要解决由于window背景设置后的一些问题
    window.setBackgroundDrawable(null)
    viewBinding.root.viewTreeObserver.addOnGlobalLayoutListener {
      if (viewBinding.root.height > 0 && isFirstSize) {
        isFirstSize = false
        initAfterSize()
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="View尺寸拿到之后获取权限和添加动画">
  private fun initAfterSize() {
    if (hasFinish) return
    mLottieAnimationView = LottieAnimationView(mContext)
    mLottieAnimationView?.let { lav ->
      lav.setAnimation("welcome2021.json")
      lav.imageAssetsFolder = "images/"
      lav.setRenderMode(RenderMode.HARDWARE)
      lav.repeatCount = 0
      lav.repeatMode = LottieDrawable.RESTART
      lav.setOnClickListener { }
      lav.addAnimatorListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
          super.onAnimationEnd(animation)
          animIsFinished = true
          goNextPage()
        }
      })
      viewBinding.root.addView(lav, ViewGroup.LayoutParams(-1, -1))
      mJob = GlobalScope.launchError { withContext(Dispatchers.IO) { delay(1000) }.let { mLottieAnimationView?.playAnimation() } }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="SD卡权限处理">
  //是否有SD卡读写权限
  private var hasSDPermission = false
  private var mJobSetting: Job? = null
  private var hasGoSetting = false
  private fun checkSDPermission() {
    //请求SD卡权限
    hasSDPermission = XXPermissions.isGranted(mContext, Permission.MANAGE_EXTERNAL_STORAGE)
    if (hasSDPermission) {
      hasSDPermission = PermissionUtils.hasSDPermission()
      if (!hasSDPermission) {
        mJobSetting = GlobalScope.launchError {
          "必须要给予SD卡权限才能使用".toast()
          withContext(Dispatchers.IO) { delay(2000) }.let {
            XXPermissions.startPermissionActivity(mContext, Permission.MANAGE_EXTERNAL_STORAGE)
            hasGoSetting = true
          }
        }
        return
      }
    }
    if (!hasSDPermission) {
      XXPermissions.with(this)
        .permission(Permission.MANAGE_EXTERNAL_STORAGE)
        .request(object : OnPermissionCallback {
          override fun onGranted(permissions: MutableList<String>, all: Boolean) {
            hasSDPermission = true
            goNextPage()
          }

          override fun onDenied(permissions: MutableList<String>, never: Boolean) {
            if (never && mJobSetting == null) { //打开APP发现被永久拒绝或者点击了永久拒绝
              mJobSetting = GlobalScope.launchError {
                "必须要给予SD卡权限才能使用".toast()
                withContext(Dispatchers.IO) { delay(2000) }.let {
                  XXPermissions.startPermissionActivity(mContext, permissions)
                  hasGoSetting = true
                }
              }
            } else if (!never || (never && hasGoSetting)) { //正常拒绝直接关闭,或者从权限回来发现还是没给
              finish()
            }
          }
        })
    } else {
      goNextPage()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="如果程序在后台，点击APP图片，不再重新走启动页流程">
  //https://www.cnblogs.com/xqz0618/p/thistaskroot.html
  private fun checkReOpenHome(): Boolean {
    // 避免从桌面启动程序后，会重新实例化入口类的activity
    if (!this.isTaskRoot && intent != null // 判断当前activity是不是所在任务栈的根
      && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
      && Intent.ACTION_MAIN == intent.action
    ) {
      finish()
      return true
    }
    return false
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="进入下个页面">
  //打开下个页面
  private fun goNextPage() {
    if (!animIsFinished) return
    if (!hasSDPermission) return
    when {
      //是否引导
      MMkvUtils.getNeedGuide() -> GuideActivity.startActivity(mContext)
      //是否登录
      UserManager.isLogin() -> MainActivity.startActivity(mContext)
      //没有其他需要，进入主页
      else -> LoginActivity.startActivity(mContext)
    }
    finish()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="生命周期">
  override fun onResume() {
    super.onResume()
    checkSDPermission()
  }

  //禁止返回
  override fun onBackPressed() {}

  override fun finish() {
    mLottieAnimationView?.pauseAnimation()
    mLottieAnimationView?.cancelAnimation()
    mLottieAnimationView?.clearAnimation()
    mJob?.cancel()
    super.finish()
  }

  override fun onDestroy() {
    super.onDestroy()
    mLottieAnimationView?.pauseAnimation()
    mLottieAnimationView?.cancelAnimation()
    mLottieAnimationView?.clearAnimation()
    mJob?.cancel()
  }
  //</editor-fold>
}