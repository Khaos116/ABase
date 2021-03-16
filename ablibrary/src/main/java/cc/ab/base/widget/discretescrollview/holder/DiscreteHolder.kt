package cc.ab.base.widget.discretescrollview.holder

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * @Description
 * @Author：CASE
 * @Date：2021/3/16
 * @Time：18:16
 */
abstract class DiscreteHolder<T, V : ViewBinding>(val viewBinding: V) : RecyclerView.ViewHolder(viewBinding.root) {
  //更新UI
  abstract fun updateUI(data: T?, binding: V, position: Int, count: Int)
}