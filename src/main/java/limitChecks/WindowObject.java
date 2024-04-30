package limitChecks;

import api.Transaction;
import java.time.LocalTime;
import java.time.ZoneId;

/** WindowObject holds the utilization of current time window */
public class WindowObject {
  private int windowSum;
  private long windowStartTimeSecond;
  private long lastWindowUpdateTime;

  public int getWindowElements() {
    return windowElements;
  }

  int windowElements;

  WindowObject(long windowStartTimeSecond) {
    this.windowStartTimeSecond = windowStartTimeSecond;
  }

  public long getWindowStartTime() {
    return windowStartTimeSecond;
  }

  void addElement(Transaction transaction) {
    windowSum += transaction.getAmount();
    ++windowElements;
    if (windowStartTimeSecond == 0) {
      windowStartTimeSecond = transaction.getTime();
    }
    LocalTime now = LocalTime.now(ZoneId.systemDefault());
    lastWindowUpdateTime = now.toSecondOfDay();
  }

  public long getLastWindowUpdateTime() {
    return lastWindowUpdateTime;
  }

  void reset() {
    windowSum = 0;
    windowElements = 0;
    windowStartTimeSecond = 0L;
    lastWindowUpdateTime = 0L;
  }

  public int getWindowSum() {
    return windowSum;
  }

  @Override
  public String toString() {
    return "WindowObject{"
        + "windowSum="
        + windowSum
        + ", windowStartTimeSecond="
        + windowStartTimeSecond
        + ", lastWindowTime="
        + lastWindowUpdateTime
        + ", windowElements="
        + windowElements
        + '}';
  }
}
