package com.coms309.Cynance;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ExpensesTest.class,
        InvestmentTest.class,
        NikhilAdminTest.class,
        NikhilSystemTest.class,
        SearchTest.class,
        SubscriptionTest.class
})
public class NikhilTest {
    // Just an entry point — no methods needed
}
