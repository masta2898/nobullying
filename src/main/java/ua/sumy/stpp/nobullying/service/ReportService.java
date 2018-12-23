package ua.sumy.stpp.nobullying.service;


import ua.sumy.stpp.nobullying.model.Report;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;
import java.util.logging.Logger;

public class ReportService {
    private EntityManager entityManager;

    private final Logger log = Logger.getLogger(ReportService.class.getName());

    ReportService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    Report getReportById(long id) {
        return null;
    }

    List<Report> getAllReports() {
        return null;
    }

    void saveReport(Report report) {

    }
}
