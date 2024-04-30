package fileParser;

import api.AmountLimit;
import api.Transaction;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvFileParser {
  final Logger logger = LoggerFactory.getLogger(CsvFileParser.class);

  public enum Headers {
    Time,
    Amount,
    Account
  }

  public enum LimitHeaders {
    Limit,
    TimeWindow
  }

  public List<Transaction> parseTransactionFile(String csvFileName) {
    List<Transaction> transactions = null;
    try {
      URI uri = ClassLoader.getSystemResource(csvFileName).toURI();
      String filePath = Paths.get(uri).toString();
      logger.atInfo().log(" Loading file from {} ", filePath);
      try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {
        CSVParser parser =
            CSVFormat.DEFAULT
                .builder()
                .setHeader(Headers.class)
                .setSkipHeaderRecord(true)
                .build()
                .parse(reader);

        List<CSVRecord> csvRecords = parser.getRecords();
        logger.atInfo().log(" Transaction CSV Record size {}", csvRecords.size());
        transactions =
            csvRecords
                .parallelStream()
                .map(
                    csvRecord ->
                        new Transaction(
                            csvRecord.get("Time"),
                            csvRecord.get("Amount"),
                            csvRecord.get("Account")))
                .collect(Collectors.toList());
      } catch (IOException e) {
        logger.atError().log(" IOException {} ", e.getMessage());
      }
    } catch (Exception e) {
      logger.atError().log(" IOException {} ", e.getMessage());
    }
    return transactions;
  }

  public List<AmountLimit> parseAccountLimitFile(String csvFileName) {
    List<AmountLimit> transactions = null;
    // Below lines will be removed
    try {
      URI uri = ClassLoader.getSystemResource(csvFileName).toURI();
      String filePath = Paths.get(uri).toString();
      logger.atInfo().log(" Loading file from  {} ", filePath);
      try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {

        CSVParser parser =
            CSVFormat.DEFAULT
                .builder()
                .setHeader(LimitHeaders.class)
                .setSkipHeaderRecord(true)
                .build()
                .parse(reader);

        List<CSVRecord> csvRecords = parser.getRecords();
        logger.atInfo().log(" CSV Record size {}", csvRecords.size());

        transactions =
            csvRecords
                .parallelStream()
                .map(
                    csvRecord ->
                        new AmountLimit(csvRecord.get("Limit"), csvRecord.get("TimeWindow")))
                .collect(Collectors.toList());
      } catch (IOException e) {
        logger.atError().log(" IOException {} ", e.getMessage());
      }
    } catch (Exception e) {
      logger.atError().log(" IOException {} ", e.getMessage());
    }
    return transactions;
  }
}
