package cc.abase.demo.component.chat

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import cc.ab.base.ext.addTextWatcher
import cc.ab.base.ext.click
import cc.ab.base.ext.mContentView
import cc.ab.base.ext.mContext
import cc.abase.demo.R
import cc.abase.demo.component.chat.viewmodel.ChatState
import cc.abase.demo.component.chat.viewmodel.ChatViewModel
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.constants.UiConstants
import cc.abase.demo.epoxy.base.dividerItem
import cc.abase.demo.epoxy.item.simpleTextItem
import cc.abase.demo.mvrx.MvRxEpoxyController
import kotlinx.android.synthetic.main.activity_chat.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/17 16:36
 */
class ChatActivity : CommTitleActivity() {
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, ChatActivity::class.java)
      context.startActivity(intent)
    }
  }

  //数据层
  private val viewModel: ChatViewModel by lazy {
    ChatViewModel()
  }

  override fun layoutResContentId() = R.layout.activity_chat

  override fun initContentView() {
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED)
    setTitleText(getString(R.string.chat_title))
    chatRecycler.setController(epoxyController)
    chatRecycler.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
    chatSend.isEnabled = false
    chatSend.alpha = UiConstants.disable_alpha
    chatEdit.addTextWatcher {
      chatSend.isEnabled = !it.isNullOrBlank()
      chatSend.alpha = if (it.isNullOrBlank()) UiConstants.disable_alpha else 1f
    }
    chatSend.click {
      viewModel.sendMsg(chatEdit.text.toString()) { suc ->
        if (suc) runOnUiThread { chatEdit.setText("") }
      }
    }
    //不固定高度，背景会跟着移动移动
    mContentView.post { mContentView.layoutParams.height = mContentView.height }
  }

  override fun needKeyListener() = true

  override fun keyBoardChange(
    isPopup: Boolean,
    keyboardHeight: Int
  ) {
    chatRecycler.translationY = -keyboardHeight.toFloat()
    chatInputLayout.translationY = -keyboardHeight.toFloat()
    Log.e("CASE", "聊天页键盘高度=${keyboardHeight}")
  }

  override fun initData() {
    //请求状态和结果监听
    viewModel.subscribe { state ->
      epoxyController.data = state
    }
  }

  //epoxy
  private val epoxyController = MvRxEpoxyController<ChatState> { state ->
    Log.e("CASE", "数据Size=${state.chatList.size}")
    state.chatList.forEachIndexed { index, s ->
      simpleTextItem {
        id(index)
        heightDp(30f)
        msg(s)
      }
      dividerItem {
        id("chat_line_$index")
      }
    }
  }
}