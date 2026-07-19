package com.amfoss.ampulse.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amfoss.ampulse.ui.components.DashboardCard
import com.amfoss.ampulse.ui.components.LeaderboardItem
import com.amfoss.ampulse.ui.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hello, Jagadeesh!",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Here's your usage summary",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* TODO */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* TODO */ },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                DashboardCard(
                    title = "Today's Screen Time",
                    value = "4h 18m",
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard(
                        label = "Weekly Avg",
                        value = "3h 52m",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Monthly Avg",
                        value = "4h 10m",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = "Usage Trend",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                // Placeholder for Chart
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text("Usage Graph Placeholder", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            item {
                Text(
                    text = "Top Apps",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                DashboardCard(
                    title = "YouTube",
                    value = "1h 42m"
                )
            }

            item {
                Text(
                    text = "Leaderboard",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        LeaderboardItem(rank = 1, name = "Rahul", time = "2h 15m")
                        Divider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)
                        LeaderboardItem(rank = 2, name = "Jagadeesh", time = "4h 18m")
                        Divider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)
                        LeaderboardItem(rank = 3, name = "Arjun", time = "5h 10m")
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
