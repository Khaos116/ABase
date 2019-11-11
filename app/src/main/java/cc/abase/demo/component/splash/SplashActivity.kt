package cc.abase.demo.component.splash

import android.Manifest
import android.util.Log
import cc.ab.base.ext.*
import cc.ab.base.utils.RxUtils
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.component.main.MainActivity
import cc.abase.demo.constants.ImageUrls
import cc.abase.demo.repository.UserRepository
import cc.abase.demo.utils.MMkvUtils
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.*
import com.gyf.immersionbar.ktx.immersionBar
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash.splashCover
import kotlinx.android.synthetic.main.activity_splash.splashTime
import me.panpf.sketch.Sketch
import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/8 10:03
 */
class SplashActivity : CommActivity() {
  //一个小时变一张图
  private val randomImg = TimeUtils.millis2String(System.currentTimeMillis())
      .split(" ")[1].split(":")[0].toInt()
  //倒计时3秒
  private val count = 3L
  //倒计时
  private var disposable: Disposable? = null
  //是否有SD卡读写权限
  private var hasSDPermission: Boolean? = null
  //倒计时是否结束
  private var countDownFinish: Boolean? = null

  //不设置状态栏填充，即显示全屏
  override fun fillStatus() = false

  //状态栏透明
  override fun initStatus() {
    immersionBar { statusBarDarkFont(false) }
  }

  override fun layoutResId() = R.layout.activity_splash

  override fun initView() {
    disposable?.dispose()
    //页面无缝过渡后重置背景，不然会导致页面显示出现问题。主要解决由于window背景设置后的一些问题
    window.setBackgroundDrawable(null)
    //有尺寸了才开始计时
    splashTime?.post {
      disposable = Flowable.intervalRange(0, count + 1, 0, 1, TimeUnit.SECONDS)
          .compose(RxUtils.instance.rx2SchedulerHelperF(lifecycleProvider))
          .doOnNext { splashTime.text = String.format("%d", max(1, count - it)) }
          .doOnComplete {
            Log.e("CASE", "倒计时结束")
            countDownFinish = true
            goNextPage()
          }
          .subscribe()
    }
  }

  override fun initData() {
    loadData()
    if (PermissionUtils.isGranted(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    ) {
      hasSDPermission = true
      goNextPage()
    } else {
      PermissionUtils.permission(PermissionConstants.STORAGE)
          .callback(object : PermissionUtils.SimpleCallback {
            //权限允许
            override fun onGranted() {
              Log.e("CASE", "有SD卡读写权限:${PermissionUtils.isGranted(PermissionConstants.STORAGE)}")
              hasSDPermission = true
              goNextPage()
            }

            //权限拒绝
            override fun onDenied() {
              mContext.toast("没有SD卡权限,不能使用APP")
              hasSDPermission = false
              goNextPage()
            }
          })
          .request()
    }
  }

  //打开下个页面
  private fun goNextPage() {
    if (hasSDPermission == null) return
    if (countDownFinish != true) return
    if (hasSDPermission == true) {
      when {
        //是否引导
        MMkvUtils.instance.getNeedGuide() -> GuideActivity.startActivity(mContext)
        //是否登录
        UserRepository.instance.isLogin() -> MainActivity.startActivity(mContext)
        //没有其他需要，进入主页
        else -> LoginActivity.startActivity(mContext)
      }
    }
    finish()
  }

  /**
   * 流程：默认显示的window背景
   * 1.有当前缓存图片，则直接展示
   * 2.没有缓存图片：
   *    A.有上次缓存图片，显示缓存并预加载新图
   *    B.没有任何缓存，显示默认并进行预加载
   */
  private fun loadData() {
    //图片地址
    val url = ImageUrls.instance.getRandomImgUrl(randomImg)
    //判断是否存在缓存图片
    val cacheFile = splashCover.getCacheFile(url)
    //缓存图片存在
    if (cacheFile?.exists() == true) {
      splashCover.load(cacheFile)
    } else {//加载网络图片
      splashCover.gone()
      Sketch.with(Utils.getApp())
          .download(url, null)
          .commit()
    }
  }

  override fun finish() {
    disposable?.dispose()
    super.finish()
  }
}