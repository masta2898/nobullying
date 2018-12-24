package ua.sumy.stpp.nobullying.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.sumy.stpp.nobullying.model.Report;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {
    private Report report;

    private ReportService reportService;

    @BeforeEach
    void setup() {
        EntityManager entityManager = mock(EntityManager.class);
        when(entityManager.find(Report.class, 1L)).thenReturn(report);

        reportService = new ReportService();
        reportService.setEntityManager(entityManager);
        reportService.getReportById(1);
        verify(entityManager).flush();
    }

    @Test
    void getReportById() {
    }

    @Test
    void getAllReports() {
    }

    @Test
    void saveReport() {
    }
}