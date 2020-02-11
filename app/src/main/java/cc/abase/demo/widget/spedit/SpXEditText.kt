package cc.abase.demo.widget.spedit

import android.content.Context
import android.text.NoCopySpan
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import androidx.appcompat.widget.AppCompatEditText
import com.sunhapper.x.spedit.gif.watcher.GifWatcher
import com.sunhapper.x.spedit.view.DefaultKeyEventProxy
import com.sunhapper.x.spedit.view.KeyEventProxy
import com.sunhapper.x.spedit.view.SpXEditableFactory
import java.util.*

/**
 * Created by sunhapper on 2019/1/25 .
 */
class SpXEditText : AppCompatEditText {
  private var mKeyEventProxy: KeyEventProxy = DefaultKeyEventProxy()

  constructor(context: Context) : super(context)

  constructor(
    context: Context,
    attrs: AttributeSet
  ) : super(context, attrs)

  constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr)

  init {
    val watchers = ArrayList<NoCopySpan>()
    watchers.add(MySpanChangedWatcher())
    watchers.add(GifWatcher(this))
    setEditableFactory(SpXEditableFactory(watchers))
    setOnKeyListener { _, _, event -> handleKeyEvent(event) }
  }

  private fun handleKeyEvent(event: KeyEvent): Boolean {
      val temp = text
      return if (temp == null) false else mKeyEventProxy.onKeyEvent(event, temp)
  }

  override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
    return CustomInputConnectionWrapper(super.onCreateInputConnection(outAttrs), true)
  }

  /**
   * 解决google输入法删除不走OnKeyListener()回调问题
   */
  private inner class CustomInputConnectionWrapper
  /**
   * Initializes a wrapper.
   *
   *
   * **Caveat:** Although the system can accept `(InputConnection) null` in some
   * places, you cannot emulate such a behavior by non-null [InputConnectionWrapper] that
   * has `null` in `target`.
   *
   * @param target  the [InputConnection] to be proxied.
   * @param mutable set `true` to protect this object from being reconfigured to target
   * another [InputConnection].  Note that this is ignored while the target is `null`.
   */
  (
    target: InputConnection,
    mutable: Boolean
  ) : InputConnectionWrapper(target, mutable) {

    override fun deleteSurroundingText(
      beforeLength: Int,
      afterLength: Int
    ): Boolean {
      return if (beforeLength == 1 && afterLength == 0) {
        sendKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_DEL
            )
        ) && sendKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_DEL
            )
        )
      } else super.deleteSurroundingText(beforeLength, afterLength)
    }

    override fun sendKeyEvent(event: KeyEvent): Boolean {
      return handleKeyEvent(event) || super.sendKeyEvent(event)
    }
  }

  fun setKeyEventProxy(keyEventProxy: KeyEventProxy) {
    mKeyEventProxy = keyEventProxy
  }

}
