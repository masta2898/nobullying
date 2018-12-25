package ua.sumy.stpp.nobullying.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ua.sumy.stpp.nobullying.model.Model;
import ua.sumy.stpp.nobullying.service.error.BadOperationException;
import ua.sumy.stpp.nobullying.service.error.BadParametersException;
import ua.sumy.stpp.nobullying.service.error.ModelNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceUtilsTest {
    private EntityManager entityManager;
    private ServiceUtils serviceUtils;
    private Model model;

    @BeforeEach
    void setup() {
        entityManager = mock(EntityManager.class);
        serviceUtils = new ServiceUtils(entityManager);

        model = mock(Model.class);
        when(model.getId()).thenReturn(1L);
        when(model.isNull()).thenReturn(false);
    }

    @Test
    void getNotExistingModelById() {
        when(entityManager.find(Model.class, 1L)).thenReturn(null);

        assertThrows(ModelNotFoundException.class, () -> serviceUtils.getModelById(Model.class, 1L));
    }

    @Test
    void getModelById() {
        when(entityManager.find(Model.class, 1L)).thenReturn(model);

        assertEquals(model, assertDoesNotThrow(() -> serviceUtils.getModelById(Model.class, 1L)));
    }

    @Test
    void getAllModelsFromEmptyDatabase() {
        String namedQuery = "fetchAllModels";
        Query query = mock(Query.class);

        when(query.getResultList()).thenReturn(null);
        when(entityManager.createNamedQuery(namedQuery)).thenReturn(query);

        List<Model> models = serviceUtils.getAllModels(namedQuery);

        assertNotNull(models);
        assertTrue(models.isEmpty());
    }

    @Test
    void getAllModels() {
        String namedQuery = "fetchAllModels";
        Query query = mock(Query.class);
        List<Model> testModels = new LinkedList<>();
        testModels.add(model);

        when(query.getResultList()).thenReturn(testModels);
        when(entityManager.createNamedQuery(namedQuery)).thenReturn(query);

        List<Model> models = serviceUtils.getAllModels(namedQuery);

        assertNotNull(models);
        assertEquals(testModels, models);
    }

    @Test
    @Disabled("Saving an existing model is similar to updating.")
    void saveExistingModel() {
        fail("Failed saving existing model test.");
    }

    @Test
    @Disabled("Saving model with empty field(s) may be allowed.")
    void saveEmptyModel() {
        fail("Failed saving model with empty field(s).");
    }

    @Test
    void saveNullModel() {
        assertThrows(BadParametersException.class, () -> serviceUtils.saveModel(null));
    }

    @Test
    void saveModel() {
        EntityTransaction entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction()).thenReturn(entityTransaction);

        assertDoesNotThrow(() -> serviceUtils.saveModel(model));

        verify(entityManager).getTransaction();
        verify(entityTransaction).begin();
        verify(entityManager).persist(model);
        verify(entityTransaction).commit();
    }

    @Test
    void deleteNullModel() {
        assertThrows(BadParametersException.class, () -> serviceUtils.deleteModel(null));
    }

    @Test
    void deleteNotExistingModel() {
        when(entityManager.find(Model.class, 1L)).thenReturn(null);

        assertThrows(BadOperationException.class, () -> serviceUtils.deleteModel(model));
    }

    @Test
    @Disabled("Model cannot be mocked properly.")
    void deleteModel() {
        EntityTransaction entityTransaction = mock(EntityTransaction.class);

        // fixme: test using concrete service object because it cannot be mocked properly.
        when(entityManager.find(Model.class, 1L)).thenReturn(model);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);

        assertDoesNotThrow(() -> serviceUtils.deleteModel(model));

        verify(entityManager).getTransaction();
        verify(entityTransaction).begin();
        verify(entityManager).remove(entityManager.merge(model));
        verify(entityTransaction).commit();
    }
}