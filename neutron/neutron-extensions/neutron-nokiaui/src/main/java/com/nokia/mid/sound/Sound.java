/*
 *  Nokia API for Neutron
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
 
package com.nokia.mid.sound;


// Nokia UI Sound API implementation

public class Sound 
{
	public static final int SOUND_PLAYING = 0;
	public static final int SOUND_STOPPED = 1;
	public static final int SOUND_UNINITIALIZED = 3;
	public static final int FORMAT_TONE = 1;
	public static final int FORMAT_WAV = 5;
	
	private int gain = 255;
	private int state = SOUND_STOPPED;
	private SoundListener listener;
	
    public Sound(byte data[], int type) 
    {
        if (data == null) {
            state = SOUND_UNINITIALIZED;
        }
    }
    
    public Sound(int freq, long duration) 
    {
    }
    
    public static int getConcurrentSoundCount(int type) 
    {
        return 1;
    }
    
    public int getGain() 
    {
        return gain;
    }
    
    public void setGain(int gain) 
    {
        if (gain < 0) gain = 0;
        if (gain > 255) gain = 255;
        this.gain = gain;
    }
    
    public int getState() 
    {
        return state;
    }
    
    public static int[] getSupportedFormats() 
    {
        return new int[] { FORMAT_TONE, FORMAT_WAV };
    }
    
    public void init(byte data[], int type) 
    {    
        state = SOUND_STOPPED;
    }
    
    public void init(int freq, long duration) 
    {
        state = SOUND_STOPPED;
    }
    
    public synchronized void play(int loop) 
    {        
        state = SOUND_PLAYING;
    }
    
    public void stop() 
    {
        state = SOUND_STOPPED;
    }
     
    public void release() 
    {
        state = SOUND_UNINITIALIZED;
    }
    
    public void resume() 
    {
        state = SOUND_PLAYING;
    }
    
    public void setSoundListener(SoundListener listener) 
    {
        this.listener = listener;
    }
    
}