package cc.ab.base.ui.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import cc.ab.base.ext.dp2px
import cc.ab.base.ext.mDialogTimes
import com.blankj.utilcode.util.ActivityUtils

/**
 * @Description
 * @Author：CASE
 * @Date：2021/3/15
 * @Time：18:40
 */
abstract class BaseBindFragmentDialog<T : ViewBinding> : DialogFragment() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  private var _binding: T? = null
  val viewBinding: T get() = _binding!!

  //弹窗布局位置相关
  var mWidth = LayoutParams.WRAP_CONTENT
  var mHeight = LayoutParams.WRAP_CONTENT
  var mGravity = Gravity.CENTER
  var mOffsetX = 0
  var mOffsetY = 0

  //动画
  var mAnimation: Int? = null

  //点击外部是否可以关闭
  var canTouchOutside: Boolean = true

  //输入法状态
  var mSoftInputMode: Int = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN

  //是否降级背景，例如图片预览的时候不可以降级（设置Activity的透明度）
  var lowerBackground = false

  //监听显示和关闭
  var showCallback: (() -> Unit)? = null
  var dismissCallback: (() -> Unit)? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="创建和销毁View">
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Light_NoActionBar)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    setMyStyle()
    _binding = loadViewBinding(inflater, null)
    return viewBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initView()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="显示的回调">
  //防止同时弹出两个一样类型的dialog，如果需要弹2个类型一样的dialog但内容不同，请设置不同的tag
  override fun show(manager: FragmentManager, tag: String?) {
    val ac = ActivityUtils.getTopActivity()
    if (ac == null || ac.isFinishing || ac.isDestroyed) {
      return
    }
    //保存的key以 “页面名称 + 弹窗名字 + tag” 作为标识符
    val keyStr = String.format("%s_%s_%s", ac.javaClass.simpleName, this.javaClass.simpleName, tag ?: "")
    //防止相同页面500ms内重复弹出相同dialog
    ac.mDialogTimes.let { list ->
      var has = false
      //找到上次弹窗时间并更新
      list.firstOrNull { it.first == keyStr }?.let { p ->
        has = true
        if (System.currentTimeMillis() - p.second < 500) {
          return
        } else list[list.indexOf(p)] = Pair(keyStr, System.currentTimeMillis())
      }
      //第一次弹窗，添加弹窗时间
      if (!has) list.add(Pair(keyStr, System.currentTimeMillis()))
    }
    showCallback?.invoke()
    //super.show(manager, tag)
    setBooleanField("mDismissed", false)
    setBooleanField("mShownByMe", true)
    val ft = manager.beginTransaction()
    ft.add(this, tag)
    ft.commitAllowingStateLoss()
  }

  private fun setBooleanField(fieldName: String, value: Boolean) {
    try {
      val field = DialogFragment::class.java.getDeclaredField(fieldName)
      field.isAccessible = true
      field.set(this, value)
    } catch (e: NoSuchFieldException) {
      e.printStackTrace()
    } catch (e: IllegalAccessException) {
      e.printStackTrace()
    }
  }

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="关闭的回调">
  override fun onDismiss(dialog: DialogInterface) {
    dismissCallback?.invoke()
    super.onDismiss(dialog)
  }

  override fun onDestroy() {
    super.onDestroy()
    dismissCallback = null
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="弹窗样式">
  //设置弹窗样式
  private fun setMyStyle() {
    //无标题
    dialog?.requestWindowFeature(STYLE_NO_TITLE)
    //外部是否可以点击
    dialog?.setCanceledOnTouchOutside(canTouchOutside)
    //获取Window
    dialog?.window?.let { window ->
      // 透明背景
      window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      if (lowerBackground) window.setDimAmount(0F) // 去除 dialog 弹出的阴影
      //设置动画
      mAnimation?.also { anim -> window.setWindowAnimations(anim) }
      //设置宽高
      window.decorView.setPadding(0, 0, 0, 0)
      window.attributes?.let { wlp ->
        wlp.width = mWidth
        wlp.height = mHeight
        //设置对齐方式
        wlp.gravity = mGravity
        //设置偏移量
        wlp.x = dialog?.context?.dp2px(mOffsetX) ?: 0
        wlp.y = dialog?.context?.dp2px(mOffsetY) ?: 0
        wlp.softInputMode = mSoftInputMode
        window.attributes = wlp
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类需要重新的方法">
  //获取XML
  protected abstract fun loadViewBinding(inflater: LayoutInflater, container: ViewGroup?): T

  //懒加载初始化
  protected abstract fun initView()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用ViewBinding">
  fun getMyViewBinding(): T? = _binding
  //</editor-fold>
}