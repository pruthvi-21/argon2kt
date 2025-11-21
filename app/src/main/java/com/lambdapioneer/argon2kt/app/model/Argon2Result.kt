package com.lambdapioneer.argon2kt.app.model

data class Argon2Result(
    val hashInHex: String? = null,
    val encodedOutputAsString: String? = null,
    val error: Exception? = null,
    val timeInMs: Long? = null
)