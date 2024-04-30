package utils;

import java.time.LocalTime;
import java.time.ZoneId;

public class TimeUtils {

  public static long getCurrentTimeSeconds() {
    LocalTime now = LocalTime.now(ZoneId.systemDefault());
    return now.toSecondOfDay();
  }
}
