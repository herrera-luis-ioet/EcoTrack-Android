package com.example.ecotrack.auth

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.ecotrack.R
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import com.example.ecotrack.MainActivity

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityUITest {

    @get:Rule
    val intentsTestRule = IntentsTestRule(LoginActivity::class.java)

    private val validEmail = "test@example.com"
    private val validPassword = "password123"
    private val invalidEmail = "invalid-email"
    private val shortPassword = "123"

    @Before
    fun setup() {
        // Initialize Intents for testing navigation
        Intents.init()
    }

    @Test
    fun testInitialViewsDisplayed() {
        // Verify all views are displayed initially
        onView(withId(R.id.emailEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.passwordEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(withId(R.id.registerButton)).check(matches(isDisplayed()))
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testEmptyEmailValidation() {
        // Click login with empty email
        onView(withId(R.id.loginButton)).perform(click())
        
        // Verify error is shown
        onView(withId(R.id.emailEditText)).check(matches(hasErrorText("Email is required")))
    }

    @Test
    fun testEmptyPasswordValidation() {
        // Enter email but leave password empty
        onView(withId(R.id.emailEditText)).perform(typeText(validEmail), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())
        
        // Verify error is shown
        onView(withId(R.id.passwordEditText)).check(matches(hasErrorText("Password is required")))
    }

    @Test
    fun testShortPasswordValidation() {
        // Enter email and short password
        onView(withId(R.id.emailEditText)).perform(typeText(validEmail), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText(shortPassword), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())
        
        // Verify error is shown
        onView(withId(R.id.passwordEditText)).check(matches(hasErrorText("Password must be at least 6 characters")))
    }

    @Test
    fun testProgressBarVisibilityDuringLogin() {
        // Enter valid credentials
        onView(withId(R.id.emailEditText)).perform(typeText(validEmail), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText(validPassword), closeSoftKeyboard())
        
        // Click login and verify progress bar is shown
        onView(withId(R.id.loginButton)).perform(click())
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
        
        // Verify buttons are disabled during progress
        onView(withId(R.id.loginButton)).check(matches(not(isEnabled())))
        onView(withId(R.id.registerButton)).check(matches(not(isEnabled())))
    }

    @Test
    fun testSuccessfulLoginNavigation() {
        // Enter valid credentials
        onView(withId(R.id.emailEditText)).perform(typeText(validEmail), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText(validPassword), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        // Verify navigation to MainActivity
        Intents.intended(hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun testButtonsEnabledAfterFailedLogin() {
        // Enter invalid credentials
        onView(withId(R.id.emailEditText)).perform(typeText(invalidEmail), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText(validPassword), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        // Verify buttons are re-enabled after failed login
        onView(withId(R.id.loginButton)).check(matches(isEnabled()))
        onView(withId(R.id.registerButton)).check(matches(isEnabled()))
    }
}