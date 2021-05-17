package cc.abase.demo.component.flexbox

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils.TruncateAt
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.databinding.ActivityFlexboxBinding
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.StringUtils
import com.google.android.flexbox.*

/**
 * Description:https://www.jianshu.com/p/a8edc312fa3d
 * @author: CASE
 * @date: 2020/3/12 17:53
 */
class FlexboxActivity : CommBindTitleActivity<ActivityFlexboxBinding>() {

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, FlexboxActivity::class.java)
      context.startActivity(intent)
    }
  }

  //标签
  private val tags = mutableListOf<String>(
      "标签",
      "一个标签",
      "随机标签",
      "这是标签",
      "这是随机标签",
      "一个随机标签",
      "这是一个标签",
      "这是一个很长很长的随机标签",
      "一个很长的随机标签"
  )

  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_flexbox))
    //主轴项目排列方向
    viewBinding.flexboxLayout.flexDirection = FlexDirection.ROW //主轴为水平方向，起点在左端
    //换行方式
    viewBinding.flexboxLayout.flexWrap = FlexWrap.WRAP //按正常方向换行
    //主轴上的对齐方式
    viewBinding.flexboxLayout.justifyContent = JustifyContent.FLEX_START //默认值，左对齐
    //添加view
    addView(viewBinding.flexboxLayout, tags)
  }

  private val paddingTop = SizeUtils.dp2px(5f)
  private val paddingStart = SizeUtils.dp2px(10f)
  private val dp1 = SizeUtils.dp2px(1f)
  private val margin = SizeUtils.dp2px(6f)
  private fun addView(
      parent: FlexboxLayout,
      tags: MutableList<String>
  ) {
    parent.setPadding(margin, margin, margin, margin)
    //清除所有view
    parent.removeAllViews()
    tags.forEach {
      val tv = TextView(parent.context)
      tv.gravity = Gravity.CENTER
      tv.text = it
      tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
      tv.setTextColor(Color.MAGENTA)
      tv.setPadding(paddingStart, paddingTop + dp1, paddingStart, paddingTop)
      tv.maxLines = 1
      tv.ellipsize = TruncateAt.END
      tv.setBackgroundResource(R.drawable.selector_stroke1_half_gray_light)
      tv.click { mContext.toast(tv.text.toString()) }
      val param = FlexboxLayout.LayoutParams(-2, -2)
      param.setMargins(margin, margin, margin, margin)
      parent.addView(tv, param)
    }
  }
}