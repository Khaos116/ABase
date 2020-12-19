package cc.abase.demo.component.marquee

import android.content.Context
import android.content.Intent
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.activity_marquee.*
import kotlinx.coroutines.*

/**
 * Description:
 * @author: CASE
 * @date: 2020/3/23 18:00
 */
class MarqueeActivity : CommTitleActivity() {

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, MarqueeActivity::class.java)
      context.startActivity(intent)
    }
  }

  override fun layoutResContentId() = R.layout.activity_marquee

  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_marquee))
    marqueeContent1.text = "这是一个很长很长的测试跑马灯效果的描述文字，如果不够长，我再添加一点文字！"
    marqueeContent2.text = "这是一个很长很长的测试跑马灯效果的描述文字，如果不够长，我再添加一点文字，我再添加一点文字！"
  }

  override fun initData() {
    marqueeTime?.post { startCountdown() }
  }

  private var launchJob: Job? = null
  private fun startCountdown() {
    launchJob?.cancel()
    //使用协程进行倒计时
    launchJob = GlobalScope.launch(Dispatchers.Main) {
      if (isActive) {
        for (i in 60 downTo 1) {
          marqueeTime?.text = i.toString()
          delay(1000)
        }
        startCountdown()
      }
    }
  }

  override fun finish() {
    super.finish()
    launchJob?.cancel()
  }
}