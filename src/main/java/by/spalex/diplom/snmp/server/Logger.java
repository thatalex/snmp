package by.spalex.diplom.snmp.server;

import by.spalex.diplom.snmp.model.LogItem;
import by.spalex.diplom.snmp.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.logging.Level;

public enum Logger {

    INSTANCE;

    private LogService logService;

    private void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void info(String message) {
        logService.save(getLogItem(Level.INFO, message));
    }

    public void error(String message) {
        logService.save(getLogItem(Level.SEVERE, message));
    }

    /**
     * Constructs {@link LogItem} instance with given arguments
     *
     * @param level   log's level
     * @param message message for logging
     * @return new instance of {@link LogItem}
     */
    private LogItem getLogItem(Level level, String message) {
        LogItem logItem = new LogItem();
        logItem.setInvoker("SERVER");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            logItem.setInvoker(authentication.getName());
        }
        logItem.setLevel(level.getName());
        logItem.setMessage(message);
        return logItem;
    }

    /**
     * this class set {@link LogService} after creation of {@link Logger}
     */
    @Component
    public static class LoggerServiceInjector {

        private final LogService logService;

        @Autowired
        public LoggerServiceInjector(LogService logService) {
            this.logService = logService;
        }

        @PostConstruct
        public void postConstruct() {
            INSTANCE.setLogService(logService);
        }
    }
}
