package cc.ab.base.ui.item

import android.view.*
import androidx.annotation.LayoutRes
import com.drakeet.multitype.ItemViewBinder

/**
 * @Description 使用BaseViewHolder引用LayoutContainer解决findViewById问题
 * @Author：CASE
 * @Date：2021/2/20
 * @Time：10:42
 */
abstract class BaseItemView<T> : ItemViewBinder<T, BaseViewHolder>() {
  //<editor-fold defaultstate="collapsed" desc="创建ViewHolder">
  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup) = BaseViewHolder(inflater.inflate(layoutResId(), parent, false))
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="绑定数据">
  //holder可以获得holder.layoutPosition
  override fun onBindViewHolder(holder: BaseViewHolder, item: T) {
    holder.apply(fillData(holder.itemView, item))
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类必须重新的方法">
  //xml布局
  @LayoutRes
  protected abstract fun layoutResId(): Int

  //加载数据(返回值是为了解决findViewById)
  abstract fun fillData(holder: View, item: T): BaseViewHolder.() -> Unit
  //</editor-fold>
}