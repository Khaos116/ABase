package cc.ab.base.utils

import android.text.InputFilter
import android.widget.EditText
import cc.ab.base.ext.toast
import com.blankj.utilcode.util.LogUtils

/**
 *description: 通用的输入限制工具类.
 *@date 2018/12/24 20:57.
 *@author: YangYang.
 */
object CcInputHelper {

  /**
   * 通用的输入限制
   * * 限制如下：
   * 1.在开头⽆法输⼊空格，
   * 2.如果是删除部分内容导致空格，则在相关内容被删除的同时也⾃动删除前⾯的空格
   * 3.若连续输⼊空格最多只能输⼊到相应⾏的末尾，不会因为空格触发前⾯已输⼊内容换⾏，再输⼊⽂字时新的⽂字出现在下⼀⾏（系统功能）
   * 4.⽤户的某⼀粘贴操作会导致字数、换⾏数超出限制，则在⽤户点击粘贴后吐司提示“字数/换⾏超出限制”，粘贴不成功
   * 5.限制最大输入换行的数量
   * @param maxLength 输入的最大字符数；-1：不限制
   * @param maxEnter 可以输入的最大换行数；-1：不限制
   */
  fun wrapCommLimit(
    editText: EditText,
    maxLength: Int,
    maxEnter: Int,
    inputLengthCallBack: ((hasInputLength: Int, maxLength: Int) -> Unit)? = null
  ) {
    inputLimit(
      editText = editText,
      maxLength = maxLength,
      maxLengthCallback = {
        editText.context.toast("字数超出限制")
      },
      startBanWordsList = mutableListOf("\n", " "),
      startBanWordsCallBack = {
        if (it == "\n") {
          LogUtils.e("AimyFunInputHelper 开头禁止输入换行")
        } else if (it == " ") {
          LogUtils.e("AimyFunInputHelper 开头禁止输入空格")
        }
      },
      maxCharMap = mapOf('\n' to maxEnter),
      maxCharCallBack = { char, num ->
        if (char == '\n' && num > 0) {
          editText.context.toast("换行超出限制")
        }
      },
      inputLengthCallBack = inputLengthCallBack
    )
  }

  /**
   * 通用的个数输入限制，数字英文汉字表情都算1
   */
  fun wrapCommCountLimit(
    editText: EditText,
    maxCount: Int,
    maxEnter: Int,
    inputCountCallBack: ((hasInputCount: Int, maxCount: Int) -> Unit)? = null
  ) {
    inputLimit(
      editText = editText,
      maxCount = maxCount,
      maxCountCallback = {
        editText.context.toast("字数超出限制")
      },
      startBanWordsList = mutableListOf("\n", " "),
      startBanWordsCallBack = {
        if (it == "\n") {
          LogUtils.e("AimyFunInputHelper 开头禁止输入换行")
        } else if (it == " ") {
          LogUtils.e("AimyFunInputHelper 开头禁止输入空格")
        }
      },
      maxCharMap = mapOf('\n' to maxEnter),
      maxCharCallBack = { char, num ->
        if (char == '\n' && num > 0) {
          editText.context.toast("换行超出限制")
        }
      },
      inputCountCallBack = inputCountCallBack
    )
  }

