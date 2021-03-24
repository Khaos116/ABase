package cc.ab.base.widget.span;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * description: 解决有SpanClick点击事件和TextView点击事件同时出发的TextView.
 *
 * @date 2018/8/13 11:54.
 * @author: YangYang.
 */
public class ClickPreventableTextView extends AppCompatTextView implements View.OnClickListener {
  private boolean preventClick;
  private View.OnClickListener clickListener;

  public ClickPreventableTextView(Context context) {
    super(context);
    init();
  }

  public ClickPreventableTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ClickPreventableTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  private void init() {
    setHighlightColor(Color.TRANSPARENT);
    setMovementMethod(new LinkTouchMovementMethod());
  }

  private Rect rect = new Rect();

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    getLocalVisibleRect(rect);
    if (!rect.contains((int) event.getX(), (int) (event.getY()))) return true;
    if (getMovementMethod() != null) {
      getMovementMethod().onTouchEvent(this, (Spannable) getText(), event);
    }
    return super.onTouchEvent(event);
  }

  /**
   * ClickSpan点击事件出发后调用
   */
  public void preventNextClick() {
    preventClick = true;
  }

  @Override
  public void setOnClickListener(View.OnClickListener listener) {
    this.clickListener = listener;
    super.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    if (preventClick) {
      preventClick = false;
    } else if (clickListener != null) {
      clickListener.onClick(v);
    }
  }
}
