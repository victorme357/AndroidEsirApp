package fr.vico.esirchatandroid.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Group(val uid: String , val groupname: String, var creator: String, val users: List<User> ) : Parcelable {
    constructor() : this("","","", listOf())
}