package com.amfoss.ampulse.screentime

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
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

    fun getTodayUsage(): List<AppUsageInfo> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val statsMap = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)
        if (statsMap.isNullOrEmpty()) return emptyList()

        val launchers = getLaunchers()
        val myPackage = context.packageName

        return statsMap.values
            .filter { 
                it.totalTimeInForeground > 0 && 
                !isSystemOrNoise(it.packageName, launchers, myPackage)
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
     * Calculates non-overlapping screen time by processing the stream of Usage Events.
     * This method reconstructs the user's activity timeline to match Digital Wellbeing.
     */
    fun getTotalScreenTimeToday(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val events = usageStatsManager.queryEvents(startTime, endTime)
        val event = UsageEvents.Event()

        var totalTime = 0L
        var lastForegroundTime = 0L
        val activeApps = mutableSetOf<String>()
        val launchers = getLaunchers()
        val myPackage = context.packageName

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            val pkg = event.packageName
            val type = event.eventType
            val time = event.timeStamp

            if (isSystemOrNoise(pkg, launchers, myPackage)) continue

            when (type) {
                UsageEvents.Event.MOVE_TO_FOREGROUND, UsageEvents.Event.ACTIVITY_RESUMED -> {
                    // If no user app was active, start the timer
                    if (activeApps.isEmpty()) {
                        lastForegroundTime = time
                    }
                    activeApps.add(pkg)
                }
                UsageEvents.Event.MOVE_TO_BACKGROUND, UsageEvents.Event.ACTIVITY_PAUSED -> {
                    activeApps.remove(pkg)
                    // If this was the last user app, stop the timer and add the duration
                    if (activeApps.isEmpty() && lastForegroundTime != 0L) {
                        totalTime += (time - lastForegroundTime)
                        lastForegroundTime = 0L
                    }
                }
            }
        }

        // If an app is still in the foreground
        if (activeApps.isNotEmpty() && lastForegroundTime != 0L) {
            totalTime += (endTime - lastForegroundTime)
        }

        return totalTime
    }

    private fun isSystemOrNoise(packageName: String, launchers: Set<String>, myPackage: String): Boolean {
        val systemNoise = setOf(
            "android",
            "com.android.systemui",
            "com.google.android.permissioncontroller",
            "com.android.settings",
            myPackage
        )
        return systemNoise.contains(packageName) || launchers.contains(packageName)
    }

    private fun getLaunchers(): Set<String> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfos.map { it.activityInfo.packageName }.toSet()
    }
}
