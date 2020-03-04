package cc.abase.demo.bean.local

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.text.*
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import cc.abase.demo.widget.spedit.DataSpan
import cc.abase.demo.widget.spedit.IntegratedBgSpan
import com.sunhapper.x.spedit.mention.span.BreakableSpan

/**
 * Description:
 * 1.ForegroundColorSpan为前景文字颜色
 * 2.IntegratedBgSpan为选中删除背景颜色
 *
 * @author: caiyoufei
 * @date: 2019/10/4 13:38
 */
data class AtBean(
  var uid: Long,
  var name: String? = "",
  var index: Int? = 0,
  var len: Int? = 0,
  var type: Int? = 0
) : BreakableSpan, DataSpan, IntegratedBgSpan, Parcelable {

  //前景展示
  var styleSpan: Any? = null
  //添加@或者#展示
  val displayText: CharSequence
    get() = "@$name "

  //展示的span效果
  val spannableString: Spannable
    get() {
      styleSpan = ForegroundColorSpan(Color.MAGENTA)
      val spannableString = SpannableString(displayText)
      spannableString.setSpan(
          styleSpan, 0, spannableString.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      )
      spannableString.setSpan(
          this, 0, spannableString.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      )
      val stringBuilder = SpannableStringBuilder()
      return stringBuilder.append(spannableString)
    }

  //删除时选中效果需要
  override var isShow = false
  override var bgSpan: BackgroundColorSpan? = null

  //判断删除
  override fun isBreak(spannable: Spannable): Boolean {
    val spanStart = spannable.getSpanStart(this)
    val spanEnd = spannable.getSpanEnd(this)
    val isBreak = spanStart >= 0 && spanEnd >= 0 && spannable.subSequence(
        spanStart,
        spanEnd
    ).toString() != displayText
    if (isBreak && styleSpan != null) {
      spannable.removeSpan(styleSpan)
      styleSpan = null
    }
    return isBreak
  }

  constructor(parcel: Parcel) : this(
      parcel.readLong(),
      parcel.readValue(Int::class.java.classLoader) as? String,
      parcel.readValue(String::class.java.classLoader) as? Int,
      parcel.readValue(Int::class.java.classLoader) as? Int,
      parcel.readValue(Int::class.java.classLoader) as? Int
  )

  override fun writeToParcel(
    parcel: Parcel,
    flags: Int
  ) {
    parcel.writeLong(uid)
    parcel.writeString(name)
    parcel.writeValue(index)
    parcel.writeValue(len)
    parcel.writeValue(type)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Creator<AtBean> {
    override fun createFromParcel(parcel: Parcel): AtBean {
      return AtBean(parcel)
    }

    override fun newArray(size: Int): Array<AtBean?> {
      return arrayOfNulls(size)
    }
  }
}