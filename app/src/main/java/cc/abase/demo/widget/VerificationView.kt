package cc.abase.demo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Base64
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView


/**
 * Author:Khaos116
 * Date:2022/3/9
 * Time:16:29
 */
@SuppressLint("ClickableViewAccessibility")
class VerificationView @kotlin.jvm.JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //背景
  private val ivBg: ImageView = ImageView(context)

  //图片滑块
  private val ivFrame: ImageView = ImageView(context)

  //提示文字
  private val tvHint: TextView = TextView(context)

  //拖拽滑块
  private val ivDrag: ImageView = ImageView(context)

  //背景位置
  private val lpBg = LayoutParams(-1, -2)

  //图片滑块位置
  private val lpFrame = LayoutParams(30, 30)

  //提示文字位置
  private val lpHint = LayoutParams(-1, dp2px(30f))

  //拖拽滑块位置
  private val lpDrag = LayoutParams(dp2px(40f), dp2px(40f))

  //图片滑块最大滑动宽度
  private var maxFrameMove: Int = 0

  //拖拽滑块最大滑动宽度
  private var maxDragMove: Int = 0

  //图片和控件的缩放比
  private var ration: Float = 0f

  //背景和拖拽滑块的间距
  private var offSet: Int = dp2px(5f)

  //滑动是否后回调位置
  var mCallBack: ((dragX: Int) -> Unit)? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="滑动临时参数">
  //记录按下位置
  private var mDownX = 0f

  //是否可以滑动
  private var mCanDrag = true
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    //设置文字信息
    tvHint.setTextSize(TypedValue.COMPLEX_UNIT_PX, dp2px(14f) * 1f)
    tvHint.setTextColor(Color.parseColor("#a2a3a5"))
    tvHint.text = "请安住滑块拖动完成拼图"
    tvHint.background = getHintBg(lpHint.height, Color.parseColor("#dedfe1"))
    tvHint.gravity = Gravity.CENTER_VERTICAL
    tvHint.setPadding(lpDrag.width + dp2px(3f), 0, 0, 0)
    //设置拖拽滑块
    ivDrag.setBackgroundColor(Color.RED)
    lpHint.topMargin = (lpBg.height - lpHint.height) / 2
    addListener()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部方法">
  //添加滑动监听
  private fun addListener() {
    //滑动计算
    ivDrag.setOnTouchListener { v, event ->
      when (event.action and MotionEvent.ACTION_MASK) {
        MotionEvent.ACTION_DOWN -> {
          mDownX = event.rawX //记录获取按压位置
          mCanDrag = ivDrag.translationX == 0f
        }
        MotionEvent.ACTION_MOVE -> {
          if (mCanDrag) {
            val mCurrentX = event.rawX //更新当前位置
            val transX = (mCurrentX - mDownX).coerceAtMost(maxDragMove * 1f)
            //拖动滑块位置
            v.translationX = 0f.coerceAtLeast(transX)
            //图标滑块位置
            ivFrame.translationX = maxFrameMove * (v.translationX / maxDragMove)
          }
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
          if (mCanDrag) mCallBack?.invoke((ivFrame.translationX / ration).toInt())
          mDownX = 0f
        }
      }
      true
    }
  }

  //设置图片信息
  private fun fillData(bg64: String, fame64: String, top: Int) {
    //转行图片
    val bytesBg: ByteArray = Base64.decode(bg64.split(",")[1], Base64.DEFAULT)
    val bitBg = BitmapFactory.decodeByteArray(bytesBg, 0, bytesBg.size)
    val bytesFrame: ByteArray = Base64.decode(fame64.split(",")[1], Base64.DEFAULT)
    val bitFrame = BitmapFactory.decodeByteArray(bytesFrame, 0, bytesFrame.size)
    //计算缩放
    ration = width * 1f / bitBg.width
    //计算位置
    lpBg.height = (bitBg.height * ration).toInt()
    lpBg.width = width
    lpFrame.width = (bitFrame.width * ration).toInt()
    lpFrame.height = (bitFrame.height * ration).toInt()
    lpFrame.topMargin = (top * ration).toInt()
    lpDrag.topMargin = lpBg.height + offSet
    lpHint.topMargin = lpBg.height + offSet + (lpDrag.height - lpHint.height) / 2
    //计算图片滑块最大滑动距离
    maxFrameMove = width - lpFrame.width
    //添加View
    this.removeAllViews()
    this.addView(ivBg, lpBg)
    this.addView(ivFrame, lpFrame)
    this.addView(tvHint, lpHint)
    this.addView(ivDrag, lpDrag)
    //填充图片
    ivBg.setImageBitmap(bitBg)
    ivFrame.setImageBitmap(bitFrame)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部访问">
  //设置图片信息
  fun setData(bg64: String, fame64: String, top: Int) {
    if (width > 0) {
      fillData(bg64, fame64, top)
    } else {
      this.post { fillData(bg64, fame64, top) }
    }
  }

  //验证出错后重置滑块位置
  fun resetMove() {
    ivFrame.translationX = 0f
    ivDrag.translationX = 0f
  }

  //设置滑块图标
  fun setDragIv(imgRes: Int) {
    ivDrag.background = null
    ivDrag.setImageResource(imgRes)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="绘制的尺寸">
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    maxDragMove = w - lpDrag.width
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="工具方法">
  private fun dp2px(dp: Float): Int {
    val width = Resources.getSystem().displayMetrics.widthPixels.coerceAtMost(Resources.getSystem().displayMetrics.heightPixels)
    return (dp * width / 360f).toInt()
  }

  private fun getHintBg(radius: Int, color: Int): GradientDrawable {
    val drawable = GradientDrawable()
    drawable.setColor(color)
    drawable.cornerRadius = radius * 1f
    return drawable
  }
  //</editor-fold>
}