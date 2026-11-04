package org.neutron.device.ui;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import static org.junit.Assert.*;

import org.neutron.device.Device;
import org.neutron.device.DeviceDisplay;
import org.neutron.device.DeviceFactory;

public class EventDispatcherTest {

    @Test
    public void testServiceRepaintsWhenNoPaintScheduledReturnsImmediately() {
        EventDispatcher dispatcher = new EventDispatcher();
        dispatcher.serviceRepaints(); // Should not block or throw
    }

    @Test(timeout = 5000)
    public void testServiceRepaintsOnEventThreadDoesNotDeadlock() throws Exception {
        final EventDispatcher dispatcher = new EventDispatcher();
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean executed = new AtomicBoolean(false);
        final AtomicBoolean repainted = new AtomicBoolean(false);

        final DeviceDisplay dummyDisplay = (DeviceDisplay) Proxy.newProxyInstance(
            DeviceDisplay.class.getClassLoader(),
            new Class<?>[] { DeviceDisplay.class },
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) {
                    if ("repaint".equals(method.getName())) {
                        repainted.set(true);
                    }
                    return null;
                }
            }
        );

        Device dummyDevice = (Device) Proxy.newProxyInstance(
            Device.class.getClassLoader(),
            new Class<?>[] { Device.class },
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) {
                    if ("getDeviceDisplay".equals(method.getName())) {
                        return dummyDisplay;
                    }
                    return null;
                }
            }
        );

        DeviceFactory.setDevice(dummyDevice);

        Thread thread = new Thread(dispatcher, "test-event-dispatcher");
        dispatcher.setThread(thread);
        thread.start();

        try {
            // Queue a runnable event that calls serviceRepaints while on the event dispatcher thread
            dispatcher.put(new Runnable() {
                @Override
                public void run() {
                    // Queue a real PaintEvent
                    dispatcher.put(dispatcher.new PaintEvent(0, 0, 100, 100));

                    // Call serviceRepaints() on the event dispatcher thread itself
                    // Prior to the fix, this would cause permanent deadlock via serviceRepaintsLock.wait()
                    dispatcher.serviceRepaints();
                    executed.set(true);
                    latch.countDown();
                }
            });

            boolean finished = latch.await(3, TimeUnit.SECONDS);
            assertTrue("serviceRepaints() on event thread must complete without deadlock", finished);
            assertTrue("Runnable on event thread must have executed", executed.get());
            assertTrue("Paint event must have been serviced", repainted.get());
        } finally {
            dispatcher.cancel();
            thread.join(1000);
        }
    }
}
