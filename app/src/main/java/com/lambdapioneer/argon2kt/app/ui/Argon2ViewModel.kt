package com.lambdapioneer.argon2kt.app.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.app.R
import com.lambdapioneer.argon2kt.app.model.Argon2Config
import com.lambdapioneer.argon2kt.app.model.Argon2Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Argon2ViewModel : ViewModel() {

    private val _config = MutableStateFlow(Argon2Config())
    val config: StateFlow<Argon2Config> = _config

    private val _result = MutableStateFlow<Argon2Result?>(null)
    val result: StateFlow<Argon2Result?> = _result

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun updateConfig(config: Argon2Config) {
        _config.value = config
    }

    fun calculateHash(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _result.value = runArgon2(context, _config.value)
            _isLoading.value = false
        }
    }

    fun checkForErrors(context: Context) {
        val errors = mutableListOf<String>()

        if (_config.value.salt.isEmpty()) errors += context.getString(R.string.error_salt_empty)

        val memoryInt = _config.value.memory.toIntOrNull()
        if (memoryInt == null || memoryInt <= 0) {
            errors += context.getString(R.string.error_memory_invalid)
        }

        val parallelismInt = _config.value.parallelism.toIntOrNull()
        if (parallelismInt == null || parallelismInt <= 0) {
            errors += context.getString(R.string.error_parallelism_invalid)
        }

        val iterationsInt = _config.value.iterations.toIntOrNull()
        if (iterationsInt == null || iterationsInt <= 0) {
            errors += context.getString(R.string.error_iterations_invalid)
        }

        val lengthInt = _config.value.length.toIntOrNull()
        if (lengthInt == null || lengthInt <= 0) {
            errors += context.getString(R.string.error_length_invalid)
        }

        if (errors.isNotEmpty()) throw IllegalArgumentException(errors.joinToString("\n"))
    }

    fun reset() {
        _isLoading.value = false
        _result.value = null
        _config.value = Argon2Config()
    }

    private suspend fun runArgon2(context: Context, params: Argon2Config): Argon2Result {
        return withContext(Dispatchers.Default) {   // heavy CPU work
            try {
                checkForErrors(context)
                val start = System.nanoTime()

                val result = Argon2Kt().hash(
                    mode = params.mode,
                    password = params.password.toByteArray(),
                    salt = params.salt.toByteArray(),
                    tCostInIterations = params.iterations.toInt(),
                    mCostInKibibyte = 1 shl params.memory.toInt(),
                    parallelism = params.parallelism.toInt(),
                    version = params.version,
                    hashLengthInBytes = params.length.toInt()
                )

                val end = System.nanoTime()

                Argon2Result(
                    hashInHex = result.rawHashAsHexadecimal(),
                    encodedOutputAsString = result.encodedOutputAsString(),
                    timeInMs = (end - start) / 1_000_000
                )
            } catch (e: Exception) {
                Argon2Result(error = e)
            }
        }
    }
}
