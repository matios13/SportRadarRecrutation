package dev.manka.scoreboard

class MatchDTO internal constructor(match: Match) {
    val homeTeam = match.homeTeam
    val awayTeam = match.awayTeam
    val homeScore = match.homeScore
    val awayScore = match.awayScore
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MatchDTO

        if (homeTeam != other.homeTeam) return false
        return awayTeam == other.awayTeam
    }

    override fun hashCode(): Int {
        var result = homeTeam.hashCode()
        result = 31 * result + awayTeam.hashCode()
        return result
    }


}