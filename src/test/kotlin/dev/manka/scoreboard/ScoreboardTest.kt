package dev.manka.scoreboard

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ScoreboardTest{
    private val scoreboard = Scoreboard()
    @Test
    fun `should start match`() {
        val match = scoreboard.startMatch("homeTeam", "awayTeam")

        SoftAssertions.assertSoftly {
            it.assertThat(match.homeTeam).isEqualTo("homeTeam")
            it.assertThat(match.awayTeam).isEqualTo("awayTeam")
            it.assertThat(match.homeScore).isEqualTo(0)
            it.assertThat(match.awayScore).isEqualTo(0)
        }
    }

    @Test
    fun `started match should be added to scoreboard`() {
        scoreboard.startMatch("homeTeam", "awayTeam")

        val matches = scoreboard.getMatches()

        SoftAssertions.assertSoftly {
            it.assertThat(matches).hasSize(1)
            it.assertThat(matches[0].homeTeam).isEqualTo("homeTeam")
            it.assertThat(matches[0].awayTeam).isEqualTo("awayTeam")
        }
    }
}