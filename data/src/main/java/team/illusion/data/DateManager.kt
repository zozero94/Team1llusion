package team.illusion.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object DateManager {
    private val calendar = Calendar.getInstance()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val today: String
        get() {
            val currentDate = LocalDateTime.now()
            return currentDate.format(dateFormatter)
        }

    val todayDate: LocalDate
        get() {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            return LocalDate.of(year, month, day)
        }

    fun isExpire(target: String): Boolean {
        val targetDate = LocalDate.parse(target, dateFormatter)
        val today = LocalDate.parse(today)
        return today.isAfter(targetDate)
    }

    fun calculateDateAfterMonths(target:String,months: Long): String {
        val currentDate = LocalDate.parse(target, dateFormatter)
        val futureDate = currentDate.plusMonths(months)
        return futureDate.format(dateFormatter)
    }
}