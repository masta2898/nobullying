package ua.sumy.stpp.nobullying.service;

import ua.sumy.stpp.nobullying.model.User;
import ua.sumy.stpp.nobullying.service.error.*;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.logging.Logger;

class UserService extends Service {
    private final Logger log = Logger.getLogger(UserService.class.getName());

    UserService(EntityManager entityManager) {
        super(entityManager);
    }

    User getUserById(long id) throws ModelNotFoundException {
        return getModelById(User.class, id);
    }

    List<User> getAllUsers() {
        return getAllModels("fetchAllUsers");
    }

    boolean verify(String login, String password) throws BadParametersException {
        checkParameters(login, password);
        return true;
    }

    void registerUser(String login, String password, String name, String surname) throws BadOperationException,
            BadParametersException {
        checkParameters(login, password, name, surname);
    }

    boolean isUserAdmin(long id) throws ModelNotFoundException {
        User user = getUserById(id);
        return user.isAdmin();
    }

    void promoteUser(long id) throws ModelNotFoundException, BadOperationException {
        User user = getUserById(id);

        if (user.isAdmin()) {
            log.warning(String.format("Attempt to promote admin user to admin (?) by id (%d).", id));
            throw new BadOperationException("User is already admin.");
        }

        user.setAdmin(true);
        try {
            saveModel(user);
            log.info(String.format("User (%d) is admin now.", id));
        } catch (BadParametersException e) {
            log.severe(String.format("Error saving user (%d): %s", id, e.getMessage()));
        }
    }

    void degradeUser(long id) throws ModelNotFoundException, BadOperationException {

    }

    void deleteUser(long id) throws ModelNotFoundException {

    }
}
