package dev.manka.scoreboard

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.time.LocalDateTime
import java.util.*

internal class Match(homeTeam: String, awayTeam: String) : Comparable<Match>{
    init {
        require(homeTeam.isNotBlank()) { "Home team cannot be blank" }
        require(awayTeam.isNotBlank()) { "Away team cannot be blank" }
        require(homeTeam != awayTeam) { "Home team and away team cannot be the same" }
    }

    val id: UUID = UUID.randomUUID()
    val homeTeam = homeTeam
    val awayTeam = awayTeam

    // it might be a good idea to move it to constructor if match could start in the past or another
    // part of the system is responsible for starting it
    private val startingTime: LocalDateTime = LocalDateTime.now()
    var homeScore = 0
        private set
    var awayScore = 0
        private set


    fun addHomeScore() {
        homeScore++
    }

    fun addAwayScore() {
        awayScore++
    }

    fun updateScore(homeScore: Int, awayScore: Int): Either<MatchUpdateError.CannotDecreaseScoreError, Unit> {
        if (homeScore < this.homeScore || awayScore < this.awayScore) {
            return MatchUpdateError.CannotDecreaseScoreError(this.homeScore, this.awayScore, homeScore, awayScore).left()
        }
        this.homeScore = homeScore
        this.awayScore = awayScore
        return Unit.right()
    }


    override fun compareTo(other: Match): Int {
        val homeScoreDiff = (homeScore+awayScore) - (other.homeScore+other.awayScore)
        return if (homeScoreDiff != 0) homeScoreDiff else this.startingTime.compareTo(other.startingTime)
    }
}