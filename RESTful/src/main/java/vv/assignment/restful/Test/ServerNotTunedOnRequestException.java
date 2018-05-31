package vv.assignment.restful.Test;

public class ServerNotTunedOnRequestException extends Exception {
    public ServerNotTunedOnRequestException(String errorMsg){
        super(errorMsg);
    }
}
