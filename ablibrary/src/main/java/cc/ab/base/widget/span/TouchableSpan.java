package cc.ab.base.widget.span;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;

import org.jetbrains.annotations.NotNull;

/**
 * 为多个TouchableSpan分别添加点击变色
 * Author:Khaos
 * Date:2016/6/1
 * Time:14:06
 */
public abstract class TouchableSpan extends ClickableSpan {
  private boolean mIsPressed;
  private boolean mIsShowUnderLine;
  private int mPressedBackgroundColor;
  private int mNormalTextColor;
  private int mPressedTextColor;

  public TouchableSpan(int normalTextColor, int pressedTextColor, int pressedBackgroundColor) {
    mNormalTextColor = normalTextColor;
    mPressedTextColor = pressedTextColor;
    mPressedBackgroundColor = pressedBackgroundColor;
  }

  public TouchableSpan(int normalTextColor, int pressedTextColor, int pressedBackgroundColor,
      boolean isShowUnderLine) {
    mNormalTextColor = normalTextColor;
    mPressedTextColor = pressedTextColor;
    mPressedBackgroundColor = pressedBackgroundColor;
    mIsShowUnderLine = isShowUnderLine;
  }

  public void setPressed(boolean isSelected) {
    mIsPressed = isSelected;
  }

  @Override
  public void updateDrawState(@NotNull TextPaint ds) {
    super.updateDrawState(ds);
    ds.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
    ds.bgColor = mIsPressed ? mPressedBackgroundColor : Color.TRANSPARENT;
    ds.setUnderlineText(mIsShowUnderLine);
  }
}
