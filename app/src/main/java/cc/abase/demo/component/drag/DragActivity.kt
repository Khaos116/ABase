package cc.abase.demo.component.drag

import android.content.Context
import android.content.Intent
import cc.abase.demo.R
import cc.abase.demo.component.chat.ChatActivity
import cc.abase.demo.component.comm.CommTitleActivity

/**
 * Description:item拖拽效果
 * @author: caiyoufei
 * @date: 2019/11/30 20:48
 */
class DragActivity : CommTitleActivity() {
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