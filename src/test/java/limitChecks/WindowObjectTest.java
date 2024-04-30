package limitChecks;

import static org.testng.Assert.*;

import api.Transaction;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.TimeUtils;

public class WindowObjectTest {

  WindowObject windowObject;
  static final int AMOUNT1 = 1000;
  static final int AMOUNT2 = 2000;

  @BeforeClass
  void setUp() {
    windowObject = new WindowObject(TimeUtils.getCurrentTimeSeconds());
  }

  @Test
  public void testAddElement() {
    Transaction transaction1 = Mockito.mock(Transaction.class);
    Mockito.when(transaction1.getAmount()).thenReturn(AMOUNT1);
    long timeinSeconds = TimeUtils.getCurrentTimeSeconds();
    Mockito.when(transaction1.getTime()).thenReturn(timeinSeconds);
    windowObject.addElement(transaction1);

    // Verify Transaction 1
    assertEquals(windowObject.getWindowSum(), 1000);
    assertEquals(windowObject.getWindowElements(), 1);
    assertNotEquals(windowObject.getLastWindowUpdateTime(), 0);

    // Verify Transaction 2
    Transaction transaction2 = Mockito.mock(Transaction.class);
    Mockito.when(transaction2.getAmount()).thenReturn(AMOUNT2);
    assertNotEquals(windowObject.getLastWindowUpdateTime(), 0);
    windowObject.addElement(transaction2);
    assertEquals(windowObject.getWindowSum(), 3000);
    assertEquals(windowObject.getWindowElements(), 2);
  }

  @Test
  public void testReset() {
    Transaction transaction1 = Mockito.mock(Transaction.class);
    Mockito.when(transaction1.getAmount()).thenReturn(AMOUNT1);
    long timeinSeconds = TimeUtils.getCurrentTimeSeconds();
    Mockito.when(transaction1.getTime()).thenReturn(timeinSeconds);

    assertEquals(windowObject.getWindowSum(), 3000);
    windowObject.reset();
    assertEquals(windowObject.getWindowStartTime(), 0);
    assertEquals(windowObject.getWindowSum(), 0);
    assertEquals(windowObject.getWindowElements(), 0);
    // Add element after object reset
    windowObject.addElement(transaction1);
    assertEquals(windowObject.getWindowSum(), 1000);
    assertEquals(windowObject.getWindowElements(), 1);
    assertNotEquals(windowObject.getLastWindowUpdateTime(), 0);
  }
}
