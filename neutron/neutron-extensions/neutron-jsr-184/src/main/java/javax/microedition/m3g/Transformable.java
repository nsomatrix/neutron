/*
 * Neutron 
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
 */
package javax.microedition.m3g;


public abstract class Transformable extends Object3D
{
	final float[] scratch = new float[16];

	private Transform matrix = new Transform();
	private Transform scale = new Transform();
	private Transform rotate = new Transform();
	private Transform translate = new Transform();

	public void getCompositeTransform(Transform transform)
	{
		if (transform == null) { throw new NullPointerException("Cannot copy composite transform data into a null transform."); }

		// Composite Transform is given as: C=T*R*S*M
		transform.setIdentity();
		transform.postMultiply(this.translate);
		transform.postMultiply(this.rotate);
		transform.postMultiply(this.scale);
		transform.postMultiply(this.matrix);
	}

	protected Object3D duplicateImpl()
	{
		Transformable copy = (Transformable) super.duplicateImpl();
		copy.matrix = new Transform(matrix);
		copy.scale = new Transform(scale);
		copy.rotate = new Transform(rotate);
		copy.translate = new Transform(translate);

		return copy;
	}

	public void getOrientation(float[] angleAxis)
	{
		if (angleAxis == null) { throw new NullPointerException("Cannot copy orientation data into a null array."); }
		if (angleAxis.length < 4) { throw new IllegalArgumentException("Illegal length of angle axis array"); }

		final float ax, ay, az;
		this.rotate.get(this.scratch);

		final float angle = M3GMath.acos(((this.scratch[0] + this.scratch[5] + this.scratch[10]) - 1.0f) * 0.5f);
		final float sinAngle = (2 * M3GMath.sin(angle));

		ax = (angle == 0.0f || sinAngle == 0.0f) ? 0.0f : (this.scratch[9] - this.scratch[6]) / sinAngle;
		ay = (angle == 0.0f || sinAngle == 0.0f) ? 0.0f: (this.scratch[2] - this.scratch[8]) / sinAngle;
		az = (angle == 0.0f || sinAngle == 0.0f) ? 0.0f : (this.scratch[4] - this.scratch[1]) / sinAngle;

		angleAxis[0] = M3GMath.toDegrees(angle); // Angle has to be in degrees here
		angleAxis[1] = ax;
		angleAxis[2] = ay;
		angleAxis[3] = az;
	}

	public void getScale(float[] xyz)
	{
		if (xyz == null) { throw new NullPointerException("Cannot copy scale data into a null array."); }
		if (xyz.length < 3) { throw new IllegalArgumentException("Illegal size of scale array"); }

		float[] m = new float[16];
		this.scale.get(m);
		xyz[0] = m[4*0 + 0];
		xyz[1] = m[4*1 + 1];
		xyz[2] = m[4*2 + 2];
	}

	public void getTransform(Transform transform)
	{
		if (transform == null) { throw new NullPointerException("Cannot copy transform data into a null transform."); }

		transform.set(this.matrix);
	}

	public void getMatrix(float[] matrix)
	{
		if (matrix == null) { throw new NullPointerException("Cannot copy matrix data into a null matrix."); }

		this.matrix.get(matrix);
	}

	public void getTranslation(float[] xyz)
	{
		if (xyz == null) { throw new NullPointerException("Cannot copy translation data into a null array."); }
		if (xyz.length < 3) { throw new IllegalArgumentException("Illegal size of translation array"); }

		float[] m = new float[16];
		this.translate.get(m);
		xyz[0] = m[4*0 + 3];
		xyz[1] = m[4*1 + 3];
		xyz[2] = m[4*2 + 3];
	}

	public void postRotate(float angle, float ax, float ay, float az)
	{
		this.rotate.postRotate(angle, ax, ay, az);
		invalidateTransformable();
	}

	public void preRotate(float angle, float ax, float ay, float az)
	{
		this.rotate.preRotate(angle, ax, ay, az);
		invalidateTransformable();
	}

	public void scale(float sx, float sy, float sz)
	{
		this.scale.postScale(sx, sy, sz);
		invalidateTransformable();
	}

