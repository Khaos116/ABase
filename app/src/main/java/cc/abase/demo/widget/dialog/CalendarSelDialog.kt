package cc.abase.demo.widget.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.Gravity
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import cc.ab.base.ext.*
import cc.ab.base.ui.dialog.BaseBindFragmentDialog
import cc.abase.demo.R
import cc.abase.demo.databinding.DialogCalendarSelBinding
import cc.abase.demo.utils.MyLanguageUtils
import com.aminography.primecalendar.common.CalendarFactory
import com.aminography.primecalendar.common.CalendarType
import com.aminography.primedatepicker.calendarview.PrimeCalendarView
import com.aminography.primedatepicker.calendarview.adapter.MonthListAdapter
import com.aminography.primedatepicker.calendarview.dataholder.MonthDataHolder
import com.aminography.primedatepicker.common.OnDayPickedListener
import com.aminography.primedatepicker.common.PickType
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.TimeUtils
import java.lang.reflect.Field
import java.util.*

/**
 * Author:Khaos116
 * Date:2022-08-17
 * Time:15:56
 */
class CalendarSelDialog : BaseBindFragmentDialog<DialogCalendarSelBinding>() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //当前选中的日期
  private var mCurrentStartYear = ""
  private var mCurrentStartMonth = ""
  private var mCurrentStartDay = ""
  private var mCurrentEndYear = ""
  private var mCurrentEndMonth = ""
  private var mCurrentEndDay = ""

  //当前选择日期
  var mDefaultDate = ""
  var mCallBack: ((start: String, end: String, type: String) -> Unit)? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun initView() {
    viewBinding.tvToday.isSelected = mDefaultDate == R.string.今日.xmlToString()
    viewBinding.tvYesterday.isSelected = mDefaultDate == R.string.昨日.xmlToString()
    viewBinding.tvWeek.isSelected = mDefaultDate == R.string.本周.xmlToString()
    viewBinding.tvMonth.isSelected = mDefaultDate == R.string.本月.xmlToString()
    initFloatDate()
    initCalendarView()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化悬浮日期">
  private fun initFloatDate() {
    viewBinding.tvToday.pressEffectAlpha()
    viewBinding.tvYesterday.pressEffectAlpha()
    viewBinding.tvWeek.pressEffectAlpha()
    viewBinding.tvMonth.pressEffectAlpha()
    viewBinding.flStartDay.pressEffectAlpha()
    viewBinding.flEndDay.pressEffectAlpha()
    viewBinding.tvCancel.pressEffectAlpha()
    viewBinding.tvConfirm.pressEffectAlpha()
    viewBinding.tvToday.click { if (!it.isSelected) changeRange(it) }
    viewBinding.tvYesterday.click { if (!it.isSelected) changeRange(it) }
    viewBinding.tvWeek.click { if (!it.isSelected) changeRange(it) }
    viewBinding.tvMonth.click { if (!it.isSelected) changeRange(it) }
    viewBinding.ivBeforeMonth.click { nextOrBefore(false) }
    viewBinding.ivNextMonth.click { nextOrBefore(true) }
    viewBinding.flStartDay.click {
      viewBinding.flStartDay.isSelected = true
      viewBinding.flEndDay.isSelected = false
      viewBinding.ivMore1.rotation = 180f
      viewBinding.ivMore2.rotation = 0f
      viewBinding.calendarView.pickType = PickType.RANGE_START
    }
    viewBinding.flEndDay.click {
      if (viewBinding.tvStartDay.text.toString() == R.string.开始日期.xmlToString()) {
        R.string.请选择开始日期.xmlToast()
      } else {
        viewBinding.flStartDay.isSelected = false
        viewBinding.flEndDay.isSelected = true
        viewBinding.ivMore1.rotation = 0f
        viewBinding.ivMore2.rotation = 180f
        viewBinding.calendarView.pickType = PickType.RANGE_END
      }
    }
    viewBinding.calendarView.pickType = PickType.RANGE_END
    viewBinding.calendarView.pickedRangeEndCalendar = CalendarFactory.newInstance(CalendarType.CIVIL)
    val today = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
    viewBinding.tvEndDay.text = today
    viewBinding.flStartDay.performClick()
    viewBinding.tvCancel.click { dismissAllowingStateLoss() }
    viewBinding.tvConfirm.click {
      val start = viewBinding.tvStartDay.text.toString()
      val end = viewBinding.tvStartDay.text.toString()
      if (start == R.string.开始日期.xmlToString()) {
        R.string.请选择开始日期.xmlToast()
      } else if (end == R.string.结束日期.xmlToString()) {
        R.string.请选择结束日期.xmlToast()
      } else {
        changeRange(null)
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="反射修改页面切换">
  private var mAdapter: MonthListAdapter? = null
  private var mLayoutManager: LinearLayoutManager? = null
  private fun nextOrBefore(next: Boolean) {
    if (mAdapter == null) {
      try {
        val field: Field = viewBinding.calendarView.javaClass.getDeclaredField("adapter")
        field.isAccessible = true
        val field2: Field = viewBinding.calendarView.javaClass.getDeclaredField("layoutManager")
        field2.isAccessible = true
        mAdapter = field.get(viewBinding.calendarView) as? MonthListAdapter
        mLayoutManager = field2.get(viewBinding.calendarView) as? LinearLayoutManager
        "反射获取MonthListAdapter和LinearLayoutManager成功".logE()
      } catch (e: Exception) {
        e.printStackTrace()
        "反射获取MonthListAdapter和LinearLayoutManager失败".logE()
      }
    }
    val adapter = mAdapter
    val layoutManager = mLayoutManager
    if (adapter != null && layoutManager != null) {
      val position = layoutManager.findFirstVisibleItemPosition()
      val monthDataHolder: MonthDataHolder? = if (position < adapter.itemCount) {
        adapter.itemAt(position)
      } else {
        null
      }
      CalendarFactory.newInstance(viewBinding.calendarView.calendarType, viewBinding.calendarView.locale).run {
        if (monthDataHolder != null) set(monthDataHolder.year, monthDataHolder.month, 1)
        add(Calendar.MONTH, if (next) 1 else -1)
        viewBinding.calendarView.goto(year, month, true)
      }
    } else {
      if (next) {
        viewBinding.calendarView.gotoNextMonth(true)
      } else {
        viewBinding.calendarView.gotoPreviousMonth(true)
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="计算选中结果">
  private fun changeRange(view: View? = null) {
    viewBinding.tvToday.isSelected = viewBinding.tvToday == view
    viewBinding.tvYesterday.isSelected = viewBinding.tvYesterday == view
    viewBinding.tvWeek.isSelected = viewBinding.tvWeek == view
    viewBinding.tvMonth.isSelected = viewBinding.tvMonth == view
    val currentTime = System.currentTimeMillis()
    val today = TimeUtils.millis2String(currentTime, "yyyy-MM-dd")
    val todaySplit = today.split("-")
    val yesterday = TimeUtils.millis2String(currentTime - TimeConstants.DAY, "yyyy-MM-dd")
    val off = when (TimeUtils.getUSWeek(currentTime).lowercase()) {
      "monday" -> 0
      "tuesday" -> 1
      "wednesday" -> 2
      "thursday" -> 3
      "friday" -> 4
      "saturday" -> 5
      "sunday" -> 6
      else -> 0
    }
    val week = TimeUtils.millis2String(currentTime - off * TimeConstants.DAY, "yyyy-MM-dd")
    val month = "${todaySplit[0]}-${todaySplit[1]}-01"
    var endDate = today
    val startDate: String
    val type: String
    when {
      viewBinding.tvToday.isSelected -> {
        type = R.string.今日.xmlToString()
        startDate = today
      }
      viewBinding.tvYesterday.isSelected -> {
        type = R.string.昨日.xmlToString()
        endDate = yesterday
        startDate = yesterday
      }
      viewBinding.tvWeek.isSelected -> {
        type = R.string.本周.xmlToString()
        startDate = week
      }
      viewBinding.tvMonth.isSelected -> {
        type = R.string.本月.xmlToString()
        startDate = month
      }
      else -> {
        startDate = "${mCurrentStartYear}-${mCurrentStartMonth}-${mCurrentStartDay}"
        endDate = "${mCurrentEndYear}-${mCurrentEndMonth}-${mCurrentEndDay}"
        type = ""
      }
    }
    if (view != null) {
      view.postDelayed({
        dismissAllowingStateLoss()
        mCallBack?.invoke(startDate, endDate, type)
      }, 100)
    } else {
      dismissAllowingStateLoss()
      mCallBack?.invoke(startDate, endDate, type)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化日历">
  @SuppressLint("SetTextI18n")
  private fun initCalendarView() {
    val calendarView = viewBinding.calendarView
    calendarView.typeface = ResourcesCompat.getFont(calendarView.context, R.font.harmonyos_sans_condensed_medium)
    calendarView.calendarType = CalendarType.CIVIL
    calendarView.pickType = PickType.RANGE_START
    calendarView.flingOrientation = PrimeCalendarView.FlingOrientation.HORIZONTAL
    calendarView.locale = Locale.getDefault()
    calendarView.animateSelection = false
    calendarView.pickedDayLabelTextColor = Color.WHITE
    calendarView.firstDayOfWeek = Calendar.SUNDAY
    calendarView.todayLabelTextColor = Color.parseColor("#c62430")
    calendarView.dayLabelTextColor = Color.parseColor("#333333")
    calendarView.disabledDayLabelTextColor = Color.parseColor("#C5C5C5")
    calendarView.weekLabelTextColor = Color.parseColor("#333333")
    calendarView.monthLabelTextColor = Color.parseColor("#333333")
    calendarView.pickedDayBackgroundColor = Color.parseColor("#c62430")
    calendarView.pickedDayInRangeBackgroundColor = Color.parseColor("#E57373")
    calendarView.pickedDayInRangeLabelTextColor = Color.WHITE
    calendarView.dividerColor = Color.TRANSPARENT
    calendarView.dividerInsetBottom = 0
    calendarView.dividerInsetTop = 0
    calendarView.dividerInsetLeft = 0
    calendarView.dividerInsetRight = 0
    calendarView.dayLabelTextSize = 14.dp2px()
    calendarView.weekLabelTextSize = 12.dp2px()
    calendarView.monthLabelTextSize = 12.dp2px()
    calendarView.minDateCalendar = CalendarFactory.newInstance(CalendarType.CIVIL).also { it[Calendar.MONTH] -= 12 }
    calendarView.maxDateCalendar = CalendarFactory.newInstance(CalendarType.CIVIL)
    calendarView.onDayPickedListener = OnDayPickedListener { _, _, startDay, endDay, _ ->
      if (startDay != null) {
        if (MyLanguageUtils.isAppChinese()) {
          viewBinding.tvStartDay.text = "${startDay.year}-${(startDay.month + 1).to2Num()}-${(startDay.dayOfMonth).to2Num()}"
        } else {
          viewBinding.tvStartDay.text = "${(startDay.dayOfMonth).to2Num()}-${(startDay.month + 1).to2Num()}-${startDay.year}"
        }
        viewBinding.tvStartDay.setTextColor(Color.BLACK)
        mCurrentStartYear = startDay.year.toString()
        mCurrentStartMonth = (startDay.month + 1).to2Num()
        mCurrentStartDay = (startDay.dayOfMonth).to2Num()
      }
      if (endDay != null) {
        if (MyLanguageUtils.isAppChinese()) {
          viewBinding.tvEndDay.text = "${endDay.year}-${(endDay.month + 1).to2Num()}-${(endDay.dayOfMonth).to2Num()}"
        } else {
          viewBinding.tvEndDay.text = "${(endDay.dayOfMonth).to2Num()}-${(endDay.month + 1).to2Num()}-${endDay.year}"
        }
        viewBinding.tvEndDay.setTextColor(Color.BLACK)
        mCurrentEndYear = endDay.year.toString()
        mCurrentEndMonth = (endDay.month + 1).to2Num()
        mCurrentEndDay = (endDay.dayOfMonth).to2Num()
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="新建对象">
  companion object {
    fun newInstance(touchCancel: Boolean = true): CalendarSelDialog {
      val dialog = CalendarSelDialog()
      dialog.mGravity = Gravity.CENTER
      dialog.canTouchOutside = touchCancel
      dialog.mWidth = (0.92f * ScreenUtils.getScreenWidth()).toInt()
      return dialog
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="调用显示">
  fun show(fragmentManager: FragmentManager) {
    show(fragmentManager, "CalendarSelDialog")
  }
  //</editor-fold>
}

//DSL style
inline fun calendarSelDialog(fragmentManager: FragmentManager, defaultDate: String? = null, dsl: CalendarSelDialog.() -> Unit) {
  val dialog = CalendarSelDialog.newInstance().apply(dsl)
  if (defaultDate != null) dialog.mDefaultDate = defaultDate
  dialog.show(fragmentManager)
}