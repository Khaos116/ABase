package cc.abase.demo.widget.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import cc.ab.base.ext.click
import cc.ab.base.ui.dialog.BaseFragmentDialog
import cc.abase.demo.R
import com.zyyoona7.wheel.WheelView
import kotlinx.android.synthetic.main.dialog_date_sel.view.*

/**
 * Description:
 * @author: CASE
 * @date: 2020/2/20 18:43
 */
class DateSelDialog : BaseFragmentDialog() {
  //选择的结果
  private var result: Triple<Int, Int, Int>? = null
  //回调
  var call: ((result: Triple<Int, Int, Int>) -> Unit)? = null

  override fun contentLayout() = R.layout.dialog_date_sel

  override fun initView(view: View, savedInstanceState: Bundle?) {
    view.dialogDateView.let {
      it.setTextSize(24f, true)
      it.setShowLabel(false);
      it.setTextBoundaryMargin(16f, true)
      it.setShowDivider(true)
      it.setDividerType((System.currentTimeMillis() % 2).toInt())
      it.setDividerColor(Color.parseColor("#9e9e9e"))
      it.monthWv.setIntegerNeedFormat("%02d")
      it.dayWv.setIntegerNeedFormat("%02d")
      it.yearWv.curvedArcDirection = WheelView.CURVED_ARC_DIRECTION_LEFT
      it.yearWv.curvedArcDirectionFactor = 0.65f
      it.dayWv.curvedArcDirection = WheelView.CURVED_ARC_DIRECTION_RIGHT
      it.dayWv.curvedArcDirectionFactor = 0.65f
      it.setOnDateSelectedListener { _, year, month, day, _ ->
        result = Triple(year, month, day)
      }
    }
    view.dialogDateCancel.click { dismissAllowingStateLoss() }
    view.dialogDateConfirm.click {
      dismissAllowingStateLoss()
      if (result == null) result = Triple(
          view.dialogDateView.selectedYear,
          view.dialogDateView.selectedMonth,
          view.dialogDateView.selectedDay
      )
      result?.let { r -> call?.invoke(r) }
    }
  }

  companion object {
    fun newInstance(
      touchCancel: Boolean = true
    ): DateSelDialog {
      val dialog = DateSelDialog()
      dialog.mGravity = Gravity.BOTTOM
      dialog.touchOutside = touchCancel
      dialog.mAnimation = R.style.BottomDialogAnimation
      dialog.mWidth = ViewGroup.LayoutParams.MATCH_PARENT
      return dialog
    }
  }

  fun show(fragmentManager: FragmentManager) {
    show(fragmentManager, "DateSelDialog")
  }
}

//  DSL style
inline fun dateSelDialog(
  fragmentManager: FragmentManager,
  dsl: DateSelDialog.() -> Unit
) {
  val dialog = DateSelDialog.newInstance().apply(dsl)
  dialog.show(fragmentManager)
}