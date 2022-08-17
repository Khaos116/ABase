package cc.abase.demo.component.calendar

import cc.ab.base.ext.click
import cc.ab.base.ext.pressEffectAlpha
import cc.ab.base.ext.xmlToString
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindFragment
import cc.abase.demo.databinding.FragmentCalendarBinding
import cc.abase.demo.widget.dialog.calendarSelDialog

/**
 * Author:Khaos116
 * Date:2022-08-17
 * Time:15:38
 */
class CalendarFragment : CommBindFragment<FragmentCalendarBinding>() {
  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun lazyInit() {
    viewBinding.flTitle.commTitleBack.pressEffectAlpha()
    viewBinding.tvDate.pressEffectAlpha()
    viewBinding.flTitle.commTitleBack.click { mActivity.finish() }
    viewBinding.flTitle.commTitleText.text = R.string.日历选择.xmlToString()
    viewBinding.tvDate.click {
      calendarSelDialog(childFragmentManager, viewBinding.tvDate.text.toString()) {
        mCallBack = { s, e, t -> viewBinding.tvDate.text = t.ifBlank { "$s\n$e" } }
      }
    }
  }
  //</editor-fold>
}