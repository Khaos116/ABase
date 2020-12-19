package cc.abase.demo.component.chat

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.component.chat.viewmodel.ChatState
import cc.abase.demo.component.chat.viewmodel.ChatViewModel
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.constants.UiConstants
import cc.abase.demo.epoxy.base.dividerItem
import cc.abase.demo.epoxy.item.simpleTextItem
import cc.abase.demo.mvrx.MvRxEpoxyController
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.LogUtils
import com.vanniktech.emoji.EmojiPopup
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.layout_comm_title.commTitleBack
import kotlin.math.max

/**
 * Description:
 * @author: CASE
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
        if (suc) runOnUiThread {
          chatEdit.setText("")
          scrollChatBottom()
        }
      }
    }
    epoxyController.addModelBuildListener {
      val isBottom = !chatRecycler.canScrollVertically(1);
      //是否处于底部，非底部不滚动到底部
      if (isBottom) scrollChatBottom()
    }
    initEmoji()
    chatEdit.requestFocus()
  }

  override fun initData() {
    //请求状态和结果监听
    viewModel.subscribe { state -> epoxyController.data = state }
  }

  //Emoji表情弹窗
  private var emojiPopup: EmojiPopup? = null

  //初始化emoji
  private fun initEmoji() {
    emojiPopup = EmojiPopup.Builder.fromRootView(mContentView)
        .setOnEmojiPopupShownListener { chatKeyEmoji.setImageResource(R.drawable.svg_keyboard) }
        .setOnEmojiPopupDismissListener { chatKeyEmoji.setImageResource(R.drawable.svg_emoji) }
        .setOnSoftKeyboardOpenListener { keyHeight -> changeKeyHeight(keyHeight) }
        .setOnSoftKeyboardCloseListener { changeKeyHeight(0) }
        .setKeyboardAnimationStyle(R.style.emoji_fade_animation_style)
        .build(chatEdit)
    chatKeyEmoji.click { emojiPopup?.toggle() }
  }

  private var keOpen = false
  //监听键盘高度
  private fun changeKeyHeight(height: Int) {
    keOpen = height > 0
    chatRecycler.translationY = -height.toFloat()
    chatInputLayout.translationY = -height.toFloat()
  }

  //滚动到底部
  private fun scrollChatBottom() {
    chatRecycler.adapter?.let { adapter ->
      (chatRecycler.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
          max(adapter.itemCount - 1, 0), Integer.MIN_VALUE
      )
    }
  }

  //epoxy
  private val epoxyController = MvRxEpoxyController<ChatState> { state ->
    LogUtils.e("CASE:数据Size=${state.chatList.size}")
    state.chatList.forEachIndexed { index, s ->
      simpleTextItem {
        id(index)
        textColor(Color.WHITE)
        gravity(Gravity.CENTER_VERTICAL)
        msg(s)
      }
      dividerItem {
        id("chat_line_$index")
      }
    }
  }

  //点击输入区域外关闭键盘
  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    ev?.let {
      if (keOpen && (it.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
        //点击哪些View需要关闭键盘同时响应点击
        if (!isTouchViewOut(commTitleBack, it)) {//多个View通过||连接
          KeyboardUtils.hideSoftInput(mActivity)
        }
        //没有点击到对于View则关闭键盘
        else if (isTouchViewOut(chatInputLayout, it)) {//多个View通过&&连接
          KeyboardUtils.hideSoftInput(mActivity)
          return true
        }
      }
    }
    return super.dispatchTouchEvent(ev)
  }

  // Return whether touch the view.
  private fun isTouchViewOut(
    v: View,
    event: MotionEvent
  ): Boolean {
    val l = intArrayOf(0, 0)
    v.getLocationInWindow(l)
    val left = l[0]
    val top = l[1]
    val bottom = top + v.height
    val right = left + v.width
    return !(event.x > left && event.x < right
        && event.y > top && event.y < bottom)
  }
}