package vv.fh.rosenheim.Transport;

public class DrivenDistance {

    double distanceInMeters;

    public DrivenDistance(double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    public double getDistanceInMeters() {
        return distanceInMeters;
    }

    public void setDistanceInMeters(double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    public double getDistanceInKilometers(){
           return (distanceInMeters/1000);
    }

    public void addDrivenDistance(double drivenDistance){
        this.distanceInMeters += drivenDistance;
    }

    @Override
    public String toString() {
        return "DrivenDistance{" +
                "distanceInMeters=" + distanceInMeters +
                '}';
    }
}
