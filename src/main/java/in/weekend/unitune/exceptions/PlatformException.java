package in.weekend.unitune.exceptions;

public class PlatformException extends RuntimeException {
    public PlatformException(){
        super();
    }
    public PlatformException(String msg){
        super(msg);
    }
    public PlatformException(Throwable e){
        super(e);
    }
    public PlatformException(String msg, Throwable e){
        super(msg, e);
    }
}
