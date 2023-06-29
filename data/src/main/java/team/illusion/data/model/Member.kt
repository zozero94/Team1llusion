package team.illusion.data.model

import android.os.Parcelable

@kotlinx.serialization.Serializable
@kotlinx.parcelize.Parcelize
data class Member(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val option: String = ""
) : Parcelable
