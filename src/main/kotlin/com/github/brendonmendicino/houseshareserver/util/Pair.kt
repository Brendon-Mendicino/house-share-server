package com.github.brendonmendicino.houseshareserver.util

/**
 * Map a pair where both members are not null.
 */
inline fun <A : Any, B : Any, C> Pair<A?, B?>.mapNotNull(crossinline transform: (A, B) -> C): C? =
    let { (a, b) -> if (a != null && b != null) transform(a, b) else null }
