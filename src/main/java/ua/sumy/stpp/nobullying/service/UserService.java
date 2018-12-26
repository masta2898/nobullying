package ua.sumy.stpp.nobullying.service;

import ua.sumy.stpp.nobullying.model.User;
import ua.sumy.stpp.nobullying.service.error.*;

import javax.persistence.Query;
import javax.persistence.EntityManager;

import java.util.List;
import java.util.logging.Logger;

class UserService extends Service {
    private final EntityManager entityManager;
    private final Logger log = Logger.getLogger(UserService.class.getName());

    UserService(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
    }

    User getUserById(long id) throws ModelNotFoundException {
        return getModelById(User.class, id);
    }

    List<User> getAllUsers() {
        return getAllModels("fetchAllUsers");
    }

    boolean verify(String login, String password) throws BadParametersException {
        if (anyIsNull(login, password) || anyIsEmpty(login, password)) {
            log.severe("Attempt to verify user with null or empty login or password.");
            throw new BadParametersException("Verifying without login or password permitted.");
        }

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
            log.severe(String.format("Verifying with login (%s) and password (%s) ended with error: %s", login,
                    password, e.getMessage()));
            // todo: throw exception about verifying error.
        }
        return result;
    }

    void registerUser(User user) throws BadOperationException, BadParametersException {
        if (anyIsNull(user)) {
            log.warning("Attempt to register null user.");
            throw new BadParametersException("Registering null user permitted.");
        }

        String login = user.getLogin();
        String password = user.getPassword();
        String name = user.getName();
        String surname = user.getSurname();

        if (anyIsNull(login, password, name, surname) || anyIsEmpty(login, password, name, surname)) {
            log.warning("Attempt to register user without login, password, name or surname.");
            throw new BadParametersException("Registering user without login, password, name or surname permitted.");
        }

        if (isUserAlreadyRegistered(login)) {
            log.warning(String.format("Attempt to register user with existing login (%s).", login));
            throw new BadOperationException("Registering with existing login permitted.");
        }

        saveModel(user);
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
        User user = getModelById(User.class, id);
        try {
            deleteModel(user);
        } catch (BadParametersException e) {
            log.severe(String.format("Error deleting user due it's null: %s.", e.getMessage()));
            // todo: throw exception about error deleting user.
        } catch (BadOperationException e) {
            log.severe(String.format("Error deleting user due it doesn't exist by id (%d): %s", id, e.getMessage()));
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
            saveModel(user);
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
           log.severe(String.format("Error getting user by login (%s): %s.", login, e.getMessage()));
        }
        return result;
    }
}
