package dev.manka.scoreboard

import arrow.core.Either
import arrow.core.left
import arrow.core.right


class Scoreboard {
    private val matches = mutableListOf<Match>()

    fun startMatch(homeTeam: String, awayTeam: String): Either<MatchAlreadyExistsError, MatchDTO> {
        val match = Match(homeTeam, awayTeam)
        if (matches.any { matchesShareSameTeam(it, match) }) {
            return MatchAlreadyExistsError(match.homeTeam, match.awayTeam).left()
        }
        matches.add(match)
        return MatchDTO(match).right()
    }

    fun getMatches(): List<MatchDTO> {
        return matches.map { MatchDTO(it) }
    }

    private fun matchesShareSameTeam(m1: Match, m2: Match) =
        m1.homeTeam == m2.homeTeam || m1.awayTeam == m2.awayTeam || m1.homeTeam == m2.awayTeam || m1.awayTeam == m2.homeTeam

    class MatchAlreadyExistsError(homeTeam: String?,awayTeam:String?): Error("Match between $homeTeam and $awayTeam already exists")

}