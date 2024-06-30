package Model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

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
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#.00", symbols);
        return Double.valueOf(df.format(distanceFromCenter));
    }
    public abstract boolean isStar();
    public abstract double getSurfaceTemperature();
    public abstract boolean hasAtmosphere();
    public abstract boolean hasOxygen();
    public abstract boolean hasWater();
    public abstract boolean hasSolidSurface();
}