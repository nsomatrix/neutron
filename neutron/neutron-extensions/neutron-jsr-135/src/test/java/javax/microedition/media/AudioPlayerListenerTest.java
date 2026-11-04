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
}

