package com.example.android.mashup

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.TypeTextAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.mashup.Creator.CancelDialogFragment
import com.example.android.mashup.Creator.CreatorFragment
import com.example.android.mashup.Creator.SaveDialogFragment
import com.example.android.mashup.Details.DetailsFragment
import com.example.android.mashup.Feed.FeedFragment

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class   ExampleInstrumentedTest {
    @Test
    fun testCancelNavigation() {
        // Create a mock NavController
        val mockNavController = mock(NavController::class.java)

        // Create a graphical FragmentScenario for the TitleScreen
        val titleScenario = launchFragment<CancelDialogFragment>()

        // Set the NavController property on the fragment
        titleScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        // Verify that performing a click prompts the correct Navigation action
        onView(ViewMatchers.withId(R.id.cancel_dialog_discard_button)).perform(ViewActions.click())
        verify(mockNavController).navigate(R.id.action_cancelDialogFragment_to_FirstFragment2)
    }
}