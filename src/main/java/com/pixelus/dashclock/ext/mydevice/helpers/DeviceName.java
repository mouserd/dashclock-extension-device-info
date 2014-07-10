package com.pixelus.dashclock.ext.mydevice.helpers;


import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.util.Log;
import com.pixelus.dashclock.ext.mydevice.R;

import static android.os.Build.VERSION.RELEASE;
import static com.pixelus.dashclock.ext.mydevice.MyDeviceExtension.getContext;

public class DeviceName {

  private static final String TAG = DeviceName.class.getName();

  private String alternateDeviceName;

  public DeviceName(String alternateDeviceName) {

    this.alternateDeviceName = alternateDeviceName;
  }

  private String getDeviceName() {

    BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
    if (myDevice != null) {
      Log.d(TAG, "Device name (from bluetooth): " + myDevice.getName());
      return myDevice.getName();
    }

    return Build.MODEL;
  }

  public String getFormattedDeviceName() {

    if (alternateDeviceName != null) {
      Log.d(TAG, "Using alternate device name: " + alternateDeviceName);
      return getContext().getString(R.string.extension_title, alternateDeviceName, RELEASE);
    }

    return getContext().getString(R.string.extension_title, getDeviceName(), RELEASE);
  }
}
