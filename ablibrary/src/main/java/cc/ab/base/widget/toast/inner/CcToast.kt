package cc.ab.base.widget.toast.inner

import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.*
import cc.ab.base.R
import cc.ab.base.widget.toast.DURATION_SHORT
import cc.ab.base.widget.toast.ToastDuration

/**
 *description: 自定义的Toast.
 *@date 2018/12/18 10:43.
 *@author: YangYang.
 */
open class CcToast(var mContext: Context) : IToast, Cloneable {

  private var contentView: View
  private var animation = android.R.style.Animation_Toast
  private var gravity = Gravity.BOTTOM or Gravity.CENTER
  private var xOffset: Int = 0
  private var yOffset: Int = 0
  private var width = WindowManager.LayoutParams.WRAP_CONTENT
  private var height = WindowManager.LayoutParams.WRAP_CONTENT
  private var priority: Int = 0//优先级
  private var timestamp: Long = 0//时间戳
  @ToastDuration
  private var duration = DURATION_SHORT
  private var isShowing: Boolean = false//TN标记为正在展示

  /**
   * @param mContext 建议使用Activity。如果使用AppContext则当通知权限被禁用且TYPE_TOAST被WindowManager.addView()抛出异常时，无法正常显示弹窗。
   * 在API25+的部分手机上TYPE_TOAST被WindowManager.addView()时会抛出异常
   */

  init {
    val layoutInflater =
      mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    this.contentView = layoutInflater.inflate(R.layout.base_layout_toast, null)
  }

  open fun getWMParams(): WindowManager.LayoutParams {
    val lp = WindowManager.LayoutParams()
    lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    lp.format = PixelFormat.TRANSLUCENT
    if (Build.VERSION.SDK_INT < 26) {
      lp.type = WindowManager.LayoutParams.TYPE_TOAST
    } else {
      lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    }
    lp.height = this.height
    lp.width = this.width
    lp.windowAnimations = this.animation
    lp.gravity = this.gravity
    lp.x = this.xOffset
    lp.y = this.yOffset
    return lp
  }

  open fun getWMManager(): WindowManager? {
    return mContext.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
  }

  //展示Toast
  override fun show() {
    CcTN.instance()
        .add(this)
  }

  /**
   * 取消Toast,会清除队列中所有Toast任务
   * 因为TN中使用的是[this.clone]，外部没有Toast队列中单个任务的引用，所以外部无法单独取消一个Toast任务
   */
  override fun cancel() {
    CcTN.instance()
        .cancelAll()
  }

  fun getContext(): Context? {
    return this.mContext
  }

  override fun setView(mView: View): CcToast {
    this.contentView = mView
    return this
  }

  override fun getView(): View {
    return this.contentView
  }

  override fun setDuration(@ToastDuration duration: Int): CcToast {
    this.duration = duration
    return this
  }

  fun getDuration(): Int {
    return this.duration
  }

  override fun setAnimation(animation: Int): CcToast {
    this.animation = animation
    return this
  }

  override fun setGravity(
    gravity: Int,
    xOffset: Int,
    yOffset: Int
  ): CcToast {
    this.gravity = gravity
    this.xOffset = xOffset
    this.yOffset = yOffset
    return this
  }

  override fun setGravity(gravity: Int): CcToast {
    return setGravity(gravity, 0, 0)
  }

  fun getGravity(): Int {
    return gravity
  }

  fun getXOffset(): Int {
    return this.xOffset
  }

  fun getYOffset(): Int {
    return this.yOffset
  }

  fun getPriority(): Int {
    return priority
  }

  override fun setPriority(mPriority: Int): CcToast {
    this.priority = mPriority
    return this
  }

  fun getTimestamp(): Long {
    return timestamp
  }

  fun setTimestamp(mTimestamp: Long): CcToast {
    timestamp = mTimestamp
    return this
  }

  /**
   * Toast引用的contentView的可见性
   *
   * @return toast是否正在展示
   */
  fun isShowing(): Boolean {
    return isShowing && this.contentView.isShown
  }

  fun setShowing(isShowing: Boolean) {
    this.isShowing = isShowing
  }

  public override fun clone(): CcToast {
    var mToast: CcToast? = null
    try {
      mToast = super.clone() as CcToast
      mToast.mContext = this.mContext
      mToast.contentView = this.contentView
      mToast.duration = this.duration
      mToast.animation = this.animation
      mToast.gravity = this.gravity
      mToast.height = this.height
      mToast.width = this.width
      mToast.xOffset = this.xOffset
      mToast.yOffset = this.yOffset
      mToast.priority = this.priority
    } catch (mE: CloneNotSupportedException) {
      mE.printStackTrace()
    }
    return mToast!!
  }

  companion object {
    var Count4BadTokenException: Long = 0//记录AimyToast连续抛出token null is not valid异常的次数

    fun cancelAll() {
      CcTN.instance()
          .cancelAll()
    }

    fun cancelActivityToast(mActivity: Activity) {
      CcTN.instance()
          .cancelActivityToast(mActivity)
    }

    //当AimyToast连续出现token null is not valid异常时，不再推荐使用AimyToast
    fun isBadChoice(): Boolean {
      return Count4BadTokenException >= 5
    }
  }
}