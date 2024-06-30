package Model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static Util.Utils.getNormalDouble;

public abstract class AstronomicalObject {
    protected String name;
    protected double distanceFromCenter; // в а.е.

    public AstronomicalObject(String name, double distanceFromCenter) {
        this.name = name;
        this.distanceFromCenter = distanceFromCenter;
    }

    public String getName() {
        return name;
    }

    public double getDistanceFromCenter() {
        return getNormalDouble(distanceFromCenter);
    }
    public abstract boolean isStar();
    public abstract double getSurfaceTemperature();
    public abstract boolean hasAtmosphere();
    public abstract boolean hasOxygen();
    public abstract boolean hasWater();
    public abstract boolean hasSolidSurface();
}