package javax.microedition.media;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineListener;
import org.junit.Test;

import static org.junit.Assert.*;

public class AudioPlayerListenerTest {

    @Test
    public void testMidiAudioPlayerDoesNotDuplicateMetaEventListenerOnRepeatedStarts() throws Exception {
        final AtomicInteger addCount = new AtomicInteger(0);
        final AtomicInteger removeCount = new AtomicInteger(0);

        Sequencer mockSequencer = (Sequencer) Proxy.newProxyInstance(
                Sequencer.class.getClassLoader(),
                new Class[]{Sequencer.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        String name = method.getName();
                        if ("addMetaEventListener".equals(name)) {
                            addCount.incrementAndGet();
                            return Boolean.TRUE;
                        } else if ("removeMetaEventListener".equals(name)) {
                            removeCount.incrementAndGet();
                            return null;
                        } else if ("start".equals(name) || "stop".equals(name) || "close".equals(name)) {
                            return null;
                        }
                        return null;
                    }
                }
        );

        MidiAudioPlayer player = new MidiAudioPlayer();
        Field seqField = MidiAudioPlayer.class.getDeclaredField("sequencer");
        seqField.setAccessible(true);
        seqField.set(player, mockSequencer);

        // Start 3 times (simulating loops or restart after pause)
        player.start();
        player.start();
        player.start();

        assertEquals("addMetaEventListener should only be called once across multiple starts", 1, addCount.get());

        player.close();
        assertEquals("removeMetaEventListener should be called on close", 1, removeCount.get());
    }

    @Test
    public void testSampledAudioPlayerDoesNotDuplicateLineListenerOnRepeatedStarts() throws Exception {
        final AtomicInteger addCount = new AtomicInteger(0);
        final AtomicInteger removeCount = new AtomicInteger(0);

        Clip mockClip = (Clip) Proxy.newProxyInstance(
                Clip.class.getClassLoader(),
                new Class[]{Clip.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        String name = method.getName();
                        if ("addLineListener".equals(name)) {
                            addCount.incrementAndGet();
                            return null;
                        } else if ("removeLineListener".equals(name)) {
                            removeCount.incrementAndGet();
                            return null;
                        } else if ("start".equals(name) || "stop".equals(name) || "close".equals(name) || "flush".equals(name)) {
                            return null;
                        }
                        return null;
                    }
                }
        );

        SampledAudioPlayer player = new SampledAudioPlayer();
        Field clipField = SampledAudioPlayer.class.getDeclaredField("clip");
        clipField.setAccessible(true);
        clipField.set(player, mockClip);

        // Start 3 times
        player.start();
        player.start();
        player.start();

        assertEquals("addLineListener should only be called once across multiple starts", 1, addCount.get());

        player.close();
        assertEquals("removeLineListener should be called on close", 1, removeCount.get());
    }

    @Test
    public void testSampledAudioPlayerListenerSelfRemovalOnEventDoesNotThrowCME() throws Exception {
        final SampledAudioPlayer player = new SampledAudioPlayer();
        final boolean[] called = new boolean[] { false, false };

        PlayerListener listener1 = new PlayerListener() {
            public void playerUpdate(Player p, String event, Object eventData) {
                called[0] = true;
                player.removePlayerListener(this);
            }
        };
        PlayerListener listener2 = new PlayerListener() {
            public void playerUpdate(Player p, String event, Object eventData) {
                called[1] = true;
            }
        };

        player.addPlayerListener(listener1);
        player.addPlayerListener(listener2);

        javax.sound.sampled.LineEvent stopEvent = new javax.sound.sampled.LineEvent(
                (javax.sound.sampled.Line) Proxy.newProxyInstance(
                        javax.sound.sampled.Line.class.getClassLoader(),
                        new Class[]{javax.sound.sampled.Line.class},
                        new InvocationHandler() {
                            public Object invoke(Object proxy, Method method, Object[] args) {
                                return null;
                            }
                        }
                ),
                javax.sound.sampled.LineEvent.Type.STOP,
                0
        );

        player.update(stopEvent);

        assertTrue("First listener should be invoked", called[0]);
        assertTrue("Second listener should be invoked without CME", called[1]);
    }

    @Test
    public void testMidiAudioPlayerListenerSelfRemovalOnEventDoesNotThrowCME() throws Exception {
        final MidiAudioPlayer player = new MidiAudioPlayer();
        final boolean[] called = new boolean[] { false, false };

        PlayerListener listener1 = new PlayerListener() {
            public void playerUpdate(Player p, String event, Object eventData) {
                called[0] = true;
                player.removePlayerListener(this);
            }
        };
        PlayerListener listener2 = new PlayerListener() {
            public void playerUpdate(Player p, String event, Object eventData) {
                called[1] = true;
            }
        };

        player.addPlayerListener(listener1);
        player.addPlayerListener(listener2);

        javax.sound.midi.MetaMessage endMsg = new javax.sound.midi.MetaMessage();
        endMsg.setMessage(0x2F, new byte[0], 0);
        player.meta(endMsg);

        assertTrue("First listener should be invoked", called[0]);
        assertTrue("Second listener should be invoked without CME", called[1]);
    }

    @Test
    public void testAmrAudioPlayerLockFreeAlienDispatch() throws Exception {
        final AmrAudioPlayer player = new AmrAudioPlayer();
        byte[] dummyAmr = new byte[] { '#', '!', 'A', 'M', 'R', '\n', 0x04, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
        player.open(new java.io.ByteArrayInputStream(dummyAmr), "audio/amr");

        final boolean[] stopCalledInCallback = new boolean[] { false };
        player.addPlayerListener(new PlayerListener() {
            public void playerUpdate(Player p, String event, Object eventData) {
                if (PlayerListener.STARTED.equals(event)) {
                    // Call stop() inside listener callback without deadlock
                    try {
                        p.stop();
                        stopCalledInCallback[0] = true;
                    } catch (Exception e) {
                        fail("Exception in callback stop: " + e.getMessage());
                    }
                }
            }
        });

        player.start();
        assertTrue("Listener callback should be able to call stop() safely", stopCalledInCallback[0]);
        player.close();
    }
}

