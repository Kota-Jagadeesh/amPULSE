package com.amfoss.ampulse.screentime

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val totalTimeInForeground: Long,
    val lastTimeUsed: Long
)

@Singleton
class UsageReader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val packageManager = context.packageManager

    /**
     * Fetches precise usage statistics for the current day from 00:00 AM until now.
     */
    fun getTodayUsage(): List<AppUsageInfo> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        // queryAndAggregateUsageStats is the most reliable way to get the cumulative 
        // foreground time for a specific time range across all apps.
        val statsMap = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)

        if (statsMap.isNullOrEmpty()) return emptyList()

        val myPackage = context.packageName

        return statsMap.values
            .filter { 
                it.totalTimeInForeground > 0 && 
                it.packageName != myPackage 
            }
            .map { usageStats ->
                val packageName = usageStats.packageName
                val appName = try {
                    val appInfo = packageManager.getApplicationInfo(packageName, 0)
                    packageManager.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    packageName.split(".").last().replaceFirstChar { it.uppercase() }
                }

                AppUsageInfo(
                    packageName = packageName,
                    appName = appName,
                    totalTimeInForeground = usageStats.totalTimeInForeground,
                    lastTimeUsed = usageStats.lastTimeUsed
                )
            }
            .sortedByDescending { it.totalTimeInForeground }
    }

    /**
     * Calculates total screen time for today.
     */
    fun getTotalScreenTimeToday(): Long {
        return getTodayUsage().sumOf { it.totalTimeInForeground }
    }
}
