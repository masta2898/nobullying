package ua.sumy.stpp.nobullying.service;


import ua.sumy.stpp.nobullying.model.Model;
import ua.sumy.stpp.nobullying.model.NullModel;
import ua.sumy.stpp.nobullying.model.Report;
import ua.sumy.stpp.nobullying.service.error.BadReportException;
import ua.sumy.stpp.nobullying.service.error.ReportIsAlreadyFinishedException;
import ua.sumy.stpp.nobullying.service.error.ReportIsAlreadyModeratingException;
import ua.sumy.stpp.nobullying.service.error.ReportNotFoundException;

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

    void beginModeratingReport(long id) throws BadReportException, ReportNotFoundException,
            ReportIsAlreadyModeratingException, ReportIsAlreadyFinishedException {
        if (getReportById(id).isNull()) {
            log.severe(String.format("Error beginning moderating report: no report found by id %d", id));
            throw new ReportNotFoundException("Report doesn't exist!");
        }

        Report report = (Report) getReportById(id);
        Report.ProcessingState state = report.getState();

        if (state == Report.ProcessingState.MODERATING) {
            log.warning(String.format("Attempt to moderate already moderating report (%d).", id));
            throw new ReportIsAlreadyModeratingException("Report cannot be moderated twice!");
        }

        if (state == Report.ProcessingState.FINISHED) {
            log.warning(String.format("Attempt to moderate already finished report (%d)", id));
            throw new ReportIsAlreadyFinishedException("Finished report cannot be moderated!");
        }

        saveNewReportState(Report.ProcessingState.MODERATING, report);
    }

    void finishModeratingReport(long id) throws ReportIsAlreadyFinishedException {

        saveNewReportState(Report.ProcessingState.FINISHED, report);
    }

    void saveReport(Report report) throws BadReportException {

    }

    void deleteReport(long id) throws BadReportException {

    }

    private void saveNewReportState(Report.ProcessingState state, Report report) throws BadReportException {
        report.setState(state);
        try {
            saveReport(report);
            log.info(String.format("Began moderating report (%d)", report.getId()));
        } catch (BadReportException e) {
            log.severe(String.format("Error saving report new state (%s): %s", state, e));
            throw e;
        }
    }
}
