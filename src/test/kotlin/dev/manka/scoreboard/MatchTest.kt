package dev.manka.scoreboard

import org.assertj.core.api.Assertions
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test


class MatchTest{
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
        Assertions.assertThatThrownBy{ Match("homeTeam", "homeTeam") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should not start match with empty home team name`() {
        Assertions.assertThatThrownBy{ Match("", "homeTeam") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should not start match with empty away team name`() {
        Assertions.assertThatThrownBy{ Match("homeTeam", "") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should not start match with both teams names empty`() {
        Assertions.assertThatThrownBy{ Match("", "") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}