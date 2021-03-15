package cc.ab.base.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import cc.ab.base.ext.dp2px

/**
 * @Description
 * @Author：CASE
 * @Date：2021/3/15
 * @Time：18:40
 */
abstract class BaseBindFragmentDialog<T : ViewBinding> : DialogFragment() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  private var _binding: T? = null
  protected val viewBinding: T get() = _binding!!

  //弹窗布局位置相关
  var mWidth = LayoutParams.WRAP_CONTENT
  var mHeight = LayoutParams.WRAP_CONTENT
  var mGravity = Gravity.CENTER
  var mOffsetX = 0
  var mOffsetY = 0

  //动画
  var mAnimation: Int? = null

  //点击外部是否可以关闭
  var touchOutside: Boolean = true

  //输入法状态
  var mSoftInputMode: Int = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN

  //是否降级背景，例如图片预览的时候不可以降级（设置Activity的透明度）
  var lowerBackground = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="创建和销毁View">
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    setStyle()
    _binding = loadViewBinding(inflater)
    return viewBinding.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="弹窗样式">
  //设置弹窗样式
  private fun setStyle() {
    //无标题
    dialog?.requestWindowFeature(DialogFragment.STYLE_NO_TITLE)
    //外部是否可以点击
    dialog?.setCanceledOnTouchOutside(touchOutside)
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
  protected abstract fun loadViewBinding(inflater: LayoutInflater): T

  //懒加载初始化
  protected abstract fun initView()
  //</editor-fold>
}