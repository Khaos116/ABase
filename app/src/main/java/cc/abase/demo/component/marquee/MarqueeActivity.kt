package cc.abase.demo.component.marquee

import android.content.Context
import android.content.Intent
import cc.ab.base.ext.toast
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.databinding.ActivityMarqueeBinding
import com.blankj.utilcode.util.StringUtils
import kotlinx.coroutines.*

/**
 * Description:
 * @author: Khaos
 * @date: 2020/3/23 18:00
 */
class MarqueeActivity : CommBindTitleActivity<ActivityMarqueeBinding>() {

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, MarqueeActivity::class.java)
      context.startActivity(intent)
    }
  }

  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_marquee))
    viewBinding.marqueeContent1.text = "这是一个很长很长的测试跑马灯效果的描述文字，如果不够长，我再添加一点文字！"
    viewBinding.marqueeContent2.text = "这是一个很长很长的测试跑马灯效果的描述文字，如果不够长，我再添加一点文字，我再添加一点文字！"
    val list = mutableListOf<String>()
    for (i in 1..20) {
      list.add("这是第${i}条公告!")
    }
    viewBinding.marqueeContent3.mCall = { it.toast() }
    viewBinding.marqueeContent3.setNewDatas(list)
    viewBinding.marqueeTime.post { startCountdown() }
  }

  private var launchJob: Job? = null
  private fun startCountdown() {
    launchJob?.cancel()
    //使用协程进行倒计时
    launchJob = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch(Dispatchers.Main) {
      if (isActive) {
        for (i in 60 downTo 1) {
          viewBinding.marqueeTime.text = i.toString()
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