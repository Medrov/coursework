class Planet extends AstronomicalObject {
    private double surfaceTemperature;
    private boolean hasAtmosphere;
    private boolean hasOxygen;
    private boolean hasWater;
    private boolean hasSolidSurface;
    private int numOfMoons;

    public Planet(String name, double distanceFromCenter, double surfaceTemperature, boolean hasAtmosphere, boolean hasOxygen, boolean hasWater, boolean hasSolidSurface, int numOfMoons) {
        super(name, distanceFromCenter);
        this.surfaceTemperature = surfaceTemperature;
        this.hasAtmosphere = hasAtmosphere;
        this.hasOxygen = hasOxygen;
        this.hasWater = hasWater;
        this.hasSolidSurface = hasSolidSurface;
        this.numOfMoons = numOfMoons;
    }

    public boolean isHabitable() {
        return hasSolidSurface && surfaceTemperature >= 0 && surfaceTemperature <= 25 && hasWater && hasAtmosphere && hasOxygen;
    }

    public double getSurfaceTemperature() {
        return surfaceTemperature;
    }

    public boolean hasAtmosphere() {
        return hasAtmosphere;
    }

    public boolean hasOxygen() {
        return hasOxygen;
    }

    public boolean hasWater() {
        return hasWater;
    }

    public boolean hasSolidSurface() {
        return hasSolidSurface;
    }

    public int getNumOfMoons() {
        return numOfMoons;
    }
}
