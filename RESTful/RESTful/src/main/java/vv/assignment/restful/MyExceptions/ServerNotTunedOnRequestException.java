package vv.assignment.restful.MyExceptions;

public class ServerNotTunedOnRequestException extends Exception {
    public ServerNotTunedOnRequestException(String message){
        super(message);
    }
}
