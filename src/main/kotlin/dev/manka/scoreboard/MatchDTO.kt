package dev.manka.scoreboard

class MatchDTO internal constructor(match: Match) {
    val homeTeam = match.homeTeam
    val awayTeam = match.awayTeam
    val homeScore = match.homeScore
    val awayScore = match.awayScore
}