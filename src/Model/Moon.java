package Model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static Util.Utils.getNormalDouble;

public class Moon extends AstronomicalObject {
    private Planet planet;
    private double surfaceTemperature;
    private boolean hasAtmosphere;
    private boolean hasOxygen;
    private boolean hasWater;
    private boolean hasSolidSurface;

    public Moon(String name, double distanceFromCenter, double surfaceTemperature, boolean hasAtmosphere, boolean hasOxygen, boolean hasWater, boolean hasSolidSurface) {
        super(name, distanceFromCenter);
        this.surfaceTemperature = surfaceTemperature;
        this.hasAtmosphere = hasAtmosphere;
        this.hasOxygen = hasOxygen;
        this.hasWater = hasWater;
        this.hasSolidSurface = hasSolidSurface;
    }

    public Planet getPlanet() {
        return planet;
    }

    @Override
    public boolean isStar() {
        return false;
    }

    @Override
    public double getSurfaceTemperature() {
        return getNormalDouble(surfaceTemperature);
    }

    @Override
    public boolean hasAtmosphere() {
        return hasAtmosphere;
    }

    @Override
    public boolean hasOxygen() {
        return hasOxygen;
    }

    @Override
    public boolean hasWater() {
        return hasWater;
    }

    @Override
    public boolean hasSolidSurface() {
        return hasSolidSurface;
    }
}