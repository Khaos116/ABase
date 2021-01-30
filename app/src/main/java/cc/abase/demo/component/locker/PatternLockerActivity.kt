package cc.abase.demo.component.locker

import cc.ab.base.ext.toast
import cc.ab.base.ext.xmlToString
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import com.github.ihsg.patternlocker.OnPatternChangeListener
import com.github.ihsg.patternlocker.PatternLockerView
import kotlinx.android.synthetic.main.activity_pattern_locker.lockerIndicator
import kotlinx.android.synthetic.main.activity_pattern_locker.lockerPattern

/**
 * @Description
 * @Author：CASE
 * @Date：2021/1/30
 * @Time：18:29
 */
class PatternLockerActivity : CommTitleActivity() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResContentId() = R.layout.activity_pattern_locker
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  private var mLastList = mutableListOf<Int>()
  override fun initContentView() {
    setTitleText(R.string.title_pattern_locker.xmlToString())
    lockerPattern.setOnPatternChangedListener(object : OnPatternChangeListener {
      override fun onChange(view: PatternLockerView, hitIndexList: List<Int>) {
        lockerIndicator?.updateState(hitIndexList, false)
      }

      override fun onClear(view: PatternLockerView) {
        lockerIndicator?.updateState(mutableListOf(), false)
      }

      override fun onComplete(view: PatternLockerView, hitIndexList: List<Int>) {
        if (hitIndexList.size < 4 && mLastList.isEmpty()) { //第一次
          "至少需要选中4个点".toast()
          lockerIndicator?.updateState(hitIndexList, true)
          lockerPattern?.updateStatus(true)
        } else {
          if (mLastList.isEmpty()) { //记录第一次的选中
            mLastList.addAll(hitIndexList)
            lockerIndicator?.updateState(hitIndexList, false)
          } else { //第二次验证(数量一样，内容一样)
            val same = (mLastList.size == hitIndexList.size) && mLastList.containsAll(hitIndexList)
            if (same) {
              lockerIndicator?.updateState(hitIndexList, false)
              lockerIndicator?.postDelayed({ finish() }, 200)
              "手势设置成功".toast()
            } else {
              lockerIndicator?.updateState(hitIndexList, true)
              lockerPattern?.updateStatus(true)
              mLastList = mutableListOf()
              "两次选中的点不同，请重新设置".toast()
            }
          }
        }
      }

      override fun onStart(view: PatternLockerView) {}
    })
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun initData() {
  }
  //</editor-fold>
}