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

public class TextFieldTest {

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

    @Test
    public void testInsertMiddlePreservesAllCharacters() {
        TextField tf = new TextField("Label", "hello", 10, TextField.ANY);
        // Prior to fix, inserting "X" at position 2 produced "heXlo", deleting the 'l' at index 2
        tf.insert("X", 2);
        assertEquals("heXllo", tf.getString());
    }

    @Test
    public void testInsertBeginningPreservesAllCharacters() {
        TextField tf = new TextField("Label", "hello", 10, TextField.ANY);
        // Prior to fix, inserting at 0 deleted the first character ("Xello")
        tf.insert("X", 0);
        assertEquals("Xhello", tf.getString());
    }

    @Test
    public void testInsertEnd() {
        TextField tf = new TextField("Label", "hello", 10, TextField.ANY);
        tf.insert("X", 5);
        assertEquals("helloX", tf.getString());
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void testInsertNegativePositionThrows() {
        TextField tf = new TextField("Label", "hello", 10, TextField.ANY);
        tf.insert("X", -1);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void testInsertPositionGreaterThanLengthThrows() {
        TextField tf = new TextField("Label", "hello", 10, TextField.ANY);
        tf.insert("X", 6);
    }

    @Test(expected = NullPointerException.class)
    public void testInsertNullStringThrows() {
        TextField tf = new TextField("Label", "hello", 10, TextField.ANY);
        tf.insert(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInsertExceedingMaxSizeThrows() {
        TextField tf = new TextField("Label", "hello", 5, TextField.ANY);
        tf.insert("X", 2);
    }
}

