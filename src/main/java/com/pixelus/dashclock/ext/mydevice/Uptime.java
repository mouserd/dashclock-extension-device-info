package com.pixelus.dashclock.ext.mydevice;

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

  public boolean isValid() {
    return uptimeMillis > 1000; // valid is deemed as at least 1 second has elapsed!
  }

  public long getDays() {
    return days;
  }

  public long getHours() {
    return hours;
  }

  public long getMinutes() {
    return minutes;
  }

  public long getSeconds() {
    return seconds;
  }
}
