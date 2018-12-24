package ua.sumy.stpp.nobullying.service.error;

public class ReportIsAlreadyModeratingException extends Exception {
    ReportIsAlreadyModeratingException(String message) {
        super(message);
    }
}
