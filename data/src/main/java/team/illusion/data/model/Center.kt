package team.illusion.data.model

enum class Center(val centerName: String) {
    Gangnam("강남"), Ilsan("일산"), None("");

    companion object {
        fun findCenter(centerName: String) =
            Center.values().firstOrNull { it.centerName == centerName } ?: None
    }
}
