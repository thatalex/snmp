package by.spalex.diplom.snmp.server;

import by.spalex.diplom.snmp.model.Item;
import by.spalex.diplom.snmp.server.snmp.Walker;
import by.spalex.diplom.snmp.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ScheduledTasks {

    private final ItemService itemService;

    private final List<Item> itemList = new ArrayList<>();
    private Walker walker;

    @Autowired
    public ScheduledTasks(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostConstruct
    public void init() {
        walker = new Walker(itemService);
    }

    @Scheduled(fixedRate = 30000)
    public void probes() {
        List<Item> items;

        synchronized (this.itemList) {
            if (itemList.isEmpty()) {
                getItems();
            }
            items = new ArrayList<>(this.itemList);
        }

        if (items.isEmpty()) return;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int probeCount = 0;
        for (Item item : items) {
            LocalDateTime now = Util.now();
            LocalDateTime probeTime = item.getModifyDate();
            if (ChronoUnit.SECONDS.between(probeTime, now) > item.getProbePeriod()) {
                executorService.execute(new Task(item));
                probeCount++;
            }

        }
        executorService.shutdown();
        if (probeCount > 0) {
            Logger.INSTANCE.info("Received probes of " + items.size() + " elements");
        }
    }

    @Scheduled(fixedRate = 300000)
    public void getItems() {
        synchronized (itemList) {
            itemList.clear();
            itemList.addAll(itemService.findAll(0L));
        }
    }

    private class Task implements Runnable {

        private final Item item;

        private Task(Item item) {
            this.item = item;
        }

        @Override
        public void run() {
            try {
                walker.probe(item);
            } catch (Exception e) {
                Logger.INSTANCE.error(e.toString());
            }
        }
    }
}