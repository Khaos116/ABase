package cc.abase.demo.component.drag

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.ab.base.ext.mActivity
import cc.ab.base.widget.engine.PicSelEngine
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.drag.viewmodel.DragState
import cc.abase.demo.component.drag.viewmodel.DragViewModel
import cc.abase.demo.epoxy.item.DragImgItem_
import cc.abase.demo.epoxy.item.dragImgItem
import cc.abase.demo.mvrx.MvRxEpoxyController
import cc.abase.demo.utils.BrowserUtils
import com.airbnb.epoxy.EpoxyTouchHelper
import com.airbnb.mvrx.withState
import com.blankj.utilcode.util.*
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_drag.dragRecycler
import kotlinx.android.synthetic.main.activity_drag.dragRoot

/**
 * Description:item拖拽效果
 * @author: CASE
 * @date: 2019/11/30 20:48
 */
class DragActivity : CommTitleActivity() {
  companion object {
    private const val INTENT_SEL_IMGS = 0x0107
    fun startActivity(context: Context) {
      val intent = Intent(context, DragActivity::class.java)
      context.startActivity(intent)
    }
  }

  //最大图片数量
  private val MAX_IMG_SIZE = 9
  //360 - (96+12+96+12+96) = 48 /2 = 24
  //列表左边间距
  private val paddingStart: Int = SizeUtils.dp2px(24f)
  //列表右边间距
  private var paddingRight: Int = SizeUtils.dp2px(24f - 12f)
  //数据层
  private val viewModel: DragViewModel by lazy {
    DragViewModel()
  }

  override fun layoutResContentId() = R.layout.activity_drag

  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_drag))
    //使用GridLayoutManager的方法
    val spanCount = 3
    val layoutManager = GridLayoutManager(this, spanCount)
    epoxyController.spanCount = spanCount
    layoutManager.spanSizeLookup = epoxyController.spanSizeLookup
    dragRecycler.layoutManager = layoutManager
    dragRecycler.adapter = epoxyController.adapter
    dragRecycler.setPadding(
        paddingStart, dragRecycler.paddingTop, paddingRight, dragRecycler.paddingBottom
    )
    initDrag(dragRecycler, epoxyController)
  }

  override fun initData() {
    viewModel.subscribe { epoxyController.data = it }
  }

  private val epoxyController = MvRxEpoxyController<DragState> {
    withState(viewModel) { state ->
      //添加选中的图片
      state.medias.forEachIndexed { _, media ->
        dragImgItem {
          id(media.path)
          localMedia(media)
          roundRadiusDp(0f)
          onClickDel { viewModel.removeSelect(media) }
          onItemClick { it, iv -> showPic(it) }
          //需要写这个cell占用的span，不然不起作用
          spanCount = 1
        }
      }
      //图片选择
      if (state.medias.size < MAX_IMG_SIZE) {
        dragImgItem {
          id("no_sel_pic")
          roundRadiusDp(0f)
          onItemClick { it, iv ->
            PictureSelector.create(mActivity)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(9)
                .isCamera(false)
                .previewImage(true)
                .loadImageEngine(PicSelEngine())
                .selectionMedia(viewModel.getSelMediaList())
                .forResult(INTENT_SEL_IMGS)
          }
          spanCount = 1
        }
      }
    }
  }

  //初始化拖拽
  private fun initDrag(
    recyclerView: RecyclerView,
    epoxyController: MvRxEpoxyController<DragState>
  ) {
    //长按拖动排序
    EpoxyTouchHelper.initDragging(epoxyController)
        .withRecyclerView(recyclerView)
        .forGrid()//可以控制是横向还是纵向或者是grid的拖拽
        .withTarget(DragImgItem_::class.java)
        .andCallbacks(object : EpoxyTouchHelper.DragCallbacks<DragImgItem_>() {
          override fun isDragEnabledForModel(model: DragImgItem_?): Boolean {
            return model?.localMedia != null
          }

          override fun onModelMoved(
            fromPosition: Int,
            toPosition: Int,
            modelBeingMoved: DragImgItem_?,
            itemView: View?
          ) {
            modelBeingMoved?.localMedia()
                ?.let {
                  viewModel.moveBean(it, fromPosition, toPosition)
                }
          }

          override fun onDragStarted(
            model: DragImgItem_?,
            itemView: View?,
            adapterPosition: Int
          ) {
            itemView?.apply {
              animate()
                  .scaleX(1.05f)
                  .scaleY(1.05f)
            }
            dragRecycler.bringToFront()
          }

          override fun onDragReleased(
            model: DragImgItem_?,
            itemView: View?
          ) {
            itemView?.apply {
              animate()
                  .scaleX(1.0f)
                  .scaleY(1.0f)
            }
          }

          override fun clearView(
            model: DragImgItem_?,
            itemView: View?
          ) {
            onDragReleased(model, itemView)
            dragRoot.removeView(dragRecycler)
            dragRoot.addView(dragRecycler, 0)
          }
        })
  }

  override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {
    super.onActivityResult(requestCode, resultCode, data)
    data?.let {
      if (requestCode == INTENT_SEL_IMGS && resultCode == Activity.RESULT_OK) {
        // 图片、视频、音频选择结果回调
        PictureSelector.obtainMultipleResult(data)
            ?.let { medias ->
              if (medias.isNotEmpty()) {
                viewModel.setSelectMedias(medias)
              }
            }
      } else {
        LogUtils.e("CASE:onActivityResult:other")
      }
    }
  }

  //预览图片
  private fun showPic(media: LocalMedia) {
    var position = 0
    val temp = viewModel.getSelMediaList()
    val current = viewModel.getSelMediaList()
        .filter { m -> TextUtils.equals(m.path, media.path) }
    val result = mutableListOf<String>()
    if (current.isNotEmpty()) position = temp.indexOf(current[0])
    temp.forEach { t -> result.add(t.path) }
    BrowserUtils.instance.show(result as ArrayList<String>, position)
  }
}