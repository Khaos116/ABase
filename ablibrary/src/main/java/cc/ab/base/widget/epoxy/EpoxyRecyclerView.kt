package cc.ab.base.widget.epoxy

import android.content.Context
import android.util.AttributeSet

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/7/4 10:10
 */
class EpoxyRecyclerView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : com.airbnb.epoxy.EpoxyRecyclerView(context, attrs, defStyleAttr) {
  init {
    //延迟移除会导致内存泄漏，所以默认改为不延迟
    setDelayMsWhenRemovingAdapterOnDetach(0)
  }
}