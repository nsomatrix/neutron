package org.neutron.app.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MIDletTimerTest {

    private MIDletTimer timer;

    @Before
    public void setUp() {
        timer = new MIDletTimer();
    }

    @After
    public void tearDown() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Test
    public void testOneTimeTaskCancelReturnsFalseAfterExecution() throws Exception {
        final CountDownLatch executed = new CountDownLatch(1);
        MIDletTimerTask task = new MIDletTimerTask() {
            public void run() {
                executed.countDown();
            }
        };

        timer.schedule(task, 10);
        assertTrue("Task should execute within 2 seconds", executed.await(2, TimeUnit.SECONDS));

        // Let the timer thread finish processing
        Thread.sleep(50);

        // For a one-time task that has already executed, cancel() must return false per JavaDoc
        assertFalse("cancel() on an executed one-time task must return false", task.cancel());
    }

    @Test
    public void testRepeatingTaskCanBeCancelledAfterExecution() throws Exception {
        final CountDownLatch firstRun = new CountDownLatch(1);
        final AtomicInteger runCount = new AtomicInteger(0);

        MIDletTimerTask task = new MIDletTimerTask() {
            public void run() {
                runCount.incrementAndGet();
                firstRun.countDown();
            }
        };

        timer.schedule(task, 10, 50);
        assertTrue("Repeating task should execute at least once", firstRun.await(2, TimeUnit.SECONDS));

        // Cancel repeating task after it has started running
        boolean cancelled = task.cancel();
        assertTrue("cancel() on a repeating task must return true", cancelled);

        // Calling cancel a second time must return false
        assertFalse("Second cancel() on already cancelled task must return false", task.cancel());

        int countAtCancel = runCount.get();
        Thread.sleep(150);
        assertEquals("Task must not continue executing after cancel()", countAtCancel, runCount.get());
    }
}

