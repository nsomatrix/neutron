package org.neutron.android.util;

import android.content.Intent;

public interface ActivityResultListener {

	boolean onActivityResult(int requestCode, int resultCode, Intent data);
	
}
