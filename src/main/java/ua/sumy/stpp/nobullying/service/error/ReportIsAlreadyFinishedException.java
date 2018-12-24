package ua.sumy.stpp.nobullying.service.error;

public class ReportIsAlreadyFinishedException extends Exception {
    public ReportIsAlreadyFinishedException(String message) {
        super(message);
    }
}
