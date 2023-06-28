package dev.manka.scoreboard

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.*


class Scoreboard {
    private val matches = mutableMapOf<UUID,Match>()

    fun startMatch(homeTeam: String, awayTeam: String): Either<MatchAlreadyExistsError, MatchDTO> {
        val match = Match(homeTeam, awayTeam)
        if (matches.any { matchesShareSameTeam(it.value, match) }) {
            return MatchAlreadyExistsError(match.homeTeam, match.awayTeam).left()
        }
        matches[match.id] = match
        return MatchDTO(match).right()
    }

    fun addHomeScore(id: UUID): Either<MatchNotFoundError, MatchDTO> {
        matches[id]?.let {
            it.addHomeScore()
            return MatchDTO(it).right()
        }
        return MatchNotFoundError(id).left()
    }

    fun addAwayScore(id: UUID): Either<MatchNotFoundError, MatchDTO> {
        matches[id]?.let {
            it.addAwayScore()
            return MatchDTO(it).right()
        }
        return MatchNotFoundError(id).left()
    }

    fun updateScore(id: UUID, homeScore: Int, awayScore: Int): Either<MatchNotFoundError, MatchDTO> {
        matches[id]?.let {
            it.updateScore(homeScore, awayScore)
            return MatchDTO(it).right()
        }
        return MatchNotFoundError(id).left()
    }

    fun getMatches(): List<MatchDTO> {
        return matches.map { MatchDTO(it.value) }
    }

    private fun matchesShareSameTeam(m1: Match, m2: Match) =
        m1.homeTeam == m2.homeTeam || m1.awayTeam == m2.awayTeam || m1.homeTeam == m2.awayTeam || m1.awayTeam == m2.homeTeam

    class MatchAlreadyExistsError(homeTeam: String?,awayTeam:String?): Error("Match between $homeTeam and $awayTeam already exists")

    class MatchNotFoundError(id: UUID) : Error("Match with id $id not found")
}