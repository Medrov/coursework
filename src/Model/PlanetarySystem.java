package Model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import static Util.Utils.getNormalDouble;

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
        return getNormalDouble(size);
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

    public static List<PlanetarySystem> generatePlanetarySystems() {
        List<PlanetarySystem> systems = new ArrayList<>();
        boolean allSystemsValid = false;

        while (!allSystemsValid) {
            systems.clear(); // Очищаем список систем перед генерацией новой

            // Генерируем 15 планетарных систем
            for (int i = 1; i <= 15; i++) {
                PlanetarySystem system = new PlanetarySystem("System-" + i, ThreadLocalRandom.current().nextDouble(10, 100));
                Star centerStar = new Star("Star-" + i, 0);
                system.addObject(centerStar);

                boolean systemValid = true; // Флаг для проверки условий компактности и колонизации
                boolean hasHabitablePlanet = true;

                // Генерируем до 4 планет в каждой системе
                for (int j = 1; j <= 4; j++) {
                    double distance = ThreadLocalRandom.current().nextDouble(1, system.getSize());
                    double temperature = ThreadLocalRandom.current().nextDouble(-100, 100);
                    boolean hasAtmosphere = ThreadLocalRandom.current().nextBoolean();
                    boolean hasOxygen = hasAtmosphere && ThreadLocalRandom.current().nextBoolean();
                    boolean hasWater = ThreadLocalRandom.current().nextBoolean();
                    boolean hasSolidSurface = ThreadLocalRandom.current().nextBoolean();
                    int numOfMoons = ThreadLocalRandom.current().nextInt(0, 6);

                    // Проверяем компактность планеты
                    if (distance < 10 || distance > 100) {
                        systemValid = false;
                        break; // Не добавляем планету в систему, если не удовлетворяет условию компактности
                    }

                    boolean hasGround = ThreadLocalRandom.current().nextBoolean();
                    boolean hasLiquidWater = ThreadLocalRandom.current().nextBoolean();
                    boolean hasOxygenAtmosphere = hasAtmosphere && ThreadLocalRandom.current().nextBoolean();
                    double averageSurfaceTemperature = ThreadLocalRandom.current().nextDouble(0, 25);

                    Planet planet = new Planet("Planet-" + i + "-" + j, distance, temperature, hasAtmosphere, hasOxygen, hasWater,
                            hasSolidSurface, hasGround, hasLiquidWater, hasOxygenAtmosphere, averageSurfaceTemperature, numOfMoons);
                    system.addObject(planet);

                    // Добавление спутников
                    for (int k = 1; k <= numOfMoons; k++) {
                        double distanceMoon = ThreadLocalRandom.current().nextDouble(1, distance / 10); // Дистанция луны от планеты
                        double temperatureMoon = ThreadLocalRandom.current().nextDouble(-10, 10);
                        boolean hasAtmosphereMoon = ThreadLocalRandom.current().nextBoolean();
                        boolean hasOxygenMoon = hasAtmosphereMoon && ThreadLocalRandom.current().nextBoolean();
                        boolean hasWaterMoon = ThreadLocalRandom.current().nextBoolean();
                        boolean hasSolidSurfaceMoon = ThreadLocalRandom.current().nextBoolean();
                        Moon moon = new Moon("Moon-" + i + "-" + j + "-" + k, distanceMoon, temperatureMoon, hasAtmosphereMoon, hasOxygenMoon, hasWaterMoon, hasSolidSurfaceMoon);
                        planet.addMoon(moon);
                    }

                    // Проверяем, есть ли хотя бы одна пригодная для колонизации планета
                    if (!hasHabitablePlanet && planet.isHabitable()) {
                        hasHabitablePlanet = true;
                    }
                }

                // Если система удовлетворяет условиям, добавляем её в список систем
                if (systemValid && hasHabitablePlanet) {
                    systems.add(system);
                } else {
                    // Если система не удовлетворяет условиям, генерируем заново
                    systems.clear();
                    break;
                }
            }

            // Проверяем, что сгенерированы все 15 систем, удовлетворяющих условиям
            if (systems.size() == 15) {
                allSystemsValid = true;
            }
        }

        return systems;
    }

}
