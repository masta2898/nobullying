package ua.sumy.stpp.nobullying.service;

import ua.sumy.stpp.nobullying.model.Model;
import ua.sumy.stpp.nobullying.service.error.BadParametersException;
import ua.sumy.stpp.nobullying.service.error.ModelNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

abstract class Service {
    protected EntityManager entityManager;

    private final Logger log = Logger.getLogger(UserService.class.getName());

    Service(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected <M extends Model> M getModelById(Class<M> modelClass, long id) throws ModelNotFoundException {
        M model = null;

        try {
            model = entityManager.find(modelClass, id);
        } catch (Exception e) {
            log.severe(String.format("Error getting model by id (%d): %s.", id, e.getMessage()));
        }

        if (model == null) {
            log.severe(String.format("Model not found by id (%d).", id));
            throw new ModelNotFoundException("Model not found!");
        }

        return model;
    }

    protected <M extends Model> List<M> getAllModels(String query) {
        List<M> models = null;
        try {
            Query namedQuery = entityManager.createNamedQuery(query);
            models = (List<M>) namedQuery.getResultList();
        } catch (Exception e) {
            log.severe(String.format("Error getting all models by query (%s): %s.", query, e.getMessage()));
        }
        return (models != null) ? models : new LinkedList<>();
    }

    protected <M extends Model> void saveModel(M model) throws BadParametersException {
        checkParameters(model);
        long id = model.getId();
        String modelName = model.getClass().getName();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            entityManager.persist(model);
            entityTransaction.commit();
            log.info(String.format("Saved (%s) with id (%d).", modelName, id));
        } catch (Exception e) {
            log.severe(String.format("Rolling back due to a %s (%d) saving error: %s.", modelName, id, e.getMessage()));
            entityTransaction.rollback();
            // todo: throw exception about saving error.
        }
    }

    protected <M extends Model> void deleteModel(M model) throws BadParametersException {
        checkParameters(model);
        long id = model.getId();
        String modelName = model.getClass().getName();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            entityManager.remove(entityManager.merge(model));
            entityTransaction.commit();
            log.info(String.format("Deleting %s with id (%d).", modelName, id));
        } catch (Exception e) {
            log.severe(String.format("Rolling back due to a %s (%d) delete error: %s.", modelName, id, e.getMessage()));
            entityTransaction.rollback();
            // todo: throw exception about deleting error.
        }
    }

    protected void checkParameters(Object... parameters) throws BadParametersException {
        for (Object parameter: parameters) {
            if (parameter == null) {
                log.warning("Passed null or empty parameter to a service function.");
                throw new BadParametersException("One of parameters is null or empty!");
            }
        }
    }
}
