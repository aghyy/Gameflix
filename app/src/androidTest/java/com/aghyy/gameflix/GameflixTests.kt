package com.aghyy.gameflix

import android.content.Context
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameflixTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        composeTestRule.launchGameflixApp()
    }

    @Test
    fun searchShowsResultAndFavoriteToggle() {
        val favoriteDescription = context.getString(R.string.content_description_add_favorite)
        val searchContentDescription = "Search"
        val searchFieldMatcher = hasContentDescription(searchContentDescription) and hasSetTextAction()
        val searchField = composeTestRule.onNode(searchFieldMatcher)
        val searchedGame = "FIFA 23"

        searchField.performTextClearance()
        searchField.performTextInput(searchedGame)
        searchField.performImeAction()

        composeTestRule.waitUntil(timeoutMillis = 15_000) {
            composeTestRule.onAllNodes(hasText(searchedGame, substring = true) and hasClickAction())
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        Thread.sleep(1000)

        val firstResultMatcher = hasText(searchedGame, substring = true) and hasClickAction()
        composeTestRule.onAllNodes(firstResultMatcher)[0].assertExists()

        val favoriteMatcher = hasContentDescription(favoriteDescription) and hasAnyAncestor(firstResultMatcher)
        composeTestRule.onNode(favoriteMatcher).assertExists()
    }
}
