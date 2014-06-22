package com.pixelus.dashclock.ext.deviceinfo;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.os.SystemClock;
import android.util.Log;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import static android.os.Build.VERSION.RELEASE;
import static android.text.format.Formatter.formatFileSize;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DeviceInfoExtension extends DashClockExtension {

  public static final String TAG = DeviceInfoExtension.class.getName();

  @Override
  protected void onUpdateData(int i) {

    String title = format("%s (v%s)", getFriendlyDeviceName(), RELEASE);
    Log.d(TAG, format("Title [%s]", title));

    StringBuilder body = new StringBuilder(80)
        .append(getFormattedUptime())
        .append(getFormattedCpuDetails())
        .append(getFormattedMemoryDetails());

    publishUpdate(new ExtensionData()
            .visible(true)
            .icon(R.drawable.ic_launcher)
            .status(getString(R.string.extension_title))
            .expandedTitle(title)
            .expandedBody(body.toString())
        //.clickIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))));
    );
  }

  private String getFormattedUptime() {

    String uptime = formatMillisToUptime(SystemClock.elapsedRealtime());
    Log.d(TAG, format("Uptime [%s]", uptime));

    return format("Uptime: %s\n", uptime);
  }

  private String getFormattedMemoryDetails() {

    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
    ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    activityManager.getMemoryInfo(mi);

    long totalMemoryBytes = mi.totalMem;
    long usedMemoryBytes = totalMemoryBytes - mi.availMem;
    int usedMemoryPercentage = Math.round(((float) usedMemoryBytes / totalMemoryBytes) * 100);

    Log.d(TAG, format("Memory [total: %d, used: %d, %% used: %d]", totalMemoryBytes, usedMemoryBytes,
        usedMemoryPercentage));

    return format("RAM: %d%% used (%s of %s)", usedMemoryPercentage,
        formatFileSize(this, usedMemoryBytes), formatFileSize(this, totalMemoryBytes));
  }

  private String getFriendlyDeviceName() {

    BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
    return myDevice.getName();
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

  public String formatMillisToUptime(long millis) {

    if (millis < 1000) {
      return "n/a";
    }

    long days = MILLISECONDS.toDays(millis);
    millis -= TimeUnit.DAYS.toMillis(days);
    long hours = MILLISECONDS.toHours(millis);
    millis -= TimeUnit.HOURS.toMillis(hours);
    long minutes = MILLISECONDS.toMinutes(millis);
    millis -= TimeUnit.MINUTES.toMillis(minutes);
    long seconds = MILLISECONDS.toSeconds(millis);

    StringBuilder sb = new StringBuilder(16)
        .append(days).append("d ")
        .append(hours).append("h ")
        .append(minutes).append("m ")
        .append(seconds).append("s");

    return sb.toString();
  }

  private String getFormattedCpuDetails() {

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

    return format("CPU: %d%% used (%d cores)\n", userCpuPercentage + systemCpuPercentage, cpuCoreCount);
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