package ua.sumy.stpp.nobullying.service;

import ua.sumy.stpp.nobullying.model.Model;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

public class UserService implements Service {
    private EntityManager entityManager;

    private final Logger log = Logger.getLogger(UserService.class.getName());

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    Model getUser(String login, String password) {
        return null;
    }

    Model register(String username, String password, String name, String surname) {
        return null;
    }

    List<Model> getAllUsers() {
        return null;
    }
}
