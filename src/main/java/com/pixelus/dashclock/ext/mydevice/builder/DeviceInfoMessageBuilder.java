package com.pixelus.dashclock.ext.mydevice.builder;

import android.os.SystemClock;
import android.util.Log;
import com.pixelus.dashclock.ext.mydevice.helpers.CpuUsage;
import com.pixelus.dashclock.ext.mydevice.helpers.DeviceName;
import com.pixelus.dashclock.ext.mydevice.helpers.MemoryUsage;
import com.pixelus.dashclock.ext.mydevice.helpers.Uptime;

public class DeviceInfoMessageBuilder {

  private static final String TAG = DeviceInfoMessageBuilder.class.getSimpleName();

  private Uptime uptime;
  private CpuUsage cpuUsage;
  private MemoryUsage memoryUsage;
  private DeviceName deviceName;
  private boolean showDeviceName;
  private boolean showFriendlyVersionName;

  public DeviceInfoMessageBuilder withDeviceName(boolean showDeviceName) {
    this.showDeviceName = showDeviceName;
    return this;
  }

  public DeviceInfoMessageBuilder withAlternateDeviceName(boolean useAlternateDeviceName, String alternateDeviceName) {
    String _alternateDeviceName = null;
    if (useAlternateDeviceName && alternateDeviceName != null) {
      _alternateDeviceName = alternateDeviceName;
    }

    deviceName = new DeviceName(_alternateDeviceName);

    return this;
  }

  public DeviceInfoMessageBuilder withDeviceUptime(boolean withDeviceUptime) {

    if (withDeviceUptime) {
      uptime = new Uptime(SystemClock.elapsedRealtime());
    }

    return this;
  }

  public DeviceInfoMessageBuilder withDeviceCpuUsage(boolean withDeviceCpuUsage) {

    if (withDeviceCpuUsage) {
      cpuUsage = new CpuUsage();
    }

    return this;
  }

  public DeviceInfoMessageBuilder withDeviceMemoryUsage(boolean withDeviceMemoryUsage) {

    if (withDeviceMemoryUsage) {
      memoryUsage = new MemoryUsage();
    }

    return this;
  }

  public DeviceInfoMessageBuilder withFriendlyVersionName(boolean showFriendlyVersionName) {
    this.showFriendlyVersionName = showFriendlyVersionName;
    return this;
  }

  public String buildExpandedTitleMessage() {
    return deviceName.getFormattedDeviceName(showFriendlyVersionName);
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
