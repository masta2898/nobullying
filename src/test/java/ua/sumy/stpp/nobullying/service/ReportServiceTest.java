package ua.sumy.stpp.nobullying.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import ua.sumy.stpp.nobullying.model.Model;
import ua.sumy.stpp.nobullying.model.NullModel;
import ua.sumy.stpp.nobullying.model.Report;
import ua.sumy.stpp.nobullying.service.error.BadReportException;
import ua.sumy.stpp.nobullying.service.error.ReportIsAlreadyFinishedException;
import ua.sumy.stpp.nobullying.service.error.ReportIsAlreadyModeratingException;
import ua.sumy.stpp.nobullying.service.error.ReportNotFoundException;

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
        reportService = new ReportService();
        reportService.setEntityManager(entityManager);
    }

    @Test
    void getNotExistingReportById() {
        when(entityManager.find(Report.class, 1L)).thenReturn(null);

        assertThrows(ReportNotFoundException.class, () -> reportService.getReportById(1L));
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

        assertThrows(ReportNotFoundException.class, () -> reportService.beginModeratingReport(1L));
    }

    @Test
    void beginModerateAlreadyModeratingReport() {
        Report report = new Report();
        report.setId(1L);
        report.setState(Report.ProcessingState.MODERATING);

        when(entityManager.find(Report.class, 1L)).thenReturn(report);

        assertThrows(ReportIsAlreadyModeratingException.class, () -> reportService.beginModeratingReport(1L));
    }

    @Test
    void beginModerateAlreadyFinishedReport() {
        Report report = new Report();
        report.setId(1L);
        report.setState(Report.ProcessingState.FINISHED);

        when(entityManager.find(Report.class, 1L)).thenReturn(report);

        assertThrows(ReportIsAlreadyFinishedException.class, () -> reportService.beginModeratingReport(1L));
    }

    @Test
    void beginModerateNewReport() {
        Report report = new Report();
        report.setId(1L);

        EntityTransaction entityTransaction = mock(EntityTransaction.class);

        when(entityManager.find(Report.class, 1L)).thenReturn(report);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);

        verify(entityTransaction).begin();
        verify(entityManager).persist(report);
        verify(entityTransaction).commit();
        verify(entityManager).close();

        assertDoesNotThrow(() -> reportService.beginModeratingReport(1L));
    }

    @Test
    void finishModeratingNotExistingReport() {
        Report report = new Report();
        report.setId(1L);

        when(entityManager.find(Report.class, 1L)).thenReturn(null);

        assertThrows(ReportNotFoundException.class, () -> reportService.finishModeratingReport(1L));
    }

    @Test
    void finishModeratingAlreadyFinishedModeratingReport() {
        Report report = new Report();
        report.setId(1L);
        report.setState(Report.ProcessingState.FINISHED);

        when(entityManager.find(Report.class, 1L)).thenReturn(report);

        assertThrows(ReportIsAlreadyFinishedException.class, () -> reportService.finishModeratingReport(1L));
    }

    @Test
    void finishModeratingReport() {
        Report report = new Report();

        EntityTransaction entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction()).thenReturn(entityTransaction);

        verify(entityTransaction).begin();
        verify(entityManager).persist(report);
        verify(entityTransaction).commit();
        verify(entityManager).close();

        assertDoesNotThrow(() -> reportService.finishModeratingReport(1L));
    }

    @Test
    void saveNullReport() {
        assertThrows(BadReportException.class, () -> reportService.saveReport(null));
    }

    @Test
    void saveEmptyReport() {
        assertThrows(BadReportException.class, () -> reportService.saveReport(new Report()));
    }

    @Test
    void saveExistingReport() {
        Date reportDate = new Date();
        Report report = new Report("Anonymous", "Text", reportDate);
        report.setId(1L);

        when(entityManager.find(Report.class, 1L)).thenReturn(report);

        assertThrows(BadReportException.class, () -> reportService.saveReport(report));
    }

    @Test
    void saveReport() {
        Report report = new Report();
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

        assertThrows(ReportNotFoundException.class, () -> reportService.deleteReport(1L));
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