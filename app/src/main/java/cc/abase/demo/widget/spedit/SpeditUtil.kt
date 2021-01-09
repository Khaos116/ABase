package cc.abase.demo.widget.spedit

import android.graphics.Color
import android.text.*
import android.view.View
import android.widget.EditText
import cc.ab.base.ext.toast
import cc.ab.base.utils.CcInputHelper
import cc.ab.base.widget.span.ClickPreventableTextView
import cc.ab.base.widget.span.TouchableSpan
import cc.abase.demo.R
import cc.abase.demo.bean.local.AtBean
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.Utils
import java.util.ArrayList

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/4 13:30
 */
object SpeditUtil {
  /**
   * 插入@用户的信息
   */
  fun insertUser(editText: SpXEditText, userName: String, uid: Long, maxLen: Int) {
    editText.text?.let { ed ->
      val insertUser = AtBean(uid, userName)
      if (maxLen - CcInputHelper.getRealLength(ed.toString()) <
          CcInputHelper.getRealLength(insertUser.displayText)
      ) {
        Utils.getApp()
            .toast(R.string.over_max_len)
      } else if (!hasIn(editText, uid)) {
        insertUserSpan(ed, insertUser.spannableString)
      } else {
        Utils.getApp()
            .toast(R.string.already_at)
      }
    }
  }

  /**
   * 获取@列表，在输入框内容处理为一行输入框后的@数据，需要配合getEditTextEnter1使用
   */
  fun getAtList1Enter(editText: SpXEditText): List<AtBean> {
    return getAtList(editText, true)
  }

  /**
   * 获取@列表，在输入框内容处理为一行输入框后的@数据，需要配合getEditTextEnter2使用
   */
  fun getAtList2Enter(editText: SpXEditText): List<AtBean> {
    return getAtList(editText, false)
  }

  /**
   * 获取多个回车合并为1个回车的字符串
   */
  fun getEditTextEnter1(editText: EditText): Editable {
    return getEditText(editText, true)
  }

  /**
   * 获取多个回车合并为2个回车的字符串
   */
  fun getEditTextEnter2(editText: EditText): Editable {
    return getEditText(editText, false)
  }

  /**使用AimyInputHelper会导致输入2个英文字母无法输入，删除@的时候还出现@效果消失的情况，所以单独写一个*/
  fun setInputFilter(
      edit: SpXEditText,
      maxLen: Int
  ) {
    edit.filters = arrayOf(
        SpeditFilter(
            maxLen
        )
    )
  }

  /**
   * 获取at的列表(文字处理后的)，需要配合getEditTextEnter1或getEditTextEnter2使用
   */
  private fun getAtList(editText: SpXEditText, oneEnter: Boolean): List<AtBean> {
    val list = ArrayList<AtBean>()
    val result = if (oneEnter) getEditTextEnter1(editText) else getEditTextEnter2(editText)
    val dataSpans = result.getSpans(0, result.length, AtBean::class.java)
    for (user in dataSpans) {
      list.add(
          AtBean(
              user.uid, user.name, result.getSpanStart(user),
              user.displayText.length, 0
          )
      )
    }
    return list
  }

  /**
   * 判断输入框中是否已经存在@的用户
   */
  private fun hasIn(editText: SpXEditText, uid: Long): Boolean {
    val has = false
    editText.text?.let { ed ->
      val dataSpans = ed.getSpans(0, editText.length(), AtBean::class.java)
      for ((id, _) in dataSpans) {
        if (id == uid) {
          return true
        }
      }
    }
    return has
  }

  /**
   * 插入@的用户
   */
  private fun insertUserSpan(editable: Editable, text: CharSequence) {
    var start = Selection.getSelectionStart(editable)
    var end = Selection.getSelectionEnd(editable)
    if (end < start) {
      val temp = start
      start = end
      end = temp
    }
    editable.replace(start, end, text)
  }

  /**
   * 限制输入长度和禁止开头输入空字符
   */
  private class SpeditFilter(max: Int) : InputFilter {
    private val maxLen: Int = max
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence {
      source?.let {
        if (CcInputHelper.getRealLength(source) +
            (if (dest == null) 0 else CcInputHelper.getRealLength(dest)) > maxLen
        ) {
          Utils.getApp()
              .toast(R.string.over_max_len)
          return ""
        }
      }
      return if (dstart == 0 && !source.isNullOrEmpty()) {
        delStartEmptyChar(source) //禁止在开头输入空格和换行
      } else {
        source ?: ""
      }
    }

