package dev.manka.scoreboard

import arrow.core.Either

internal fun Either<Error, Any>.getLeft() = this.swap().getOrNull()