package cc.abase.demo.component.decoration

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import androidx.recyclerview.widget.GridLayoutManager
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.epoxy.item.simpleTextItem
import cc.abase.demo.mvrx.MvRxEpoxyController
import cc.abase.demo.widget.ItemDecorationPowerful
import com.airbnb.epoxy.EpoxyItemSpacingDecorator
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.activity_decoration.decorRecycler

/**
 * Description:分割线展示
 * @author: caiyoufei
 * @date: 2020/2/19 14:20
 */
class DecorationActivity : CommTitleActivity() {

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, DecorationActivity::class.java)
      context.startActivity(intent)
    }
  }

  override fun layoutResContentId() = R.layout.activity_decoration

  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_decoration))
    val spanCount = if (System.currentTimeMillis() % 2 == 0L) 1 else 4
    val layoutManager = GridLayoutManager(this, spanCount)
    epoxyController.spanCount = spanCount
    layoutManager.spanSizeLookup = epoxyController.spanSizeLookup
    decorRecycler.layoutManager = layoutManager
    decorRecycler.adapter = epoxyController.adapter
    val hasEdge = System.currentTimeMillis() % 2 == 0L
    val decorator = if (!hasEdge) EpoxyItemSpacingDecorator(20)
    else ItemDecorationPowerful(ItemDecorationPowerful.GRID_DIV, Color.CYAN, 20)
    decorRecycler.addItemDecoration(decorator)
  }

  override fun initData() {
    val datas = mutableListOf<String>()
    for (i in 1..30) datas.add("我是第${i}个元素")
    epoxyController.data = datas
  }

  private val epoxyController = MvRxEpoxyController<MutableList<String>> { list ->
    list.forEachIndexed { index, s ->
      simpleTextItem {
        id(index)
        gravity(Gravity.CENTER)
        bgColor(Color.parseColor("#550fff00"))
        heightDp(70f)
        msg(s)
        spanCount = 1
      }
    }
  }
}