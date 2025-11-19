package com.github.brendonmendicino.houseshareserver.util

import java.util.*

fun ByteArray.toB64Url(): String = Base64.getUrlEncoder().encodeToString(this).replace("=", "")