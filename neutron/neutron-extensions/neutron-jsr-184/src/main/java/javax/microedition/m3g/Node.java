package javax.microedition.m3g;

public abstract class Node extends Transformable {

	public static final int NONE = 144;
	public static final int ORIGIN = 145;
	public static final int X_AXIS = 146;
	public static final int Y_AXIS = 147;
	public static final int Z_AXIS = 148;

	private Node parent = null;
	private boolean renderingEnabled = true;
	private boolean pickingEnabled = true;
	private int scope = -1;
	private float alphaFactor = 1.0f;

	protected Node() {}

	public Node getParent() {
		return parent;
	}

	void setParent(Node parent) {
		this.parent = parent;
	}

	public void setRenderingEnable(boolean enable) {
		this.renderingEnabled = enable;
	}

	public boolean isRenderingEnabled() {
		return renderingEnabled;
	}

	public void setPickingEnable(boolean enable) {
		this.pickingEnabled = enable;
	}

	public boolean isPickingEnabled() {
		return pickingEnabled;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public int getScope() {
		return scope;
	}

	public void setAlphaFactor(float alphaFactor) {
		if (alphaFactor < 0.0f || alphaFactor > 1.0f) throw new IllegalArgumentException();
		this.alphaFactor = alphaFactor;
	}

	public float getAlphaFactor() {
		return alphaFactor;
	}

	public void setAlignment(Node zRef, int zTarget, Node yRef, int yTarget) {
		// Alignment to target reference nodes
	}

	public boolean getTransformTo(Node target, Transform transform) {
		if (transform == null) throw new NullPointerException();

		// Accumulate world transforms from this to root
		Transform thisToRoot = new Transform();
		Node cur = this;
		while (cur != null) {
			Transform t = new Transform();
			cur.getCompositeTransform(t);
			t.postMultiply(thisToRoot);
			thisToRoot.set(t);
			cur = cur.getParent();
		}

		if (target == null) {
			transform.set(thisToRoot);
			return true;
		}

		Transform targetToRoot = new Transform();
		cur = target;
		while (cur != null) {
			Transform t = new Transform();
			cur.getCompositeTransform(t);
			t.postMultiply(targetToRoot);
			targetToRoot.set(t);
			cur = cur.getParent();
		}

		try {
			targetToRoot.invert();
			targetToRoot.postMultiply(thisToRoot);
			transform.set(targetToRoot);
			return true;
		} catch (ArithmeticException e) {
			return false;
		}
	}
}

