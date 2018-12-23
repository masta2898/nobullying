package ua.sumy.stpp.nobullying.service;

import ua.sumy.stpp.nobullying.model.Model;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;
import java.util.logging.Logger;

public class UserService {
    private EntityManager entityManager;

    private final Logger log = Logger.getLogger(UserService.class.getName());

    UserService(String databaseUrl) {
        entityManager = Persistence.createEntityManagerFactory(databaseUrl).createEntityManager();
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
