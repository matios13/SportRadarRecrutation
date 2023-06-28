package dev.manka.scoreboard

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ScoreboardTest{
    private val scoreboard = Scoreboard()
    @Test
    fun `should start match`() {
        val match = scoreboard.startMatch("homeTeam", "awayTeam")
        assertEquals("homeTeam", match.homeTeam)
        assertEquals("awayTeam", match.awayTeam)
        assertEquals(0, match.homeScore)
        assertEquals(0, match.awayScore)
    }
}