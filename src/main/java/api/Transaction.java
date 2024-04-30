package api;

import java.time.LocalTime;

public class Transaction {
  final long time;
  final int amount;
  final int account;

  public Transaction(String time, String amount, String account) {
    this.time = LocalTime.parse(time).toSecondOfDay();
    this.amount = Integer.parseInt(amount);
    this.account = Integer.parseInt(account);
  }

  public long getTime() {
    return time;
  }

  public int getAmount() {
    return amount;
  }

  public int getAccount() {
    return account;
  }

  @Override
  public String toString() {
    return "Transaction{"
        + "time='"
        + time
        + '\''
        + ", amount="
        + amount
        + ", account="
        + account
        + '}';
  }
}
