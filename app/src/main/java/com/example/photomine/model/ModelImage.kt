package com.example.photomine.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "favorite_item")
data class ModelImage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageFile: String,
    val dateAdded: Long,
    val size: Long,
    val quality: String,
    var isFavorite: Boolean = false
) : Serializable  /*{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()?: "",
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString()?: "",
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(imageFile)
        parcel.writeLong(dateAdded)
        parcel.writeLong(size)
        parcel.writeString(quality)
        parcel.writeByte(if (isFavorite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelImage> {
        override fun createFromParcel(parcel: Parcel): ModelImage {
            return ModelImage(parcel)
        }

        override fun newArray(size: Int): Array<ModelImage?> {
            return arrayOfNulls(size)
        }
    }
}*/

