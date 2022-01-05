package cc.ab.base.widget.discretescrollview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import cc.ab.base.ext.*
import cc.ab.base.ui.item.BaseViewHolder
import cc.ab.base.widget.discretescrollview.listener.OnItemClickListener
import com.dylanc.viewbinding.base.inflateBindingWithGeneric

/**
 * @Description
 * @Author：Khaos
 * @Date：2021/3/16
 * @Time：18:18
 */
abstract class DiscretePageAdapter<T, V : ViewBinding>(
  private val datas: MutableList<T> = mutableListOf(),
  var onItemClickListener: OnItemClickListener<T>? = null,
) : RecyclerView.Adapter<BaseViewHolder<V>>() {

  //<editor-fold defaultstate="collapsed" desc="创建ViewHolder">
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<V> {
    return BaseViewHolder(this.inflateBindingWithGeneric(LayoutInflater.from(parent.context), parent, false))
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="绑定数据">
  override fun onBindViewHolder(holder: BaseViewHolder<V>, position: Int) {
    //更新数据
    fillData(datas[position], holder.viewBinding, position, itemCount)
    //点击事件
    if (onItemClickListener != null) {
      holder.itemView.click { onItemClickListener?.onItemClick(position, datas[position]) }
      holder.itemView.pressEffectAlpha(0.9f)
    } else {
      holder.itemView.pressEffectDisable()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据大小">
  override fun getItemCount() = datas.size
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类必须重新的方法">
  //加载数据
  abstract fun fillData(data: T, binding: V, position: Int, count: Int)
  //</editor-fold>
}