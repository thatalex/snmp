package by.spalex.diplom.snmp.model;

import javax.persistence.*;

@Entity
@Table(name = "oid")
public class OidInfo {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "oid")
    private String oid;

    @Column(name = "name")
    private String name;

    @Column(name = "active")
    private boolean active;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
