package cc.ab.base.ui.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import cc.ab.base.ext.click
import com.drakeet.multitype.ItemViewBinder
import com.dylanc.viewbinding.base.ViewBindingUtil

/**
 * @Description 使用BaseViewHolder嵌套ViewBinding解决findViewById问题
 * @Author：Khaos
 * @Date：2021/2/20
 * @Time：10:42
 */
abstract class BaseBindItemView<T, V : ViewBinding>(var onItemClick: ((item: T) -> Unit)? = null) : ItemViewBinder<T, BaseViewHolder<V>>() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  protected var mLayoutPosition = 0
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="创建ViewHolder">
  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): BaseViewHolder<V> {
    return BaseViewHolder(ViewBindingUtil.inflateWithGeneric(this, inflater, parent, false))
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="绑定数据">
  //holder可以获得holder.layoutPosition
  override fun onBindViewHolder(holder: BaseViewHolder<V>, item: T) {
    //写在fillData前，方便fillData修改点击事件
    if (onItemClick == null) {
      holder.itemView.setOnClickListener(null)
    } else {
      holder.itemView.click { onItemClick?.invoke(item) }
    }
    mLayoutPosition = holder.layoutPosition
    fillData(holder, item)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类必须重新的方法">
  //加载数据
  abstract fun fillData(holder: BaseViewHolder<V>, item: T)
  //</editor-fold>
}