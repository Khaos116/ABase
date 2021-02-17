package cc.abase.demo.component.count

import cc.ab.base.ext.xmlToString
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import com.blankj.utilcode.util.TimeUtils
import com.cc.countsdk.utils.CountInfoUtils
import kotlinx.android.synthetic.main.activity_count.countTv

/**
 * @Description
 * @Author：CASE
 * @Date：2021/2/17
 * @Time：16:15
 */
class CountActivity : CommTitleActivity() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResContentId() = R.layout.activity_count
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initContentView() {
    setTitleText(R.string.title_count.xmlToString())
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun initData() {
    countTv.text = StringBuilder()
        .append("统计key:").append(CountInfoUtils.getCountKey()).append("\n\n")
        .append("AppInfo:\n").append(
            CountInfoUtils.getAppInfo().replace("{", "{\n\t")
                .replace(",", ",\n\t")
                .replace("}", "\n}"))
        .append("\n\n")
        .append("PhoneInfo:\n").append(
            CountInfoUtils.getPhoneInfo()
                .replace("{", "{\n\t")
                .replace(",", ",\n\t")
                .replace("}", "\n}"))
    CountInfoUtils.getHistoryDurationInfo { list ->
      val sb = StringBuilder().append("\n\n").append("时长信息:")
      list.forEach { d ->
        if (d.key.isBlank()) {
          sb.append("\n\n").append(TimeUtils.millis2String(d.time?.toLong() ?: System.currentTimeMillis()))
        } else {
          sb.append("\n").append(d.key).append("：").append("${d.value / 1000}s")
        }
      }
      countTv.append(sb)
      //处理完成后清除旧数据
      //CountInfoUtils.clearHistoryDurationInfo()
    }
    CountInfoUtils.getHistoryPagePathInfo { list ->
      val sb = StringBuilder().append("\n\n").append("路径信息:")
      list.forEach { d ->
        if (d.key.isBlank()) {
          sb.append("\n\n").append(TimeUtils.millis2String(d.time?.toLong() ?: System.currentTimeMillis()))
        } else {
          sb.append("\n").append(d.key).append("：").append(d.value)
        }
      }
      countTv.append(sb)
      //处理完成后清除旧数据
      //CountInfoUtils.clearHistoryPagePathInfo()
    }
  }
  //</editor-fold>
}