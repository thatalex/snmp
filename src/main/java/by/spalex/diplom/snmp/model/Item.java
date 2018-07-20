package by.spalex.diplom.snmp.model;

import by.spalex.diplom.snmp.server.Util;
import by.spalex.diplom.snmp.server.snmp.Mib;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = "item")
public class Item {
    private static final String VARIABLE = "VARIABLE";
    private static final String ROOT = "ROOT";
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "oid")
    private String oid;

    @Column(name = "public")
    private String publicCommunity = "public";

    @Column(name = "private")
    private String privateCommunity;

    @Column(name = "address")
    private String address;


    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "items")
    private Set<Group> groups = new HashSet<>();


    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime createDate = Util.now();

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime modifyDate = Util.now();
    @Column(name = "type")
    private String type = "";
    @Column(name = "probePeriod")
    private Long probePeriod = 300L;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Probe> probes = new TreeSet<>();
    @ManyToOne(fetch = FetchType.LAZY)
    private Item parentItem;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parentItem", cascade = CascadeType.ALL)
    private Set<Item> childItems = new HashSet<>();

    public Item() {
    }

    public Item(String address, String oid) {
        this.address = address;
        this.oid = oid;
        name = Mib.getName(oid);
    }

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

    public String getPublicCommunity() {
        return publicCommunity;
    }

    public void setPublicCommunity(String publicCommunity) {
        this.publicCommunity = publicCommunity;
    }

    public String getPrivateCommunity() {
        return privateCommunity;
    }

    public void setPrivateCommunity(String privateCommunity) {
        this.privateCommunity = privateCommunity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(LocalDateTime modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) &&
                Objects.equals(name, item.name) &&
                Objects.equals(publicCommunity, item.publicCommunity) &&
                Objects.equals(privateCommunity, item.privateCommunity) &&
                Objects.equals(address, item.address) &&
                Objects.equals(oid, item.oid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, publicCommunity, privateCommunity, address, oid, getType());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getProbePeriod() {
        return probePeriod;
    }

    public void setProbePeriod(Long probePeriod) {
        this.probePeriod = probePeriod;
    }

    public Set<Probe> getProbes() {
        return new TreeSet<>(probes);
    }

    public void setProbes(Set<Probe> probes) {
        this.probes = probes;
    }

    public Probe first() {
        return probes.isEmpty() ? null : new TreeSet<>(probes).first();
    }

    public void addProbe(Probe probe) {
        probes.add(probe);
    }

    public Item getParentItem() {
        return parentItem;
    }

    public void setParentItem(Item parentItem) {
        this.parentItem = parentItem;
    }

    public Set<Item> getChildItems() {
        return childItems;
    }

    public void setChildItems(Set<Item> childItems) {
        this.childItems = childItems;
    }

    public void addChild(Item child) {
        childItems.add(child);
        child.setParentItem(this);
    }

    public void addValue(String value) {
        if (value == null) return;
        Probe lastProbe = first();
        if (lastProbe == null || !value.equals(lastProbe.getValue())) {
            probes.add(new Probe(value));
        }
    }

    public void makeRoot() {
        type = ROOT;
        if (parentItem != null) {
            parentItem.getChildItems().remove(this);
            parentItem = null;
        }
        probes.clear();
        probePeriod = 0L;
    }

    public void makeVariable() {
        type = VARIABLE;
        childItems.clear();
        probePeriod = 300L;
    }
}
