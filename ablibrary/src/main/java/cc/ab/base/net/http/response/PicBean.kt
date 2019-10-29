package cc.ab.base.net.http.response

import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/4 13:44
 */
data class PicBean(
  var id: Long = 0L,
  var url: String? = null,
  var mBounds: Rect? = null, // 记录坐标
  var width: Int = 100,
  var height: Int = 100,
  var size: Float = 0f
) : Parcelable {

  constructor(parcel: Parcel) : this(
      parcel.readLong(),
      parcel.readString(),
      parcel.readParcelable(Rect::class.java.classLoader),
      parcel.readInt(),
      parcel.readInt(),
      parcel.readFloat()
  )

  override fun writeToParcel(
    parcel: Parcel,
    flags: Int
  ) {
    parcel.writeLong(id)
    parcel.writeString(url)
    parcel.writeParcelable(mBounds, flags)
    parcel.writeInt(width)
    parcel.writeInt(height)
    parcel.writeFloat(size)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Creator<PicBean> {
    override fun createFromParcel(parcel: Parcel): PicBean {
      return PicBean(parcel)
    }

    override fun newArray(size: Int): Array<PicBean?> {
      return arrayOfNulls(size)
    }
  }

}