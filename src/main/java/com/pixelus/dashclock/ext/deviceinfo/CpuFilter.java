package com.pixelus.dashclock.ext.deviceinfo;


import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class CpuFilter implements FileFilter {

  private Pattern pattern = Pattern.compile("cpu[0-9]+");

  @Override
  public boolean accept(File pathname) {
    //Check if filename is "cpu", followed by a single digit number

    return pattern.matcher(pathname.getName()).matches();
  }
}
