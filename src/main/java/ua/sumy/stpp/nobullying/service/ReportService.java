package ua.sumy.stpp.nobullying.service;


import ua.sumy.stpp.nobullying.model.Model;
import ua.sumy.stpp.nobullying.model.NullModel;
import ua.sumy.stpp.nobullying.model.Report;
import ua.sumy.stpp.nobullying.service.error.BadReportException;
import ua.sumy.stpp.nobullying.service.error.ReportIsAlreadyFinishedException;
import ua.sumy.stpp.nobullying.service.error.ReportIsAlreadyModeratingException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

public class ReportService implements Service {
    private EntityManager entityManager;

    private final Logger log = Logger.getLogger(ReportService.class.getName());

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    Model getReportById(long id) {
        Report report = entityManager.find(Report.class, id);
        return (report != null) ? report : new NullModel();
    }

    List<Report> getAllReports() {
        return null;
    }

    void beginModeratingReport(long id) throws ReportIsAlreadyModeratingException {

    }

    void finishModeratingReport(long id) throws ReportIsAlreadyFinishedException {

    }

    void saveReport(Report report) throws BadReportException {

    }

    void deleteReport(long id) throws BadReportException {

    }
}
