import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlanetarySystem {
    private String name;
    private List<AstronomicalObject> objects;
    private double size;

    public PlanetarySystem(String name, double size) {
        this.name = name;
        this.size = size;
        this.objects = new ArrayList<>();
    }

    public void addObject(AstronomicalObject obj) {
        objects.add(obj);
    }

    public List<AstronomicalObject> getObjects() {
        return objects;
    }

    public double getSize() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#.00", symbols);
        return Double.valueOf(df.format(size));
    }

    public boolean hasHabitablePlanets() {
        for (AstronomicalObject obj : objects) {
            if (obj instanceof Planet && ((Planet) obj).isHabitable()) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }
}
