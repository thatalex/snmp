package by.spalex.diplom.snmp.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Object for {@link Item} filtering
 */
public class Filter {

    private String name = "";

    private String address = "";

    private String type = "";

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime createDateBegin;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime createDateEnd;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime modifyDateBegin;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime modifyDateEnd;

    private boolean includeChilds = false;

    public Filter() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreateDateBegin() {
        return createDateBegin;
    }

    public void setCreateDateBegin(LocalDateTime createDateBegin) {
        this.createDateBegin = createDateBegin;
    }

    public LocalDateTime getCreateDateEnd() {
        return createDateEnd;
    }

    public void setCreateDateEnd(LocalDateTime createDateEnd) {
        this.createDateEnd = createDateEnd;
    }

    public LocalDateTime getModifyDateBegin() {
        return modifyDateBegin;
    }

    public void setModifyDateBegin(LocalDateTime modifyDateBegin) {
        this.modifyDateBegin = modifyDateBegin;
    }

    public LocalDateTime getModifyDateEnd() {
        return modifyDateEnd;
    }

    public void setModifyDateEnd(LocalDateTime modifyDateEnd) {
        this.modifyDateEnd = modifyDateEnd;
    }

    public boolean isIncludeChilds() {
        return includeChilds;
    }

    public void setIncludeChilds(boolean includeChilds) {
        this.includeChilds = includeChilds;
    }
}
