package ua.sumy.stpp.nobullying.service;

import ua.sumy.stpp.nobullying.model.User;
import ua.sumy.stpp.nobullying.service.error.*;

import javax.persistence.Query;
import javax.persistence.EntityManager;

import java.util.List;
import java.util.logging.Logger;

class UserService {
    private final ServiceUtils serviceUtils;
    private final EntityManager entityManager;
    private final Logger log = Logger.getLogger(UserService.class.getName());

    UserService(ServiceUtils serviceUtils) {
        this.serviceUtils = serviceUtils;
        this.entityManager = serviceUtils.getEntityManager();
    }

    User getUserById(long id) throws ModelNotFoundException {
        return serviceUtils.getModelById(User.class, id);
    }

    List<User> getAllUsers() {
        return serviceUtils.getAllModels("fetchAllUsers");
    }

    boolean verify(String login, String password) throws BadParametersException {
        serviceUtils.checkParameters(login, password);

        String queryText = "SELECT u FROM User u WHERE u.login = :login AND u.password = :password";
        Query query = entityManager.createQuery(queryText);
        query.setParameter("login", login);
        query.setParameter("password", password);

        boolean result = false;
        try {
            User user = (User) query.getSingleResult();
            if (user != null) {
                result = true;
                log.info(String.format("Successfully verified with login (%s) and password (%s).", login, password));
            } else {
                log.info(String.format("Verifying with login (%s) and password (%s) wasn't successful.", login,
                        password));
            }
        } catch (Exception e) {
            log.warning(String.format("Verifying with login (%s) and password (%s) ended with error: %s", login,
                    password, e.getMessage()));
            // todo: throw exception about verifying error.
        }
        return result;
    }

    void registerUser(String login, String password, String name, String surname) throws BadOperationException,
            BadParametersException {
        serviceUtils.checkParameters(login, password, name, surname);

        if (login.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty()) {
            log.warning("Attempt to register user without login, password name or surname.");
            throw new BadParametersException("Registering without login, password, name or surname permitted.");
        }

        if (isUserAlreadyRegistered(login)) {
            log.warning(String.format("Attempt to register user with existing login (%s).", login));
            throw new BadOperationException("Registering with existing login permitted.");
        }

        serviceUtils.saveModel(new User(login, password, name, surname));
        log.info(String.format("Registered new user (%s %s (%s:%s)).", name, surname, login, password));
    }

    boolean isUserAdmin(long id) throws ModelNotFoundException {
        User user = getUserById(id);
        return user.isAdmin();
    }

    void promoteUser(long id) throws ModelNotFoundException, BadOperationException {
        saveNewUserPermissions(getUserById(id), true);
    }

    void degradeUser(long id) throws ModelNotFoundException, BadOperationException {
        saveNewUserPermissions(getUserById(id), false);
    }

    void deleteUser(long id) throws ModelNotFoundException {
        User user = serviceUtils.getModelById(User.class, id);
        try {
            serviceUtils.deleteModel(user);
        } catch (BadParametersException e) {
            log.severe(String.format("Error deleting user due it's null: %s.", e.getMessage()));
            // todo: throw exception about error deleting user.
        }
    }

    private void saveNewUserPermissions(User user, boolean isAdmin) throws BadOperationException {
        long id = user.getId();

        if (isAdmin == user.isAdmin()) {
            log.warning(String.format("Attempt to apply same permissions to user by id (%d).", id));
            throw new BadOperationException("Applying same permissions permitted.");
        }

        user.setAdmin(isAdmin);

        try {
            serviceUtils.saveModel(user);
            log.info(String.format("User (%d) is%s admin now.", id, (isAdmin) ? "" : "n't"));
        } catch (BadParametersException e) {
            log.severe(String.format("Error saving user (%d) new permissions: %s", id, e.getMessage()));
        }
    }

    private boolean isUserAlreadyRegistered(String login) {
        String queryText = "SELECT u FROM User u WHERE u.login = :login";
        Query query = entityManager.createQuery(queryText);
        query.setParameter("login", login);

        boolean result = false;
        try {
            User user = (User) query.getSingleResult();
            if (user != null) {
                result = true;
            }
        } catch (Exception e) {
           log.warning(String.format("Exception that should never be thrown has been thrown: %s.", e.getMessage()));
        }
        return result;
    }
}
