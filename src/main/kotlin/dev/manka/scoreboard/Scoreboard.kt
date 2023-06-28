package dev.manka.scoreboard


class Scoreboard {
    private val matches = mutableListOf<Match>()

    fun startMatch(homeTeam: String, awayTeam: String): MatchDTO {
        val match = Match(homeTeam, awayTeam)
        matches.add(match)
        return MatchDTO(match)
    }

    fun getMatches(): List<MatchDTO> {
        return matches.map { MatchDTO(it) }
    }
}