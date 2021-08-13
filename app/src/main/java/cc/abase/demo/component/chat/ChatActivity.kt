package cc.abase.demo.component.chat

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import cc.ab.base.ext.*
import cc.ab.base.ui.viewmodel.DataState
import cc.ab.base.widget.livedata.MyObserver
import cc.abase.demo.R
import cc.abase.demo.bean.local.DividerBean
import cc.abase.demo.bean.local.SimpleTxtBean
import cc.abase.demo.component.chat.viewmodel.ChatViewModel
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.constants.UiConstants
import cc.abase.demo.databinding.ActivityChatBinding
import cc.abase.demo.item.DividerItem
import cc.abase.demo.item.SimpleTxtItem
import com.blankj.utilcode.util.KeyboardUtils
import com.drakeet.multitype.MultiTypeAdapter
import com.mikepenz.itemanimators.SlideInOutBottomAnimator
import com.vanniktech.emoji.EmojiPopup
import kotlin.math.max

/**
 * Description:
 * @author: Khaos
 * @date: 2019/10/17 16:36
 */
class ChatActivity : CommBindTitleActivity<ActivityChatBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部打开">
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, ChatActivity::class.java)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //多类型适配器
  private val multiTypeAdapter = MultiTypeAdapter()

  //数据层
  private val viewModel: ChatViewModel by lazy { ChatViewModel() }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initContentView() {
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED)
    setTitleText(getString(R.string.chat_title))
    //注册多类型
    multiTypeAdapter.register(SimpleTxtItem())
    multiTypeAdapter.register(DividerItem())
    //设置适配器
    viewBinding.chatRecycler.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
    SlideInOutBottomAnimator(viewBinding.chatRecycler)
    viewBinding.chatRecycler.adapter = multiTypeAdapter
    viewBinding.chatSend.isEnabled = false
    viewBinding.chatSend.alpha = UiConstants.disable_alpha
    viewBinding.chatEdit.doAfterTextChanged {
      viewBinding.chatSend.isEnabled = !it.isNullOrBlank()
      viewBinding.chatSend.alpha = if (it.isNullOrBlank()) UiConstants.disable_alpha else 1f
    }
    viewBinding.chatSend.click {
      val msg = viewBinding.chatEdit.text.toString()
      if (msg.isNotBlank()) viewModel.sendMsg(msg) { suc -> if (suc) viewBinding.chatEdit.setText("") }
    }
    initEmoji()
    viewBinding.chatEdit.requestFocus()
    //监听加载结果
    viewModel.chatLiveData.observe(this, MyObserver {
      if (it is DataState.SuccessMore) {
        val isBottom = !viewBinding.chatRecycler.canScrollVertically(1)
        val items = multiTypeAdapter.items.toMutableList()
        val size1 = items.size
        it.newData?.forEach { d ->
          items.add(SimpleTxtBean(txt = d).also { stb ->
            stb.gravity = Gravity.CENTER_VERTICAL
            stb.textColor = Color.WHITE
          })
          items.add(DividerBean(heightPx = 1))
          multiTypeAdapter.items = items
          val size2 = items.size
          multiTypeAdapter.notifyItemRangeChanged(size1, size2 - size1)
          //是否处于底部，非底部不滚动到底部
          if (isBottom) scrollChatBottom()
        }
      }
    })
    //判断手机号
    viewBinding.chatEdit.addTextChangedListener {
      if (it?.toString()?.isPhoneNumber("CN") == true) {
        viewBinding.chatEdit.setTextColor(Color.GREEN)
      } else {
        viewBinding.chatEdit.setTextColor(Color.BLACK)
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Emoji表情">
  //Emoji表情弹窗
  private var emojiPopup: EmojiPopup? = null

  //初始化emoji
  private fun initEmoji() {
    //SOFT_INPUT_ADJUST_RESIZE才能正确监听键盘高度
    this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    emojiPopup = EmojiPopup.Builder.fromRootView(mContentView)
      .setOnEmojiPopupShownListener { viewBinding.chatKeyEmoji.setImageResource(R.drawable.svg_keyboard) }
      .setOnEmojiPopupDismissListener { viewBinding.chatKeyEmoji.setImageResource(R.drawable.svg_emoji) }
      .setOnSoftKeyboardOpenListener { keyHeight -> changeKeyHeight(keyHeight) }
      .setOnSoftKeyboardCloseListener { changeKeyHeight(0) }
      .setKeyboardAnimationStyle(R.style.emoji_fade_animation_style)
      .build(viewBinding.chatEdit)
    viewBinding.chatKeyEmoji.click { emojiPopup?.toggle() }
  }

  private var keOpen = false

  //监听键盘高度
  private fun changeKeyHeight(height: Int) {
    keOpen = height > 0
    viewBinding.chatRecycler.translationY = -height.toFloat()
    viewBinding.chatInputLayout.translationY = -height.toFloat()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="滚动到列表底部">
  //滚动到底部
  private fun scrollChatBottom() {
    viewBinding.chatRecycler.adapter?.let { adapter ->
      (viewBinding.chatRecycler.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(max(adapter.itemCount - 1, 0), Integer.MIN_VALUE)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="点击输入区域外关闭键盘">
  //点击输入区域外关闭键盘
  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    ev?.let {
      if (keOpen && (it.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
        //点击哪些View需要关闭键盘同时响应点击
        if (!isTouchViewOut(viewBindingTitle.commTitleBack, it)) { //多个View通过||连接
          KeyboardUtils.hideSoftInput(mActivity)
          //super.dispatchTouchEvent可能偶发不触发点击事件，所以手动处理点击
          viewBindingTitle.commTitleBack.performClick()
          return true
        }
        //没有点击到对于View则关闭键盘
        else if (isTouchViewOut(viewBinding.chatInputLayout, it)) { //多个View通过&&连接
          KeyboardUtils.hideSoftInput(mActivity)
          return true
        }
      }
    }
    return super.dispatchTouchEvent(ev)
  }

  // Return whether touch the view.
  private fun isTouchViewOut(v: View, event: MotionEvent): Boolean {
    val l = intArrayOf(0, 0)
    v.getLocationInWindow(l)
    val left = l[0]
    val top = l[1]
    val bottom = top + v.height
    val right = left + v.width
    return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
  }
  //</editor-fold>
}