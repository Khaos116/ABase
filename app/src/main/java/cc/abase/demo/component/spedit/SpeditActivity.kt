package cc.abase.demo.component.spedit

import android.content.Context
import android.content.Intent
import cc.abase.demo.R
import cc.abase.demo.component.chat.ChatActivity
import cc.abase.demo.component.comm.CommTitleActivity

/**
 * Description:@或者#变色效果
 * @author: caiyoufei
 * @date: 2019/11/30 20:49
 */
class SpeditActivity : CommTitleActivity() {
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, ChatActivity::class.java)
      context.startActivity(intent)
    }
  }

  override fun layoutResContentId() = R.layout.spedit

  override fun initContentView() {
  }

  override fun initData() {
  }
}