package ua.sumy.stpp.nobullying.service;

import ua.sumy.stpp.nobullying.model.User;
import ua.sumy.stpp.nobullying.service.error.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.LinkedList;
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
        User user = null;

        try {
            user = entityManager.find(User.class, id);
        } catch (Exception e) {
            log.severe(String.format("Error getting user by id (%d): %s", id, e.getMessage()));
        }

        if (user == null) {
            log.severe(String.format("User not found by id (%d).", id));
            throw new UserNotFoundException("User not found!");
        }

        return user;
    }

    List<User> getAllUsers() {
        List<User> users = null;
        String query = "fetchAllUsers";
        try {
            Query namedQuery = entityManager.createNamedQuery(query);
            users = namedQuery.getResultList();
        } catch (Exception e) {
            log.severe(String.format("Error getting all users by query (%s): %s.", query, e.getMessage()));
        }
        return (users != null) ? users : new LinkedList<>();
    }

    boolean verify(String login, String password) throws BadParametersException {
        return true;
    }

    void registerUser(String login, String password, String name, String surname)
            throws UserIsAlreadyRegisteredException, BadReportException {
    }

    boolean isUserAdmin(long id) throws UserNotFoundException {
        User user = getById(id);
        return user.isAdmin();
    }

    void promoteUser(long id) throws UserNotFoundException, UserIsAlreadyAdminException {

    }

    void degradeUser(long id) throws UserNotFoundException, UserIsNotAdminException {

    }

    void deleteUser(long id) throws UserNotFoundException {

    }
}
