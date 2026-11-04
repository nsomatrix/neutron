package javax.microedition.lcdui;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.neutron.device.Device;
import org.neutron.device.DeviceFactory;
import org.neutron.device.InputMethod;
import org.neutron.device.ui.UIFactory;

public class CustomItemTest {

    static class StubCustomItem extends CustomItem {
        StubCustomItem() {
            super("TestCustomItem");
        }

        @Override
        protected int getMinContentHeight() { return 10; }
        @Override
        protected int getMinContentWidth() { return 10; }
        @Override
        protected int getPrefContentHeight(int width) { return 20; }
        @Override
        protected int getPrefContentWidth(int height) { return 20; }
        @Override
        protected void paint(Graphics g, int w, int h) {}
    }

    static class DummyInputMethod extends InputMethod {
        @Override
        public void dispose() {}

        @Override
        public int getGameAction(int keyCode) {
            if (keyCode == Canvas.KEY_NUM2) {
                return Canvas.UP;
            }
            if (keyCode == Canvas.KEY_NUM8) {
                return Canvas.DOWN;
            }
            return 0;
        }

        @Override
        public int getKeyCode(int gameAction) {
            return 0;
        }

        @Override
        public String getKeyName(int keyCode) {
            return "";
        }
    }

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

        final InputMethod dummyInputMethod = new DummyInputMethod();

        Device dummyDevice = (Device) Proxy.newProxyInstance(
            Device.class.getClassLoader(),
            new Class<?>[] { Device.class },
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) {
                    if ("getUIFactory".equals(method.getName())) {
                        return dummyUIFactory;
                    }
                    if ("getInputMethod".equals(method.getName())) {
                        return dummyInputMethod;
                    }
                    return null;
                }
            }
        );

        DeviceFactory.setDevice(dummyDevice);
    }

    @Test
    public void testGetGameActionDelegatesToDisplayInputMethod() {
        StubCustomItem item = new StubCustomItem();
        // Prior to fix, getGameAction unconditionally returned 0 (stub TODO)
        assertEquals("KEY_NUM2 must map to game action UP", Canvas.UP, item.getGameAction(Canvas.KEY_NUM2));
        assertEquals("KEY_NUM8 must map to game action DOWN", Canvas.DOWN, item.getGameAction(Canvas.KEY_NUM8));
        assertEquals("Unmapped key must return 0", 0, item.getGameAction(9999));
    }
}