  /**
   * @param maxLength 最大可输入字符，和xml中的length会有冲突
   * @param maxLengthCallback 超出最大可输入长度的回调
   * @param maxCount 最大可输入个数，和maxLength同时设置时会以length为准
   * @param maxCountCallback 超出最大可输入个数的回调
   * @param isCenterCopyClipMax 中间粘贴时超出最大可输入是否截取
   * @param banWordsList 禁止输入的字符列表,禁止输入回车会导致单词模式输入有问题,需要禁止输入回车设置回车最大输入0就行了
   * @param banWordsCallBack 输入了禁止输入字符的回调
   * @param startBanWordsList 开头禁止输入的字符列表
   * @param startBanWordsCallBack 开头输入了禁止输入的字符的回调
   * @param maxCharMap 指定字符可输入最大数量的map集合
   * @param maxCharCallBack 指定字符输入达到最大限制的回调
   * @param inputLengthCallBack 输入的字符长度的实时回调，如果setText存在问题，只是输入
   * @param inputLengthCallBack 输入的字符个数的实时回调，如果setText存在问题，只是输入
   */
  fun inputLimit(
    editText: EditText,
    maxLength: Int = -1,
    maxLengthCallback: (() -> Unit)? = {
      editText.context.toast("字数超出限制")
    },
    maxCount: Int = -1,
    maxCountCallback: (() -> Unit)? = {
      editText.context.toast("字数超出限制")
    },
    isCenterCopyClipMax: Boolean = false,
    banWordsList: List<String>? = null,
    banWordsCallBack: ((words: String) -> Unit)? = null,
    startBanWordsList: List<String>? = null,
    startBanWordsCallBack: ((words: String) -> Unit)? = null,
    maxCharMap: Map<Char, Int>? = null,
    maxCharCallBack: ((char: Char, num: Int) -> Unit)? = null,
    inputLengthCallBack: ((hasInputLength: Int, maxLength: Int) -> Unit)? = null,
    inputCountCallBack: ((hasInputCount: Int, maxCount: Int) -> Unit)? = null
  ) {
    //最大长度输入限制的Filter
    var maxLengthFilter: InputFilter? = null
    //最大个数输入限制的Filter
    var maxCountFilter: InputFilter? = null
    if (maxLength > -1) {
      maxLengthFilter = InputFilter { source, start, end, dest, dstart, dend ->
        var hasInputLength = 0
        val result: CharSequence = if (maxLength < 0) {
          //长度无限制
          source
        } else {
          val keep =
            maxLength - (getRealLength(dest) - getRealLength(dest.subSequence(dstart, dend)))
          when {
            keep < 0 -> {
              //超出最大字符数
              maxLengthCallback?.invoke()
              hasInputLength = maxLength - keep
              ""
            }
            keep < getRealLength(source.subSequence(start, end)) -> {
              //输入之后长度超出，需要截取
              maxLengthCallback?.invoke()
              //粘贴截取最大可输入字符
              if (dest.length > dstart) {
                //中间插入
                if (isCenterCopyClipMax) {
                  val result = subString(source.toString(), start, end, keep)
                  hasInputLength = maxLength - (keep - getRealLength(result))
                  result
                } else {
                  ""
                }
              } else {
                //最后
                val result = subString(source.toString(), start, end, keep)
                hasInputLength = maxLength - (keep - getRealLength(result))
                result
              }
            }
            else -> {
              hasInputLength = maxLength - (keep - getRealLength(source))
              source
            }
          }
        }
        if (maxLength > 0) {
          inputLengthCallBack?.invoke(hasInputLength, maxLength)
        }
        result
      }
    } else if (maxCount > -1) {
      maxCountFilter = InputFilter { source, start, end, dest, dstart, dend ->
        var hasInputCount = 0
        val result: CharSequence = if (maxCount < 0) {
          //长度无限制
          source
        } else {
          val keep =
            maxCount - (getRealCount(dest) - getRealCount(dest.subSequence(dstart, dend)))
          when {
            keep < 0 -> {
              //超出最大字符数
              maxCountCallback?.invoke()
              hasInputCount = maxCount - keep
              ""
            }
            keep < getRealCount(source.subSequence(start, end)) -> {
              //输入之后长度超出，需要截取
              maxCountCallback?.invoke()
              if (dest.length > dstart) {
                //中间插入
                if (isCenterCopyClipMax) {
                  val result = subCountString(source.toString(), start, end, keep)
                  hasInputCount = maxCount - (keep - getRealCount(result))
                  result
                } else {
                  ""
                }
              } else {
                //最后
                val result = subCountString(source.toString(), start, end, keep)
                hasInputCount = maxCount - (keep - getRealCount(result))
                result
              }
            }
            else -> {
              hasInputCount = maxCount - (keep - getRealCount(source))
              source
            }
          }
        }
        if (maxCount > 0) {
          inputCountCallBack?.invoke(hasInputCount, maxCount)
        }
        result
      }
    }

    //禁止输入字符限制的Filter
    var banFilter: InputFilter? = null
    banWordsList?.let { list ->
      if (list.isNotEmpty()) {
        banFilter = InputFilter { source, start, end, dest, dstart, dend ->
          replaceBanWords(source, list, banWordsCallBack)
        }
      }
    }
    //开头禁止输入限制的Filter
    var startFilter: InputFilter? = null
    startBanWordsList?.let { list ->
      if (list.isNotEmpty()) {
        startFilter = InputFilter { source, start, end, dest, dstart, dend ->
          if (source.isNotEmpty() && dstart == 0) {
            replaceStartBanWords(source, list, startBanWordsCallBack)
          } else {
            source
          }
        }
      }
    }
    //字符输入最大数量限制
    var maxNumCharInputFilter: InputFilter? = null
    maxCharMap?.let { map ->
      maxNumCharInputFilter =
        InputFilter { source, start, end, dest, dstart, dend ->
          val newStr = (editText.text?.toString() ?: "") + source.toString()
          map.forEach { (key, value) ->
            val num = getCharNum(key, newStr)
            if (num != -1 && num > value) {
              maxCharCallBack?.invoke(key, value)
              return@InputFilter ""
            }
          }
          source
        }
    }
    //删除操作的Filter
    val deleteFilter = InputFilter { source, start, end, dest, dstart, dend ->
      if (source.isEmpty() && dend > dstart && dstart == 0) {
        //删除了最前边的
        editText.setText("")
        editText.setText(dest.removeRange(dstart, dend))
        return@InputFilter ""
      }
      source
    }
    val filterList = mutableListOf<InputFilter>()
    maxLengthFilter?.let {
      filterList.add(it)
    }
    maxCountFilter?.let {
      filterList.add(it)
    }
    banFilter?.let {
      filterList.add(it)
    }
    startFilter?.let {
      filterList.add(it)
    }
    maxNumCharInputFilter?.let {
      filterList.add(it)
    }
    filterList.add(deleteFilter)
    if (editText.filters != null) {
      filterList.addAll(editText.filters)
    }
    editText.filters = filterList.toTypedArray()
  }

