package Model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Planet extends AstronomicalObject {
    private double surfaceTemperature;
    private boolean hasAtmosphere;
    private boolean hasOxygen;
    private boolean hasWater;
    private boolean hasSolidSurface;
    private List<Moon> moons;
    private boolean hasGround; // Наличие грунта
    private boolean hasLiquidWater; // Наличие воды
    private boolean hasOxygenAtmosphere; // Наличие атмосферы с кислородом
    private double averageSurfaceTemperature; // Средняя температура поверхности

    public Planet(String name, double distanceFromCenter, double surfaceTemperature, boolean hasAtmosphere, boolean hasOxygen, boolean hasWater, boolean hasSolidSurface, boolean hasGround, boolean hasLiquidWater,
                  boolean hasOxygenAtmosphere, double averageSurfaceTemperature, int moons) {
        super(name, distanceFromCenter);
        this.surfaceTemperature = surfaceTemperature;
        this.hasAtmosphere = hasAtmosphere;
        this.hasOxygen = hasOxygen;
        this.hasWater = hasWater;
        this.hasSolidSurface = hasSolidSurface;
        this.hasGround = hasGround;
        this.hasLiquidWater = hasLiquidWater;
        this.hasOxygenAtmosphere = hasOxygenAtmosphere;
        this.averageSurfaceTemperature = averageSurfaceTemperature;
        this.moons = new ArrayList<>();
    }

    public boolean isHabitable() {
        // Проверяем все условия для пригодности планеты для колонизации
        return hasGround && hasLiquidWater && hasOxygenAtmosphere &&
                averageSurfaceTemperature >= 0 && averageSurfaceTemperature <= 25;
    }


    @Override
    public boolean isStar() {
        return false;
    }

    public double getSurfaceTemperature() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#.00", symbols);
        return Double.valueOf(df.format(surfaceTemperature));
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

    public List<Moon> getMoons() {
        return moons;
    }

    public void addMoon(Moon moon) {
        moons.add(moon);
    }

    public boolean hasGround() {
        return hasGround;
    }

    public void setGround(boolean hasGround) {
        this.hasGround = hasGround;
    }

    public boolean hasLiquidWater() {
        return hasLiquidWater;
    }

    public void setLiquidWater(boolean hasLiquidWater) {
        this.hasLiquidWater = hasLiquidWater;
    }

    public boolean hasOxygenAtmosphere() {
        return hasOxygenAtmosphere;
    }

    public void setOxygenAtmosphere(boolean hasOxygenAtmosphere) {
        this.hasOxygenAtmosphere = hasOxygenAtmosphere;
    }

    public double getAverageSurfaceTemperature() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#.00", symbols);
        return Double.valueOf(df.format(averageSurfaceTemperature));
    }

    public void setAverageSurfaceTemperature(double averageSurfaceTemperature) {
        this.averageSurfaceTemperature = averageSurfaceTemperature;
    }

    public int getNumOfMoons() {
        return moons.size();
    }
}