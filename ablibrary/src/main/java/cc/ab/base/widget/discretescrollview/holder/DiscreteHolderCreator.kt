package cc.ab.base.widget.discretescrollview.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * @Description
 * @Author：CASE
 * @Date：2021/3/16
 * @Time：18:06
 */
abstract class DiscreteHolderCreator<T, V : ViewBinding> {
  //xml布局
  abstract fun loadViewBinding(inflater: LayoutInflater, parent: ViewGroup): V

  //创建Holder
  abstract fun createHolder(binding: V): DiscreteHolder<T, V>
}