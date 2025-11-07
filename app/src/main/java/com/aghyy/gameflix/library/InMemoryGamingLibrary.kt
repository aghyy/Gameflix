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
                        thumbnailUrl = "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/292030/ad9240e088f953a84aee814034c50a6a92bf4516/header.jpg?t=1761131270",
                        description = "The Witcher 3: Wild Hunt is an action role-playing game with a third-person perspective. Players control Geralt of Rivia, a monster slayer known as a Witcher.",
                        developer = "CD Projekt Red",
                        releaseDate = "May 19, 2015"
                    ),
                    Game(
                        id = "eldenring",
                        title = "Elden Ring",
                        thumbnailUrl = "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1245620/header.jpg?t=1748630546",
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
                        thumbnailUrl = "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1091500/e9047d8ec47ae3d94bb8b464fb0fc9e9972b4ac7/header.jpg?t=1756209867",
                        description = "Cyberpunk 2077 is a non-linear, open-world, first-person, action-RPG. The game is set in the dystopian metropolis of Night City.",
                        developer = "CD Projekt Red",
                        releaseDate = "December 10, 2020"
                    ),
                    Game(
                        id = "rdr2",
                        title = "Red Dead Redemption 2",
                        thumbnailUrl = "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1174180/header.jpg?t=1759502961",
                        description = "Red Dead Redemption 2 is a Western-themed action-adventure game. Played from a first or third-person perspective, the game is set in an open-world environment featuring a fictionalized version of the Western, Midwestern and Southern United States in 1899.",
                        developer = "Rockstar Games",
                        releaseDate = "October 26, 2018"
                    ),
                     Game(
                        id = "godofwar",
                        title = "God of War",
                        thumbnailUrl = "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1593500/header.jpg?t=1750949016",
                        description = "While the first seven games were loosely based on Greek mythology, this installment is rooted in Norse mythology. The main protagonists are Kratos, the former Greek God of War, and his young son Atreus.",
                        developer = "Santa Monica Studio",
                        releaseDate = "April 20, 2018"
                    )
                )
            )
        )
    }
}
