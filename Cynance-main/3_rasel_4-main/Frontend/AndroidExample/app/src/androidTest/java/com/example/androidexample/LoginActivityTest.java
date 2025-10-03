package com.example.androidexample;

import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> activityRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void emptyUsernameAndPassword_showsErrorMessage() {
        onView(withId(R.id.login_login_btn)).perform(click());
        onView(withId(R.id.login_message_text)).check(matches(withText("Please enter both username and password.")));
    }

    @Test
    public void invalidLogin_showsErrorOrNetworkMessage() {
        // Type dummy username
        onView(withId(R.id.login_username_edt)).perform(typeText("fakeuser"));
        onView(withId(R.id.login_username_edt)).perform(closeSoftKeyboard());

        // Type dummy password
        onView(withId(R.id.login_password_edt)).perform(typeText("fakepass"));
        onView(withId(R.id.login_password_edt)).perform(closeSoftKeyboard());

        // Tap login button
        onView(withId(R.id.login_login_btn)).perform(click());

        // Wait for server/network
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Match any likely result from the backend
        onView(withId(R.id.login_message_text)).check(matches(
                anyOf(
                        withText(containsString("Invalid username or password")),
                        withText(containsString("Error")),
                        withText(containsString("Network error"))
                )
        ));
    }
}
