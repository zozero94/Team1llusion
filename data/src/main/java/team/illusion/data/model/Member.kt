package team.illusion.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import team.illusion.data.datasource.DateManager

@Serializable
@Parcelize
data class Member(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val sex: Sex = Sex.Male,
    val address: String = "",
    val remainCount: Count = Count(0),
    val startDate: String = "",
    val endDate: String = "",
    val comment: String = "",
    val checkInDate: List<String> = emptyList(),
) : Parcelable

fun Member.displayRemainCount() = remainCount.toString()
fun Member.isExpireDate() = DateManager.isExpire(endDate)

fun Member.isBeforeDate() = DateManager.isBefore(startDate)

enum class Sex {
    Male, FeMale
}

@Parcelize
@Serializable
data class Count(val count: Int? = null) : Parcelable {

    override fun toString(): String = count?.toString() ?: "INFINITE"

    fun isExpire() = if (count == null) false else count == 0


    operator fun plus(other: Int): Count {
        return if (count == null) this
        else Count(count + other)
    }

}
