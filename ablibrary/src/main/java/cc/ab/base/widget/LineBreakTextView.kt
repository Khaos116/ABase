package cc.ab.base.widget

import android.content.Context
import android.graphics.Canvas
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * 原文：https://github.com/changer0/LineBreakTextView
 * 可以解决文字显示换行问题
 *
 * @author zhanglulu on 2019/12/9.
 * for 断行 TextView 排版 绘制 <br/>
 *  使用方式： <br/>
 *  1. 设置文本 text
 *  2. 排版 lineBreak
 */

private const val TAG = "LineBreakTextView"

class LineBreakTextView : View {

  /**
   * 段首缩进 字符数
   */
  var paragraphIndentSize = 2

  /**
   * 标题间距 倍数
   */
  var titleSpacingMultiplier = 2.0f

  /**
   * 段间距 倍数
   */
  var paragraphSpacingMultiplier = 1.5f

  /**
   * 正文间距 倍数
   */
  var lineSpacingMultiplier = 1.0f

  /**
   * 最大行数
   */
  var maxLines = Int.MAX_VALUE
    set(value) {
      field = value
      //重新测量
      requestLayout()
    }

  /**
   * 最大高度
   */
  var maxHeight = Int.MAX_VALUE
    set(value) {
      field = value
      //重新测量
      requestLayout()
    }

  /**
   * 文字大小
   */
  var textSizeSp = 18f
    set(value) {
      field = dp2px(value)
      textPaint.textSize = dp2px(value)
    }

  /**
   * 标题文字大小
   */
  var titleSizeSp = 22f
    set(value) {
      field = dp2px(value)
      titlePaint.textSize = dp2px(value)
    }

  /**
   * 文字颜色
   */
  var textColor = 0x000000
    set(value) {
      field = value
      textPaint.color = value
    }

  /**
   * 文字透明度
   */
  var textAlpha = 255
    set(value) {
      field = value
      textPaint.alpha = value
    }

  /**
   * 是否需要标题
   */
  var isNeedTitle = true

  /**
   * 标题文字颜色
   */
  var titleColor = 0x000000
    set(value) {
      field = value
      titlePaint.color = value
    }

  /**
   * 标题是否加粗
   */
  var titleBold = true
    set(value) {
      field = value
      titlePaint.isFakeBoldText = value
    }

  /**
   * 标题文字透明度
   */
  var titleAlpha = 255
    set(value) {
      field = value
      titlePaint.alpha = value
    }

  /**
   * 文字位置
   */
  private val textPositions = ArrayList<TextPosition>()

  /**
   * 行 Y 坐标
   */
  private val textLineYs = ArrayList<Float>()

  /**
   * 布局高度
   */
  private var layoutHeight = 0f

  /**
   * 文本内容(设置后需要调用lineBreak方法)
   */
  var text = ""
    set(value) {
      field = value
      textCharArray = value.toCharArray()
    }

  private var textCharArray: CharArray? = null
  private var textPaint: TextPaint = TextPaint()
  private var titlePaint: TextPaint = TextPaint()

  constructor(ctx: Context) : super(ctx) {
    init(ctx)
  }

  constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
    init(ctx)
  }

  constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
    ctx,
    attrs,
    defStyleAttr
  ) {
    init(ctx)
  }

  private fun init(ctx: Context) {
    textPaint.color = textColor
    textPaint.alpha = textAlpha
    textPaint.textSize = dp2px(textSizeSp)
    textPaint.isAntiAlias = true //抗锯齿

    titlePaint.color = titleColor
    titlePaint.alpha = titleAlpha
    titlePaint.textSize = dp2px(titleSizeSp)
    titlePaint.isFakeBoldText = true
    titlePaint.isAntiAlias = true //抗锯齿
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val width = MeasureSpec.getSize(widthMeasureSpec)
    var height = MeasureSpec.getSize(heightMeasureSpec)

    if (layoutHeight > 0) {
      height = layoutHeight.toInt()
    }
    if (getLines() > maxLines && maxLines - 1 > 0) {
      val textBottomH = textPaint.fontMetrics.bottom.toInt()
      height = (textLineYs[maxLines - 1]).toInt() + paddingBottom + textBottomH
    }
    if (height > maxHeight) {
      height = maxHeight
    }
    setMeasuredDimension(width, height)

  }

  /**
   * 绘制
   */
  override fun draw(canvas: Canvas?) {
    super.draw(canvas)
    for (i in 0 until textPositions.size) {
      val textPosition = textPositions[i]
      val paint = if (textPosition.type == TextPosition.TITLE) {
        titlePaint
      } else {
        textPaint
      }
      canvas?.drawText(textPosition.text, textPosition.x, textPosition.y, paint)
    }
  }

  /**
   * 排版(设置文字后调用)
   */
  fun lineBreak(maxWidth: Int) {
    val availableWidth = maxWidth - paddingRight
    textLineYs.clear()
    textPositions.clear()
    //X 的初始化位置
    val initX = paddingLeft.toFloat()
    var curX = initX
    var curY = paddingTop.toFloat()

    var isNeedCheckParagraphHeadEmptyChar = false //是否检查段首空白字符

    val titleFontMetrics = titlePaint.fontMetrics
    val textFontMetrics = textPaint.fontMetrics
    val lineHeight = textFontMetrics.bottom - textFontMetrics.top

    //首行是否为标题
    var textType = if (isNeedTitle) {
      curY -= titleFontMetrics.top //指定顶点坐标
      TextPosition.TITLE
    } else {
      curY -= textFontMetrics.top //指定顶点坐标
      TextPosition.NORMAL
    }

    val size = textCharArray?.size
    size?.let {
      var i = 0
      while (i < size) {
        val textPosition = TextPosition()
        val c = textCharArray?.get(i)
        if (isNeedCheckParagraphHeadEmptyChar) {
          //空白字符判断
          if (c == ' ' || c == '\u0020' || c == '\u3000') {
            i++
            continue
          }
        }

        //当前文字宽度
        val cW = if (textType == TextPosition.TITLE) {
          titlePaint.measureText(c.toString())
        } else {
          textPaint.measureText(c.toString())
        }

        //位置保存点
        textPosition.x = curX
        textPosition.y = curY
        textPosition.text = c.toString()
        textPosition.type = textType

        //curX 向右移动一个字
        curX += cW

        isNeedCheckParagraphHeadEmptyChar = false
        if (isParagraph(textCharArray, i)) {
          textLineYs.add(curY)
          //如果是段落,再移动一位
          i++
          curX = initX + textPaint.measureText("中") * paragraphIndentSize //段首缩进
          //根据不同的文字类型设置不同的行高
          curY += if (textType == TextPosition.TITLE) {
            (lineHeight * titleSpacingMultiplier)
          } else {
            (lineHeight * paragraphSpacingMultiplier)
          }
          isNeedCheckParagraphHeadEmptyChar = true
          //除了首段，后续段落都为 Normal
          textType = TextPosition.NORMAL

        } else if (isNeedNewLine(textCharArray, i, curX, availableWidth)) {
          textLineYs.add(curY)
          //断行需要回溯
          curX = initX
          curY += lineHeight * lineSpacingMultiplier
        }
        textPositions.add(textPosition)
        //移动下一个游标
        i++
      }
      curY += paddingBottom
      layoutHeight = curY + textFontMetrics.bottom //应加上后面的Bottom

      Log.d(TAG, "总行数： ${textLineYs.size}")
    }
  }

  /**
   * 是否需要另起一行
   */
  private fun isNeedNewLine(
    charArray: CharArray?,
    curIndex: Int,
    curX: Float,
    maxWith: Int
  ): Boolean {
    charArray?.let {
      if (charArray.size <= curIndex + 1) { //需要判断下一个 char
        return false
      }
      //判断下一个 char 是否到达边界
      if (curX + textPaint.measureText(charArray[curIndex + 1].toString()) > maxWith) {
        return true
      }
    }
    if (curX > maxWith) {
      return true
    }
    return false
  }

  /**
   * 是否是段落
   */
  private fun isParagraph(charArray: CharArray?, curIndex: Int): Boolean {
    charArray?.let {
      if (charArray.size <= curIndex + 1) { //需要判断下一个 char
        return false
      }
      if (charArray[curIndex] == '\r' && charArray[curIndex + 1] == '\n') {
        return true
      }
    }
    return false
  }

  /**
   * 获取当前的行数
   */
  fun getLines(): Int {
    return textLineYs.size
  }

  /**
   * 当前文字位置
   */
  class TextPosition {
    companion object {
      const val NORMAL = 0x0
      const val TITLE = 0x1
    }

    var text = ""
    var x = 0f
    var y = 0f
    var type = NORMAL
  }

  private fun dp2px(dipValue: Float): Float {
    val displayMetrics = context.applicationContext.resources.displayMetrics
    return dipValue * displayMetrics.density + 0.5f
  }
}
