package com.pixelus.dashclock.ext.mydevice.helpers;

import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.pixelus.dashclock.ext.mydevice.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.pixelus.dashclock.ext.mydevice.DeviceInfoExtension.getContext;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;

public class CpuUsage {

  private static final String TAG = CpuUsage.class.getSimpleName();

  private int userCpuPercentage;
  private int systemCpuPercentage;
  private int cpuCoreCount;

  public CpuUsage() {
    computeCpuUsage();
  }

  public String getFormattedCpuUsage() {
    return getContext().getString(R.string.extension_expanded_body_cpu_line, userCpuPercentage + systemCpuPercentage,
        cpuCoreCount, (cpuCoreCount > 1 ? "cores" : "core"));
  }

  public String getFormattedShortCpuUsage() {
    return getContext().getString(R.string.extension_status_cpu, userCpuPercentage);
  }

  private void computeCpuUsage() {

    String topCpuStats = executeTop();
    Log.d(TAG, "TOP: " + topCpuStats);

    // Sanitize the CPU stats produced by top so that we can get the raw values.
    topCpuStats = topCpuStats.replaceAll("(,|User|System|IOW|IRQ|%)", "");
    while (topCpuStats.contains("  ")) {
      topCpuStats = topCpuStats.replaceAll("  ", " ");
    }
    topCpuStats = topCpuStats.trim();

    String[] cpuUsagePercentages = topCpuStats.split(" ");
    try {
      userCpuPercentage = Integer.parseInt(cpuUsagePercentages[0]);
      systemCpuPercentage = Integer.parseInt(cpuUsagePercentages[1]);
    } catch (NumberFormatException e) {
      Crashlytics.log(1, Crashlytics.TAG, "Error formatting top output: " + topCpuStats);
      Crashlytics.log(1, Crashlytics.TAG, "Unable to parse to integer: "
          + cpuUsagePercentages[0] + "|" + cpuUsagePercentages[1]);
    }
    cpuCoreCount = getCpuCoreCount();

    Log.d(TAG, format("CPU [user %%: %d, system %%: %d, # %s: %d]", userCpuPercentage, systemCpuPercentage,
        (cpuCoreCount > 1 ? "cores" : "core"), cpuCoreCount));
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
    String output = "";

    try {

      topProcess = getRuntime().exec("top -n 1");
      in = new BufferedReader(new InputStreamReader(topProcess.getInputStream()));
      String currentLine = in.readLine();
      while (output.contentEquals("") && currentLine != null) {
        if (currentLine.indexOf("User") >= 0) {
          output = currentLine;
        }
        currentLine = in.readLine();
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
