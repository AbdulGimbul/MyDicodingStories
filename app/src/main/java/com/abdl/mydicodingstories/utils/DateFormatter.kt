package com.abdl.mydicodingstories.utils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateFormatter {
    fun formatDate(currentDate: String): String? {
        val currentFormat = "yyyy-MM-dd'T'hh:mm:ss.sss'Z'"
        val targetFormat = "dd MMM yyyy | HH:mm"
        val timeZone = "GMT"
        val currentDf: DateFormat = SimpleDateFormat(currentFormat, Locale.getDefault())
        currentDf.timeZone = TimeZone.getTimeZone(timeZone)
        val targetDf: DateFormat = SimpleDateFormat(targetFormat, Locale.getDefault())
        var targetDate: String? = null
        try {
            val date = currentDf.parse(currentDate)
            if (date != null) {
                targetDate = targetDf.format(date)
            }
        } catch (ex: ParseException) {
            ex.printStackTrace()
        }
        return targetDate
    }
}