package by.spalex.diplom.snmp.model;

import by.spalex.diplom.snmp.server.Util;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.logging.Level;

@Entity
@Table(name = "logitem")
public class LogItem {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "level")
    private String level = Level.INFO.getName();

    @Column(name = "message", length = 1024)
    private String message;

    @Column(name = "invoker")
    private String invoker;

    @Column(name = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date = Util.now();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInvoker() {
        return invoker;
    }

    public void setInvoker(String invoker) {
        this.invoker = invoker;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

}
