package javax.microedition.m3g;

import java.util.Vector;

public class Group extends Node {

	final Vector children = new Vector();

	public Group() {}

	public void addChild(Node child) {
		if (child == null) throw new NullPointerException();
		if (child == this || child.getParent() != null) throw new IllegalArgumentException();
		children.addElement(child);
		child.setParent(this);
	}

	public void removeChild(Node child) {
		if (children.removeElement(child)) {
			child.setParent(null);
		}
	}

	public int getChildCount() {
		return children.size();
	}

	public Node getChild(int index) {
		return (Node) children.elementAt(index);
	}

	public boolean pick(int scope, float ox, float oy, float oz, float dx, float dy, float dz, RayIntersection ri) {
		return false;
	}

	public boolean pick(int scope, float x, float y, Camera camera, RayIntersection ri) {
		return false;
	}
}

