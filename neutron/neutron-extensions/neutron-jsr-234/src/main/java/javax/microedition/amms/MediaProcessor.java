/*
 *  Neutron JSR-234 AMMS
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package javax.microedition.amms;

import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.media.Controllable;
import javax.microedition.media.MediaException;

public interface MediaProcessor extends Controllable {
	int UNREALIZED = 100;
	int REALIZED = 200;
	int PROCESSING = 300;
	int STOPPED = 400;

	void complete() throws MediaException;
	int getProgress();
	int getState();
	void setInput(InputStream input, int length) throws MediaException;
	void setOutput(OutputStream output);
	void start() throws MediaException;
	void stop() throws MediaException;
}

