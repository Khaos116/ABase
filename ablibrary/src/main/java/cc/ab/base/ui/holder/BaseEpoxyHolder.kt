package cc.ab.base.ui.holder

import android.view.View
import com.airbnb.epoxy.EpoxyHolder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/24 11:23
 */
open class BaseEpoxyHolder : EpoxyHolder() {
  lateinit var itemView: View

  override fun bindView(view: View) {
    itemView = view
  }

  protected fun <V : View> bind(id: Int): ReadOnlyProperty<BaseEpoxyHolder, V> =
    Lazy { holder: BaseEpoxyHolder, prop ->
      holder.itemView.findViewById(id) as V?
        ?: throw IllegalStateException("View ID $id for '${prop.name}' not found.")
    }

  /**
   * 代理
   */
  private class Lazy<V>(private val initializer: (BaseEpoxyHolder, KProperty<*>) -> V) :
    ReadOnlyProperty<BaseEpoxyHolder, V> {

    private var value: Any? = EMPTY
    override fun getValue(thisRef: BaseEpoxyHolder, property: KProperty<*>): V {
      if (value == EMPTY) {
        value = initializer(thisRef, property)
      }
      @Suppress("UNCHECKED_CAST")
      return value as V
    }

    private object EMPTY
  }
}