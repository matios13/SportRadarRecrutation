package dev.manka.scoreboard

import org.assertj.core.api.Assertions.*
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test


class MatchTest {
    @Test
    fun `should start match`() {
        val match = Match("homeTeam", "awayTeam")
        SoftAssertions.assertSoftly {
            it.assertThat(match.homeTeam).isEqualTo("homeTeam")
            it.assertThat(match.awayTeam).isEqualTo("awayTeam")
            it.assertThat(match.homeScore).isEqualTo(0)
            it.assertThat(match.awayScore).isEqualTo(0)
        }
    }

    @Test
    fun `should not start match with one team`() {
        assertThatThrownBy { Match("homeTeam", "homeTeam") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should not start match with empty home team name`() {
        assertThatThrownBy { Match("", "homeTeam") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should not start match with empty away team name`() {
        assertThatThrownBy { Match("homeTeam", "") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should not start match with both teams names empty`() {
        assertThatThrownBy { Match("", "") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should add home score`() {
        val match = Match("homeTeam", "awayTeam")
        match.addHomeScore()
        assertThat(match.homeScore).isEqualTo(1)
    }

    @Test
    fun `should add away score`() {
        val match = Match("homeTeam", "awayTeam")
        match.addAwayScore()
        assertThat(match.awayScore).isEqualTo(1)
    }

    @Test
    fun `should add multiple scores`() {
        val match = Match("homeTeam", "awayTeam")

        match.addHomeScore()
        match.addHomeScore()
        match.addHomeScore()
        match.addHomeScore()
        match.addHomeScore()
        match.addHomeScore()

        match.addAwayScore()
        match.addAwayScore()
        match.addAwayScore()

        SoftAssertions.assertSoftly {
            it.assertThat(match.homeScore).isEqualTo(6)
            it.assertThat(match.awayScore).isEqualTo(3)
        }
    }

    @Test
    fun `should update score with absolute values`() {
        val match = Match("homeTeam", "awayTeam")

        match.updateScore(3, 2)

        SoftAssertions.assertSoftly {
            it.assertThat(match.homeScore).isEqualTo(3)
            it.assertThat(match.awayScore).isEqualTo(2)
        }
    }
}