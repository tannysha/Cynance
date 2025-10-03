package com.example.androidexample;

import android.content.Intent;
import android.widget.*;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AdminActivityTest {

    private ActivityScenario<?> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(AdminActivity.class);
    }

    @After
    public void tearDown() {
        scenario.close();
    }

    @Test
    public void testActivityLaunches() {
        assertNotNull(scenario);
    }
}
