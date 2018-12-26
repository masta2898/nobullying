package ua.sumy.stpp.nobullying.service;

import ua.sumy.stpp.nobullying.model.Report;
import ua.sumy.stpp.nobullying.service.error.BadOperationException;
import ua.sumy.stpp.nobullying.service.error.BadParametersException;
import ua.sumy.stpp.nobullying.service.error.ModelNotFoundException;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.logging.Logger;

class ReportService extends Service {
    private final EntityManager entityManager;
    private final Logger log = Logger.getLogger(ReportService.class.getName());

    ReportService(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
    }

    Report getReportById(long id) throws ModelNotFoundException {
        return getModelById(Report.class, id);
    }

    List<Report> getAllReports() {
        return getAllModels("fetchAllReports");
    }

    void beginModeratingReport(long id) throws ModelNotFoundException, BadOperationException {
        Report report = getReportById(id);
        Report.ProcessingState state = report.getState();

        if (state == Report.ProcessingState.MODERATING) {
            log.warning(String.format("Attempt to moderate already moderating report (%d).", id));
            throw new BadOperationException("Report cannot be moderated twice!");
        }

        if (state == Report.ProcessingState.FINISHED) {
            log.warning(String.format("Attempt to moderate already finished report (%d).", id));
            throw new BadOperationException("Finished report cannot be moderated!");
        }

        saveNewReportState(Report.ProcessingState.MODERATING, report);
    }

    void finishModeratingReport(long id) throws ModelNotFoundException, BadOperationException {
        Report report = getReportById(id);
        Report.ProcessingState state = report.getState();

        if (state == Report.ProcessingState.FINISHED) {
            log.warning(String.format("Attempt to moderate already finished report (%d).", id));
            throw new BadOperationException("Finished report cannot be moderated!");
        }

        saveNewReportState(Report.ProcessingState.FINISHED, report);
    }

    void saveReport(Report report) throws BadParametersException {
        if (anyIsNull(report)) {
            log.warning("Attempt to save null report.");
            throw new BadParametersException("Saving null report permitted.");
        }

        if (anyIsNull(report.getUsername(), report.getText(), report.getSentDate()) ||
                anyIsEmpty(report.getUsername(), report.getText())) {
            log.warning("Attempt to save report without username, text or sent date.");
            throw new BadParametersException("Saving report without username, text or sent date permitted.");
        }

        saveModel(report);
    }

    void deleteReport(long id) throws ModelNotFoundException {
        Report report = getReportById(id);
        try {
            deleteModel(report);
        } catch (BadParametersException e) {
            log.severe(String.format("Error deleting report due it's null: %s", e.getMessage()));
            // todo: throw exception about error deleting report.
        } catch (BadOperationException e) {
            log.severe(String.format("Error deleting report due it doesn't exist by id (%d): %s.", id, e.getMessage()));
        }
    }

    private void saveNewReportState(Report.ProcessingState state, Report report) {
        report.setState(state);
        long id = report.getId();
        try {
            saveModel(report);
            log.info(String.format("Began moderating report (%d).", id));
        } catch (BadParametersException e) {
            log.severe(String.format("Error saving report (%d) new state (%s): %s.", id, state, e.getMessage()));
        }
    }
}
