package ua.sumy.stpp.nobullying.model;

public class NullModel implements Model {
    public boolean isNull() {
        return true;
    }

    public long getId() {
        return 0L;
    }
}
