package org.c9504.entities;

public class Project {

    public Integer id;
    public String value;

    public Project() {}

    public Project(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
