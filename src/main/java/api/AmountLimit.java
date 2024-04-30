package api;

public class AmountLimit {
  final int Amount;
  final int timeWindow;

  public AmountLimit(String amount, String timeWindow) {
    Amount = Integer.parseInt(amount);
    this.timeWindow = Integer.parseInt(timeWindow);
  }

  public int getAmount() {
    return Amount;
  }

  public int getTimeWindow() {
    return timeWindow;
  }

  @Override
  public String toString() {
    return "AmountLimit{" + "Amount=" + Amount + ", timeWindow=" + timeWindow + '}';
  }
}
