package com.company.eve.ui.home.kotlintest

import android.os.Parcel
import android.os.Parcelable

data class Charger(
    var addr: String? = null,
    var city: String? = null,
    var limit: String? = null,
    var output: String? = null,
    var statNm: String? = null,
    var typeS: String? = null,
    var lat: Double? = null,
    var lng: Double? = null,
    var place: String? = null,
    var province: String? = null,
    var space: String? = null,
    var type: String? = null,
    var Ctype: String? = null,
    var bnm: String? = null,
    var busiNm: String? = null,
    var chgerId: Int? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(addr)
        parcel.writeString(city)
        parcel.writeString(limit)
        parcel.writeString(output)
        parcel.writeString(statNm)
        parcel.writeString(typeS)
        parcel.writeValue(lat)
        parcel.writeValue(lng)
        parcel.writeString(place)
        parcel.writeString(province)
        parcel.writeString(space)
        parcel.writeString(type)
        parcel.writeString(Ctype)
        parcel.writeString(bnm)
        parcel.writeString(busiNm)
        parcel.writeValue(chgerId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Charger> {
        override fun createFromParcel(parcel: Parcel): Charger {
            return Charger(parcel)
        }

        override fun newArray(size: Int): Array<Charger?> {
            return arrayOfNulls(size)
        }
    }
}
