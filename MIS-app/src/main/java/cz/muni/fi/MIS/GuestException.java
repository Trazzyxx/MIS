package cz.muni.fi.MIS;

/**
 * Created by V.Mecko on 27.3.2017.
 */
public class GuestException extends Exception {

    public GuestException(String message,Throwable cause){
        super(message,cause);
    }

    public GuestException(String message) {super(message);}
}