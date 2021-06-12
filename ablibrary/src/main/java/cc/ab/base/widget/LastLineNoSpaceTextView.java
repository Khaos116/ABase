package cc.ab.base.widget;

import android.content.Context;
import android.graphics.Rect;
import android.text.Layout;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * Description:去除TextView设置lineSpacingExtra后，最后一行多出的空白
 * https://www.cnblogs.com/tangZH/p/11985745.html
 *
 * @author: Khaos
 * @date: 2019/12/7 16:37
 */
public class LastLineNoSpaceTextView extends AppCompatTextView {
  private Rect mRect;

  public LastLineNoSpaceTextView(Context context) {
    super(context);
    init();
  }

  private void init() {
    mRect = new Rect();
  }

  public LastLineNoSpaceTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public LastLineNoSpaceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  protected void onMeasure(int i, int i2) {
    super.onMeasure(i, i2);
    int measuredHeight = getMeasuredHeight() - getLastLineSpace();
    setMeasuredDimension(getMeasuredWidth(), measuredHeight);
  }

  public int getLastLineSpace() {
    int lastLineIndex = getLineCount() - 1;
    if (lastLineIndex < 0) {
      return 0;
    }
    Layout layout = getLayout();
    int baseline = getLineBounds(lastLineIndex, mRect);
    if (getMeasuredHeight() - getPaddingTop() - getPaddingBottom() != layout.getHeight()) {
      return 0;
    }
    int fontHeight = baseline + layout.getPaint().getFontMetricsInt().descent;
    int lineHeight = mRect.bottom;
    return lineHeight - fontHeight;
  }
}