package cc.ab.base.widget.ninegridview;

import android.content.Context;
import android.widget.ImageView;

import java.util.List;

/**
 * description: ItemClickListener.
 *
 * @date 2018/10/22 11:05.
 * @author: YangYang.
 */
public interface ItemClickListener<T> {
  void onItemClick(Context context, ImageView imageView, int index, List<T> list);
}
