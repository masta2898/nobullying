package ua.sumy.stpp.nobullying.service;

import ua.sumy.stpp.nobullying.model.Report;
import ua.sumy.stpp.nobullying.service.error.BadOperationException;
import ua.sumy.stpp.nobullying.service.error.BadParametersException;
import ua.sumy.stpp.nobullying.service.error.ModelNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class ReportService extends Service {
    private final Logger log = Logger.getLogger(ReportService.class.getName());

    ReportService(EntityManager entityManager) {
        super(entityManager);
    }

    Report getReportById(long id) throws ModelNotFoundException {
        return getModelById(Report.class, id);
    }

    List<Report> getAllReports() {
        return getAllModels("fetchAllReports");
    }

    void beginModeratingReport(long id) throws BadParametersException, ModelNotFoundException, BadOperationException {
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

    void finishModeratingReport(long id) throws BadParametersException, ModelNotFoundException, BadOperationException {
        Report report = getReportById(id);
        Report.ProcessingState state = report.getState();

        if (state == Report.ProcessingState.FINISHED) {
            log.warning(String.format("Attempt to moderate already finished report (%d).", id));
            throw new BadOperationException("Finished report cannot be moderated!");
        }

        saveNewReportState(Report.ProcessingState.FINISHED, report);
    }

    void saveReport(Report report) throws BadParametersException {
        checkParameters(report);

        if (report.getUsername() == null || report.getText() == null || report.getSentDate() == null) {
            log.severe("Error saving report without username, text or sent date.");
            throw new BadParametersException("Report has null fields!");
        }

        saveModel(report);
    }

    void deleteReport(long id) throws ModelNotFoundException, BadParametersException {
        Report report = getReportById(id);
        deleteModel(report);
    }

    private void saveNewReportState(Report.ProcessingState state, Report report) throws BadParametersException {
        report.setState(state);
        try {
            saveModel(report);
            log.info(String.format("Began moderating report (%d).", report.getId()));
        } catch (BadParametersException e) {
            log.severe(String.format("Error saving report new state (%s): %s.", state, e.getMessage()));
            throw e;
        }
    }
}
