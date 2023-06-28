package dev.manka.scoreboard

import arrow.core.getOrElse
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ScoreboardTest {
    private val scoreboard = Scoreboard()

    @Test
    fun `should start match`() {
        val match = createMatch("homeTeam", "awayTeam")

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

    @Test
    fun `should add multiple matches`() {
        val match1 = createMatch("homeTeam1", "awayTeam1")
        val match2 = createMatch("homeTeam2", "awayTeam2")
        val match3 = createMatch("homeTeam3", "awayTeam3")

        SoftAssertions.assertSoftly {
            it.assertThat(scoreboard.getMatches().size).isEqualTo(3)
            it.assertThat(scoreboard.getMatches())
                .containsExactlyInAnyOrder(match1, match2, match3)
        }
    }

    @Test
    fun `should not add match for team already playing`() {
        createMatch("homeTeam1", "awayTeam1")
        createMatch("homeTeam2", "awayTeam2")

        val match3Result = scoreboard.startMatch("homeTeam1", "awayTeam3")

        SoftAssertions.assertSoftly {
            it.assertThat(match3Result.getLeft()).isInstanceOf(Scoreboard.MatchAlreadyExistsError::class.java)
            it.assertThat(scoreboard.getMatches().size).isEqualTo(2)
            it.assertThat(scoreboard.getMatches().map { m -> m.awayTeam }).doesNotContain("awayTeam3")
        }
    }

    @Test
    fun `should not start match for both teams already playing`() {
        createMatch("homeTeam1", "awayTeam1")
        createMatch("homeTeam2", "awayTeam2")

        val match3Result = scoreboard.startMatch("homeTeam1", "awayTeam2")

        SoftAssertions.assertSoftly {
            it.assertThat(match3Result.getLeft()).isInstanceOf(Scoreboard.MatchAlreadyExistsError::class.java)
            it.assertThat(scoreboard.getMatches().size).isEqualTo(2)
        }
    }

    @Test
    fun `should not start match for home team that's already playing as away team in other match`() {
        createMatch("homeTeam1", "awayTeam1")
        val match3Result = scoreboard.startMatch("awayTeam1", "awayTeam3")

        SoftAssertions.assertSoftly {
            it.assertThat(match3Result.getLeft()).isInstanceOf(Scoreboard.MatchAlreadyExistsError::class.java)
            it.assertThat(scoreboard.getMatches().size).isEqualTo(1)
            it.assertThat(scoreboard.getMatches().map { m -> m.awayTeam }).doesNotContain("awayTeam3")
        }
    }



    private fun createMatch(homeTeam: String, awayTeam: String) =
        scoreboard.startMatch(homeTeam, awayTeam).getOrElse { fail("Match has not been created", it) }
}