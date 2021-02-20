package cc.ab.base.ui.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import cc.ab.base.ext.click
import com.drakeet.multitype.ItemViewBinder

/**
 * @Description 使用BaseViewHolder引用LayoutContainer解决findViewById问题
 * @Author：CASE
 * @Date：2021/2/20
 * @Time：10:42
 */
abstract class BaseItemView<T>(
    protected var onItemClick: ((item: T) -> Unit)? = null
) : ItemViewBinder<T, BaseViewHolder>() {
  //<editor-fold defaultstate="collapsed" desc="创建ViewHolder">
  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup) = BaseViewHolder(inflater.inflate(layoutResId(), parent, false))
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="绑定数据">
  //holder可以获得holder.layoutPosition
  override fun onBindViewHolder(holder: BaseViewHolder, item: T) {
    //写在fillData前，方便fillData修改点击事件
    if (onItemClick == null) {
      holder.itemView.setOnClickListener(null)
    } else {
      holder.itemView.click { onItemClick?.invoke(item) }
    }
    //SDL方式填充数据，解决findViewById的麻烦
    holder.apply(fillData(item))
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类必须重新的方法">
  //xml布局
  @LayoutRes
  protected abstract fun layoutResId(): Int

  //加载数据(返回值是为了解决findViewById)
  abstract fun fillData(item: T): BaseViewHolder.() -> Unit
  //</editor-fold>
}