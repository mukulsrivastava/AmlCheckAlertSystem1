package limitChecks;

import static org.testng.Assert.*;

import api.Transaction;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.TimeUtils;

public class ThrottleAmountCheckTest {

  ThrottleAmountCheck throttleAmountCheck;

  @BeforeClass
  void setUp() {
    throttleAmountCheck = new ThrottleAmountCheck(60, 5000);
  }

  @Test
  public void testGetCurrentWindow() {}

  @Test
  public void testAddTransaction() {
    Transaction transaction1 = Mockito.mock(Transaction.class);
    Mockito.when(transaction1.getAmount()).thenReturn(2000);
    long timeinSeconds = TimeUtils.getCurrentTimeSeconds();
    Mockito.when(transaction1.getTime()).thenReturn(timeinSeconds);

    throttleAmountCheck.addTransaction(transaction1);
    assertEquals(throttleAmountCheck.getCurrentBinTotal(), 2000);

    // Move time by 10 seconds
    Mockito.when(transaction1.getAmount()).thenReturn(1000);
    Mockito.when(transaction1.getTime()).thenReturn(timeinSeconds + 10);
    throttleAmountCheck.addTransaction(transaction1);
    assertEquals(throttleAmountCheck.getCurrentBinTotal(), 3000);

    // Move time by 70 seconds
    Mockito.when(transaction1.getAmount()).thenReturn(4000);
    Mockito.when(transaction1.getTime()).thenReturn(timeinSeconds + 70);
    throttleAmountCheck.addTransaction(transaction1);
    assertEquals(throttleAmountCheck.getCurrentBinTotal(), 4000);
  }

  @Test
  public void testExpireCurrentBin() {}

  @Test
  public void testExpire() {
    ThrottleAmountCheck throttleAmountCheck = Mockito.mock(ThrottleAmountCheck.class);
    throttleAmountCheck.setCurBinIndex(0);

    WindowObject windowObjectMock = Mockito.mock(WindowObject.class);
    assertEquals(throttleAmountCheck.getBinStartIndex(), 0);
    throttleAmountCheck.expire(TimeUtils.getCurrentTimeSeconds());
    // Mockito.when(throttleAmountCheck.getCurrentWindow()).thenReturn(windowObjectMock);
  }

  @Test
  public void testGetThrottleSize() {}
}
