/*
 *  Neutron
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 */

package javax.microedition.media;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

import org.neutron.media.control.J2SEVolumeControl;

class MidiAudioPlayer implements Player, MetaEventListener 
{
    private int state;
    
	private Sequence  sequence = null;             // The contents of a MIDI file
	private Sequencer sequencer = null;            // We play MIDI Sequences with a Sequencer
	private Vector    vListeners = null;           // All PlayerListeners for this audio
	private int       iLoopCount = 1;
	private boolean   metaListenerAdded = false;
	
	public boolean open( InputStream stream, String type ) 
	{
		try {
    		// First, get a Sequencer to play sequences of MIDI events
    		//That is, to send events to a Synthesizer at the right time.
    		sequencer = MidiSystem.getSequencer( ); // Used to play sequences
    		sequencer.open(); // Turn it on.
    		//Get a Synthesizer for the Sequencer to send notes to
    		Synthesizer synth = MidiSystem.getSynthesizer( );
    		synth.open( ); // acquire whatever resources it needs
    		//The Sequencer obtained above may be connected to a Synthesizer
    		//by default, or it may not. Therefore, we explicitly connect it.
    		Transmitter transmitter = sequencer.getTransmitter( );
    		Receiver receiver = synth.getReceiver( );
    		transmitter.setReceiver(receiver);
    		//Read the sequence from the file and tell the sequencer about it
    		sequence = MidiSystem.getSequence( stream );
    		sequencer.setSequence(sequence);
	    } catch(UnsatisfiedLinkError e) { 
	        e.printStackTrace(); 
	    } catch(IOException e) { 
	        e.printStackTrace(); 
	    } catch(MidiUnavailableException e) {
	        e.printStackTrace(); 
	    } catch( InvalidMidiDataException e ) { 
	        e.printStackTrace(); 
	    }
	    
	    state = UNREALIZED;
	    
		return false;
	}

	protected void dispose() 
	{
        close();
	}
	
	public synchronized void addPlayerListener(PlayerListener playerListener) 
	{
		if (playerListener == null)
			return;
		if( vListeners == null )
			vListeners = new Vector();
		if (!vListeners.contains(playerListener)) {
			vListeners.add( playerListener );
		}
	}

	public void close() 
	{
		Manager.mediaDone( this );
		if( sequencer != null ) {
			if (metaListenerAdded) {
				sequencer.removeMetaEventListener(this);
				metaListenerAdded = false;
			}
			sequencer.close();
		}
	}

	public void deallocate() {
	}

	public String getContentType() {
		return "audio/midi";
	}

	public long getDuration() {
		return 0;
	}

	public long getMediaTime() 
	{
		if( sequencer != null )
			return sequencer.getMicrosecondPosition();
		return 0;
	}

	public int getState() {
		return state;
	}

	public void prefetch() throws MediaException {
		// TODO Auto-generated method stub

	}

	public void realize() throws MediaException {
		// TODO Auto-generated method stub

	}

	public synchronized void removePlayerListener(PlayerListener playerListener) 
	{
		if( vListeners == null || playerListener == null )
			return;
		vListeners.remove( playerListener );
	}

	private final J2SEVolumeControl volumeControl = new J2SEVolumeControl();

	public void setLoopCount(int count) 
	{
		if (count == 0 || count < -1) {
			throw new IllegalArgumentException("Invalid loop count: " + count);
		}
		iLoopCount = count;
	}

	public long setMediaTime(long now) throws MediaException {
		if( sequencer != null )
			sequencer.setMicrosecondPosition( now );
		return now;
	}

	public void start() throws MediaException {
		if (sequencer != null) {
			if (!metaListenerAdded) {
				sequencer.addMetaEventListener( this );
				metaListenerAdded = true;
			}
			sequencer.start();
			state = STARTED;
		}
	}

	public void stop() throws MediaException {
		if( sequencer != null ) {
			sequencer.stop();
			state = PREFETCHED;
		}
	}

	public Control getControl(String controlType) {
		if ("VolumeControl".equals(controlType) || "javax.microedition.media.control.VolumeControl".equals(controlType)) {
			return volumeControl;
		}

		return null;
	}

	public Control[] getControls() {
		return new Control[] { volumeControl };
	}

    public void meta( MetaMessage event )
    {
        if (event.getType() == 47) //End of Track type
        {
            if (iLoopCount > 0) 
            {
                iLoopCount--;
            }
            if( iLoopCount > 0 || iLoopCount == -1)
        	{
        		sequencer.setMicrosecondPosition( 0 );
        		try{ start(); } 
        		catch( MediaException e ) { e.printStackTrace(); }
        	}
        	else
        	{
    			state = PREFETCHED;
    			if (sequencer != null) {
    				sequencer.stop();
    				sequencer.setMicrosecondPosition(0);
    			}
    			iLoopCount = 1;
    			java.util.List copy = null;
    			synchronized (this) {
    				if (vListeners != null && !vListeners.isEmpty()) {
    					copy = new java.util.ArrayList(vListeners);
    				}
    			}
    			if (copy != null) {
    				for (int i = 0; i < copy.size(); i++) {
    					PlayerListener listener = (PlayerListener) copy.get(i);
    					try {
    						listener.playerUpdate(this, PlayerListener.END_OF_MEDIA, null);
    					} catch (Throwable t) {
    					}
    				}
    			}
        	}
        }
    }

}
