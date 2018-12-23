package ua.sumy.stpp.nobullying.service;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class DatabaseUtils {
    EntityManager getEntityManager(String database) {
        return Persistence.createEntityManagerFactory(database).createEntityManager();
    }
}
