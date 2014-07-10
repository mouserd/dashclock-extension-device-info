package com.pixelus.dashclock.ext.mydevice.builder;

import android.os.SystemClock;
import android.util.Log;
import com.pixelus.dashclock.ext.mydevice.helpers.CpuUsage;
import com.pixelus.dashclock.ext.mydevice.helpers.DeviceName;
import com.pixelus.dashclock.ext.mydevice.helpers.MemoryUsage;
import com.pixelus.dashclock.ext.mydevice.helpers.Uptime;

public class MyDeviceMessageBuilder {

  private static final String TAG = MyDeviceMessageBuilder.class.getName();

  private Uptime uptime;
  private CpuUsage cpuUsage;
  private MemoryUsage memoryUsage;
  private DeviceName deviceName;

  public MyDeviceMessageBuilder withAlternateDeviceName(boolean useAlternateDeviceName, String alternateDeviceName) {
    String _alternateDeviceName = null;
    if (useAlternateDeviceName && alternateDeviceName != null) {
      _alternateDeviceName = alternateDeviceName;
    }

    deviceName = new DeviceName(_alternateDeviceName);

    return this;
  }

  public MyDeviceMessageBuilder withDeviceUptime(boolean withDeviceUptime) {

    if (withDeviceUptime) {
      uptime = new Uptime(SystemClock.elapsedRealtime());
    }

    return this;
  }

  public MyDeviceMessageBuilder withDeviceCpuUsage(boolean withDeviceCpuUsage) {

    if (withDeviceCpuUsage) {
      cpuUsage = new CpuUsage();
    }

    return this;
  }

  public MyDeviceMessageBuilder withDeviceMemoryUsage(boolean withDeviceMemoryUsage) {

    if (withDeviceMemoryUsage) {
      memoryUsage = new MemoryUsage();
    }

    return this;
  }

  public String buildExpandedTitleMessage() {
    return deviceName.getFormattedDeviceName();
  }

  public String buildExpandedBodyMessage() {

    String expandedBody = "";
    if (uptime != null) {
      expandedBody += uptime.getFormattedUptime();
    }

    if (cpuUsage != null) {

      expandedBody += addLineSeparator(expandedBody) + cpuUsage.getFormattedCpuUsage();
    }

    if (memoryUsage != null) {

      expandedBody += addLineSeparator(expandedBody) + memoryUsage.getFormattedMemoryUsage();
    }

    Log.d(TAG, "ExpandedBody [" + expandedBody + "]");
    return expandedBody;
  }

  public String buildStatusMessage() {

    String status = "";
    if (uptime != null) {
      status += uptime.getFormattedShortUptime() + "\n";
    }

    if (cpuUsage != null) {

      status += cpuUsage.getFormattedShortCpuUsage();
    }

    if (memoryUsage != null) {

      if (cpuUsage != null) {
        status += "  ";
      }
      status += memoryUsage.getFormattedShortMemoryUsage();
    }

    Log.d(TAG, "Status [" + status + "]");
    return status;
  }

  private String addLineSeparator(String string) {
    if (string.isEmpty()) {
      return "";
    }

    return "\n";
  }
}
