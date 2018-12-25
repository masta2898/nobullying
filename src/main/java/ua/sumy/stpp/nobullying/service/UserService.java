package ua.sumy.stpp.nobullying.service;

import ua.sumy.stpp.nobullying.model.User;
import ua.sumy.stpp.nobullying.service.error.*;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.logging.Logger;

class UserService {
    private ServiceUtils serviceUtils;
    private final Logger log = Logger.getLogger(UserService.class.getName());

    UserService(EntityManager entityManager) {
        this.serviceUtils = new ServiceUtils(entityManager);
    }

    User getUserById(long id) throws ModelNotFoundException {
        return serviceUtils.getModelById(User.class, id);
    }

    List<User> getAllUsers() {
        return serviceUtils.getAllModels("fetchAllUsers");
    }

    boolean verify(String login, String password) throws BadParametersException {
        serviceUtils.checkParameters(login, password);
        return true;
    }

    void registerUser(String login, String password, String name, String surname) throws BadOperationException,
            BadParametersException {
        serviceUtils.checkParameters(login, password, name, surname);
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
}
