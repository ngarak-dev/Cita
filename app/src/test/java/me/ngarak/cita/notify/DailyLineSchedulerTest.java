package me.ngarak.cita.notify;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DailyLineSchedulerTest {

    @Test
    public void millisUntilNext_isPositiveAndUnderTwoDays() {
        long ms = DailyLineScheduler.millisUntilNext(9, 0);
        assertTrue(ms > 0);
        assertTrue(ms <= 48L * 60L * 60L * 1000L);
    }
}
