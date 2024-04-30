package fileParser;

import static org.testng.Assert.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CsvFileParserTest {
  CsvFileParser csvFileParser;

  @BeforeClass
  void setUp() {
    csvFileParser = new CsvFileParser();
  }

  @Test
  public void testParseEmptyFile() {
    assertNull(csvFileParser.parseTransactionFile(""));
  }

  @Test
  public void testParseFileNotExist() {
    assertNull(csvFileParser.parseTransactionFile("test.csv"));
  }

  @Test
  public void testFileLoaded() {
    assertNotNull(csvFileParser.parseTransactionFile("Transactions.csv"));
    assertEquals(csvFileParser.parseTransactionFile("Transactions.csv").size(), 4);
  }
}
