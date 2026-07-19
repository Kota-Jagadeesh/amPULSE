package com.amfoss.ampulse.ui.permission

import androidx.lifecycle.ViewModel
import com.amfoss.ampulse.screentime.UsagePermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    val permissionManager: UsagePermissionManager
) : ViewModel()
