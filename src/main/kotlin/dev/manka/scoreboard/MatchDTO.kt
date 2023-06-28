package dev.manka.scoreboard

class MatchDTO internal constructor(match: Match) {
    val id = match.id
    val homeTeam = match.homeTeam
    val awayTeam = match.awayTeam
    val homeScore = match.homeScore
    val awayScore = match.awayScore

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MatchDTO

        return id == other.id
    }

    override fun hashCode() = id.hashCode()
}