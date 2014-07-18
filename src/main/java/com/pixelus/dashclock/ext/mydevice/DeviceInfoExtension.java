package com.pixelus.dashclock.ext.mydevice;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.crashlytics.android.Crashlytics;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.pixelus.dashclock.ext.mydevice.builder.DeviceInfoMessageBuilder;

public class DeviceInfoExtension extends DashClockExtension {

  public static final String TAG = DeviceInfoExtension.class.getName();

  // TODO These should be declared in strings.xml
  public static final String ALTERNATE_DEVICE_NAME = "alternate_device_name";
  public static final String USE_ALTERNATE_DEVICE_NAME = "use_alternate_device_name";
  public static final String SHOW_DEVICE_UPTIME = "show_device_uptime";
  public static final String SHOW_DEVICE_CPU_USAGE = "show_device_cpu_usage";
  public static final String SHOW_DEVICE_MEMORY_USAGE = "show_device_memory_usage";

  private static Context context;
  private boolean crashlyticsStarted = false;

  @Override public void onCreate() {
    super.onCreate();
    DeviceInfoExtension.context = getApplicationContext();
  }

  public static Context getContext() {
    return context;
  }

  @Override
  protected void onUpdateData(int i) {

    if (!crashlyticsStarted) {
      Crashlytics.start(this);
      crashlyticsStarted = true;
    }

    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

    final boolean showDeviceUptime = sp.getBoolean(SHOW_DEVICE_UPTIME, true);
    final boolean showDeviceCpuUsage = sp.getBoolean(SHOW_DEVICE_CPU_USAGE, true);
    final boolean showDeviceMemoryUsage = sp.getBoolean(SHOW_DEVICE_MEMORY_USAGE, true);
    final boolean useAlternateDeviceName = sp.getBoolean(USE_ALTERNATE_DEVICE_NAME, false);
    final String alternateDeviceName = sp.getString(ALTERNATE_DEVICE_NAME, "");

    final DeviceInfoMessageBuilder deviceInfoMessageBuilder = new DeviceInfoMessageBuilder()
        .withAlternateDeviceName(useAlternateDeviceName, alternateDeviceName)
        .withDeviceUptime(showDeviceUptime)
        .withDeviceCpuUsage(showDeviceCpuUsage)
        .withDeviceMemoryUsage(showDeviceMemoryUsage);

    publishUpdate(new ExtensionData()
            .visible(true)
            .icon(R.drawable.ic_launcher)
            .status(deviceInfoMessageBuilder.buildStatusMessage())
            .expandedTitle(deviceInfoMessageBuilder.buildExpandedTitleMessage())
            .expandedBody(deviceInfoMessageBuilder.buildExpandedBodyMessage())
        //.clickIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))));
    );
  }

}