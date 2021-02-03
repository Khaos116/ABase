package cc.ab.base.widget.span;

import android.graphics.*;
import android.text.style.ReplacementSpan;
import com.blankj.utilcode.util.SizeUtils;

/**
 * Description: https://blog.csdn.net/industriously/article/details/53493392?utm_source=blogxgwz9
 *
 * @author: caiyoufei
 * @date: 2020/1/4 20:02
 */
public class RadiusBackgroundSpan extends ReplacementSpan {
  private int bgColor;
  private int textColor = 0;
  private int textHalfWidthAdd;
  private int topOffset;
  private int bottomOffset;
  private int textRealWidth;
  private int radius;
  private int textSizePx;

  //<editor-fold defaultstate="collapsed" desc="构造函数">
  public RadiusBackgroundSpan(int bgColor, int radius) {
    this.bgColor = bgColor;
    this.radius = radius;
    this.textHalfWidthAdd = radius;
  }

  public RadiusBackgroundSpan(int bgColor, int textColor, int radius) {
    this.bgColor = bgColor;
    this.textColor = textColor;
    this.radius = radius;
    this.textHalfWidthAdd = SizeUtils.dp2px(8);
    this.topOffset = SizeUtils.dp2px(2);
    this.bottomOffset = topOffset;
  }

  public RadiusBackgroundSpan(int mBgColor, int mTextColor, int mTextHalfWidthAdd, int mTopOffset,
      int mBottomOffset, int mRadius, int mTextSizePx) {
    bgColor = mBgColor;
    textColor = mTextColor;
    textHalfWidthAdd = mTextHalfWidthAdd;
    topOffset = mTopOffset;
    bottomOffset = mBottomOffset;
    radius = mRadius;
    textSizePx = mTextSizePx;
  }
  //</editor-fold>

  @Override
  public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
    if (textSizePx > 0) paint.setTextSize(textSizePx);
    textRealWidth = (int) (paint.measureText(text, start, end) + 2 * textHalfWidthAdd);
    //mSize就是span的宽度，span有多宽，开发者可以在这里随便定义规则
    //我的规则：这里text传入的是SpannableString，start，end对应setSpan方法相关参数
    //可以根据传入起始截至位置获得截取文字的宽度，最后加上左右两个圆角的半径得到span宽度
    return textRealWidth;
  }

  @Override
  public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
    int color = paint.getColor();//保存文字颜色
    paint.setColor(bgColor);//设置背景颜色
    paint.setAntiAlias(true);// 设置画笔的锯齿效果
    RectF oval = new RectF(x, y + paint.ascent() - topOffset - 3, x + textRealWidth, y + paint.descent() + bottomOffset - 3);
    //设置文字背景矩形，x为span其实左上角相对整个TextView的x值，y为span左上角相对整个View的y值。paint.ascent()获得文字上边缘，paint.descent()获得文字下边缘
    canvas.drawRoundRect(oval, radius, radius, paint);//绘制圆角矩形，第二个参数是x半径，第三个参数是y半径
    paint.setColor(textColor == 0 ? color : textColor);//恢复画笔的文字颜色
    if (textSizePx > 0) paint.setTextSize(textSizePx);
    canvas.drawText(text, start, end, x + textHalfWidthAdd, y - 2, paint);//绘制文字
  }
}