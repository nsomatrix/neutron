package javax.microedition.amms;

import org.junit.Test;
import static org.junit.Assert.*;

public class GlobalManagerTest {

    @Test
    public void testGlobalManagerFactoryMethods() throws Exception {
        String[] types = GlobalManager.getSupportedSoundSourceTypes();
        assertNotNull(types);
        assertTrue(types.length > 0);

        EffectModule effectModule = GlobalManager.createEffectModule();
        assertNotNull(effectModule);

        SoundSource3D soundSource3D = GlobalManager.createSoundSource3D();
        assertNotNull(soundSource3D);

        SpectatorModelling spectator = GlobalManager.getSpectatorModelling();
        assertNotNull(spectator);

        MediaProcessor processor = GlobalManager.createMediaProcessor("image/raw");
        assertNotNull(processor);
        assertEquals(MediaProcessor.UNREALIZED, processor.getState());
    }
}

