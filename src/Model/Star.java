package Model;

public class Star extends AstronomicalObject {
    public Star(String name, double distanceFromCenter) {
        super(name, distanceFromCenter);
    }

    @Override
    public boolean isStar() {
        return true;
    }

    @Override
    public double getSurfaceTemperature() {
        throw new UnsupportedOperationException("Stars do not have surface temperature characteristic.");
    }

    @Override
    public boolean hasAtmosphere() {
        throw new UnsupportedOperationException("Stars do not have atmosphere.");
    }

    @Override
    public boolean hasOxygen() {
        throw new UnsupportedOperationException("Stars do not have oxygen.");
    }

    @Override
    public boolean hasWater() {
        throw new UnsupportedOperationException("Stars do not have water.");
    }

    @Override
    public boolean hasSolidSurface() {
        throw new UnsupportedOperationException("Stars do not have solid surface.");
    }
}
