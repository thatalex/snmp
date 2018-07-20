package by.spalex.diplom.snmp.service;

import by.spalex.diplom.snmp.model.LogItem;

import java.util.List;

public interface LogService {
    List<LogItem> findAll();

    void save(LogItem logItem);
}
