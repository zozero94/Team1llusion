package team.illusion.data.model

@kotlinx.serialization.Serializable
data class Member(
    val name: String,
    val phone: String,
    val option: String
) {
    val key = "$name($phone)"
}
