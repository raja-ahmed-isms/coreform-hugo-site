package csimsoft;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

public class AcctLogger {
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final static AcctLogger instance = new AcctLogger();

    private AcctLogger() {
      this.logger.setLevel(Level.INFO);
      try {
         FileHandler file = new FileHandler("/files/log.txt");
         SimpleFormatter formatter = new SimpleFormatter();
         file.setFormatter(formatter);
         this.logger.addHandler(file);
      }
      catch(IOException e) {
         //Why can't every language have Rust's awesome error handling?
      }
    }

    public static Logger get() {
        return instance.logger;
    }
}