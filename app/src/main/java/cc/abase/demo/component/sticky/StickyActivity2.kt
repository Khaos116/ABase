package cc.abase.demo.component.sticky

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.ab.base.ext.mContext
import cc.ab.base.ext.visible
import cc.abase.demo.R
import cc.abase.demo.bean.local.UserScoreBean
import cc.abase.demo.bean.local.UserStickyBean
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.sticky.adapter.StickyHeaderAdapter2
import cc.abase.demo.component.sticky.widget.StickyHeaderLinearLayoutManager
import cc.abase.demo.epoxy.base.dividerItem
import cc.abase.demo.epoxy.item.sticky2LeftItem
import cc.abase.demo.mvrx.MvRxEpoxyController
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.activity_sticky2.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/4/21 14:34
 */
class StickyActivity2 : CommTitleActivity() {
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, StickyActivity2::class.java)
      context.startActivity(intent)
    }
  }

  override fun layoutResContentId() = R.layout.activity_sticky2

  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_sticky2))
    val manager = StickyHeaderLinearLayoutManager<StickyHeaderAdapter2>(this)
    sticky2Recycler2.layoutManager = manager
    sticky2Recycler1.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
    sticky2Recycler1.setController(leftController)
    sticky2Recycler1.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (sticky2Recycler1.scrollState != 0) sticky2Recycler2.scrollBy(dx, dy) //使右边recyclerView进行联动
      }
    })
    sticky2Recycler2.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (sticky2Recycler2.scrollState != 0) sticky2Recycler1.scrollBy(dx, dy) //使左边边recyclerView进行联动
      }
    })
    leftController.addModelBuildListener {
      sticky2Recycler2?.postDelayed({
        dismissLoadingView()
        sticky2Recycler1Parent?.visible()
        sticky2Recycler2?.visible()
      }, 200)
    }
  }

  override fun initData() {
    showLoadingView()
    sticky2Recycler2.postDelayed({
      var users = mutableListOf<UserStickyBean>()
      //随机增加个学生成绩
      for (i in 0..(Math.random() * 70).toInt()) users.add(UserStickyBean(score = UserScoreBean()))
      //按总成绩排序
      users = users.sortedByDescending { it.score?.scores?.sum() }.toMutableList()
      //添加标题
      users.add(0, UserStickyBean(name = "", title = true))
      sticky2Recycler2?.adapter = StickyHeaderAdapter2(users)
      leftController.data = users
    }, 1500)
  }

  private val leftController = MvRxEpoxyController<MutableList<UserStickyBean>> { list ->
    list.forEachIndexed { index, bean ->
      sticky2LeftItem {
        id(bean.name + index.toString())
        name(bean.name)
      }
      dividerItem {
        id(if (bean.title) "title_line" else "${bean.name + index.toString()}_line")
        bgColorRes(R.color.gray)
        heightPx(1)
      }
    }
  }
}