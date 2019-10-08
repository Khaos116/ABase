package cc.ab.base.net.http.response

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/4 13:38
 */
data class AtBean(
  var uid: Long,
  var index: Int? = 0,
  var len: Int? = 0,
  var type: Int? = 0
): Parcelable {
  override fun equals(other: Any?): Boolean {
    return super.equals(other)
  }

  override fun hashCode(): Int {
    return super.hashCode()
  }

  constructor(parcel: Parcel) : this(
      parcel.readLong(),
      parcel.readValue(Int::class.java.classLoader) as? Int,
      parcel.readValue(Int::class.java.classLoader) as? Int,
      parcel.readValue(Int::class.java.classLoader) as? Int
  ) {
  }

  override fun writeToParcel(
    parcel: Parcel,
    flags: Int
  ) {
    parcel.writeLong(uid)
    parcel.writeValue(index)
    parcel.writeValue(len)
    parcel.writeValue(type)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Creator<AtBean> {
    override fun createFromParcel(parcel: Parcel): AtBean {
      return AtBean(parcel)
    }

    override fun newArray(size: Int): Array<AtBean?> {
      return arrayOfNulls(size)
    }
  }
}