package cc.ab.base.widget.nineimageview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import cc.ab.base.ext.ViewExtKt;
import com.blankj.utilcode.util.SizeUtils;
import java.util.ArrayList;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import me.panpf.sketch.request.DisplayOptions;

/**
 * https://github.com/sunfusheng/GlideImageView/blob/master/glideimageview/src/main/java/com/sunfusheng/widget/NineImageView.java
 *
 * @author sunfusheng on 2018/6/19.
 */
public class NineImageView extends ViewGroup {
  //是否强制使用每行3个模式
  private static final Boolean FORCE_MAX_SPAN_COUNT = true;

  private static final int MAX_IMAGE_SIZE = 9;
  private static final int MAX_SPAN_COUNT = 3;

  private List<ImageData> dataSource;
  private int size;
  private int cellWidth;
  private int cellHeight;
  private boolean shouldLoad;
  private int margin;

  private OnItemClickListener onItemClickListener;
  //图片加载配置
  private DisplayOptions mDisplayOptions;

  public NineImageView(Context context) {
    this(context, null);
  }

  public NineImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public NineImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    cellWidth = cellHeight = SizeUtils.dp2px(100);
    margin = SizeUtils.dp2px(5);
  }

  public NineImageView setDisplayOptions(DisplayOptions displayOptions) {
    this.mDisplayOptions = displayOptions;
    return this;
  }

  public void setData(List<ImageData> list) {
    dataSource = list;
    fillData();
  }

  public void setData(List<ImageData> data, LayoutHelper layoutHelper) {
    this.dataSource = data;
    this.shouldLoad = true;
    mFirstLoad = true;
    if (layoutHelper == null) {
      layoutHelper = getDefaultLayoutHelper(data);
    }

    size = data == null ? 0 : data.size();
    if (size > 0) {
      int index = 0;
      for (; index < size; index++) {
        ImageData imageData = data.get(index);
        imageData.from(imageData, layoutHelper, index);
        ImageCell imageCell = (ImageCell) getChildAt(index);
        if (imageCell == null) {
          imageCell = new ImageCell(getContext());
          final int position = index;
          ViewExtKt.click(imageCell, new Function1<View, Unit>() {
            @Override public Unit invoke(View mView) {
              if (onItemClickListener != null) onItemClickListener.onItemClick(position, mView);
              return null;
            }
          });
          addView(imageCell);
        }
        imageCell.setData(imageData, mDisplayOptions);
        imageCell.setVisibility(VISIBLE);
      }
      for (; index < getChildCount(); index++) {
        getChildAt(index).setVisibility(GONE);
      }
    }
    requestLayout();
  }

  private GridLayoutHelper getDefaultLayoutHelper(List<ImageData> list) {
    int spanCount = list != null ? list.size() : 0;
    if (FORCE_MAX_SPAN_COUNT) {
      spanCount = MAX_SPAN_COUNT;
    } else {
      if (spanCount > MAX_SPAN_COUNT) {
        spanCount = (int) Math.ceil(Math.sqrt(spanCount));
      }
      if (spanCount > MAX_SPAN_COUNT) {
        spanCount = MAX_SPAN_COUNT;
      }
    }
    if (mWidth > 0 && spanCount > 0) {
      cellWidth = cellHeight = (int) ((mWidth - (spanCount - 1) * margin) * 1f / spanCount);
    }
    return new GridLayoutHelper(spanCount, cellWidth, cellHeight, margin);
  }

  public List<ImageData> getData() {
    return dataSource;
  }

  public List<View> geViews() {
    List<View> mViews = new ArrayList<>();
    for (int i = 0; i < getChildCount(); i++) {
      View mChild = getChildAt(i);
      if (mChild.getVisibility() == View.VISIBLE) {
        mViews.add(mChild);
      }
    }
    return mViews;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = 0;
    int height = 0;
    for (int i = 0; i < size; i++) {
      ImageData item = dataSource.get(i);
      int currWidth = item.startX + item.width;
      int currHeight = item.startY + item.height;
      width = currWidth > width ? currWidth : width;
      height = currHeight > height ? currHeight : height;
    }

    int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
    float widthScale = 1;
    float heightScale = 1;

    switch (widthSpecMode) {
      case MeasureSpec.EXACTLY:
      case MeasureSpec.AT_MOST:
        if (width > widthSpecSize) {
          widthScale = widthSpecSize * 1.0f / width;
        }
        break;
      case MeasureSpec.UNSPECIFIED:
      default:
        break;
    }

    switch (heightSpecMode) {
      case MeasureSpec.EXACTLY:
      case MeasureSpec.AT_MOST:
        if (height > heightSpecSize) {
          heightScale = heightSpecSize * 1.0f / height;
        }
        break;
      case MeasureSpec.UNSPECIFIED:
      default:
        break;
    }

    float scale = Math.min(widthScale, heightScale);

    if (scale < 1) {
      width = 0;
      height = 0;
      for (int i = 0; i < size; i++) {
        ImageData item = dataSource.get(i);
        item.startX *= scale;
        item.startY *= scale;
        item.width *= scale;
        item.height *= scale;

        int currWidth = item.startX + item.width;
        int currHeight = item.startY + item.height;
        width = currWidth > width ? currWidth : width;
        height = currHeight > height ? currHeight : height;
      }
    }
    if (mWidth == 0 && widthSpecSize > 0) {
      mWidth = widthSpecSize;
      fillData();
    }
    super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    for (int i = 0; i < dataSource.size(); i++) {
      ImageCell imageCell = (ImageCell) getChildAt(i);
      if (imageCell != null && imageCell.getVisibility() != GONE) {
        ImageData imageData = dataSource.get(i);
        imageCell.measure(MeasureSpec.makeMeasureSpec(imageData.width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(imageData.height, MeasureSpec.EXACTLY));
      }
    }
  }

  // 处理onLayout会被重复调用的问题，只有在重新设置了数据源后才需要重写layoutChildView
  private boolean mFirstLoad = true;
  private int mWidth = 0;

  private void fillData() {
    if (mWidth > 0 && dataSource != null) {
      setData(dataSource, null);
    }
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    // glide在加载完图片后会回调changed为false 的onlayout方法，所以这里做判断
    if (changed || mFirstLoad) {
      mFirstLoad = false;
      for (int i = 0; i < size; i++) {
        ImageCell imageCell = (ImageCell) getChildAt(i);
        if (imageCell != null && imageCell.getVisibility() != GONE) {
          ImageData imageData = dataSource.get(i);
          imageCell.layout(imageData.startX, imageData.startY,
              imageData.startX + imageCell.getMeasuredWidth(),
              imageData.startY + imageCell.getMeasuredHeight());
          if (shouldLoad) {
            shouldLoad = false;
            imageCell.setData(imageData, mDisplayOptions);
          }
        }
      }
    }
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  public interface OnItemClickListener {
    void onItemClick(int position, View view);
  }
}