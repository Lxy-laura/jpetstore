package com.jpetstore.domain;

import lombok.Data;
import java.io.Serializable;

@Data
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userid;
    private String email;
    private String firstname;
    private String lastname;
    private String status;
    private String addr1;
    private String addr2;
    private String city;
    private String state;
    private String zip;
    private String country;
    private String phone;
    private String role;

    private SignOn signOn;
    private Profile profile;

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public void setAdmin(boolean b) {
    }

    public String getFullName() {
        return null;
    }
}