package org.neutron.jsr179;

import org.junit.Assert;
import org.junit.Test;

import javax.microedition.location.Coordinates;
import javax.microedition.location.Location;
import javax.microedition.location.LocationProvider;

public class JSR179Test {
    @Test
    public void testCoordinatesDistanceAndAzimuth() {
        Coordinates london = new Coordinates(51.5074, -0.1278, 15f);
        Coordinates paris = new Coordinates(48.8566, 2.3522, 35f);

        float distance = london.distance(paris);
        // London to Paris is ~343 km
        Assert.assertTrue(distance > 300000 && distance < 400000);

        float azimuth = london.azimuthTo(paris);
        Assert.assertTrue(azimuth > 100 && azimuth < 180);
    }

    @Test
    public void testLocationProvider() throws Exception {
        LocationProvider provider = LocationProvider.getInstance(null);
        Assert.assertNotNull(provider);
        Assert.assertEquals(LocationProvider.AVAILABLE, provider.getState());

        Location loc = provider.getLocation(1000);
        Assert.assertNotNull(loc);
        Assert.assertTrue(loc.isValid());
        Assert.assertNotNull(loc.getQualifiedCoordinates());
    }
}

