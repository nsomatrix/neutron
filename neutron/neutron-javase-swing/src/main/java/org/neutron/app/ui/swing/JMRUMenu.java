/**
 *  Neutron
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
 *  @version $Id$
 */
package org.neutron.app.ui.swing;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.neutron.app.Config;
import org.neutron.app.util.MRUListListener;
import org.neutron.app.util.MidletURLReference;

/**
 * @author vlads
 * 
 */
public class JMRUMenu extends JMenu implements MRUListListener {

	private static final long serialVersionUID = 1L;

	public static class MRUActionEvent extends ActionEvent {

		private static final long serialVersionUID = 1L;

		Object sourceMRU;

		public MRUActionEvent(Object sourceMRU, ActionEvent orig) {
			super(orig.getSource(), orig.getID(), orig.getActionCommand(), orig.getWhen(), orig.getModifiers());
			this.sourceMRU = sourceMRU;
		}

		public Object getSourceMRU() {
			return sourceMRU;
		}

	}

	public JMRUMenu(String s) {
		super(s);
	}

	private class MRUAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		private final MidletURLReference ref;

		public MRUAction(String name, MidletURLReference ref) {
			super(name);
			this.ref = ref;
		}

		public String getUrl() {
			return ref.getUrl();
		}

		public void actionPerformed(ActionEvent e) {
			JMRUMenu.this.fireActionPerformed(new MRUActionEvent(ref, e));
		}
	}

	public void listItemChanged(final Object item) {
		if (!(item instanceof MidletURLReference)) {
			return;
		}
		final MidletURLReference ref = (MidletURLReference) item;
		String cleanName = ref.getCleanName();

		for (int i = 0; i < getItemCount(); i++) {
			JMenuItem mItem = getItem(i);
			if (mItem != null && mItem.getAction() instanceof MRUAction) {
				MRUAction mruAct = (MRUAction) mItem.getAction();
				if (mruAct.getUrl().equals(ref.getUrl())) {
					remove(i);
					break;
				}
			}
		}

		MRUAction a = new MRUAction(cleanName, ref);
		JMenuItem menu = new JMenuItem(a);

		try {
			File iconFile = new File(new File(Config.getConfigPath(), "icons"), String.valueOf(ref.getUrl().hashCode()) + ".png");
			if (iconFile.exists()) {
				ImageIcon icon = new ImageIcon(iconFile.getAbsolutePath());
				Image img = icon.getImage();
				Image scaledImg = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
				menu.setIcon(new ImageIcon(scaledImg));
			} else {
				menu.setIcon(getDefaultIcon());
			}
		} catch (Exception ex) {
			menu.setIcon(getDefaultIcon());
		}

		this.insert(menu, 0);
	}

	private ImageIcon getDefaultIcon() {
		java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_ARGB);
		java.awt.Graphics2D g = img.createGraphics();
		g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new java.awt.Color(120, 120, 120));
		g.fillRoundRect(1, 4, 14, 8, 4, 4);
		g.setColor(java.awt.Color.WHITE);
		g.fillRect(3, 7, 3, 2);
		g.fillRect(4, 6, 1, 4);
		g.setColor(new java.awt.Color(200, 50, 50));
		g.fillOval(10, 7, 2, 2);
		g.fillOval(12, 7, 2, 2);
		g.dispose();
		return new ImageIcon(img);
	}

	/**
	 * Do not create new Event
	 */
	protected void fireActionPerformed(ActionEvent event) {
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ActionListener.class) {
				((ActionListener) listeners[i + 1]).actionPerformed(event);
			}
		}
	}

}
