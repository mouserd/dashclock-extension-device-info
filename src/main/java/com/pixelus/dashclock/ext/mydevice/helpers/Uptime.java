package com.pixelus.dashclock.ext.mydevice.helpers;

import com.pixelus.dashclock.ext.mydevice.R;

import static com.pixelus.dashclock.ext.mydevice.DeviceInfoExtension.getContext;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class Uptime {

  private long days = 0;
  private long hours = 0;
  private long minutes = 0;
  private long seconds = 0;
  private long uptimeMillis;

  public Uptime(long uptimeMillis) {
    this.uptimeMillis = uptimeMillis;

    days = MILLISECONDS.toDays(uptimeMillis);
    uptimeMillis -= DAYS.toMillis(days);

    hours = MILLISECONDS.toHours(uptimeMillis);
    uptimeMillis -= HOURS.toMillis(hours);

    minutes = MILLISECONDS.toMinutes(uptimeMillis);
    uptimeMillis -= MINUTES.toMillis(minutes);

    seconds = MILLISECONDS.toSeconds(uptimeMillis);
  }

  private boolean isValid() {
    return uptimeMillis > 1000; // valid is deemed as at least 1 second has elapsed!
  }

  public String getFormattedUptime() {

    if (!isValid()) {
      return getContext().getString(R.string.extension_expanded_body_uptime_unavailable);
    }

    return getContext().getString(R.string.extension_expanded_body_uptime_format, days, hours, minutes, seconds);

  }

  public String getFormattedShortUptime() {

    if (!isValid()) {
      return getContext().getString(R.string.extension_expanded_body_uptime_unavailable);
    }

    return getContext().getString(R.string.extension_expanded_body_uptime_shortformat, days, hours, minutes);
  }
}
