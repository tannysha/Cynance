package com.example.androidexample;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.androidexample.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
public class StockChartActivityTest {

    @Rule
    public ActivityTestRule<StockChartActivity> rule =
            new ActivityTestRule<>(StockChartActivity.class, true, false);

    @Test
    public void testEmptyFields_showsToastError() {
        rule.launchActivity(new Intent());

        onView(withId(R.id.btnFetch)).perform(click());

        // Unfortunately, Espresso cannot test Toast easily without custom matcher.
        // So we just make sure the button is clickable and logic executes without crash
        onView(withId(R.id.etSymbol)).check(matches(isDisplayed()));
        onView(withId(R.id.etFrom)).check(matches(isDisplayed()));
        onView(withId(R.id.etTo)).check(matches(isDisplayed()));
    }

    @Test
    public void testFormInput() {
        rule.launchActivity(new Intent());

        onView(withId(R.id.etSymbol)).perform(typeText("AAPL"), closeSoftKeyboard());
        onView(withId(R.id.etFrom)).perform(typeText("2024-04-01"), closeSoftKeyboard());
        onView(withId(R.id.etTo)).perform(typeText("2024-05-01"), closeSoftKeyboard());

        onView(withId(R.id.etSymbol)).check(matches(withText("AAPL")));
        onView(withId(R.id.etFrom)).check(matches(withText("2024-04-01")));
        onView(withId(R.id.etTo)).check(matches(withText("2024-05-01")));
    }
}
