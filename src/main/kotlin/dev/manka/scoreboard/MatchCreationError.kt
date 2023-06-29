package dev.manka.scoreboard

sealed class MatchCreationError private constructor(message: String) : Error(message){
    class MatchAlreadyExistsError(homeTeam: String?,awayTeam:String?): MatchCreationError("Match between $homeTeam and $awayTeam already exists")
    class IllegalArgumentError(message: String?) : MatchCreationError(message ?: "Illegal argument error")
}


