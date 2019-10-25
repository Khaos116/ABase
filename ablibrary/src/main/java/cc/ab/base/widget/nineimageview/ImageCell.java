package cc.ab.base.widget.nineimageview;

import android.content.Context;
import android.util.AttributeSet;
import cc.ab.base.R;
import cc.ab.base.utils.RandomPlaceholder;
import com.blankj.utilcode.util.SizeUtils;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchImageView;
import me.panpf.sketch.display.FadeInImageDisplayer;
import me.panpf.sketch.request.DisplayOptions;
import me.panpf.sketch.request.ShapeSize;
import me.panpf.sketch.shaper.RoundRectImageShaper;

/**
 * @author sunfusheng on 2018/6/19.
 */
public class ImageCell extends SketchImageView {
  private DisplayOptions displayOptions;

  public ImageCell(Context context) {
    this(context, null);
  }

  public ImageCell(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ImageCell(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    this.setShowPressedStatusEnabled(true);
    this.setClickRetryOnDisplayErrorEnabled(false);
    displayOptions = new DisplayOptions();
    displayOptions.setErrorImage(R.drawable.svg_placeholder_fail);
  }

  public void setData(ImageData imageData, DisplayOptions options) {
    if (imageData != null) {
      load(imageData.url, options);
    }
  }

  public void load(final String url, final DisplayOptions options) {
    if (getWidth() > 0) {
      if (options == null) {
        int holder = RandomPlaceholder.Companion.getInstance().getPlaceHolder(url);
        displayOptions.setLoadingImage(holder);
        //圆角
        RoundRectImageShaper shaper = new RoundRectImageShaper(SizeUtils.dp2px(5f));
        //shaper.setStroke(colorStroke, 1)
        displayOptions.setShaper(shaper);
        //图片尺寸
        ShapeSize shapeSize = new ShapeSize(getWidth(), getHeight(), ScaleType.CENTER_CROP);
        displayOptions.setShapeSize(shapeSize);
        this.setOptions(displayOptions);
        this.getOptions().setDisplayer(new FadeInImageDisplayer());
      }
      // DisplayHelper
      Sketch.with(getContext())
          .display(url, this)
          .commit();
    } else {
      this.post(new Runnable() {
        @Override public void run() {
          load(url, options);
        }
      });
    }
  }
}