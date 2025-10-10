package com.aghyy.gameflix.library

class InMemoryGamingLibrary : GamingLibrary {
    override fun getGames(): List<Game> {
        return listOf(
            Game("The Witcher 3", "https://i.imgur.com/1Z3g0g8.jpeg"),
            Game("Cyberpunk 2077", "https://i.imgur.com/8QJg1m1.jpeg"),
            Game("Elden Ring", "https://i.imgur.com/2s3aS2U.jpeg"),
            Game("Red Dead Redemption 2", "https://i.imgur.com/2s3aS2U.jpeg"),
            Game("God of War", "https://i.imgur.com/C4yY8n4.jpeg")
        )
    }
}
