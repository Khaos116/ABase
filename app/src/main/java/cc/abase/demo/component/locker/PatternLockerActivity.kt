package cc.abase.demo.component.locker

import android.view.LayoutInflater
import cc.ab.base.ext.toast
import cc.ab.base.ext.xmlToString
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.databinding.ActivityPatternLockerBinding
import com.github.ihsg.patternlocker.OnPatternChangeListener
import com.github.ihsg.patternlocker.PatternLockerView

/**
 * @Description
 * @Author：Khaos
 * @Date：2021/1/30
 * @Time：18:29
 */
class PatternLockerActivity : CommBindTitleActivity<ActivityPatternLockerBinding>() {
  //<editor-fold defaultstate="collapsed" desc="初始化View">
  private var mLastList = mutableListOf<Int>()
  override fun initContentView() {
    setTitleText(R.string.title_pattern_locker.xmlToString())
    viewBinding.lockerPattern.setOnPatternChangedListener(object : OnPatternChangeListener {
      override fun onChange(view: PatternLockerView, hitIndexList: List<Int>) {
        viewBinding.lockerIndicator.updateState(hitIndexList, false)
      }

      override fun onClear(view: PatternLockerView) {
        viewBinding.lockerIndicator.updateState(mutableListOf(), false)
      }

      override fun onComplete(view: PatternLockerView, hitIndexList: List<Int>) {
        if (hitIndexList.size < 4 && mLastList.isEmpty()) { //第一次
          "至少需要选中4个点".toast()
          viewBinding.lockerIndicator.updateState(hitIndexList, true)
          viewBinding.lockerPattern.updateStatus(true)
        } else {
          if (mLastList.isEmpty()) { //记录第一次的选中
            mLastList.addAll(hitIndexList)
            viewBinding.lockerIndicator.updateState(hitIndexList, false)
          } else { //第二次验证(数量一样，内容一样)
            val same = (mLastList.size == hitIndexList.size) && mLastList.containsAll(hitIndexList)
            if (same) {
              viewBinding.lockerIndicator.updateState(hitIndexList, false)
              viewBinding.lockerIndicator.postDelayed({ finish() }, 200)
              "手势设置成功".toast()
            } else {
              viewBinding.lockerIndicator.updateState(hitIndexList, true)
              viewBinding.lockerPattern.updateStatus(true)
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
}