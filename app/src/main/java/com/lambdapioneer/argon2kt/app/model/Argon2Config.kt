package com.lambdapioneer.argon2kt.app.model

import com.lambdapioneer.argon2kt.Argon2Mode
import com.lambdapioneer.argon2kt.Argon2Version

data class Argon2Config(
    val password: String = "",
    val salt: String = "",
    val iterations: String = "2",
    val memory: String = "12",   // 2^N
    val parallelism: String = "2",
    val mode: Argon2Mode = Argon2Mode.ARGON2_ID,
    val version: Argon2Version = Argon2Version.V13
)