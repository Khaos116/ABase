package cc.ab.base.widget.toast.inner

import android.content.Context
import android.os.Build
import android.os.Handler
import android.view.*
import android.widget.Toast
import cc.ab.base.R
import cc.ab.base.widget.toast.*

/**
 *description: 系统toast的封装.
 *@date 2018/12/21 17:13.
 *@author: YangYang.
 */
class SystemToast(var mContext: Context) : IToast, Cloneable {

  private var mToast: Toast? = null
  private var priority: Int = 0//优先级
  private var contentView: View? = null
  private var animation = android.R.style.Animation_Toast
  private var gravity = Gravity.BOTTOM or Gravity.CENTER
  private var xOffset: Int = 0
  private var yOffset: Int = 0
  @ToastDuration
  private var duration = DURATION_SHORT

  init {
    val layoutInflater =
      mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    this.contentView = layoutInflater.inflate(R.layout.base_layout_toast, null)
  }

  //外部调用
  override fun show() {
    SystemTN.instance()
        .add(this)
  }

  fun showLong() {
    this.setDuration(DURATION_LONG)
        .show()
  }

  override fun cancel() {
    SystemTN.instance()
        .cancelAll()
  }

  //不允许被外部调用
  fun showInternal() {
    mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT)
    hookHandler(mToast)
    copyToToast(mToast)
    mToast!!.show()
  }

  fun cancelInternal() {
    if (mToast != null) {
      mToast!!.cancel()
      mToast = null
    }
  }

  private fun copyToToast(toast: Toast?) {
    if (toast == null) return
    if (contentView != null) {
      toast.view = this.contentView
    }
    toast.setGravity(this.gravity, xOffset, yOffset)
    setupToastAnim(toast, this.animation)
    if (duration == DURATION_SHORT) {
      toast.duration = Toast.LENGTH_SHORT
    } else if (duration == DURATION_LONG) {
      toast.duration = Toast.LENGTH_LONG
    }
  }

  override fun setView(mView: View): SystemToast {
    this.contentView = mView
    return this
  }

  override fun getView(): View {
    return this.contentView!!
  }

  override fun setDuration(@ToastDuration duration: Int): SystemToast {
    this.duration = duration
    return this
  }

  fun getDuration(): Int {
    return this.duration
  }

  override fun setAnimation(animation: Int): SystemToast {
    this.animation = animation
    return this
  }

  override fun setGravity(
    gravity: Int,
    xOffset: Int,
    yOffset: Int
  ): SystemToast {
    this.gravity = gravity
    this.xOffset = xOffset
    this.yOffset = yOffset
    return this
  }

  override fun setGravity(gravity: Int): SystemToast {
    setGravity(gravity, 0, 0)
    return this
  }

  fun getGravity(): Int {
    return this.gravity
  }

  fun getXOffset(): Int {
    return this.xOffset
  }

  fun getYOffset(): Int {
    return this.yOffset
  }

  fun getPriority(): Int {
    return this.priority
  }

  override fun setPriority(mPriority: Int): SystemToast {
    this.priority = mPriority
    return this
  }

  //捕获8.0之前Toast的BadTokenException，Google在Android 8.0的代码提交中修复了这个问题
  fun hookHandler(toast: Toast?) {
    if (Build.VERSION.SDK_INT >= 26) return
    try {
      val sField_TN = Toast::class.java.getDeclaredField("mTN")
      sField_TN.isAccessible = true
      val sField_TN_Handler = sField_TN.type.getDeclaredField("mHandler")
      sField_TN_Handler.isAccessible = true

      val tn = sField_TN.get(toast)
      val preHandler = sField_TN_Handler.get(tn) as Handler
      sField_TN_Handler.set(tn, SafelyHandlerWrapper(preHandler))
    } catch (e: Exception) {
      e.printStackTrace()
    }

  }

  //设置Toast动画
  fun setupToastAnim(
    toast: Toast,
    animRes: Int
  ) {
    try {
      val mTN = getField(toast, "mTN")
      if (mTN != null) {
        val mParams = getField(mTN, "mParams")
        if (mParams != null && mParams is WindowManager.LayoutParams) {
          val params = mParams as WindowManager.LayoutParams?
          params!!.windowAnimations = animRes
        }
      }
    } catch (e: Throwable) {
      e.printStackTrace()
    }

  }

  /**
   * 反射字段
   *
   * @param object    要反射的对象
   * @param fieldName 要反射的字段名称
   */
  @Throws(Exception::class)
  private fun getField(
    ob: Any,
    fieldName: String
  ): Any? {
    val field = ob.javaClass.getDeclaredField(fieldName)
    field.isAccessible = true
    return field.get(ob)
  }

  public override fun clone(): SystemToast {
    var mToast: SystemToast? = null
    try {
      mToast = super.clone() as SystemToast
      mToast.mContext = this.mContext
      mToast.contentView = this.contentView
      mToast.duration = this.duration
      mToast.animation = this.animation
      mToast.gravity = this.gravity
      mToast.xOffset = this.xOffset
      mToast.yOffset = this.yOffset
      mToast.priority = this.priority
    } catch (mE: CloneNotSupportedException) {
      mE.printStackTrace()
    }
    return mToast!!
  }

  companion object {
    fun cancelAll() {
      SystemTN.instance()
          .cancelAll()
    }
  }
}