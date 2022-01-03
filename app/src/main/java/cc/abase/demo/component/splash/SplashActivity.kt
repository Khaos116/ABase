package cc.abase.demo.component.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
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
@SuppressLint("CustomSplashScreen")
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

    //是否结束权限检查(好像现在上架的APP不允许强制给予权限才能使用，加上目前APP基本使用私有目录存储，所以只做权限请求，不做强制，需要使用的时候才进行强制)
    private var permissionFinished = false

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

    //<editor-fold defaultstate="collapsed" desc="由于是异步加载XML，所以如果已经打开过APP就不能让程序继续走onCreate">
    override fun isOpenAgainFromHome(): Boolean {
        return checkReOpenHome()
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="初始化View">
    private var isFirstSize = true
    override fun initView() {
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

    //<editor-fold defaultstate="collapsed" desc="View尺寸拿到之后获取权限和添加动画+检查权限">
    private fun initAfterSize() {
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
            mJob = launchError { withContext(Dispatchers.IO) { delay(1000) }.let { mLottieAnimationView?.playAnimation() } }
        }
        checkSDPermission()
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="SD卡权限处理">
    private fun checkSDPermission() {
        //请求SD卡权限
        val hasSDPermission = XXPermissions.isGranted(mContext, Permission.MANAGE_EXTERNAL_STORAGE)
        if (!hasSDPermission) {
            XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                        permissionFinished = true
                        goNextPage()
                    }

                    override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                        permissionFinished = true
                        goNextPage()
                    }
                })
        } else {
            permissionFinished = true
            goNextPage()
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="如果程序在后台，点击APP图片，不再重新走启动页流程">
    //https://www.cnblogs.com/xqz0618/p/thistaskroot.html
    private fun checkReOpenHome(): Boolean {
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        return (!this.isTaskRoot && intent != null // 判断当前activity是不是所在任务栈的根
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && Intent.ACTION_MAIN == intent.action)
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="进入下个页面">
    //打开下个页面
    private fun goNextPage() {
        if (!animIsFinished) return
        if (!permissionFinished) return
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