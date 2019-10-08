package cc.ab.base.widget.ninegridview;

import android.content.Context;
import android.widget.ImageView;

import java.util.List;

import me.panpf.sketch.SketchImageView;

/**
 * description: NineGridViewAdapter.
 *
 * @date 2018/10/22 11:06.
 * @author: YangYang.
 */
public abstract class NineGridViewAdapter<T> {
  protected abstract void onDisplayImage(Context context, ImageView imageView, T t);

  protected void onItemImageClick(Context context, ImageView imageView, int index, List<T> list) {
  }

  protected boolean onItemImageLongClick(Context context, ImageView imageView, int index,
      List<T> list) {
    return false;
  }

  protected ImageView generateImageView(Context context) {
    SketchImageView imageView = new SketchImageView(context);
    imageView.setShowPressedStatusEnabled(true);
    imageView.setClickRetryOnDisplayErrorEnabled(false);
    return imageView;
  }
}
