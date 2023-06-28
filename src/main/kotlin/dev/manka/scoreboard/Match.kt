package dev.manka.scoreboard

class Match(homeTeam: String, awayTeam: String) {
    init {
        require(homeTeam.isNotBlank()) { "Home team cannot be blank" }
        require(awayTeam.isNotBlank()) { "Away team cannot be blank" }
        require(homeTeam != awayTeam) { "Home team and away team cannot be the same" }
    }
    val homeTeam= homeTeam
    val awayTeam= awayTeam
    var homeScore = 0
    var awayScore = 0
}