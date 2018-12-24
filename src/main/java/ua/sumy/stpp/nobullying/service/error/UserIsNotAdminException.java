package ua.sumy.stpp.nobullying.service.error;

public class UserIsNotAdminException extends Exception {
    public UserIsNotAdminException(String message) {
        super(message);
    }
}
