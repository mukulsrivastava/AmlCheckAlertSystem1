import api.AmountLimit;
import api.Transaction;
import fileParser.CsvFileParser;
import java.util.List;
import limitChecks.AccountChecks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmlCheckAlertMain implements AutoCloseable {
  final Logger logger = LoggerFactory.getLogger(AmlCheckAlertMain.class);

  List<Transaction> transactionList;
  List<AmountLimit> limits;
  AccountChecks throttleAccountCheck;
  final CsvFileParser csvFileParser;

  public AmlCheckAlertMain() {
    csvFileParser = new CsvFileParser();
    loadTransactions();
    loadLimits();
    throttleAccountCheck = new AccountChecks(limits.get(0));
  }

  public void loadTransactions() {
    transactionList = csvFileParser.parseTransactionFile("Transactions.csv");
    if (transactionList != null) {
      transactionList.stream()
          .forEach(transaction -> logger.atInfo().log(" Transaction {} ", transaction));
    }
  }

  public void loadLimits() {
    limits = csvFileParser.parseAccountLimitFile("Limits.csv");
    if (transactionList != null) {
      transactionList.stream().forEach(limits -> logger.atInfo().log(" Limits {} ", limits));
    }
  }

  public void streamTransactions() {
    transactionList.stream().forEach(throttleAccountCheck::checkTransactions);
  }

  public static void main(String[] args) {
    AmlCheckAlertMain amlCheckAlertMain = new AmlCheckAlertMain();
    amlCheckAlertMain.streamTransactions();
  }

  @Override
  public void close() throws Exception {
    throttleAccountCheck.stopCheck();
  }
}
