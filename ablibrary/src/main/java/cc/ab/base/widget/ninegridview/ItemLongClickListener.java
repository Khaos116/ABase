package cc.ab.base.widget.ninegridview;

import android.content.Context;
import android.widget.ImageView;

import java.util.List;

/**
 * description: ItemLongClickListener.
 *
 * @date 2018/10/22 11:06.
 * @author: YangYang.
 */
public interface ItemLongClickListener<T> {
  boolean onItemLongClick(Context context, ImageView imageView, int index, List<T> list);
}
