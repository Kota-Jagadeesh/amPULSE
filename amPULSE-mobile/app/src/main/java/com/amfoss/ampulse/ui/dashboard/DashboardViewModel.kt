package com.amfoss.ampulse.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amfoss.ampulse.screentime.AppUsageInfo
import com.amfoss.ampulse.screentime.UsageReader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val totalScreenTime: String = "0h 0m",
    val topApps: List<AppUsageInfo> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val usageReader: UsageReader
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun refreshUsageData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val totalMillis = usageReader.getTotalScreenTimeToday()
            val apps = usageReader.getTodayUsage().take(5) // Get top 5 apps
            
            _uiState.value = DashboardUiState(
                totalScreenTime = formatMillis(totalMillis),
                topApps = apps,
                isLoading = false
            )
        }
    }

    private fun formatMillis(millis: Long): String {
        val totalMinutes = millis / (1000 * 60)
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return "${hours}h ${minutes}m"
    }
}
