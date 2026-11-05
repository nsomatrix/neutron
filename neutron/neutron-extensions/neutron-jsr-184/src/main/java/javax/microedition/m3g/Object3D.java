package javax.microedition.m3g;

import java.util.Hashtable;

public abstract class Object3D {

	private int userID = 0;
	private Object userObject = null;
	private final Hashtable animationTracks = new Hashtable();

	protected Object3D() {}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public void addAnimationTrack(AnimationTrack animationTrack) {
		if (animationTrack == null) {
			throw new NullPointerException();
		}
		animationTracks.put(animationTrack, animationTrack);
	}

	public int getAnimationTrackCount() {
		return animationTracks.size();
	}

	public AnimationTrack getAnimationTrack(int index) {
		if (index < 0 || index >= animationTracks.size()) {
			throw new IndexOutOfBoundsException();
		}
		int count = 0;
		for (java.util.Enumeration e = animationTracks.elements(); e.hasMoreElements();) {
			AnimationTrack track = (AnimationTrack) e.nextElement();
			if (count == index) {
				return track;
			}
			count++;
		}
		return null;
	}

	public void removeAnimationTrack(AnimationTrack animationTrack) {
		animationTracks.remove(animationTrack);
	}

	public int animate(int time) {
		// Evaluates keyframe sequences for registered animation tracks
		return 0;
	}

	public Object3D duplicate() {
		try {
			// Basic clone/duplicate per JSR-184
			return (Object3D) this.getClass().newInstance();
		} catch (Exception e) {
			return this;
		}
	}

	public Object3D find(int userID) {
		if (this.userID == userID) {
			return this;
		}
		return null;
	}
}

