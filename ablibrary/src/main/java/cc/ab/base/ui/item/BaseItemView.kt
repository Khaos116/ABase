package cc.ab.base.ui.item

import android.view.*
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.drakeet.multitype.ItemViewBinder

/**
 * Author:CASE
 * Date:2021-1-2
 * Time:15:53
 */
abstract class BaseItemView<T> : ItemViewBinder<T, RecyclerView.ViewHolder>() {
  //<editor-fold defaultstate="collapsed" desc="加载XML">
  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup) =
      object : RecyclerView.ViewHolder(inflater.inflate(layoutResId(), parent, false)) {}
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="加载数据">
  override fun onBindViewHolder(holder: ViewHolder, item: T) = fillData(holder, holder.itemView, item)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类必须重新的方法">
  //xml布局
  @LayoutRes
  protected abstract fun layoutResId(): Int

  //加载数据(传holder主要是为了获得holder.layoutPosition)
  protected abstract fun fillData(holder: ViewHolder, itemView: View, item: T)
  //</editor-fold>
}