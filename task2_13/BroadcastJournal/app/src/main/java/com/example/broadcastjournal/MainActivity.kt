package com.example.broadcastjournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.broadcastjournal.ui.theme.BroadcastJournalTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BroadcastViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            BroadcastJournalTheme {

                val events by
                viewModel.events.collectAsStateWithLifecycle()

                BroadcastJournalScreen(
                    events = events,
                    onDelete = { event ->
                        viewModel.deleteEvent(event)
                    },
                    onClearAll = {
                        viewModel.clearAll()
                    }
                )
            }
        }
    }
}

@Composable
fun BroadcastJournalScreen(
    events: List<BroadcastEvent>,
    onDelete: (BroadcastEvent) -> Unit,
    onClearAll: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Журнал системних подій",
            style = MaterialTheme.typography.headlineSmall
        )

        Button(
            onClick = onClearAll,
            modifier = Modifier.padding(
                top = 12.dp,
                bottom = 12.dp
            )
        ) {
            Text("Очистити журнал")
        }

        if (events.isEmpty()) {

            Text(
                text = "Подій поки немає",
                style = MaterialTheme.typography.bodyLarge
            )

        } else {

            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = events,
                    key = { event ->
                        event.id
                    }
                ) { event ->

                    ExpandableEventCard(
                        event = event,
                        onDelete = {
                            onDelete(event)
                        },
                        modifier =
                            Modifier.animateItem()
                    )
                }
            }
        }
    }
}

@Composable
fun ExpandableEventCard(
    event: BroadcastEvent,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {

    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable {
                isExpanded = !isExpanded
            }
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = event.type,
                        style =
                            MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = event.timestamp,
                        style =
                            MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                IconButton(
                    onClick = onDelete
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Delete,
                        contentDescription =
                            "Видалити подію"
                    )
                }
            }

            if (isExpanded) {

                Text(
                    text = "Деталі:",
                    style =
                        MaterialTheme.typography.titleSmall,
                    modifier =
                        Modifier.padding(top = 12.dp)
                )

                Text(
                    text = event.details,
                    style =
                        MaterialTheme.typography.bodyMedium,
                    modifier =
                        Modifier.padding(top = 4.dp)
                )

                Text(
                    text = "Точний час: ${event.timestamp}",
                    style =
                        MaterialTheme.typography.bodySmall,
                    modifier =
                        Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}