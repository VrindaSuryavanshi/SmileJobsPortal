package com.example.smilejobportal.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties

data class JobModel(
    val jobId: String = "",
    val title: String = "",
    val company: String = "",
    val picUrl: String = "",
    val time: String = "",
    val model: String = "",
    val level: String = "",
    val location: String = "",
    val salary: String = "",
    val category: String = "",
    val about: String = "",
    val description: String = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(jobId)
        parcel.writeString(title)
        parcel.writeString(company)
        parcel.writeString(picUrl)
        parcel.writeString(time)
        parcel.writeString(model)
        parcel.writeString(level)
        parcel.writeString(location)
        parcel.writeString(salary)
        parcel.writeString(category)
        parcel.writeString(about)
        parcel.writeString(description)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<JobModel> {
        override fun createFromParcel(parcel: Parcel): JobModel = JobModel(parcel)
        override fun newArray(size: Int): Array<JobModel?> = arrayOfNulls(size)
    }
}
