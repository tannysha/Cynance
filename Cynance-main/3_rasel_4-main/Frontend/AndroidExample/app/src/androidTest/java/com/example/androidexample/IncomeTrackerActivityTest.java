package com.example.androidexample;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.example.androidexample.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class IncomeTrackerActivityTest {

    @Rule
    public IntentsTestRule<IncomeTrackerActivity> rule =
            new IntentsTestRule<>(IncomeTrackerActivity.class, true, false);

    @Test
    public void testIncomeSaveButton_withValidInput() {
        Intent intent = new Intent();
        intent.putExtra("USERNAME", "testUser");
        rule.launchActivity(intent);

        onView(withId(R.id.income_amount_input)).perform(typeText("5000"), closeSoftKeyboard());
        onView(withId(R.id.income_source_input)).perform(typeText("Internship"), closeSoftKeyboard());

        // Select spinner item (you may need to mock the spinner adapter in a full test)
        onView(withId(R.id.payment_frequency_spinner)).perform(click());
        onView(withText("Monthly")).perform(click());

        onView(withId(R.id.save_income_button)).perform(click());

        // Add delay or IdlingResource to wait for HTTP response if needed
        // onView(...).check(matches(...)) – test expected result
    }
}
