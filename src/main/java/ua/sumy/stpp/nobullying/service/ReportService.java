package ua.sumy.stpp.nobullying.service;


import ua.sumy.stpp.nobullying.model.Model;
import ua.sumy.stpp.nobullying.model.NullModel;
import ua.sumy.stpp.nobullying.model.Report;
import ua.sumy.stpp.nobullying.service.error.BadReportException;
import ua.sumy.stpp.nobullying.service.error.ReportIsAlreadyFinishedException;
import ua.sumy.stpp.nobullying.service.error.ReportIsAlreadyModeratingException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.LinkedList;
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
        Report report = null;
        try {
            report = entityManager.find(Report.class, id);
        } catch (Exception e) {
            log.severe(String.format("Error getting report by id (%d): %s", id, e));
        }
        return (report != null) ? report : new NullModel();
    }

    List<Report> getAllReports() {
        List<Report> reports = null;
        String query = "fetchAllReports";
        try {
            Query namedQuery = entityManager.createNamedQuery(query);
            reports = namedQuery.getResultList();
        } catch (Exception e) {
            log.severe(String.format("Error getting all reports by query (%s): %s", query, e));
        }
        return (reports != null) ? reports : new LinkedList<>();
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
