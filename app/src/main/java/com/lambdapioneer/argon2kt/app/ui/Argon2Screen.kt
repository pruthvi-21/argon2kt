package com.lambdapioneer.argon2kt.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lambdapioneer.argon2kt.Argon2Mode
import com.lambdapioneer.argon2kt.Argon2Version
import com.lambdapioneer.argon2kt.app.R
import com.lambdapioneer.argon2kt.app.ui.components.AppDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Argon2Screen(viewModel: Argon2ViewModel = viewModel()) {
    val config by viewModel.config.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("Argon2") }) }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 15.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppDropdown(
                    items = Argon2Mode.entries,
                    selectedItem = config.mode,
                    onItemSelected = { viewModel.updateConfig(config.copy(mode = it)) },
                    label = "Mode",
                    modifier = Modifier.weight(1f)
                )

                AppDropdown(
                    items = Argon2Version.entries,
                    selectedItem = config.version,
                    onItemSelected = { viewModel.updateConfig(config.copy(version = it)) },
                    label = "Version",
                    modifier = Modifier.weight(1f)
                )
            }

            TextField(
                value = config.password,
                onValueChange = { viewModel.updateConfig(config.copy(password = it)) },
                placeholder = { Text(stringResource(R.string.hint_password)) },
                label = { Text(stringResource(R.string.label_password)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    autoCorrect = false,
                ),
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = config.salt,
                onValueChange = { viewModel.updateConfig(config.copy(salt = it)) },
                placeholder = { Text(stringResource(R.string.hint_salt)) },
                label = { Text(stringResource(R.string.label_salt)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    autoCorrect = false,
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TextField(
                    value = config.memory,
                    onValueChange = { viewModel.updateConfig(config.copy(memory = it)) },
                    label = { Text(stringResource(R.string.label_memory)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                        autoCorrect = false,
                    ),
                    modifier = Modifier.weight(1f)
                )
                TextField(
                    value = config.parallelism,
                    onValueChange = { viewModel.updateConfig(config.copy(parallelism = it)) },
                    label = { Text(stringResource(R.string.label_parallelism_count)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                        autoCorrect = false,
                    ),
                    modifier = Modifier.weight(1f)
                )
                TextField(
                    value = config.iterations,
                    onValueChange = { viewModel.updateConfig(config.copy(iterations = it)) },
                    label = { Text(stringResource(R.string.label_iteration_count)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                        autoCorrect = false,
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 20.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.reset()
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    },
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier
                        .weight(4f)
                        .heightIn(min = 48.dp)
                ) {
                    Text(stringResource(R.string.button_reset))
                }
                val context = LocalContext.current
                Button(
                    onClick = {
                        viewModel.calculateHash(context)
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    },
                    shape = RoundedCornerShape(2.dp),
                    enabled = !isLoading,
                    modifier = Modifier
                        .weight(6f)
                        .heightIn(min = 48.dp)
                ) {
                    if (!isLoading) {
                        Text(stringResource(R.string.button_calculate_hash))
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 15.dp))

            if (result != null) {
                val output = result!!
                if (output.error != null) {
                    Text(
                        text = output.error.message ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    val clipboard = LocalClipboardManager.current
                    Text(stringResource(R.string.wall_time, output.timeInMs ?: 0))

                    Text(stringResource(R.string.label_output_hash))
                    Text(
                        text = output.hashInHex.orEmpty(),
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(5.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        clipboard.setText(AnnotatedString(output.hashInHex.orEmpty()))
                                    }
                                )
                            }
                    )

                    Text(stringResource(R.string.label_output_encoded))
                    Text(
                        text = output.encodedOutputAsString.orEmpty(),
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(5.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        clipboard.setText(AnnotatedString(output.encodedOutputAsString.orEmpty()))
                                    }
                                )
                            }
                    )
                }
            }
        }
    }
}
