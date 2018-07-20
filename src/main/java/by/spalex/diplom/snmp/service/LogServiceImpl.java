package by.spalex.diplom.snmp.service;

import by.spalex.diplom.snmp.model.LogItem;
import by.spalex.diplom.snmp.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("logService")
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    @Autowired
    public LogServiceImpl(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public List<LogItem> findAll() {
        return logRepository.findAll();
    }

    @Override
    public void save(LogItem logItem) {
        logRepository.save(logItem);
    }
}
