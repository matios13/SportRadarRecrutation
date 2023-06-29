import java.util.*

sealed class MatchUpdateError(message: String) : Error(message) {
    class CannotDecreaseScoreError(fromHomeScore: Int, fromAwayScore: Int, toHomeScore: Int, toAwayScore: Int) :
        MatchUpdateError("Cannot decrease score from $fromHomeScore:$fromAwayScore to $toHomeScore:$toAwayScore")

    class MatchNotFoundError(id: UUID) : MatchUpdateError("Match with id $id not found")
}

