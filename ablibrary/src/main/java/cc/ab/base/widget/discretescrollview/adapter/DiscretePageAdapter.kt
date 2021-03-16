package cc.ab.base.widget.discretescrollview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import cc.ab.base.ext.*
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import cc.ab.base.widget.discretescrollview.listener.OnItemClickListener

/**
 * @Description
 * @Author：CASE
 * @Date：2021/3/16
 * @Time：18:18
 */
class DiscretePageAdapter<T, V : ViewBinding>(
    private val creator: DiscreteHolderCreator<T, V>,
    private val datas: MutableList<T> = mutableListOf(),
    var onItemClickListener: OnItemClickListener<T>? = null,
) : RecyclerView.Adapter<DiscreteHolder<T, V>>() {

  //创建Holder
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscreteHolder<T, V> {
    return creator.createHolder(creator.loadViewBinding(LayoutInflater.from(parent.context), parent))
  }

  //绑定数据
  override fun onBindViewHolder(holder: DiscreteHolder<T, V>, position: Int) {
    //更新数据
    holder.updateUI(datas[position], holder.viewBinding, position, itemCount)
    //点击事件
    if (onItemClickListener != null) {
      holder.itemView.click { onItemClickListener?.onItemClick(position, datas[position]) }
      holder.itemView.pressEffectAlpha(0.9f)
    } else {
      holder.itemView.pressEffectDisable()
    }
  }

  //数据大小
  override fun getItemCount() = datas.size
}