  //移除开头禁止输入的字符
  private fun replaceStartBanWords(
    source: CharSequence, startBanWordsList: List<String>,
    startBanWordsCallBack: ((words: String) -> Unit)? = null
  ): CharSequence {
    if (source.isEmpty() || startBanWordsList.isEmpty()) {
      return source
    }
    var startHasBanWords = false
    var startBanWords: String? = null
    val size = startBanWordsList.size
    for (i in 0 until size) {
      val words = startBanWordsList[i]
      if (source.startsWith(words)) {
        startHasBanWords = true
        startBanWords = words
      }
    }
    return if (startHasBanWords) {
      startBanWords?.let { words ->
        startBanWordsCallBack?.invoke(words)
      }
      val newSource = source.subSequence(startBanWords?.length ?: 0, source.length)
      replaceStartBanWords(newSource, startBanWordsList)
    } else {
      source
    }
  }

  //替换禁止输入的字符
  private fun replaceBanWords(
    source: CharSequence, banWordsList: List<String>,
    banWordsCallBack: ((words: String) -> Unit)? = null
  ): CharSequence {
    if (source.isEmpty() || banWordsList.isEmpty()) {
      return source
    }
    var result = source
    banWordsList.forEach {
      if (source.contains(Regex(it))) {
        banWordsCallBack?.invoke(it)
      }
      result = result.replace(Regex(it), "")
    }
    return result
  }

  //获取字符串中指定字符的个数
  private fun getCharNum(char: Char, source: String): Int {
    var charNum = 0
    source.forEach {
      if (it == char) {
        charNum++
      }
    }
    return charNum
  }

  /**
   * 获取字符串的真正长度，英文数字1，汉字2，emoji表情4
   */
  fun getRealLength(str: CharSequence): Int {
    var length = 0
    for (i in 0 until str.length) {
      val ascii = Character.codePointAt(str, i)
      if (ascii in 0..255) {
        length++
      } else {
        length += 2
      }
    }
    return length
  }

  /**
   * 获取字符串输入的字符数量，英文数字中文表情都算一个
   */
  fun getRealCount(str: CharSequence): Int {
    var count = 0
    var start = 0
    while (start < str.length) {
      start += getOneCountLength(str.toString(), count, count + 1)
      count++
    }
    return count
  }

  /**
   * 获取一个内容真正的length，英文中文=1，表情=2
   */
  fun getOneCountLength(source: String, start: Int, end: Int): Int {
    return try {
      source.substring(
        source.offsetByCodePoints(0, start),
        source.offsetByCodePoints(0, end)
      ).length
    } catch (e: Exception) {
      val newEnd = end + 1
      getOneCountLength(source, start, newEnd)
    }
  }

  /**
   * 截取指定长度的带Emoji的字符串
   */
  fun subString(source: String, start: Int, end: Int, realLength: Int): String {
    var subEnd = end
    if (subEnd > source.length) {
      subEnd = source.length - 1
    }
    var result = try {
      source.substring(
        source.offsetByCodePoints(0, start),
        source.offsetByCodePoints(0, subEnd)
      )
    } catch (e: Exception) {
      val newEnd = subEnd - 1
      return subString(source, start, newEnd, realLength)
    }
    if (getRealLength(result) > realLength) {
      val newEnd = subEnd - 1
      result = subString(source, start, newEnd, realLength)
    }
    return result
  }

  /**
   * 截取指定个数的带Emoji的字符串
   */
  fun subCountString(source: String, start: Int, end: Int, realCount: Int): String {
    var subEnd = end
    if (subEnd > source.length) {
      subEnd = source.length - 1
    }
    var result = try {
      source.substring(
        source.offsetByCodePoints(0, start),
        source.offsetByCodePoints(0, subEnd)
      )
    } catch (e: Exception) {
      val newEnd = subEnd - 1
      return subCountString(source, start, newEnd, realCount)
    }
    if (getRealCount(result) > realCount) {
      val newEnd = subEnd - 1
      result = subCountString(source, start, newEnd, realCount)
    }
    return result
  }
}