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
    val jobType: String = "",
    val model: String = "",
    val experience: String = "",
    val location: String = "",
    val salary: String = "",
    val category: String = "",
    val about: String = "",
    val description: String = "",
    val hrContact: String = "",
    val timestamp: Long = System.currentTimeMillis()
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
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(jobId)
        parcel.writeString(title)
        parcel.writeString(company)
        parcel.writeString(picUrl)
        parcel.writeString(jobType)
        parcel.writeString(model)

        parcel.writeString(experience)
        parcel.writeString(location)
        parcel.writeString(salary)
        parcel.writeString(category)
        parcel.writeString(about)
        parcel.writeString(description)
        parcel.writeString(hrContact)
        parcel.writeLong(timestamp)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<JobModel> {
        override fun createFromParcel(parcel: Parcel): JobModel = JobModel(parcel)
        override fun newArray(size: Int): Array<JobModel?> = arrayOfNulls(size)
    }
}