    /**
     * 去掉前面空字符
     */
    private fun delStartEmptyChar(cs: CharSequence): CharSequence {
      return if (cs.startsWith(" ") || cs.startsWith("\n")) {
        return delStartEmptyChar(cs.subSequence(1, cs.length))
      } else {
        cs
      }
    }
  }

  /**
   * 获取输入框中的字符串,
   * @param oneEnter true多个回车合并成一个，false多个回车合并成2个
   */
  private fun getEditText(editText: EditText, oneEnter: Boolean): Editable {
    //①去掉前面的空字符
    val cs1 = delStartEmptyChar(editText.editableText)
    //②去掉后面的空字符
    val cs2 = delEndEmptyChar(cs1)
    //③去掉回车间的空字符
    val cs3 = delEnterMidSpace(cs2)
    return if (oneEnter) {
      multiEnterTo1(cs3)
    } else {
      multiEnterTo2(cs3)
    }
  }

  /**
   * 多行回车变成1个回车
   */
  private fun multiEnterTo1(cs: Editable): Editable {
    return if (cs.contains("\n\n")) { //包含多个回车都处理掉
      val index = cs.indexOf("\n\n")
      return multiEnterTo1(cs.replace(index, index + 2, "\n"))
    } else {
      cs
    }
  }

  /**
   * 多行回车变成2个回车(中间空一行)
   */
  private fun multiEnterTo2(cs: Editable): Editable {
    return if (cs.contains("\n\n\n")) { //包含多个回车都处理掉
      val index = cs.indexOf("\n\n\n")
      return multiEnterTo2(cs.replace(index, index + 3, "\n\n"))
    } else {
      cs
    }
  }

  /**
   * 防止出现"回车+空格+回车"
   */
  private fun delEnterMidSpace(cs: Editable): Editable {
    return if (cs.contains("\n")) {
      val delArray = mutableListOf<Pair<Int, Int>>()
      val arrayCS = cs.split("\n")
      val sbTemp = SpannableStringBuilder()
      for (shortCS in arrayCS) {
        if (sbTemp.isNotEmpty()) sbTemp.append("\n")
        if (shortCS.isNotEmpty() && shortCS.trim().isBlank()) {
          delArray.add(Pair(sbTemp.length, sbTemp.length + shortCS.length))
        }
        sbTemp.append(shortCS)
      }
      var csTemp = cs
      for (i in delArray.size - 1 downTo 0) {
        val pair = delArray[i]
        csTemp = csTemp.delete(pair.first, pair.second)
      }
      csTemp
    } else {
      cs
    }
  }

  /**
   * 去掉前面空字符
   */
  fun delStartEmptyChar(cs: Editable): Editable {
    return if (cs.startsWith(" ") || cs.startsWith("\n")) {
      return delStartEmptyChar(cs.delete(0, 1))
    } else {
      cs
    }
  }

  /**
   * 去掉后面空字符
   */
  private fun delEndEmptyChar(cs: Editable): Editable {
    return when {
      //裁掉末尾是回车的字符
      cs.endsWith("\n") ->
        return delEndEmptyChar(cs.delete(cs.length - 1, cs.length))
      //末尾是空格的字符
      cs.endsWith(" ") -> {
        val spans = cs.getSpans(0, cs.length, AtBean::class.java)
        if (spans.isNotEmpty()) {
          val endStr = spans[spans.size - 1].displayText
          if (cs.endsWith(endStr)) return cs
        }
        //如果是普通的空格，则删除
        delEndEmptyChar(cs.delete(cs.length - 1, cs.length))
      }
      //正常字符，直接返回
      else -> cs
    }
  }

  /**
   * 转换需要展示的@效果
   */
  fun getAtSpan(
      content: String,
      atList: MutableList<AtBean>? = null,
      spanColorNormal: Int = ColorUtils.getColor(R.color.style_Primary),
      spanColorPress: Int = ColorUtils.getColor(R.color.style_PrimaryDark),
      click: ((at: AtBean) -> Unit)? = null
  ): SpannableStringBuilder {
    val result = SpannableStringBuilder()
    result.append(content)
    if (!atList.isNullOrEmpty()) {
      atList.forEach {
        val index = it.index
        val len = it.len
        if (index != null && len != null && len > 0 && index + len <= content.length) {
          val stringClick = SpannableString(content.substring(index, index + len))
          stringClick.setSpan(
              object : TouchableSpan(spanColorNormal, spanColorPress, Color.TRANSPARENT) {
                override fun onClick(widget: View) {
                  if (widget is ClickPreventableTextView) {
                    widget.preventNextClick()
                  }
                  click?.invoke(it)
                }
              }, 0, stringClick.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
          )
          result.replace(index, index + len, stringClick)
        }
      }
    }
    return result
  }
}