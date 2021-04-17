package cc.abase.demo.widget

import android.app.Instrumentation
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import cc.ab.base.ext.click
import cc.ab.base.ext.dp2px
import cc.abase.demo.databinding.LayoutNumberKeyboardBinding

/**
 * @Description https://blog.csdn.net/lianwanfei/article/details/48052073
 * @Author：CASE
 * @Date：2021-04-17
 * @Time：17:59
 */
class NumberKeyboardView @JvmOverloads constructor(c: Context, a: AttributeSet? = null, d: Int = 0) : ConstraintLayout(c, a, d) {
  //<editor-fold defaultstate="collapsed" desc="变量">
  var mCallHide: (() -> Unit)? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    setBackgroundColor(Color.parseColor("#fbfbfb"))
    setPadding(11.dp2px(), 14.dp2px(), 11.dp2px(), 4.dp2px())
    val viewBind = LayoutNumberKeyboardBinding.inflate(LayoutInflater.from(c), this)
    viewBind.tv0.click { performKeyDown(KeyEvent.KEYCODE_0) }
    viewBind.tv1.click { performKeyDown(KeyEvent.KEYCODE_1) }
    viewBind.tv2.click { performKeyDown(KeyEvent.KEYCODE_2) }
    viewBind.tv3.click { performKeyDown(KeyEvent.KEYCODE_3) }
    viewBind.tv4.click { performKeyDown(KeyEvent.KEYCODE_4) }
    viewBind.tv5.click { performKeyDown(KeyEvent.KEYCODE_5) }
    viewBind.tv6.click { performKeyDown(KeyEvent.KEYCODE_6) }
    viewBind.tv7.click { performKeyDown(KeyEvent.KEYCODE_7) }
    viewBind.tv8.click { performKeyDown(KeyEvent.KEYCODE_8) }
    viewBind.tv9.click { performKeyDown(KeyEvent.KEYCODE_9) }
    viewBind.tvPoint.click { performKeyDown(KeyEvent.KEYCODE_NUMPAD_DOT) }
    viewBind.ivDel.click { performKeyDown(KeyEvent.KEYCODE_DEL) }
    viewBind.tvHide.click { mCallHide?.invoke() }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="模拟键盘输入">
  //模拟键盘输入
  private fun performKeyDown(keyCode: Int) {
    Thread {
      try {
        Instrumentation().sendKeyDownUpSync(keyCode)
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }.start()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="禁止输入框弹出键盘">
  //禁止输入框弹出键盘
  fun disableShowSoftInput(vararg editTexts: EditText) {
    try {
      val method = EditText::class.java.getMethod("setShowSoftInputOnFocus", Boolean::class.javaPrimitiveType)
      method.isAccessible = true
      for (et in editTexts) method.invoke(et, false)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
  //</editor-fold>
}