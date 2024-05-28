abstract class AstronomicalObject {
    protected String name;
    protected double distanceFromCenter; // in AU

    public AstronomicalObject(String name, double distanceFromCenter) {
        this.name = name;
        this.distanceFromCenter = distanceFromCenter;
    }

    public String getName() {
        return name;
    }

    public double getDistanceFromCenter() {
        return distanceFromCenter;
    }
}
