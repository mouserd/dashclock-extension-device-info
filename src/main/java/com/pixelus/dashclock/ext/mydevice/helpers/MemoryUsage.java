package com.pixelus.dashclock.ext.mydevice.helpers;

import android.app.ActivityManager;
import android.util.Log;
import com.pixelus.dashclock.ext.mydevice.R;

import static android.text.format.Formatter.formatFileSize;
import static com.pixelus.dashclock.ext.mydevice.DeviceInfoExtension.getContext;
import static java.lang.String.format;

public class MemoryUsage {

  private static final String TAG = MemoryUsage.class.getName();

  private long totalMemoryBytes;
  private long usedMemoryBytes;
  private int usedMemoryPercentage;

  public MemoryUsage() {
    computeMemoryDetails();
  }

  private void computeMemoryDetails() {

    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
    ActivityManager activityManager = (ActivityManager) getContext().getSystemService(getContext().ACTIVITY_SERVICE);
    activityManager.getMemoryInfo(mi);

    totalMemoryBytes = mi.totalMem;
    usedMemoryBytes = totalMemoryBytes - mi.availMem;
    usedMemoryPercentage = Math.round(((float) usedMemoryBytes / totalMemoryBytes) * 100);

    Log.d(TAG, format("Memory [total: %d, used: %d, %% used: %d]", totalMemoryBytes, usedMemoryBytes,
        usedMemoryPercentage));
  }

  public String getFormattedMemoryUsage() {
    return getContext().getString(R.string.extension_expanded_body_ram_line, usedMemoryPercentage,
        formatFileSize(getContext(), usedMemoryBytes), formatFileSize(getContext(), totalMemoryBytes));
  }

  public String getFormattedShortMemoryUsage() {
    return getContext().getString(R.string.extension_status_ram, usedMemoryPercentage);
  }
}
