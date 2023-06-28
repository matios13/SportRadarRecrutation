package dev.manka.scoreboard

import arrow.core.getOrElse
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

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

    @Test
    fun `should add homeTeam score only to specific match`() {
        val match1 = createMatch("homeTeam1", "awayTeam1")
        val match2 = createMatch("homeTeam2", "awayTeam2")
        val match3 = createMatch("homeTeam3", "awayTeam3")

        scoreboard.addHomeScore(match1.id)
        scoreboard.addHomeScore(match1.id)
        scoreboard.addHomeScore(match2.id)

        val matches = scoreboard.getMatches()

        SoftAssertions.assertSoftly {
            it.assertThat(matches.find { m -> m.id == match1.id }?.homeScore).isEqualTo(2)
            it.assertThat(matches.find { m -> m.id == match2.id }?.homeScore).isEqualTo(1)
            it.assertThat(matches.find { m -> m.id == match3.id }?.homeScore).isEqualTo(0)
        }
    }

    @Test
    fun `should add awayTeam score only to specific match`() {
        val match1 = createMatch("homeTeam1", "awayTeam1")
        val match2 = createMatch("homeTeam2", "awayTeam2")
        val match3 = createMatch("homeTeam3", "awayTeam3")

        scoreboard.addAwayScore(match3.id)
        scoreboard.addAwayScore(match3.id)
        scoreboard.addAwayScore(match1.id)

        val matches = scoreboard.getMatches()

        SoftAssertions.assertSoftly {
            it.assertThat(matches.find { m -> m.id == match1.id }?.awayScore).isEqualTo(1)
            it.assertThat(matches.find { m -> m.id == match2.id }?.awayScore).isEqualTo(0)
            it.assertThat(matches.find { m -> m.id == match3.id }?.awayScore).isEqualTo(2)
        }
    }

    @Test
    fun `should add both homeTeam and awayTeam score only to specific match`() {
        val match1 = createMatch("homeTeam1", "awayTeam1")
        val match2 = createMatch("homeTeam2", "awayTeam2")
        val match3 = createMatch("homeTeam3", "awayTeam3")

        scoreboard.addHomeScore(match1.id)
        scoreboard.addHomeScore(match1.id)
        scoreboard.addHomeScore(match2.id)

        scoreboard.addAwayScore(match3.id)
        scoreboard.addAwayScore(match3.id)
        scoreboard.addAwayScore(match1.id)

        val matches = scoreboard.getMatches()

        SoftAssertions.assertSoftly {
            it.assertThat(matches.find { m -> m.id == match1.id }?.homeScore).isEqualTo(2)
            it.assertThat(matches.find { m -> m.id == match2.id }?.homeScore).isEqualTo(1)
            it.assertThat(matches.find { m -> m.id == match3.id }?.homeScore).isEqualTo(0)
            it.assertThat(matches.find { m -> m.id == match1.id }?.awayScore).isEqualTo(1)
            it.assertThat(matches.find { m -> m.id == match2.id }?.awayScore).isEqualTo(0)
            it.assertThat(matches.find { m -> m.id == match3.id }?.awayScore).isEqualTo(2)
        }
    }

    @Test
    fun `should not update homeScore when Match is not existing`() {
        createMatch("homeTeam", "awayTeam").id
        val matchUpdateResult = scoreboard.addHomeScore(UUID.randomUUID())
        SoftAssertions.assertSoftly {
            it.assertThat(matchUpdateResult.getLeft()).isInstanceOf(Scoreboard.MatchNotFoundError::class.java)
            it.assertThat(scoreboard.getMatches().first().homeScore).isEqualTo(0)
        }
    }


    @Test
    fun `should not update awayScore when Match is not existing`() {
        createMatch("homeTeam", "awayTeam").id
        val matchUpdateResult = scoreboard.addAwayScore(UUID.randomUUID())
        SoftAssertions.assertSoftly {
            it.assertThat(matchUpdateResult.getLeft()).isInstanceOf(Scoreboard.MatchNotFoundError::class.java)
            it.assertThat(scoreboard.getMatches().first().awayScore).isEqualTo(0)
        }
    }

    @Test
    fun `should not update score when Match is not existing`() {
        createMatch("homeTeam", "awayTeam").id
        val matchUpdateResult = scoreboard.updateScore(UUID.randomUUID(), 1, 2)
        SoftAssertions.assertSoftly {
            it.assertThat(matchUpdateResult.getLeft()).isInstanceOf(Scoreboard.MatchNotFoundError::class.java)
            it.assertThat(scoreboard.getMatches().first().awayScore).isEqualTo(0)
            it.assertThat(scoreboard.getMatches().first().homeScore).isEqualTo(0)
        }
    }

    @Test
    fun `should update only proper match score`() {
        val matchID = createMatch("homeTeam", "awayTeam").id
        val match2ID = createMatch("homeTeam2", "awayTeam2").id
        val match3ID = createMatch("homeTeam3", "awayTeam3").id
        scoreboard.updateScore(matchID, 1, 2).getOrElse { fail("Match has not been created", it) }
        scoreboard.updateScore(match3ID, 5, 3).getOrElse { fail("Match has not been created", it) }
        val matches = scoreboard.getMatches()
        SoftAssertions.assertSoftly {
            it.assertThat(matches.find { m -> m.id == matchID }?.homeScore).isEqualTo(1)
            it.assertThat(matches.find { m -> m.id == matchID }?.awayScore).isEqualTo(2)
            it.assertThat(matches.find { m -> m.id == match2ID }?.homeScore).isEqualTo(0)
            it.assertThat(matches.find { m -> m.id == match2ID }?.awayScore).isEqualTo(0)
            it.assertThat(matches.find { m -> m.id == match3ID }?.homeScore).isEqualTo(5)
            it.assertThat(matches.find { m -> m.id == match3ID }?.awayScore).isEqualTo(3)
        }
    }

    @Test
    fun `should remove finished match`() {
        val matchID = createMatch("homeTeam", "awayTeam").id
        scoreboard.finishMatch(matchID)
        val matches = scoreboard.getMatches()
        assertThat(matches.size).isEqualTo(0)
    }

    @Test
    fun `should remove only finished match`() {
        val match = createMatch("homeTeam", "awayTeam")
        val match2 = createMatch("homeTeam2", "awayTeam2")
        val match3 = createMatch("homeTeam3", "awayTeam3")
        val match4 = createMatch("homeTeam4", "awayTeam4")
        scoreboard.finishMatch(match.id)
        scoreboard.finishMatch(match3.id)
        val matches = scoreboard.getMatches()
        assertThat(matches).containsExactlyInAnyOrder(match2, match4)
    }

    @Test
    fun `should handle multiple finishes of one match`(){
        val match = createMatch("homeTeam", "awayTeam")
        scoreboard.finishMatch(match.id)
        scoreboard.finishMatch(match.id)
        scoreboard.finishMatch(match.id)
        val matches = scoreboard.getMatches()
        assertThat(matches.size).isEqualTo(0)
    }

    private fun createMatch(homeTeam: String, awayTeam: String) =
        scoreboard.startMatch(homeTeam, awayTeam).getOrElse { fail("Match has not been created", it) }
}