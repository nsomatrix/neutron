package org.neutron.jsr256;

import org.junit.Assert;
import org.junit.Test;

import javax.microedition.sensor.LimitCondition;
import javax.microedition.sensor.RangeCondition;
import javax.microedition.sensor.SensorInfo;
import javax.microedition.sensor.SensorManager;
import javax.microedition.sensor.Unit;

public class JSR256Test {
    @Test
    public void testFindSensors() {
        SensorInfo[] sensors = SensorManager.findSensors("acceleration", null);
        Assert.assertNotNull(sensors);
        Assert.assertTrue(sensors.length > 0);
        Assert.assertEquals("acceleration", sensors[0].getQuantity());
        Assert.assertNotNull(sensors[0].getChannelInfos());
        Assert.assertEquals(3, sensors[0].getChannelInfos().length);
    }

    @Test
    public void testConditions() {
        LimitCondition limit = new LimitCondition(10.0, LimitCondition.OP_GREATER_THAN);
        Assert.assertTrue(limit.isMet(15.0));
        Assert.assertFalse(limit.isMet(5.0));

        RangeCondition range = new RangeCondition(5.0, 15.0, RangeCondition.OP_INSIDE);
        Assert.assertTrue(range.isMet(10.0));
        Assert.assertFalse(range.isMet(20.0));
    }

    @Test
    public void testUnit() {
        Unit u = Unit.getUnit("m/s^2");
        Assert.assertNotNull(u);
        Assert.assertEquals("m/s^2", u.toString());
    }
}

