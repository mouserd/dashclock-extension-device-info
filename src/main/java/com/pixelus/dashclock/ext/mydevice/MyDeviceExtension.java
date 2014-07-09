package com.pixelus.dashclock.ext.mydevice;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.os.Build.VERSION.RELEASE;
import static android.text.format.Formatter.formatFileSize;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;

public class MyDeviceExtension extends DashClockExtension {

  public static final String TAG = MyDeviceExtension.class.getName();

  // TODO These should be declared in strings.xml
  public static final String ALTERNATE_DEVICE_NAME = "alternate_device_name";
  public static final String USE_SYSTEM_DEVICE_NAME = "use_system_device_name";

  @Override
  protected void onUpdateData(int i) {

    String expandedTitle = getString(R.string.extension_title, getFriendlyDeviceName(), RELEASE);
    Log.d(TAG, format("Title [%s]", expandedTitle));

    Pair<String, String> cpuDetails = getFormattedCpuDetails();
    Pair<String, String> memoryDetails = getFormattedMemoryDetails();
    Pair<String, String> uptimeDetails = getFormattedUptime();

    String status = uptimeDetails.second + memoryDetails.second + " " + cpuDetails.second;
    String expandedBody = uptimeDetails.first + cpuDetails.first + memoryDetails.first;

    publishUpdate(new ExtensionData()
            .visible(true)
            .icon(R.drawable.ic_launcher)
            .status(status)
            .expandedTitle(expandedTitle)
            .expandedBody(expandedBody)
        //.clickIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))));
    );
  }

  private Pair<String, String> getFormattedUptime() {

    Uptime uptime = new Uptime(SystemClock.elapsedRealtime());
    if (!uptime.isValid()) {

      return new Pair<String, String>(
          getString(R.string.extension_expanded_body_uptime_unavailable),
          getString(R.string.extension_expanded_body_uptime_unavailable));
    }

    String uptimeFormatted = getString(R.string.extension_expanded_body_uptime_format, uptime.getDays(),
        uptime.getHours(), uptime.getMinutes(), uptime.getSeconds());
    String uptimeFormattedShort = getString(R.string.extension_expanded_body_uptime_shortformat, uptime.getDays(),
        uptime.getHours(), uptime.getMinutes());

    Log.d(TAG, format("Uptime [%s]", uptime));

    return new Pair<String, String>(
        getString(R.string.extension_expanded_body_uptime_line, uptimeFormatted),
        getString(R.string.extension_status_uptime, uptimeFormattedShort));
  }

  private Pair<String, String> getFormattedMemoryDetails() {

    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
    ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    activityManager.getMemoryInfo(mi);

    long totalMemoryBytes = mi.totalMem;
    long usedMemoryBytes = totalMemoryBytes - mi.availMem;
    int usedMemoryPercentage = Math.round(((float) usedMemoryBytes / totalMemoryBytes) * 100);

    Log.d(TAG, format("Memory [total: %d, used: %d, %% used: %d]", totalMemoryBytes, usedMemoryBytes,
        usedMemoryPercentage));

    return new Pair<String, String>(
        getString(R.string.extension_expanded_body_ram_line, usedMemoryPercentage,
            formatFileSize(this, usedMemoryBytes), formatFileSize(this, totalMemoryBytes)),
        getString(R.string.extension_status_ram, usedMemoryPercentage));
  }

  private String getFriendlyDeviceName() {

    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    final String alternateDeviceName = sp.getString(ALTERNATE_DEVICE_NAME, null);
    final Boolean useSystemDeviceName = sp.getBoolean(USE_SYSTEM_DEVICE_NAME, true);

    if (!useSystemDeviceName) {
      Log.d(TAG, "CUSTOM DEVICE NAME: " + alternateDeviceName);
      return  alternateDeviceName;
    }


    BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
    if (myDevice != null) {
      return myDevice.getName();
    }

    return Build.MODEL;
  }

  private Pair<String, String> getFormattedCpuDetails() {

    String topCpuStats = executeTop();
    Log.d(TAG, "TOP: " + topCpuStats);

    // Sanitize the CPU stats produced by top so that we can get the raw values.
    topCpuStats = topCpuStats.replaceAll("(,|User|System|IOW|IRQ|%)", "");
    while (topCpuStats.contains("  ")) {
      topCpuStats = topCpuStats.replaceAll("  ", " ");
    }
    topCpuStats = topCpuStats.trim();

    String[] cpuUsagePercentages = topCpuStats.split(" ");
    int userCpuPercentage = Integer.parseInt(cpuUsagePercentages[0]);
    int systemCpuPercentage = Integer.parseInt(cpuUsagePercentages[1]);
    int cpuCoreCount = getCpuCoreCount();

    Log.d(TAG, format("CPU [user %%: %d, system %%: %d, # %s: %d]", userCpuPercentage, systemCpuPercentage,
        (cpuCoreCount > 1 ? "cores" : "core"), cpuCoreCount));

    return new Pair<String, String>(
        getString(R.string.extension_expanded_body_cpu_line, userCpuPercentage + systemCpuPercentage,
            cpuCoreCount, (cpuCoreCount > 1 ? "cores" : "core")),
        getString(R.string.extension_status_cpu, userCpuPercentage));
  }

  private int getCpuCoreCount() {

    try {

      File dir = new File("/sys/devices/system/cpu/");  // Directory containing CPU info
      return dir.listFiles(new CpuFilter()).length;     // Filter the files for CPU types, the result being
      // the number of cores (virtual CPU devices)
    } catch (Exception e) {

      return 1;
    }
  }

  private String executeTop() {

    Process topProcess = null;
    BufferedReader in = null;
    String output = null;

    try {

      topProcess = getRuntime().exec("top -n 1");
      in = new BufferedReader(new InputStreamReader(topProcess.getInputStream()));
      while (output == null || output.contentEquals("")) {
        output = in.readLine();
      }

    } catch (IOException e) {

      Log.e(TAG, "Error getting first line of top");
      e.printStackTrace();
    } finally {

      try {

        if (in != null) {
          in.close();
        }
        if (topProcess != null) {
          topProcess.destroy();
        }
      } catch (IOException e) {

        Log.e(TAG, "Error closing and destroying top topProcess");
        e.printStackTrace();
      }
    }

    return output;
  }
}