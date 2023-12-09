package com.vanniktech.emoji

import android.text.InputFilter
import android.text.Spanned

/**
 * Edit.filters = arrayOf(EmojiMaxLenFilter(maxLen){})
 * 一个表情按照长度为1进行计算
 * Author:Khaos
 * Date:2023/12/9
 * Time:14:04
 */
class EmojiMaxLenFilter(private val maxLen: Int, var callOverMax: ((max: Int) -> Unit)? = null) : InputFilter {
  /**
   * @param source    输入的内容
   * @param start     输入的内容的起始位置，即0
   * @param end       输入的内容的结束位置，也就是source的长度
   * @param dest      已存在的内容
   * @param dstart    光标在已存在的内容的起始位置
   * @param dend      光标在已存在的内容的结束位置
   * @return 输入内容
   * 如果返回值为 null，则表示接受全部的输入内容，不作任何处理
   * 如果返回值为一个空字符串 ""，则表示不接受任何输入，即丢弃用户输入的字符
   * 如果返回值为一个 CharSequence 类型的字符串，则表示替换用户输入的当前文本段，具体将替换从 start 到 end 的源字符中间的一段，返回的字符串就是要替换的内容
   */
  override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
    //已经输入的长度
    val existLen = dest.length - invokeAllEmoji(dest).sumOf { a -> a.range.last - a.range.first - 1 }
    val replaceStr = dest.subSequence(dstart, dend)
    //需要替换的长度
    val replaceLen = replaceStr.length - invokeAllEmoji(replaceStr).sumOf { a -> a.range.last - a.range.first - 1 }
    //最大可输入长度
    var keep = maxLen - (existLen - replaceLen)
    return if (keep <= 0) {
      callOverMax?.invoke(maxLen)
      ""//返回一个空字符串 ""，表示清除输入，即不显示输入的字符
    } else if (keep >= end - start) {
      null //不进行任何过滤，保留输入的字符
    } else {
      val inputEmoji = invokeAllEmoji(source)
      //将要输入的长度
      val addLen = source.length - inputEmoji.sumOf { a -> a.range.last - a.range.first - 1 }
      if (addLen <= keep) return source//如果需要输入的内容满足输入长度，则直接输入
      keep += start//需要裁切的开始位置
      if (inputEmoji.isEmpty()) {//如果没有emoji表情，则直接按照系统方法裁切即可
        if (Character.isHighSurrogate(source[keep - 1])) {
          --keep
          if (keep == start) {
            callOverMax?.invoke(maxLen)
            return ""
          }
        }
        source.subSequence(start, keep)
      } else {//如果存在emoji表情，则需要判断需要裁切到哪个位置
        var count = 0
        var endIndex = keep
        for (i in 0..source.length) {
          val emoji: EmojiRange? = inputEmoji.firstOrNull { f -> i >= f.range.first && i < f.range.last }
          if (emoji == null || (i == emoji.range.last - 1)) {//如果没有在emoji范围或者是emoji最后一个位置就计数1
            count++
          }
          if (count >= keep) {
            endIndex = emoji?.range?.last ?: (i + 1)
            break
          }
        }
        return source.subSequence(start, endIndex)
      }
    }
  }

  private var mRegex: Regex? = null
  private var mEmojiMap: MutableMap<String, Emoji>? = null

  @Suppress("UNCHECKED_CAST")
  private fun invokeAllEmoji(s: CharSequence?): List<EmojiRange> {
    if (mRegex == null) {
      val emojiManager = EmojiManager
      val cls = emojiManager.javaClass
      val field = cls.getDeclaredField("emojiPattern")
      field.isAccessible = true
      val value = field.get(emojiManager)
      mRegex = value as? Regex
    }
    if (mEmojiMap == null) {
      val emojiManager = EmojiManager
      val cls = emojiManager.javaClass
      val field = cls.getDeclaredField("emojiMap")
      field.isAccessible = true
      val value = field.get(emojiManager)
      mEmojiMap = (value as? MutableMap<String, Emoji>)
    }
    return findAllEmojis(s, mRegex, mEmojiMap ?: mutableMapOf())
  }


  private fun findAllEmojis(text: CharSequence?, emojiPattern: Regex?, emojiMap: MutableMap<String, Emoji>): List<EmojiRange> {
    if (!text.isNullOrEmpty()) {
      return emojiPattern?.findAll(text)?.mapNotNull {
        val emoji = findEmoji(it.value, emojiMap)
        if (emoji != null) {
          EmojiRange(emoji, IntRange(it.range.first, it.range.last + 1))
        } else {
          null
        }
      }
        .orEmpty()
        .toList()
    }
    return emptyList()
  }

  private fun findEmoji(candidate: CharSequence, emojiMap: MutableMap<String, Emoji>): Emoji? {
    return emojiMap[candidate.toString()]
  }
}