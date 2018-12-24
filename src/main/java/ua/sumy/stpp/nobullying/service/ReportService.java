package ua.sumy.stpp.nobullying.service;

import ua.sumy.stpp.nobullying.model.Report;
import ua.sumy.stpp.nobullying.service.error.BadReportException;
import ua.sumy.stpp.nobullying.service.error.ReportIsAlreadyFinishedException;
import ua.sumy.stpp.nobullying.service.error.ReportIsAlreadyModeratingException;
import ua.sumy.stpp.nobullying.service.error.ReportNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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

    Report getReportById(long id) throws ReportNotFoundException {
        Report report = null;

        try {
            report = entityManager.find(Report.class, id);
        } catch (Exception e) {
            log.severe(String.format("Error getting report by id (%d): %s.", id, e.getMessage()));
        }

        if (report == null) {
            log.warning(String.format("Report not found by id (%d).", id));
            throw new ReportNotFoundException("Report not found!");
        }

        return report;
    }

    List<Report> getAllReports() {
        List<Report> reports = null;
        String query = "fetchAllReports";
        try {
            Query namedQuery = entityManager.createNamedQuery(query);
            reports = namedQuery.getResultList();
        } catch (Exception e) {
            log.severe(String.format("Error getting all reports by query (%s): %s.", query, e.getMessage()));
        }
        return (reports != null) ? reports : new LinkedList<>();
    }

    void beginModeratingReport(long id) throws BadReportException, ReportNotFoundException,
            ReportIsAlreadyModeratingException, ReportIsAlreadyFinishedException {
        Report report = getReportById(id);
        Report.ProcessingState state = report.getState();

        if (state == Report.ProcessingState.MODERATING) {
            log.warning(String.format("Attempt to moderate already moderating report (%d).", id));
            throw new ReportIsAlreadyModeratingException("Report cannot be moderated twice!");
        }

        if (state == Report.ProcessingState.FINISHED) {
            log.warning(String.format("Attempt to moderate already finished report (%d).", id));
            throw new ReportIsAlreadyFinishedException("Finished report cannot be moderated!");
        }

        saveNewReportState(Report.ProcessingState.MODERATING, report);
    }

    void finishModeratingReport(long id) throws BadReportException, ReportNotFoundException,
            ReportIsAlreadyFinishedException {
        Report report = getReportById(id);
        Report.ProcessingState state = report.getState();

        if (state == Report.ProcessingState.FINISHED) {
            log.warning(String.format("Attempt to moderate already finished report (%d).", id));
            throw new ReportIsAlreadyFinishedException("Finished report cannot be moderated!");
        }

        saveNewReportState(Report.ProcessingState.FINISHED, report);
    }

    void saveReport(Report report) throws BadReportException {
        checkReport(report);

        long id = report.getId();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            entityManager.persist(report);
            entityTransaction.commit();
            log.info(String.format("Saving report (%d).", id));
        } catch (Exception e) {
            log.severe(String.format("Rolling back due to a report (%d) saving error: %s.", id, e.getMessage()));
            entityTransaction.rollback();
            // todo: throw exception about saving error.
        }
    }

    void deleteReport(long id) throws ReportNotFoundException {
        Report report = getReportById(id);
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            entityManager.remove(entityManager.merge(report));
            entityTransaction.commit();
            log.info(String.format("Deleting report (%d).", id));
        } catch (Exception e) {
            log.severe(String.format("Rolling back due to a report (%d) delete error: %s.", id, e.getMessage()));
            entityTransaction.rollback();
            // todo: throw exception about deleting error.
        }
    }

    private void saveNewReportState(Report.ProcessingState state, Report report) throws BadReportException {
        report.setState(state);
        try {
            saveReport(report);
            log.info(String.format("Began moderating report (%d).", report.getId()));
        } catch (BadReportException e) {
            log.severe(String.format("Error saving report new state (%s): %s.", state, e.getMessage()));
            throw e;
            // todo: throw exception about saving error.
        }
    }

    private void checkReport(Report report) throws BadReportException {
        if (report == null) {
            log.severe("Error processing null report.");
            throw new BadReportException("Report is null!");
        }

        if (report.getUsername() == null || report.getText() == null || report.getSentDate() == null) {
            log.severe("Error processing report without id, username or text.");
            throw new BadReportException("Report without id, username or text.");
        }
    }
}
