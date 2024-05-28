import java.util.ArrayList;
import java.util.List;

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
        return size;
    }

    public String getName() {
        return name;
    }
}
