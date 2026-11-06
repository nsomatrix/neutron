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

public class World extends Group
{

	private Camera camera;
	private Background background;

	public World() { }

	protected Object3D duplicateImpl()
	{
		World copy = (World) super.duplicateImpl();

		/*
		 * The camera and background fields were shallow-copied by clone().
		 * Per JSR-184, if the active camera is part of the duplicated set of
		 * nodes, the duplicate must reference the corresponding duplicated
		 * camera instead; any other references are left as they are.
		 */
		Node copyCamera = (this.camera != null) ? Node.matchingNode(this, this.camera, copy) : null;
		copy.setActiveCamera((copyCamera instanceof Camera) ? (Camera) copyCamera : this.camera);
		copy.setBackground(this.background);

		return copy;
	}

	public Camera getActiveCamera() { return camera; }

	public void setActiveCamera(Camera camera)
	{
		removeReference(this.camera);
		this.camera = camera;
		addReference(this.camera);
	}

	public Background getBackground() { return background; }

	public void setBackground(Background background)
	{
		removeReference(this.background);
		this.background = background;
		addReference(this.background);
	}

}
