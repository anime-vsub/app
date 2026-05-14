package git.shin.animevsub.ui.screens.plugins

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.plugin.Plugin
import git.shin.animevsub.plugin.PluginLoadStatus
import git.shin.animevsub.plugin.RepoIndex
import git.shin.animevsub.plugin.RepoPlugin
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginScreen(
    onNavigateBack: () -> Unit,
    viewModel: PluginViewModel = hiltViewModel()
) {
    val plugins by viewModel.plugins.collectAsState()
    val repos by viewModel.repos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val activePluginId by viewModel.activePluginId.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val tempFile = File(context.cacheDir, "plugin.jar")
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            viewModel.installFromFile(tempFile)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_plugins)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { filePicker.launch(arrayOf("application/java-archive", "application/x-java-archive", "*/*")) }) {
                        Icon(Icons.Default.Folder, contentDescription = "Install from file")
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.add_repository)) },
                            onClick = {
                                showMenu = false
                                viewModel.showAddRepoDialog()
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (repos.isNotEmpty()) {
                Text(
                    stringResource(R.string.plugin_repositories),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                repos.forEach { repo ->
                    RepoItem(
                        repo = repo,
                        onBrowse = { viewModel.browseRepo(repo) },
                        onRemove = { viewModel.removeRepo(repo.url) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                stringResource(R.string.installed_plugins),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (plugins.isEmpty()) {
                Text(
                    stringResource(R.string.no_plugins_installed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn {
                    items(plugins.values.toList()) { plugin ->
                        PluginItem(
                            plugin = plugin,
                            isActive = plugin.info.id == activePluginId,
                            onSetActive = { viewModel.setActivePlugin(plugin.info.id) },
                            onToggle = {
                                if (plugin.info.enabled) viewModel.disablePlugin(plugin.info.id)
                                else viewModel.enablePlugin(plugin.info.id)
                            },
                            onUninstall = { viewModel.uninstallPlugin(plugin.info.id) }
                        )
                    }
                }
            }
        }
    }

    if (uiState.showAddRepoDialog) {
        AddRepoDialog(
            onDismiss = { viewModel.hideAddRepoDialog() },
            onConfirm = { url -> viewModel.addRepo(url) }
        )
    }

    if (uiState.showBrowseDialog && uiState.selectedRepo != null) {
        BrowseRepoDialog(
            repo = uiState.selectedRepo!!,
            plugins = uiState.repoPlugins,
            onDismiss = { viewModel.hideBrowseDialog() },
            onInstall = { viewModel.installPlugin(it) }
        )
    }

    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = { TextButton(onClick = { viewModel.clearError() }) { Text("OK") } }
        )
    }
}

@Composable
private fun RepoItem(
    repo: RepoIndex,
    onBrowse: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(repo.name, style = MaterialTheme.typography.bodyLarge)
                Text(repo.url, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row {
                OutlinedButton(onClick = onBrowse) { Text(stringResource(R.string.browse_plugins)) }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
            }
        }
    }
}

@Composable
private fun PluginItem(
    plugin: Plugin,
    isActive: Boolean,
    onSetActive: () -> Unit,
    onToggle: () -> Unit,
    onUninstall: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plugin.info.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    stringResource(R.string.plugin_version, plugin.info.version) + " - " +
                    (if (plugin.info.author.isNotEmpty()) stringResource(R.string.plugin_by, plugin.info.author) else ""),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    when (plugin.status) {
                        PluginLoadStatus.LOADED -> stringResource(R.string.plugin_loaded)
                        PluginLoadStatus.DISABLED -> stringResource(R.string.plugin_disabled)
                        PluginLoadStatus.FAILED -> stringResource(R.string.plugin_failed) + ": ${plugin.error}"
                        else -> "Not loaded"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (plugin.status == PluginLoadStatus.FAILED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = plugin.info.enabled,
                    onCheckedChange = { onToggle() }
                )
                if (isActive) {
                    Text("Active", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(if (isActive) "Deactivate" else stringResource(R.string.set_active)) },
                        onClick = {
                            showMenu = false
                            onSetActive()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.uninstall)) },
                        onClick = {
                            showMenu = false
                            onUninstall()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AddRepoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_repository)) },
        text = {
            Column {
                Text("Enter repository URL (index.json)", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL") },
                    placeholder = { Text(stringResource(R.string.repo_url_hint)) },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(url) },
                enabled = url.isNotBlank()
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun BrowseRepoDialog(
    repo: RepoIndex,
    plugins: List<RepoPlugin>,
    onDismiss: () -> Unit,
    onInstall: (RepoPlugin) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Available in ${repo.name}") },
        text = {
            if (plugins.isEmpty()) {
                Text("No plugins available")
            } else {
                LazyColumn {
                    items(plugins) { plugin ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(plugin.name, style = MaterialTheme.typography.bodyMedium)
                                    Text("v${plugin.version} - ${plugin.packageName}", style = MaterialTheme.typography.bodySmall)
                                    if (plugin.description.isNotEmpty()) {
                                        Text(plugin.description, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                Button(onClick = { onInstall(plugin) }) { Text(stringResource(R.string.install)) }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}