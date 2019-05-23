package de.si.backdrop

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.si.backdroplibrary.R
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BackdropActivityTest {
    @Rule
    @JvmField
    var activityScenarioRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    companion object {
        @BeforeClass
        fun setup() {
            // Setting up
            Intents.init()
        }

        @Before
        fun setup2() {
            // Setting up
            Intents.init()
        }

        @AfterClass
        fun tearDown() {
            Intents.release()
        }
    }

    @Test
    fun creationTest() {
        onView(ViewMatchers.withId(R.id.layout_backdrop)).check(matches(ViewMatchers.isDisplayed()))

        onView(ViewMatchers.withId(R.id.button_backdrop_toolbar_menu_show)).perform(click())
        onView(ViewMatchers.withId(R.id.layout_backdrop_cardstack)).check(matches(ViewMatchers.hasChildCount(0)))
    }
}