	public void setOrientation(float angle, float ax, float ay, float az)
	{
		this.rotate.setIdentity();
		this.rotate.preRotate(angle, ax, ay, az);
		invalidateTransformable();
	}

	// Internal helper: sets the R component directly from a quaternion,
	// avoiding a lossy quaternion -> angle-axis -> matrix round trip.
	// Used by Node alignment, where the alignment rotation is a quaternion.
	void setOrientationQuat(float qx, float qy, float qz, float qw)
	{
		this.rotate.setIdentity();
		this.rotate.preRotateQuat(qx, qy, qz, qw);
		invalidateTransformable();
	}

	public void setScale(float sx, float sy, float sz)
	{
		this.scale.setIdentity();
		this.scale.postScale(sx, sy, sz);
		invalidateTransformable();
	}

	public void setTransform(Transform transform)
	{
		invalidateTransformable();
		if (transform == null)
		{
			this.matrix.setIdentity();
			return;
		}

		if (this instanceof Node)
		{
			transform.get(this.scratch);

			float m12 = this.scratch[12];
			float m13 = this.scratch[13];
			float m14 = this.scratch[14];
			float m15 = this.scratch[15];

			// Check if the bottom row is invalid (with a small tolerance for float imprecision)
			// This SHOULD be an exception, but EA released a few NFS titles that hit this, and work in Nokia devices.
			if (Math.abs(m12) > M3GMath.EPSILON || Math.abs(m13) > M3GMath.EPSILON ||
				Math.abs(m14) > M3GMath.EPSILON || Math.abs(m15 - 1.0f) > M3GMath.EPSILON)
			{
				//throw new IllegalArgumentException("The bottom row of the transform must be (0, 0, 0, 1) for Node objects.");
				this.matrix.setIdentity();
			}
		}

		this.matrix.set(transform);
	}

	public void setTranslation(float tx, float ty, float tz)
	{
		this.translate.setIdentity();
		this.translate.postTranslate(tx, ty, tz);
		invalidateTransformable();
	}

	public void translate(float tx, float ty, float tz)
	{
		this.translate.postTranslate(tx, ty, tz);
		invalidateTransformable();
	}

	@Override
	void updateProperty(int property, float[] value)
	{
		M3GLog.log(M3GLog.LOG_DEBUG, Graphics3D.class.getPackage().getName() + "." + Graphics3D.class.getSimpleName() + ": " + "AnimTrack updating Transformable property");
		switch (property)
		{
			case AnimationTrack.ORIENTATION:
				/*
				 * ORIENTATION track values are a quaternion (x, y, z, w). Going
				 * through (angle, axis) and back into a matrix is both slower and
				 * lossy: the fast atan2 leaves the recovered angle off by up to
				 * 1.17 degrees (0.55 on average, measured over all 444 samples of
				 * the 12 SLERP tracks of Ops Sniper 3D's role.m3g at the frame
				 * times the game animates at), and every bone hands that error
				 * down to its whole subtree. Building R straight from the
				 * quaternion drops it to 8e-4 degrees at worst.
				 */
				if (value[0] == 0.0f && value[1] == 0.0f && value[2] == 0.0f && value[3] == 0.0f)
					{ setOrientation(0.0f, 0.0f, 1.0f, 0.0f); }
				else
					{ setOrientationQuat(value[0], value[1], value[2], value[3]); }
				break;
			case AnimationTrack.TRANSLATION:
				setTranslation(value[0], value[1], value[2]);
				break;
			case AnimationTrack.SCALE:
				if(value.length == 1) { setScale(value[0], value[0], value[0]); }
				else { setScale(value[0], value[1], value[2]); }
				break;
			default:
				super.updateProperty(property, value);
		}
	}

	void invalidateTransformable()
	{
		if (!(this instanceof Texture2D))
		{
			Node node = (Node) this;
			if (node.parent != null && (node.hasRenderables || node.hasBones))
			{
				node.parent.invalidateNode(new boolean[]{false, false});
			}
		}
	}

	boolean animTrackCompatible(AnimationTrack track)
	{
		switch (track.getTargetProperty())
		{
			case AnimationTrack.ORIENTATION:
			case AnimationTrack.SCALE:
			case AnimationTrack.TRANSLATION:
				return true;
			default:
				return super.animTrackCompatible(track);
		}
	}
}
