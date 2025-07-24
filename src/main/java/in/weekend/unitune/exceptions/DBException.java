package in.weekend.unitune.exceptions;

public class DBException extends RuntimeException {
    public DBException(String message, Throwable e){
        super(message, e);
    }

    public DBException(String message){
        super(message);
    }
}
