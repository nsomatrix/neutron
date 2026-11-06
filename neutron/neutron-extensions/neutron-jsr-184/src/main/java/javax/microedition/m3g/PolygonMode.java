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

public class PolygonMode extends Object3D
{

	public static final int CULL_BACK = 160;
	public static final int CULL_FRONT = 161;
	public static final int CULL_NONE = 162;
	public static final int SHADE_FLAT = 164;
	public static final int SHADE_SMOOTH = 165;
	public static final int WINDING_CCW = 168;
	public static final int WINDING_CW = 169;

	private int culling;
	private int shading;
	private int winding;
	private boolean twoSidedLighting;
	private boolean localCameraLighting;
	private boolean perspectiveCorrection;

	public PolygonMode()
	{
		this.culling = CULL_BACK;
		this.winding = WINDING_CCW;
		this.shading = SHADE_SMOOTH;
		this.twoSidedLighting = false;
		this.localCameraLighting = false;
		this.perspectiveCorrection = false;
	}

	protected Object3D duplicateImpl()
	{
		PolygonMode copy = (PolygonMode) super.duplicateImpl();
		copy.culling = this.culling;
		copy.shading = this.shading;
		copy.winding = this.winding;
		copy.twoSidedLighting = this.twoSidedLighting;
		copy.localCameraLighting = this.localCameraLighting;
		copy.perspectiveCorrection = this.perspectiveCorrection;
		return copy;
	}

	public int getCulling() { return this.culling; }

	public int getShading() { return this.shading; }

	public int getWinding() { return this.winding; }

	public boolean isLocalCameraLightingEnabled() { return this.localCameraLighting; }

	public boolean isPerspectiveCorrectionEnabled() { return this.perspectiveCorrection; }

	public boolean isTwoSidedLightingEnabled() { return this.twoSidedLighting; }

	public void setCulling(int mode)
	{
		if (mode != CULL_BACK &&
			mode != CULL_FRONT &&
			mode != CULL_NONE)
			throw new java.lang.IllegalArgumentException("Invalid cull mode:" + mode);

		this.culling = mode;
	}

	public void setLocalCameraLightingEnable(boolean enable) { this.localCameraLighting = enable; }

	public void setPerspectiveCorrectionEnable(boolean enable) { this.perspectiveCorrection = enable; }

	public void setShading(int mode)
	{
		if (mode != SHADE_FLAT &&
			mode != SHADE_SMOOTH)
			throw new java.lang.IllegalArgumentException("Invalid shading:" + mode);

		this.shading = mode;
	}

	public void setTwoSidedLightingEnable(boolean enable) { this.twoSidedLighting = enable; }

	public void setWinding(int mode)
	{
		if (mode != WINDING_CCW &&
			mode != WINDING_CW)
			throw new java.lang.IllegalArgumentException("Invalid winding:" + mode);

		this.winding = mode;
	}
}
