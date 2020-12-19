package cc.ab.base.widget.nineimageview

import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/25 20:30
 */
data class ImageData(
  var url: String,
  var mBounds: Rect? = null, // 记录坐标
  var startX: Int = 0,
  var startY: Int = 0,
  var width: Int = 1,
  var height: Int = 1
) : Parcelable {
  constructor(parcel: Parcel) : this(
      parcel.readString() ?: "",
      parcel.readParcelable(Rect::class.java.classLoader),
      parcel.readInt(),
      parcel.readInt(),
      parcel.readInt(),
      parcel.readInt()
  )

  override fun writeToParcel(
    parcel: Parcel,
    flags: Int
  ) {
    parcel.writeString(url)
    parcel.writeParcelable(mBounds, flags)
    parcel.writeInt(startX)
    parcel.writeInt(startY)
    parcel.writeInt(width)
    parcel.writeInt(height)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Creator<ImageData> {
    override fun createFromParcel(parcel: Parcel): ImageData {
      return ImageData(parcel)
    }

    override fun newArray(size: Int): Array<ImageData?> {
      return arrayOfNulls(size)
    }
  }

  fun from(
    imageData: ImageData?,
    layoutHelper: LayoutHelper?,
    position: Int
  ): ImageData? {
    if (imageData != null && layoutHelper != null) {
      val coordinate = layoutHelper.getCoordinate(position)
      if (coordinate != null) {
        imageData.startX = coordinate.x
        imageData.startY = coordinate.y
      }

      val size = layoutHelper.getSize(position)
      if (size != null) {
        imageData.width = size.x
        imageData.height = size.y
      }
    }
    return imageData
  }
}