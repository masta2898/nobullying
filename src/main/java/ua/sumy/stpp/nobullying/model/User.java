package ua.sumy.stpp.nobullying.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@NamedQuery(name = "fetchAllUsers", query = "SELECT u FROM User u")
public class User implements Model, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String login;
    private String password;

    private String name;
    private String surname;

    private boolean isAdmin;

    public User() {

    }

    public User(String login, String password, String name, String surname) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isNull() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s %s", name, surname);
    }
}
