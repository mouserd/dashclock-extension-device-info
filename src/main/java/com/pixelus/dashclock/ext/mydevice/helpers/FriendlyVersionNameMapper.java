package com.pixelus.dashclock.ext.mydevice.helpers;

import android.os.Build;
import android.util.Log;

import static android.os.Build.VERSION_CODES;

public class FriendlyVersionNameMapper {

  private static final String TAG = FriendlyVersionNameMapper.class.getSimpleName();

  public static String getFriendlyVersionName() {

    int versionCode = Build.VERSION.SDK_INT;
    Log.d(TAG, "Getting friendly version name for versionCode: " + versionCode);

    switch (versionCode) {
      case VERSION_CODES.BASE:
      case VERSION_CODES.BASE_1_1:
        return "Base";
      case VERSION_CODES.CUPCAKE:
        return "Cupcake";
      case VERSION_CODES.DONUT:
        return "Donut";
      case VERSION_CODES.ECLAIR:
      case VERSION_CODES.ECLAIR_0_1:
      case VERSION_CODES.ECLAIR_MR1:
        return "Eclair";
      case VERSION_CODES.FROYO:
        return "Froyo";
      case VERSION_CODES.GINGERBREAD:
      case VERSION_CODES.GINGERBREAD_MR1:
        return "Gingerbread";
      case VERSION_CODES.HONEYCOMB:
      case VERSION_CODES.HONEYCOMB_MR1:
      case VERSION_CODES.HONEYCOMB_MR2:
        return "Honeycomb";
      case VERSION_CODES.ICE_CREAM_SANDWICH:
      case VERSION_CODES.ICE_CREAM_SANDWICH_MR1:
        return "Ice Cream Sandwich";
      case VERSION_CODES.JELLY_BEAN:
      case VERSION_CODES.JELLY_BEAN_MR1:
      case VERSION_CODES.JELLY_BEAN_MR2:
        return "Jelly Bean";
      case VERSION_CODES.KITKAT:
        return "KitKat";
      default:
        return "Unknown";
    }
  }
}
