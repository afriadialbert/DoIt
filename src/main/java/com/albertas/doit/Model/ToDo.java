package com.albertas.doit.Model;

public class ToDo {
    private String id,tittle,description;

    public ToDo(){

    }

    public ToDo(String id, String tittle, String description) {
        this.id = id;
        this.tittle = tittle;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
