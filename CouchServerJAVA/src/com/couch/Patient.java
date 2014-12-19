package com.couch;

import org.codehaus.jackson.annotate.JsonProperty;


public class Patient {


    /**
     * 
     */
    @JsonProperty("_id")
    private String id;
    @JsonProperty("_rev")
    private String revision;
    
    
    private String firstName;
    private String lastName;
    private int age;

    
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    public String getRevision() {
        return revision;
    }
    public void setRevision(String revision) {
        this.revision = revision;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }


    @Override
    public String toString() {
        return getId() + ": " + getFirstName() + " " + getLastName()+ " " + getAge();
    }
}
