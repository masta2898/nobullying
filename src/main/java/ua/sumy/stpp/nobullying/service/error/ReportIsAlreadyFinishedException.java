package ua.sumy.stpp.nobullying.service.error;

public class ReportIsAlreadyFinishedException extends Exception {
    ReportIsAlreadyFinishedException(String message) {
        super(message);
    }
}
