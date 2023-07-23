package team.illusion.data.model

import android.os.Parcelable
import team.illusion.data.DateManager

@kotlinx.serialization.Serializable
@kotlinx.parcelize.Parcelize
data class Member(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val sex: Sex = Sex.Male,
    val address: String = "",
    val option: Options = Options.BASIC,
    val remainCount: Int? = null,
    val startDate: String = "",
    val endDate: String = "",
    val comment: String = "",
    val checkInDate: List<String> = emptyList()
) : Parcelable

fun Member.displayRemainCount() = remainCount?.toString() ?: "INFINITE"
fun Member.isExpireDate() = DateManager.isExpire(endDate)

fun Member.isBeforeDate() = DateManager.isBefore(startDate)

enum class Sex {
    Male, FeMale
}

enum class Options(val count: Int?, val extraCount: Int) {
    BASIC(count = 4, extraCount = 3),
    STANDARD(count = 12, extraCount = 4),
    PREMIUM(count = null, extraCount = Int.MAX_VALUE)
}
