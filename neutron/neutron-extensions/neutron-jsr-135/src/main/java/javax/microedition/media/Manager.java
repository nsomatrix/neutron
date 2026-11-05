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
 *  
 *  Contributor(s):
 *    Travis Berthelot
 */

package javax.microedition.media;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.microedition.io.Connector;
import org.neutron.media.audio.PCTone;

public final class Manager
{

    public static final String TONE_DEVICE_LOCATOR = "device://tone";
    static Vector vMedia = new Vector();

    private static final String[] SUPPORTED_CONTENT_TYPES = new String[] {
        "audio/x-wav",
        "audio/wav",
        "audio/basic",
        "audio/midi",
        "audio/x-midi",
        "audio/mid",
        "audio/amr",
        "audio/amr-nb",
        "audio/amr-wb",
        "audio/3gpp"
    };
    
    public static String[] getSupportedContentTypes(String protocol)
    {
        return (String[]) SUPPORTED_CONTENT_TYPES.clone();
    }
    
    
    public static String[] getSupportedProtocols(String content_type)
    {
        // TODO
        return new String[0];
    }
    
    
    public static Player createPlayer(String locator)
            throws IOException, MediaException
    {
        if (locator == null) {
            throw new IllegalArgumentException("locator cannot be null");
        }
    	// should we simulate video capturing?
    	if (locator.startsWith("capture://video")) {
    		return new VideoCapturePlayer(locator);
    	}
        if (locator.startsWith("http:") || locator.startsWith("https:") || locator.startsWith("file:") || locator.startsWith("jar:")) {
            try {
                InputStream is = Connector.openInputStream(locator);
                String type = null;
                String lower = locator.toLowerCase();
                if (lower.endsWith(".amr") || lower.endsWith(".amr-nb")) {
                    type = "audio/amr";
                } else if (lower.endsWith(".mid") || lower.endsWith(".midi")) {
                    type = "audio/midi";
                } else if (lower.endsWith(".wav")) {
                    type = "audio/x-wav";
                }
                return createPlayer(is, type);
            } catch (Exception e) {
                // fall through
            }
        }
        return null;
    }
    
    
    public static Player createPlayer(InputStream stream, String type)
            throws IOException, MediaException
    {
        if (stream == null) {
            throw new IllegalArgumentException("stream cannot be null");
        }

        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }

        if (type == null) {
            stream.mark(16);
            byte[] header = new byte[8];
            int read = stream.read(header);
            stream.reset();
            if (read >= 4) {
                if (header[0] == 'M' && header[1] == 'T' && header[2] == 'h' && header[3] == 'd') {
                    type = "audio/midi";
                } else if (header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F') {
                    type = "audio/x-wav";
                } else if (header[0] == '.' && header[1] == 's' && header[2] == 'n' && header[3] == 'd') {
                    type = "audio/basic";
                } else if (read >= 5 && header[0] == '#' && header[1] == '!' && header[2] == 'A' && header[3] == 'M' && header[4] == 'R') {
                    type = "audio/amr";
                }
            }
        }

        if (type != null) {
            if (type.equalsIgnoreCase("audio/x-wav") || type.equalsIgnoreCase("audio/wav") ||
                type.equalsIgnoreCase("audio/basic") || type.equalsIgnoreCase("audio/mpeg")) {
                SampledAudioPlayer audPlayer = new SampledAudioPlayer();
                audPlayer.open(stream, type);
                vMedia.add(audPlayer);
                return audPlayer;
            } else if (type.equalsIgnoreCase("audio/midi") || type.equalsIgnoreCase("audio/x-midi") ||
                       type.equalsIgnoreCase("audio/mid")) {
                MidiAudioPlayer midiPlayer = new MidiAudioPlayer();
                midiPlayer.open(stream, type);
                vMedia.add(midiPlayer);
                return midiPlayer;
            } else if (type.equalsIgnoreCase("audio/amr") || type.equalsIgnoreCase("audio/amr-nb") ||
                       type.equalsIgnoreCase("audio/amr-wb") || type.equalsIgnoreCase("audio/3gpp")) {
                AmrAudioPlayer amrPlayer = new AmrAudioPlayer();
                amrPlayer.open(stream, type);
                vMedia.add(amrPlayer);
                return amrPlayer;
            }
        }

        return null;
    }
    
    
    public synchronized static void playTone(int note, int duration, int volume)
			throws MediaException 
	{
		if (duration <= 0) {
			throw new IllegalArgumentException("duration must be positive");
		}
		if (note < 0 || note > 127) {
			throw new IllegalArgumentException("note must be between 0 and 127");
		}
		if (volume < 0 || volume > 100) {
			throw new IllegalArgumentException("volume must be between 0 and 100");
		}

		double frequency = 440.0 * Math.pow(2.0, (note - 69.0) / 12.0);
		try {
			PCTone tone = new PCTone();
			tone.play(frequency, duration, volume);
		} catch (Throwable t) {
			throw new MediaException("Unable to play tone: " + t.getMessage());
		}
	}

    static void mediaDone(Object objMedia)
	{
    	//remove the media from our list of media to cleanup
        vMedia.remove(objMedia);
	}
    
    public static void cleanupMedia()
	{
		synchronized (vMedia) {
			Vector copy = new Vector(vMedia);
			vMedia.clear();
			for (int i = 0; i < copy.size(); i++) {
				Player p = (Player) copy.get(i);
				try {
					p.close();
				} catch (Throwable t) {
					// Ignore, proceed with cleaning up remaining media players
				}
			}
		}
	}

}



