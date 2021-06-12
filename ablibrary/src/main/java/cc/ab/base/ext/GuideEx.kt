package cc.ab.base.ext

import android.app.Activity
import android.graphics.Color
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import cc.ab.base.R

/**
 * description : 新手引导相关扩展
 *
 * @author : Khaos
 * @date : 2019/3/12 14:58
 */
/**显示引导:必须传view或者viewId*/

/**
 * description :Guide的位置
 *
 * @author : Khaos
 * @date : 2019/3/12 11:09
 */
enum class GuidePosition(value: Int) {
  TOP_START(0),//上面居左对齐
  TOP_END(1),//上面居右对齐
  TOP_CENTER(2),//上面居中对齐
  BOTTOM_START(3),//下面居左对齐
  BOTTOM_END(4),//下面居右对齐
  BOTTOM_CENTER(5),//下面居中对齐
  CENTER(6),//居中对齐
  START(7),//位于左侧上下居中
  END(8),//位于右侧上下居中
}

//显示新手引导
fun Activity.extShowGuide(
  rootView: FrameLayout = mContentView,//需要显示引导的根布局(fragment需要传)
  view: View? = null,//需要引导的View
  layoutId: Int = 0,//需要引导的View的id
  offsetX: Float = 0F,//需要引导的View的X偏移量
  offsetY: Float = 0F,//需要引导的View的Y偏移量
  dependView: View? = null,//相对摆放的View
  outsideClick: Boolean = true,//外面是否可以点击
  insideClick: Boolean = true,//里面是否可以点击
  position: GuidePosition = GuidePosition.CENTER,//引导View对相对view的摆放位置
  @ColorInt overColor: Int = Color.TRANSPARENT,//引导背景颜色（默认透明，会透过去点击）
  clickGuid: ((view: View) -> Unit)? = null,//自己回调引导的处理点击效果
  resultView: ((self: View, newSelf: View) -> Unit)? = null//self为传入的view，newSelf为有可能增加了包裹的view
) {
  //方便判断的tag
  val tag = view?.hashCode() ?: layoutId
  //判断是否已经有显示了
  for (i in 0 until rootView.childCount) {
    val tagTem = rootView.getChildAt(i)
      .getTag(R.id.id_tag_guide)
    //判断是否有相同tag在显示
    if (tagTem != null && tag == tagTem.toString().toInt()) {
      return
    }
  }
  //获取引导View
  val guideView =
    view ?: LayoutInflater.from(mContext).inflate(layoutId, rootView, false) ?: return
  //设置自己的hashCode为tag，方便移除
  guideView.setTag(R.id.id_tag_guide, tag)
  //为了获取宽高而不让用户知道
  guideView.visibility = View.INVISIBLE
  //监听高度
  guideView.viewTreeObserver.addOnGlobalLayoutListener(
    object : ViewTreeObserver.OnGlobalLayoutListener {
      override fun onGlobalLayout() {
        //获取到引导布局的信息后才设置操作
        if (guideView.height > 0) {
          val guideViewHeight = guideView.height
          val guideViewWidth = guideView.width
          val rootWidth = rootView.width
          val rootHeight = rootView.height - rootView.paddingTop
          //判断是否是全屏的引导，全屏的引导就不再单独处理
          val isFullGuide = guideViewWidth == rootWidth
              && guideViewHeight == rootHeight
          guideView.viewTreeObserver.removeOnGlobalLayoutListener(this)
          //全屏的只写监听即可
          if (isFullGuide) {
            //监听引导的点击即可
            if (insideClick) {
              guideView.click {
                extHideGuide(view = guideView)
                clickGuid?.invoke(guideView)
              }
            }
            resultView?.invoke(guideView, guideView)
          } else {//非全屏的引导需要处理的比较多
            //设置偏移量
            guideView.translationX = offsetX
            guideView.translationY = offsetY
            //为了解决间距问题，需要外部嵌套一层
            val guideViewRoot = FrameLayout(mContext)
            //设置自己的子类hashCode为tag，方便移除
            guideViewRoot.setTag(R.id.id_tag_guide, tag)
            //设置背景色
            guideViewRoot.setBackgroundColor(overColor)
            //如果外部可以点击消失
            if (outsideClick && overColor != Color.TRANSPARENT) {
              //如果外部可以点击，背景不透明，才设置点击(背景透明的时候不设置点击，使其透过去点击)
              guideViewRoot.click {
                extHideGuide(view = guideViewRoot)
                clickGuid?.invoke(guideViewRoot)
              }
            } else if (!outsideClick) {//外部不能点击(即设置一个空的点击事件，不执行)
              guideViewRoot.click {}
            }
            //引导的点击事件
            if (insideClick) {
              guideView.click {
                extHideGuide(view = guideViewRoot)
                clickGuid?.invoke(guideView)
              }
            }
            //先移除
            guideView.removeParent()
            //添加到需要显示的容器中
            guideViewRoot.addView(guideView)
            //添加容器
            rootView.addView(guideViewRoot, -1, -1)
            //设置布局位置
            val lp = guideView.layoutParams as FrameLayout.LayoutParams
            lp.gravity = Gravity.NO_GRAVITY
            //有相对位置的View
            if (dependView != null) {
              //获取到位置的View信息
              val location = IntArray(2)
              dependView.getLocationOnScreen(location)
              //左上角位置信息
              val dependX = location[0]
              val dependY = if (rootView.paddingTop > 0)
                location[1] - rootView.paddingTop else location[1]
              when (position) {
                GuidePosition.TOP_START -> {
                  lp.topMargin = dependY - guideViewHeight
                  lp.marginStart = dependX
                }
                GuidePosition.TOP_CENTER -> {
                  lp.topMargin = dependY - guideViewHeight
                  lp.marginStart =
                    (dependX + dependView.width / 2f - guideViewWidth / 2f)
                      .toInt()
                }
                GuidePosition.TOP_END -> {
                  lp.topMargin = dependY - guideViewHeight
                  lp.marginStart = dependX + dependView.width - guideViewWidth
                }
                GuidePosition.BOTTOM_START -> {
                  lp.topMargin = dependY + dependView.height
                  lp.marginStart = dependX
                }
                GuidePosition.BOTTOM_CENTER -> {
                  lp.topMargin = dependY + dependView.height
                  lp.marginStart =
                    (dependX + dependView.width / 2f - guideViewWidth / 2f)
                      .toInt()
                }
                GuidePosition.BOTTOM_END -> {
                  lp.topMargin = dependY + dependView.height
                  lp.marginStart = dependX + dependView.width - guideViewWidth
                }
                GuidePosition.START -> {
                  lp.topMargin =
                    (dependY + dependView.height / 2f - guideViewHeight / 2f)
                      .toInt()
                  lp.marginStart = dependX - guideViewWidth

                }
                GuidePosition.CENTER -> {
                  lp.topMargin =
                    (dependY + dependView.height / 2f - guideViewHeight / 2f)
                      .toInt()
                  lp.marginStart =
                    (dependX + dependView.width / 2f - guideViewWidth / 2f)
                      .toInt()
                }
                GuidePosition.END -> {
                  lp.topMargin =
                    (dependY + dependView.height / 2f - guideViewHeight / 2f)
                      .toInt()
                  lp.marginStart = dependX + dependView.width
                }
              }
            } else {
              when (position) {
                GuidePosition.TOP_START -> {
                  lp.topMargin = 0
                  lp.marginStart = 0
                }
                GuidePosition.TOP_CENTER -> {
                  lp.topMargin = 0
                  lp.marginStart = (rootWidth / 2f - guideViewWidth / 2f).toInt()
                }
                GuidePosition.TOP_END -> {
                  lp.topMargin = 0
                  lp.marginStart = rootWidth - guideViewWidth
                }
                GuidePosition.BOTTOM_START -> {
                  lp.topMargin = rootHeight - guideViewHeight
                  lp.marginStart = 0
                }
                GuidePosition.BOTTOM_CENTER -> {
                  lp.topMargin = rootHeight - guideViewHeight
                  lp.marginStart = (rootWidth / 2f - guideViewWidth / 2f).toInt()
                }
                GuidePosition.BOTTOM_END -> {
                  lp.topMargin = rootHeight - guideViewHeight
                  lp.marginStart = rootWidth - guideViewWidth
                }
                GuidePosition.START -> {
                  lp.topMargin = (rootHeight / 2f - guideViewHeight / 2f).toInt()
                  lp.marginStart = 0
                }
                GuidePosition.CENTER -> {
                  lp.topMargin = (rootHeight / 2f - guideViewHeight / 2f).toInt()
                  lp.marginStart = (rootWidth / 2f - guideViewWidth / 2f).toInt()
                }
                GuidePosition.END -> {
                  lp.topMargin = (rootHeight / 2f - guideViewHeight / 2f).toInt()
                  lp.marginStart = rootWidth - guideViewWidth
                }
              }
            }
            resultView?.invoke(guideView, guideViewRoot)
          }
          //显示View
          guideView.visibility = View.VISIBLE
        }
      }
    }
  )
  //添加到布局中
  rootView.addView(guideView)
}

//隐藏新手引导:不传view表示全部关闭
private fun Activity.extHideGuide(
  rootView: FrameLayout = mContentView,//需要显示引导的根布局(fragment需要传)
  view: View? = null
) {
  //移除传入的view
  if (view != null) {
    view.removeParent()
  } else {
    //没有指定的，删除所有为新手引导的布局
    for (i in rootView.childCount - 1 downTo 0) {
      val tagTem = rootView.getChildAt(i)
        .getTag(R.id.id_tag_guide)
      if (tagTem != null && tagTem.toString().toInt() > 0) {
        rootView.removeViewAt(i)
      }
    }
  }
}