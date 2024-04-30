package limitChecks;

import api.AmountLimit;
import api.Transaction;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import utils.TimeUtils;

/**
 * The AccountChecks class maintain the map of account and rollingAmount structure for each account
 * Below solution assume that day will not change as Transactions data structure only has time
 * information
 */
public class AccountChecks {
  Map<Integer, ThrottleAmountCheck> accountCheckMap;
  AmountLimit amountLimit;

  /*
   Fixed Time scheduler is required while parsing real time feed.
   This expires the current bin after every 1 second which is bin size.
  */
  ScheduledExecutorService scheduledExecutor;

  public AccountChecks(AmountLimit amountLimit) {
    this.amountLimit = amountLimit;
    this.accountCheckMap = new HashMap<>();

    scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    scheduledExecutor.scheduleAtFixedRate(
        () ->
            accountCheckMap
                .values()
                .forEach(
                    throttleAmountCheck -> {
                      throttleAmountCheck.expire(TimeUtils.getCurrentTimeSeconds());
                    }),
        60,
        1,
        TimeUnit.SECONDS);
  }

  public void checkTransactions(Transaction transaction) {
    ThrottleAmountCheck throttleAmountCheck =
        accountCheckMap.getOrDefault(
            transaction.getAccount(),
            new ThrottleAmountCheck(amountLimit.getTimeWindow(), amountLimit.getAmount()));
    if (!accountCheckMap.containsKey(transaction.getAccount())) {
      accountCheckMap.put(transaction.getAccount(), throttleAmountCheck);
    }
    throttleAmountCheck.addTransaction(transaction);
  }

  public void stopCheck() {
    scheduledExecutor.shutdown();
    try {
      if (!scheduledExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
        scheduledExecutor.shutdown();
      }
    } catch (InterruptedException e) {
      scheduledExecutor.shutdownNow();
    }
  }
}
