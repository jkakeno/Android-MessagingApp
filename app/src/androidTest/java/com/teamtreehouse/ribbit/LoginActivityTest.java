package com.teamtreehouse.ribbit;

import android.content.ComponentName;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.teamtreehouse.ribbit.ui.LoginActivity;
import com.teamtreehouse.ribbit.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    @Rule
    public ActivityTestRule<LoginActivity> activityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void setUp() throws Exception{
        Intents.init();
    }

    @Test
    public void correctDialogIsDisplayedWhenNoUserName() throws Exception {
        // Arrange
        String userName = "";
        onView(withId(R.id.usernameField)).perform(typeText(userName));

        // Act
        onView(withId(R.id.loginButton)).perform(click());

        // Assert
        onView(withText("Please make sure you enter a username and password!")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
    }

    @Test
    public void correctDialogIsDisplayedWhenNoPassword() throws Exception {
        // Arrange
        String passWord = "";
        onView(withId(R.id.passwordField)).perform(typeText(passWord));

        // Act
        onView(withId(R.id.loginButton)).perform(click());

        // Assert
        onView(withText("Please make sure you enter a username and password!")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
    }

    @Test
    public void userAbleToLogIn() throws Exception {
        // Arrange
        String userName = "Ben";
        String passWord = "test";
        onView(withId(R.id.usernameField)).perform(typeText(userName));
        onView(withId(R.id.passwordField)).perform(typeText(passWord));

        // Act
        onView(withId(R.id.loginButton)).perform(click());

        // Assert
        Intents.intended(IntentMatchers.hasComponent(new ComponentName(getTargetContext(),MainActivity.class)));

    }

    @After
    public void tearDown() throws Exception{
        Intents.release();
    }
}
