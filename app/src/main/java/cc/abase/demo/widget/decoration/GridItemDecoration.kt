package cc.abase.demo.widget.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.State

/**
 * Description: 均分固定数量的Grid模式分割线，如果spanSizeOverride会变化请不要使用
 *
 * @author: caiyoufei
 * @date: 2020/4/16 14:32
 */
class GridItemDecoration(private val spanCount: Int, private val spacing: Int,
    private val includeStartEnd: Boolean = false, private val includeTop: Boolean = false,
    private val includeBottom: Boolean = false) : ItemDecoration() {

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
    val position = parent.getChildAdapterPosition(view)
    val column = position % spanCount
    if (includeStartEnd) {//需要左右边缘间距
      outRect.left = spacing - column * spacing / spanCount
      outRect.right = (column + 1) * spacing / spanCount
    } else {//不需要左右边缘间距
      outRect.left = column * spacing / spanCount
      outRect.right = spacing - (column + 1) * spacing / spanCount
    }
    if (includeBottom) {//需要底部边缘间距
      outRect.bottom = spacing
      if (includeTop && position < spanCount) {//需要顶部间距：由于底部统一添加了，所以只需要第一排添加顶部即可
        outRect.top = spacing
      } else {
        outRect.top = 0
      }
    } else {//不需要底部边缘间距
      if (includeTop) {//需要顶部：由于不需要底部，所以统一添加顶部即可
        outRect.top = spacing
      } else {
        outRect.top = if (position < spanCount) 0 else spacing//不需要顶部也不需要底部，所以只需要去除第一排的顶部即可
      }
      outRect.bottom = 0
    }
  }
}