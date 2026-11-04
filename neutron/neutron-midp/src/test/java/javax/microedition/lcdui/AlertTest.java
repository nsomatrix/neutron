package javax.microedition.lcdui;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.neutron.device.Device;
import org.neutron.device.DeviceFactory;
import org.neutron.device.ui.UIFactory;

public class AlertTest {

    @Before
    public void setUp() {
        final UIFactory dummyUIFactory = (UIFactory) Proxy.newProxyInstance(
            UIFactory.class.getClassLoader(),
            new Class<?>[] { UIFactory.class },
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) {
                    Class<?> returnType = method.getReturnType();
                    if (returnType.isInterface()) {
                        return Proxy.newProxyInstance(
                            returnType.getClassLoader(),
                            new Class<?>[] { returnType },
                            new InvocationHandler() {
                                @Override
                                public Object invoke(Object p, Method m, Object[] a) {
                                    return null;
                                }
                            }
                        );
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
                    if ("getUIFactory".equals(method.getName())) {
                        return dummyUIFactory;
                    }
                    return null;
                }
            }
        );

        DeviceFactory.setDevice(dummyDevice);
    }

    private static class DummyDisplayable extends Displayable {
        DummyDisplayable(String title) {
            super(title);
        }

        @Override
        public void paint(Graphics g) {
        }
    }

    @Test
    public void testAlertNextDisplayableIsInstanceSpecific() {
        Alert alert1 = new Alert("Alert 1");
        Alert alert2 = new Alert("Alert 2");

        Displayable target1 = new DummyDisplayable("Screen 1");
        Displayable target2 = new DummyDisplayable("Screen 2");

        alert1.nextDisplayable = target1;
        alert2.nextDisplayable = target2;

        // Verify state is independent and alert2 did not overwrite alert1
        assertSame("alert1 target must be target1", target1, alert1.nextDisplayable);
        assertSame("alert2 target must be target2", target2, alert2.nextDisplayable);

        // Dismiss alert1 via its default listener
        alert1.defaultListener.commandAction(Alert.DISMISS_COMMAND, alert1);

        // alert1's nextDisplayable is reset to null, but alert2's must remain target2
        assertNull("alert1 target must be null after dismissal", alert1.nextDisplayable);
        assertSame("alert2 target must remain target2", target2, alert2.nextDisplayable);
    }
}
