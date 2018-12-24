package ua.sumy.stpp.nobullying.service;

import ua.sumy.stpp.nobullying.model.User;
import ua.sumy.stpp.nobullying.service.error.UserIsAlreadyAdminException;
import ua.sumy.stpp.nobullying.service.error.UserIsAlreadyRegisteredException;
import ua.sumy.stpp.nobullying.service.error.UserIsNotAdminException;
import ua.sumy.stpp.nobullying.service.error.UserNotFoundException;

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

    User getById(long id) throws UserNotFoundException {
        return null;
    }

    boolean verify(String login, String password) {
        return true;
    }

    void registerUser(String login, String password, String name, String surname)
            throws UserIsAlreadyRegisteredException {
    }

    List<User> getAllUsers() {
        return null;
    }

    boolean isUserAdmin(long id) throws UserNotFoundException {
        return false;
    }

    void promoteUser(long id) throws UserNotFoundException, UserIsAlreadyAdminException {

    }

    void degradeUser(long id) throws UserNotFoundException, UserIsNotAdminException {

    }

    void deleteUser(long id) throws UserNotFoundException {

    }
}
