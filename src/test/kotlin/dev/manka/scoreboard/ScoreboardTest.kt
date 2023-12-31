package dev.manka.scoreboard

import MatchUpdateError
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
        val match = createMatch("homeTeam", "awayTeam")

        val matches = scoreboard.getSortedMatches()

        assertThat(matches).containsExactlyInAnyOrder(match)
    }

    @Test
    fun `should add multiple matches`() {
        val match1 = createMatch("homeTeam1", "awayTeam1")
        val match2 = createMatch("homeTeam2", "awayTeam2")
        val match3 = createMatch("homeTeam3", "awayTeam3")

        SoftAssertions.assertSoftly {
            it.assertThat(scoreboard.getSortedMatches().size).isEqualTo(3)
            it.assertThat(scoreboard.getSortedMatches())
                .containsExactlyInAnyOrder(match1, match2, match3)
        }
    }

    @Test
    fun `should not add match for team already playing`() {
        createMatch("homeTeam1", "awayTeam1")
        createMatch("homeTeam2", "awayTeam2")

        val match3Result = scoreboard.startMatch("homeTeam1", "awayTeam3")

        val matches = scoreboard.getSortedMatches()

        SoftAssertions.assertSoftly {
            it.assertThat(match3Result.getLeft()).isInstanceOf(MatchCreationError.MatchAlreadyExistsError::class.java)
            it.assertThat(matches.size).isEqualTo(2)
            it.assertThat(matches.map { m -> m.awayTeam }).doesNotContain("awayTeam3")
        }
    }

    @Test
    fun `should not start a match for team with empty names`() {
        val matchCreationResult = scoreboard.startMatch("", "")

        SoftAssertions.assertSoftly {
            it.assertThat(matchCreationResult.getLeft())
                .isInstanceOf(MatchCreationError.IllegalArgumentError::class.java)
            it.assertThat(scoreboard.getSortedMatches().size).isEqualTo(0)
        }
    }

    @Test
    fun `should not start match for both teams already playing`() {
        createMatch("homeTeam1", "awayTeam1")
        createMatch("homeTeam2", "awayTeam2")

        val match3Result = scoreboard.startMatch("homeTeam1", "awayTeam2")

        SoftAssertions.assertSoftly {
            it.assertThat(match3Result.getLeft()).isInstanceOf(MatchCreationError.MatchAlreadyExistsError::class.java)
            it.assertThat(scoreboard.getSortedMatches().size).isEqualTo(2)
        }
    }

    @Test
    fun `should not start match for home team that's already playing as away team in other match`() {
        createMatch("homeTeam1", "awayTeam1")
        val match3Result = scoreboard.startMatch("awayTeam1", "awayTeam3")

        SoftAssertions.assertSoftly {
            it.assertThat(match3Result.getLeft()).isInstanceOf(MatchCreationError.MatchAlreadyExistsError::class.java)
            it.assertThat(scoreboard.getSortedMatches().size).isEqualTo(1)
            it.assertThat(scoreboard.getSortedMatches().map { m -> m.awayTeam }).doesNotContain("awayTeam3")
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

        val matches = scoreboard.getSortedMatches()

        SoftAssertions.assertSoftly {
            it.assertThat(matches.getById(match1.id).homeScore).isEqualTo(2)
            it.assertThat(matches.getById(match2.id).homeScore).isEqualTo(1)
            it.assertThat(matches.getById(match3.id).homeScore).isEqualTo(0)
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

        val matches = scoreboard.getSortedMatches()

        SoftAssertions.assertSoftly {
            it.assertThat(matches.getById(match1.id).awayScore).isEqualTo(1)
            it.assertThat(matches.getById(match2.id).awayScore).isEqualTo(0)
            it.assertThat(matches.getById(match3.id).awayScore).isEqualTo(2)
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

        val matches = scoreboard.getSortedMatches()

        SoftAssertions.assertSoftly {
            it.assertThat(matches.getById(match1.id).homeScore).isEqualTo(2)
            it.assertThat(matches.getById(match2.id).homeScore).isEqualTo(1)
            it.assertThat(matches.getById(match3.id).homeScore).isEqualTo(0)
            it.assertThat(matches.getById(match1.id).awayScore).isEqualTo(1)
            it.assertThat(matches.getById(match2.id).awayScore).isEqualTo(0)
            it.assertThat(matches.getById(match3.id).awayScore).isEqualTo(2)
        }
    }

    @Test
    fun `should not update homeScore when Match is not existing`() {
        val matchId = createMatch("homeTeam", "awayTeam").id
        val matchUpdateResult = scoreboard.addHomeScore(UUID.randomUUID())
        SoftAssertions.assertSoftly {
            it.assertThat(matchUpdateResult.getLeft()).isInstanceOf(MatchUpdateError.MatchNotFoundError::class.java)
            it.assertThat(scoreboard.getSortedMatches().getById(matchId).homeScore).isEqualTo(0)
        }
    }

    @Test
    fun `should not update awayScore when Match is not existing`() {
        val matchId = createMatch("homeTeam", "awayTeam").id
        val matchUpdateResult = scoreboard.addAwayScore(UUID.randomUUID())
        SoftAssertions.assertSoftly {
            it.assertThat(matchUpdateResult.getLeft()).isInstanceOf(MatchUpdateError.MatchNotFoundError::class.java)
            it.assertThat(scoreboard.getSortedMatches().getById(matchId).awayScore).isEqualTo(0)
        }
    }

    @Test
    fun `should not update score when Match is not existing`() {
        val matchId = createMatch("homeTeam", "awayTeam").id
        val matchUpdateResult = scoreboard.updateScore(UUID.randomUUID(), 1, 2)
        SoftAssertions.assertSoftly {
            it.assertThat(matchUpdateResult.getLeft()).isInstanceOf(MatchUpdateError.MatchNotFoundError::class.java)
            it.assertThat(scoreboard.getSortedMatches().getById(matchId).awayScore).isEqualTo(0)
            it.assertThat(scoreboard.getSortedMatches().getById(matchId).homeScore).isEqualTo(0)
        }
    }

    @Test
    fun `should not decrease score for a match`() {
        val matchId = createMatch("homeTeam", "awayTeam").id
        scoreboard.updateScore(matchId, 4, 5)

        val matchUpdateResult = scoreboard.updateScore(matchId, 3, 5)

        SoftAssertions.assertSoftly {
            it.assertThat(matchUpdateResult.getLeft())
                .isInstanceOf(MatchUpdateError.CannotDecreaseScoreError::class.java)
            it.assertThat(scoreboard.getSortedMatches().getById(matchId).homeScore).isEqualTo(4)
            it.assertThat(scoreboard.getSortedMatches().getById(matchId).awayScore).isEqualTo(5)
        }
    }

    @Test
    fun `should update only proper match score`() {
        val matchID = createMatch("homeTeam", "awayTeam").id
        val match2ID = createMatch("homeTeam2", "awayTeam2").id
        val match3ID = createMatch("homeTeam3", "awayTeam3").id

        scoreboard.updateScore(matchID, 1, 2).getOrElse { fail("Match has not been created", it) }
        scoreboard.updateScore(match3ID, 5, 3).getOrElse { fail("Match has not been created", it) }

        val matches = scoreboard.getSortedMatches()

        SoftAssertions.assertSoftly {
            it.assertThat(matches.getById(matchID).homeScore).isEqualTo(1)
            it.assertThat(matches.getById(matchID).awayScore).isEqualTo(2)
            it.assertThat(matches.getById(match2ID).homeScore).isEqualTo(0)
            it.assertThat(matches.getById(match2ID).awayScore).isEqualTo(0)
            it.assertThat(matches.getById(match3ID).homeScore).isEqualTo(5)
            it.assertThat(matches.getById(match3ID).awayScore).isEqualTo(3)
        }
    }

    @Test
    fun `should remove finished match`() {
        val matchID = createMatch("homeTeam", "awayTeam").id
        scoreboard.finishMatch(matchID)
        val matches = scoreboard.getSortedMatches()
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

        val matches = scoreboard.getSortedMatches()

        assertThat(matches).containsExactlyInAnyOrder(match2, match4)
    }

    @Test
    fun `should handle multiple finishes of one match`() {
        val match = createMatch("homeTeam", "awayTeam")
        scoreboard.finishMatch(match.id)
        scoreboard.finishMatch(match.id)
        scoreboard.finishMatch(match.id)

        val matches = scoreboard.getSortedMatches()

        assertThat(matches.size).isEqualTo(0)
    }

    @Test
    fun `should return proper data when sorting matches`() {
        createMatchWithScore("Mexico", "Canada", 0, 5)
        createMatchWithScore("Spain", "Brazil", 10, 2)

        val sortedMatches = scoreboard.getSortedMatches()

        SoftAssertions.assertSoftly {
            it.assertThat(sortedMatches[0].homeTeam).isEqualTo("Spain")
            it.assertThat(sortedMatches[0].awayTeam).isEqualTo("Brazil")
            it.assertThat(sortedMatches[0].homeScore).isEqualTo(10)
            it.assertThat(sortedMatches[0].awayScore).isEqualTo(2)
            it.assertThat(sortedMatches[1].homeTeam).isEqualTo("Mexico")
            it.assertThat(sortedMatches[1].awayTeam).isEqualTo("Canada")
            it.assertThat(sortedMatches[1].homeScore).isEqualTo(0)
            it.assertThat(sortedMatches[1].awayScore).isEqualTo(5)
        }
    }


    @Test
    fun `should sort matches by total score`() {
        createMatchWithScore("Mexico", "Canada", 0, 5)
        createMatchWithScore("Spain", "Brazil", 10, 2)
        createMatchWithScore("Germany", "France", 2, 2)

        val sortedMatches = scoreboard.getSortedMatches()

        SoftAssertions.assertSoftly {
            it.assertThat(sortedMatches[0].homeTeam).isEqualTo("Spain")
            it.assertThat(sortedMatches[1].homeTeam).isEqualTo("Mexico")
            it.assertThat(sortedMatches[2].homeTeam).isEqualTo("Germany")
        }
    }

    @Test
    fun `should sort matches by time when total score is the same`() {
        createMatchWithScore("Mexico", "Canada", 0, 5)
        createMatchWithScore("Spain", "Brazil", 5, 0)
        createMatchWithScore("Germany", "France", 2, 3)

        val sortedMatches = scoreboard.getSortedMatches()

        SoftAssertions.assertSoftly {
            it.assertThat(sortedMatches[0].homeTeam).isEqualTo("Germany")
            it.assertThat(sortedMatches[1].homeTeam).isEqualTo("Spain")
            it.assertThat(sortedMatches[2].homeTeam).isEqualTo("Mexico")
        }
    }

    private fun createMatch(homeTeam: String, awayTeam: String) =
        scoreboard.startMatch(homeTeam, awayTeam).getOrElse { fail("Match has not been created", it) }

    private fun createMatchWithScore(homeTeam: String, awayTeam: String, homeScore: Int, awayScore: Int) {
        val match = createMatch(homeTeam, awayTeam)
        scoreboard.updateScore(match.id, homeScore, awayScore)
    }

    private fun List<MatchDTO>.getById(id: UUID) = this.find { it.id == id} ?: fail("Match not found")
}
