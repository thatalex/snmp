package by.spalex.diplom.snmp.model;

import by.spalex.diplom.snmp.server.Util;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "probe")
public class Probe implements Comparable<Probe> {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date = Util.now();

    @Column(name = "value", length = 1024)
    private String value;

    public Probe() {
    }

    public Probe(String value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int compareTo(Probe o) {
        return o == null ? -1 : o.date.compareTo(date);
    }
}
