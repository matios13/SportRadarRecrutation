package dev.manka.scoreboard

import MatchUpdateError
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.*


class Scoreboard {
    private val matches = mutableMapOf<UUID, Match>()

    fun startMatch(homeTeam: String, awayTeam: String): Either<MatchCreationError, MatchDTO> {
        try {
            val match = Match(homeTeam, awayTeam)
            if (matches.any { matchesShareSameTeam(it.value, match) }) {
                return MatchCreationError.MatchAlreadyExistsError(match.homeTeam, match.awayTeam).left()
            }
            matches[match.id] = match
            return MatchDTO(match).right()
        }catch (e: IllegalArgumentException){
            return MatchCreationError.IllegalArgumentError(e.message).left()
        }
    }

    fun addHomeScore(id: UUID): Either<MatchUpdateError.MatchNotFoundError, MatchDTO> {
        matches[id]?.let {
            it.addHomeScore()
            return MatchDTO(it).right()
        }
        return MatchUpdateError.MatchNotFoundError(id).left()
    }

    fun addAwayScore(id: UUID): Either<MatchUpdateError.MatchNotFoundError, MatchDTO> {
        matches[id]?.let {
            it.addAwayScore()
            return MatchDTO(it).right()
        }
        return MatchUpdateError.MatchNotFoundError(id).left()
    }

    fun updateScore(id: UUID, homeScore: Int, awayScore: Int): Either<MatchUpdateError, MatchDTO> {
        matches[id]?.let { match ->
            return match.updateScore(homeScore, awayScore).map { MatchDTO(match) }
        }
        return MatchUpdateError.MatchNotFoundError(id).left()
    }

    fun finishMatch(id: UUID) {
        matches.remove(id)
    }

    fun getSortedMatches(): List<MatchDTO> {
        return matches
            .values
            .sortedDescending()
            .map { MatchDTO(it) }
    }

    private fun matchesShareSameTeam(m1: Match, m2: Match) =
        m1.homeTeam == m2.homeTeam || m1.awayTeam == m2.awayTeam || m1.homeTeam == m2.awayTeam || m1.awayTeam == m2.homeTeam
}