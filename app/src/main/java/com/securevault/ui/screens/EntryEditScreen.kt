package com.securevault.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryEditScreen(
    entryId: Long?,
    onBack: () -> Unit,
    viewModel: EntryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val existingEntry by viewModel.entry.collectAsState()
    val saved by viewModel.saved.collectAsState()

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Общее") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isFavorite by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var showGeneratorDialog by remember { mutableStateOf(false) }

    val categories = listOf("Общее", "Социальные сети", "Банки", "Работа", "Почта", "Другое")

    LaunchedEffect(entryId) {
        if (entryId != null) viewModel.loadEntry(entryId)
    }

    LaunchedEffect(existingEntry) {
        existingEntry?.let { e ->
            title = e.title
            category = e.category
            username = e.username
            password = e.password
            url = e.url
            notes = e.notes
            isFavorite = e.isFavorite
        }
    }

    LaunchedEffect(saved) {
        if (saved) onBack()
    }

    if (showGeneratorDialog) {
        var genLength by remember { mutableStateOf(20f) }
        var genUpper by remember { mutableStateOf(true) }
        var genDigits by remember { mutableStateOf(true) }
        var genSymbols by remember { mutableStateOf(true) }
        var preview by remember { mutableStateOf(viewModel.generatePassword()) }

        AlertDialog(
            onDismissRequest = { showGeneratorDialog = false },
            title = { Text("Генератор паролей") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = preview,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text("Длина: ${genLength.toInt()}")
                    Slider(value = genLength, onValueChange = {
                        genLength = it
                        preview = viewModel.generatePassword(it.toInt(), genUpper, genDigits, genSymbols)
                    }, valueRange = 8f..40f)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(genUpper, { genUpper = it; preview = viewModel.generatePassword(genLength.toInt(), it, genDigits, genSymbols) })
                        Text("Заглавные буквы")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(genDigits, { genDigits = it; preview = viewModel.generatePassword(genLength.toInt(), genUpper, it, genSymbols) })
                        Text("Цифры")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(genSymbols, { genSymbols = it; preview = viewModel.generatePassword(genLength.toInt(), genUpper, genDigits, it) })
                        Text("Символы")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    password = preview
                    showGeneratorDialog = false
                }) { Text("Использовать") }
            },
            dismissButton = {
                TextButton(onClick = { showGeneratorDialog = false }) { Text("Отмена") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (entryId == null) "Новая запись" else "Редактировать") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            null,
                            tint = if (isFavorite) MaterialTheme.colorScheme.secondary
                                   else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = {
                        viewModel.saveEntry(
                            id = entryId ?: 0,
                            title = title, category = category, username = username,
                            password = password, url = url, notes = notes, isFavorite = isFavorite
                        )
                    }) {
                        Icon(Icons.Default.Check, "Сохранить")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название *") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Label, null) },
                singleLine = true
            )

            var catExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = catExpanded,
                onExpandedChange = { catExpanded = it }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Категория") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(catExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = { category = cat; catExpanded = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Логин / Email") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, null) },
                trailingIcon = {
                    if (username.isNotEmpty()) {
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("username", username))
                        }) { Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp)) }
                    }
                },
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль *") },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    Row {
                        if (password.isNotEmpty()) {
                            IconButton(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("password", password))
                            }) { Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp)) }
                        }
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                        }
                        IconButton(onClick = { showGeneratorDialog = true }) {
                            Icon(Icons.Default.Casino, null)
                        }
                    }
                },
                singleLine = true
            )

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL (необязательно)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Language, null) },
                singleLine = true
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Заметки (необязательно)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6
            )
        }
    }
}
