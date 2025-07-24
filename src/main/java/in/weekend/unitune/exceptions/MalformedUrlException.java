package in.weekend.unitune.exceptions;

public class MalformedUrlException extends RuntimeException {
    public MalformedUrlException(String message, Throwable e){
        super(message, e);
    }

    public MalformedUrlException(String message){
        super(message);
    }
}
