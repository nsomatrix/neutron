/*
 *  Neutron JSR-234 AMMS
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package javax.microedition.amms;

import javax.microedition.media.Controllable;
import javax.microedition.media.Player;

public interface Module extends Controllable {
	void addMIDIChannel(Player player, int channel);
	void addPlayer(Player player);
	void removeMIDIChannel(Player player, int channel);
	void removePlayer(Player player);
}

