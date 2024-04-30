package limitChecks;

import api.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThrottleAmountCheck {

  final Logger logger = LoggerFactory.getLogger(ThrottleAmountCheck.class);

  // Bin of Time window. In this scenario this bin size is 1 sec
  WindowObject[] windowElements;
  int transactionWindowLimit;
  long binExpireTime;
  int curBinIndex;
  int prevBinIndex;
  int binStartIndex;
  int binIndex;
  int previousBinTotalAmount;
  final int windowSize;

  long trackingStartTime;

  int currentTrackingTotal;

  int currentBinTotal;

  public ThrottleAmountCheck(int throttleWindow, int amountLimit) {
    this.windowSize = throttleWindow;
    this.windowElements = new WindowObject[throttleWindow];
    previousBinTotalAmount = 0;
    binIndex = 0;
    prevBinIndex = -1;
    curBinIndex = 0;
    binExpireTime = -1;
    currentBinTotal = 0;
    transactionWindowLimit = amountLimit;
    trackingStartTime = 0;
  }

  public WindowObject getCurrentWindow(long transactionTime) {
    curBinIndex = (int) (transactionTime - trackingStartTime) % this.windowSize;
    WindowObject windowObject = windowElements[curBinIndex];
    if (windowObject == null) {
      windowObject = new WindowObject(transactionTime);
      windowElements[curBinIndex] = windowObject;
    } else {
      if (transactionTime - windowObject.getWindowStartTime() > 1) {
        expire(curBinIndex);
      }
    }
    return windowObject;
  }

  private WindowObject getWindowBin(int binIndex) {
    return windowElements[binIndex];
  }

  // Reading input from CSV is different from  realtime as time elapsed has already passed and
  // 1 second timer used for expiring bins will not be correct solution.
  // bins need to expire based on the time window also.
  // At each second bin will rollover and transaction should map to the correct bin based on the
  // timestamp
  // e.g. in case of 5 second window trasaction coming at 3 second should fall in 3rd second window
  //
  public void addTransaction(Transaction transaction) {
    // This will be timestamp to idetify when tracking started
    if (trackingStartTime == 0) {
      trackingStartTime = transaction.getTime();
    }

    // See if current Transaction is in 1 seconds
    WindowObject windowObject;
    // Below check is relevant if there is realtime stream and expiry is done by Timer
    if (transaction.getTime() < binExpireTime) {
      windowObject = getWindowBin(prevBinIndex);
      // Below use case to allow replay capability along with realtime handling
      if (windowObject == null) {
        windowObject = new WindowObject(transaction.getTime());
      }
      previousBinTotalAmount += transaction.getAmount();
    } else {
      windowObject = getCurrentWindow(transaction.getTime());
      // windowObject = returnValidBin(transaction.getTime(), windowObject);
      currentBinTotal += transaction.getAmount();
    }
    windowObject.addElement(transaction);

    currentTrackingTotal = previousBinTotalAmount + currentBinTotal;
    checkTransactionBreached(transaction);
  }

  private boolean checkTransactionBreached(Transaction transaction) {
    if (currentTrackingTotal >= transactionWindowLimit) {
      logger.atInfo().log(" Alert raised for transaction", transaction);
      return true;
    }
    return false;
  }

  void expireCurrentBin(long binExpireTime) {
    this.binExpireTime = binExpireTime;
    prevBinIndex = (curBinIndex - 1) % windowSize;
  }

  public int getCurrentBinTotal() {
    return currentBinTotal;
  }

  public int getBinStartIndex() {
    return binStartIndex;
  }

  public void setCurBinIndex(int curBinIndex) {
    this.curBinIndex = curBinIndex;
  }

  // Expire current bin and reuse it for new accumulation
  void expire(long binExpireTime) {
    expireCurrentBin(binExpireTime);
    WindowObject curWindowObject = getWindowBin(binStartIndex);
    binStartIndex = (binStartIndex + 1) % windowSize;
    if (curWindowObject != null) {
      currentTrackingTotal =
          previousBinTotalAmount + currentBinTotal - curWindowObject.getWindowSum();
      currentBinTotal = 0;
      curWindowObject.reset();
    } else {
      currentTrackingTotal = previousBinTotalAmount + currentBinTotal;
      currentBinTotal = 0;
    }
  }
}
