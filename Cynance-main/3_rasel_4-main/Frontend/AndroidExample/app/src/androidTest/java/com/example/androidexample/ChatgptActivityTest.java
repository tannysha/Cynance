package com.example.androidexample;

import android.content.SharedPreferences;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.androidexample.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class ChatgptActivityTest {

    @Rule
    public ActivityTestRule<ChatgptActivity> rule =
            new ActivityTestRule<>(ChatgptActivity.class, true, false);

    @Before
    public void setup() {
        SharedPreferences.Editor editor = ApplicationProvider.getApplicationContext()
                .getSharedPreferences("AppPrefs", 0).edit();
        editor.putString("USERNAME", "TestUser");
        editor.apply();
    }

    @Test
    public void testPromptInputAndButtonWorks() {
        rule.launchActivity(new Intent());

        // Type something in the prompt input
        onView(withId(R.id.promptEditText))
                .perform(typeText("Hello GPT"), closeSoftKeyboard());

        // Press the send button
        onView(withId(R.id.sendButton))
                .perform(click());

        // Ensure text was entered correctly (you can't test network response here without a mock server)
        onView(withId(R.id.promptEditText))
                .check(matches(withText("Hello GPT")));
    }
}
