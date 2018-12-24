package ua.sumy.stpp.nobullying.service.error;

public class UserIsAlreadyRegisteredException extends Exception {
    public UserIsAlreadyRegisteredException(String message) {
        super(message);
    }
}
