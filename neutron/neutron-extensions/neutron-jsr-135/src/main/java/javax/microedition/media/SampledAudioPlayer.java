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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.neutron.media.control.J2SEVolumeControl;

class SampledAudioPlayer implements Player, LineListener 
{
	private AudioInputStream audioInputStream = null;
    private AudioInputStream decodedStream = null;
	private Clip clip = null;
	private Vector vListeners = null;           // All PlayerListeners for this audio
	private String strType = null;
	private boolean lineListenerAdded = false;
	private int state = UNREALIZED;
	
	public boolean open( InputStream stream, String type ) 
	{
	    strType = type;
		try 
	    {
			audioInputStream = AudioSystem.getAudioInputStream( new BufferedInputStream( stream ) );
			AudioFormat format = audioInputStream.getFormat();
			if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) 
			{
			    AudioFormat baseFormat = audioInputStream.getFormat();
			    AudioFormat decodedFormat = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED,
										                     baseFormat.getSampleRate(),
										                     16,
										                     baseFormat.getChannels(),
										                     baseFormat.getChannels() * 2,
										                     baseFormat.getSampleRate(),
										                     false );
			    decodedStream = AudioSystem.getAudioInputStream( decodedFormat, audioInputStream );
			    int frameLength = (int) decodedStream.getFrameLength();
			    int frameSize = decodedFormat.getFrameSize();
			    DataLine.Info info = new DataLine.Info( Clip.class, decodedFormat,
			    										frameLength * frameSize);
			    clip = (Clip) AudioSystem.getLine( info );
		    	clip.open( decodedStream );
			}
	        else
	        {
				DataLine.Info info2 = new DataLine.Info( Clip.class, format, AudioSystem.NOT_SPECIFIED );
				clip = (Clip) AudioSystem.getLine( info2 );
		    	clip.open( audioInputStream );
	        }
	    } 
	    catch( UnsupportedAudioFileException e ){ e.printStackTrace(); return false; }
	    catch( IOException e ){ e.printStackTrace(); return false; }
	    catch( LineUnavailableException e ){ e.printStackTrace(); return false; }
		return true;
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
		state = CLOSED;
		if( clip != null )
		{
			if (lineListenerAdded) {
				clip.removeLineListener(this);
				lineListenerAdded = false;
			}
			clip.flush();
	    	clip.close();
		}
		
		try 
	    {
			if( decodedStream != null )
				decodedStream.close();
			if( audioInputStream != null )
				audioInputStream.close();
	    } 
	    catch( IOException e ) { e.printStackTrace(); }
	}

	public void deallocate() {
		if( clip != null )
			clip.flush();
	}

	public String getContentType() {
		return strType;
	}

	public long getDuration() {
		if (clip != null) {
			return clip.getMicrosecondLength();
		}
		return 0;
	}

	public long getMediaTime() {
		if( clip != null )
			return clip.getMicrosecondPosition();
		return 0;
	}

	public int getState() {
		return state;
	}

	public void prefetch() throws MediaException {
		if (state < PREFETCHED) {
			state = PREFETCHED;
		}
	}

	public void realize() throws MediaException {
		if (state < REALIZED) {
			state = REALIZED;
		}
	}

	public synchronized void removePlayerListener(PlayerListener playerListener) 
	{
		if( vListeners == null || playerListener == null )
			return;
		vListeners.remove( playerListener );
	}

	public void setLoopCount(int count) {
		if (count == 0 || count < -1) {
			throw new IllegalArgumentException("Invalid loop count: " + count);
		}
		if( clip != null )
			clip.loop( count == -1 ? Clip.LOOP_CONTINUOUSLY : (count - 1) );
	}

	public long setMediaTime(long now) throws MediaException {
		if( clip != null )
			clip.setMicrosecondPosition( now );
		return 0;
	}

	public void start() throws MediaException {
		if( clip != null )
		{
			if (!lineListenerAdded) {
				clip.addLineListener( this );
				lineListenerAdded = true;
			}
			clip.start();
			state = STARTED;
		}
	}

	public void stop() throws MediaException {
		if( clip != null )
			clip.stop();
		if (state == STARTED) {
			state = PREFETCHED;
		}
	}

	private J2SEVolumeControl volumeControl = new J2SEVolumeControl();

	public Control getControl(String controlType) {
		if ("VolumeControl".equals(controlType) || "javax.microedition.media.control.VolumeControl".equals(controlType)) {
			return volumeControl;
		}
		return null;
	}

	public Control[] getControls() {
		return new Control[] { volumeControl };
	}

	public void update( LineEvent event ) 
	{
		if (event.getType().equals(LineEvent.Type.STOP))
		{
			state = PREFETCHED;
			if (clip != null) {
				clip.setMicrosecondPosition(0);
			}
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
