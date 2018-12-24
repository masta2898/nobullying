package ua.sumy.stpp.nobullying.service.error;

public class UserIsAlreadyAdminException extends Exception {
    public UserIsAlreadyAdminException(String message) {
        super(message);
    }
}
