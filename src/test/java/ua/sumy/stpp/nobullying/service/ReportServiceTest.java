package ua.sumy.stpp.nobullying.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import ua.sumy.stpp.nobullying.model.Report;
import ua.sumy.stpp.nobullying.service.error.BadOperationException;
import ua.sumy.stpp.nobullying.service.error.BadParametersException;
import ua.sumy.stpp.nobullying.service.error.ModelNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {
    private EntityManager entityManager;
    private ReportService reportService;

    @BeforeEach
    void setup() {
        entityManager = mock(EntityManager.class);
        reportService = new ReportService(entityManager);
    }

    @Test
    void getNotExistingReportById() {
        when(entityManager.find(Report.class, 1L)).thenReturn(null);

        assertThrows(ModelNotFoundException.class, () -> reportService.getReportById(1L));
    }

    @Test
    void getExistingReportById() {
        Report existingReport = new Report("Anonymous", "Test existing report.", new Date());
        existingReport.setId(1L);

        when(entityManager.find(Report.class, 1L)).thenReturn(existingReport);

        Report report = assertDoesNotThrow(() -> reportService.getReportById(1L));

        assertNotNull(report);
        assertFalse(report.isNull());
        assertEquals(report, existingReport);
    }

    @Test
    void getAllReportsFromEmptyDatabase() {
        Query query = mock(Query.class);

        when(query.getResultList()).thenReturn(null);
        when(entityManager.createNamedQuery("fetchAllReports")).thenReturn(query);

        List<Report> allReports = reportService.getAllReports();

        assertNotNull(allReports);
        assertTrue(allReports.isEmpty());
    }

    @Test
    void getAllReports() {
        List<Report> testReports = new LinkedList<>();
        testReports.add(new Report("Anonymous1", "Text 1", new Date()));
        testReports.add(new Report("Anonymous2", "Text 2", new Date()));
        testReports.add(new Report("Anonymous3", "Text 3", new Date()));

        Query query = mock(Query.class);

        when(query.getResultList()).thenReturn(testReports);
        when(entityManager.createNamedQuery("fetchAllReports")).thenReturn(query);

        List<Report> allReports = reportService.getAllReports();

        assertNotNull(allReports);
        assertEquals(testReports, allReports);
    }

    @Test
    void beginModerateNotExistingReport() {
        Report report = new Report();
        report.setId(1L);

        when(entityManager.find(Report.class, 1L)).thenReturn(null);

        assertThrows(ModelNotFoundException.class, () -> reportService.beginModeratingReport(1L));
    }

    @Test
    void beginModerateAlreadyModeratingReport() {
        Report report = new Report();
        report.setId(1L);
        report.setState(Report.ProcessingState.MODERATING);

        when(entityManager.find(Report.class, 1L)).thenReturn(report);

        assertThrows(BadOperationException.class, () -> reportService.beginModeratingReport(1L));
    }

    @Test
    void beginModerateAlreadyFinishedReport() {
        Report report = new Report();
        report.setId(1L);
        report.setState(Report.ProcessingState.FINISHED);

        when(entityManager.find(Report.class, 1L)).thenReturn(report);

        assertThrows(BadOperationException.class, () -> reportService.beginModeratingReport(1L));
    }

    @Test
    void beginModerateNewReport() {
        Report report = new Report("Anonymous", "Text", new Date());
        report.setId(1L);

        EntityTransaction entityTransaction = mock(EntityTransaction.class);

        when(entityManager.find(Report.class, 1L)).thenReturn(report);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);

        assertDoesNotThrow(() -> reportService.beginModeratingReport(1L));

        verify(entityTransaction).begin();
        verify(entityManager).persist(report);
        verify(entityTransaction).commit();
    }

    @Test
    void finishModeratingNotExistingReport() {
        Report report = new Report();
        report.setId(1L);

        when(entityManager.find(Report.class, 1L)).thenReturn(null);

        assertThrows(ModelNotFoundException.class, () -> reportService.finishModeratingReport(1L));
    }

    @Test
    void finishModeratingAlreadyFinishedModeratingReport() {
        Report report = new Report();
        report.setId(1L);
        report.setState(Report.ProcessingState.FINISHED);

        when(entityManager.find(Report.class, 1L)).thenReturn(report);

        assertThrows(BadOperationException.class, () -> reportService.finishModeratingReport(1L));
    }

    @Test
    void finishModeratingReport() {
        Report report = new Report("Anonymous", "Text", new Date());
        report.setId(1L);

        EntityTransaction entityTransaction = mock(EntityTransaction.class);

        when(entityManager.find(Report.class, 1L)).thenReturn(report);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);

        assertDoesNotThrow(() -> reportService.finishModeratingReport(1L));

        verify(entityTransaction).begin();
        verify(entityManager).persist(report);
        verify(entityTransaction).commit();
    }

    @Test
    void saveNullReport() {
        assertThrows(BadParametersException.class, () -> reportService.saveReport(null));
    }

    @Test
    void saveEmptyReport() {
        assertThrows(BadParametersException.class, () -> reportService.saveReport(new Report()));
    }

    @Test
    void saveReport() {
        Report report = new Report("Anonymous", "Text", new Date());
        report.setId(1L);

        EntityTransaction entityTransaction = mock(EntityTransaction.class);

        when(entityManager.find(Report.class, 1L)).thenReturn(null);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);

        assertDoesNotThrow(() -> reportService.saveReport(report));

        verify(entityTransaction).begin();
        verify(entityManager).persist(report);
        verify(entityTransaction).commit();
    }

    @Test
    void deleteNotExistingReport() {
        Report report = new Report();
        report.setId(1L);

        when(entityManager.find(Report.class, 1L)).thenReturn(null);

        assertThrows(ModelNotFoundException.class, () -> reportService.deleteReport(1L));
    }

    @Test
    void deleteReport() {
        Report report = new Report("Anonymous", "Text", new Date());
        report.setId(1L);

        EntityTransaction entityTransaction = mock(EntityTransaction.class);

        when(entityManager.find(Report.class, 1L)).thenReturn(report);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);

        assertDoesNotThrow(() -> reportService.deleteReport(1L));

        verify(entityTransaction).begin();
        verify(entityManager).remove(entityManager.merge(report));
        verify(entityTransaction).commit();
    }
}