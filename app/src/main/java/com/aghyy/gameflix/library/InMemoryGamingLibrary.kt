package com.aghyy.gameflix.library

class InMemoryGamingLibrary : GamingLibrary {
    override fun getGames(): List<GameCategory> {
        return listOf(
            GameCategory(
                title = "Action RPGs",
                games = listOf(
                    Game(
                        id = "witcher3",
                        title = "The Witcher 3: Wild Hunt",
                        thumbnailUrl = "https://i.imgur.com/1Z3g0g8.jpeg",
                        description = "The Witcher 3: Wild Hunt is an action role-playing game with a third-person perspective. Players control Geralt of Rivia, a monster slayer known as a Witcher.",
                        developer = "CD Projekt Red",
                        releaseDate = "May 19, 2015"
                    ),
                    Game(
                        id = "eldenring",
                        title = "Elden Ring",
                        thumbnailUrl = "https://i.imgur.com/2s3aS2U.jpeg",
                        description = "Elden Ring is an action role-playing game developed by FromSoftware. The game is a collaboration between game director Hidetaka Miyazaki and fantasy novelist George R. R. Martin.",
                        developer = "FromSoftware",
                        releaseDate = "February 25, 2022"
                    )
                )
            ),
            GameCategory(
                title = "Open World Adventures",
                games = listOf(
                    Game(
                        id = "cyberpunk2077",
                        title = "Cyberpunk 2077",
                        thumbnailUrl = "https://i.imgur.com/8QJg1m1.jpeg",
                        description = "Cyberpunk 2077 is a non-linear, open-world, first-person, action-RPG. The game is set in the dystopian metropolis of Night City.",
                        developer = "CD Projekt Red",
                        releaseDate = "December 10, 2020"
                    ),
                    Game(
                        id = "rdr2",
                        title = "Red Dead Redemption 2",
                        thumbnailUrl = "https://i.imgur.com/pT2eJ3o.jpeg",
                        description = "Red Dead Redemption 2 is a Western-themed action-adventure game. Played from a first or third-person perspective, the game is set in an open-world environment featuring a fictionalized version of the Western, Midwestern and Southern United States in 1899.",
                        developer = "Rockstar Games",
                        releaseDate = "October 26, 2018"
                    ),
                     Game(
                        id = "godofwar",
                        title = "God of War",
                        thumbnailUrl = "https://i.imgur.com/C4yY8n4.jpeg",
                        description = "While the first seven games were loosely based on Greek mythology, this installment is rooted in Norse mythology. The main protagonists are Kratos, the former Greek God of War, and his young son Atreus.",
                        developer = "Santa Monica Studio",
                        releaseDate = "April 20, 2018"
                    )
                )
            )
        )
    }
}
