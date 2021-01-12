package cc.abase.demo.component.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.view.ViewGroup
import cc.ab.base.ext.mContext
import cc.ab.base.ext.toast
import cc.ab.base.utils.PermissionUtils
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.component.main.MainActivity
import cc.abase.demo.rxhttp.repository.UserRepository
import cc.abase.demo.utils.MMkvUtils
import com.airbnb.lottie.*
import com.blankj.utilcode.util.TimeUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.permissions.*
import kotlinx.android.synthetic.main.activity_splash.splashRoot

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/8 10:03
 */
class SplashActivity : CommActivity() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //一个小时变一张图
  private val randomImg = TimeUtils.millis2String(System.currentTimeMillis()).split(" ")[1].split(":")[0].toInt()

  //是否有SD卡读写权限
  private var hasSDPermission = false

  //动画是否结束
  private var animIsFinished = false

  //是否需要关闭页面
  private var hasFinish = false

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

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.activity_splash
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  private var isFirstSize = true
  override fun initView() {
    hasFinish = checkReOpenHome()
    if (hasFinish) return
    //页面无缝过渡后重置背景，不然会导致页面显示出现问题。主要解决由于window背景设置后的一些问题
    window.setBackgroundDrawable(null)
    splashRoot.viewTreeObserver.addOnGlobalLayoutListener {
      if (splashRoot.height > 0 && isFirstSize) {
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
      splashRoot.addView(lav, ViewGroup.LayoutParams(-1, -1))
      mLottieAnimationView?.playAnimation()
    }
    //请求SD卡权限
    hasSDPermission = PermissionUtils.hasSDPermission()
    if (!hasSDPermission) {
      XXPermissions.with(this)
          .permission(Permission.MANAGE_EXTERNAL_STORAGE)
          .request(object : OnPermissionCallback {
            override fun onGranted(granted: MutableList<String>, all: Boolean) {
              if (PermissionUtils.hasSDPermission()) {
                hasSDPermission = true
                goNextPage()
              } else {
                mContext.toast("没有SD卡权限,不能使用APP")
                finish()
              }
            }

            override fun onDenied(denied: MutableList<String>, quick: Boolean) {
              mContext.toast("没有SD卡权限,不能使用APP")
              finish()
            }
          })
    } else {
      goNextPage()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun initData() {
    if (hasFinish) return
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
      UserRepository.isLogin() -> MainActivity.startActivity(mContext)
      //没有其他需要，进入主页
      else -> LoginActivity.startActivity(mContext)
    }
    finish()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="生命周期">
  //禁止返回
  override fun onBackPressed() {}

  override fun finish() {
    mLottieAnimationView?.pauseAnimation()
    mLottieAnimationView?.cancelAnimation()
    mLottieAnimationView?.clearAnimation()
    super.finish()
  }

  override fun onDestroy() {
    super.onDestroy()
    mLottieAnimationView?.pauseAnimation()
    mLottieAnimationView?.cancelAnimation()
    mLottieAnimationView?.clearAnimation()
  }
  //</editor-fold>
}