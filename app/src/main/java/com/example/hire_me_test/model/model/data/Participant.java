package com.example.hire_me_test.model.model.data;

public class Participant {
    private String idNumber;
    private String name;
    private String contact;

    public Participant(String idNumber, String name, String contact) {
        this.idNumber = idNumber;
        this.name = name;
        this.contact = contact;
    }

    public String getIdNumber() { return idNumber; }
    public String getName() { return name; }
    public String getContact() { return contact; }
}
