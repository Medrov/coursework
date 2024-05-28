import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        Expedition expedition = new Expedition();

        // Создаем планетарные системы
        for (int i = 1; i <= 15; i++) {
            PlanetarySystem system = new PlanetarySystem("System-" + i, ThreadLocalRandom.current().nextDouble(10, 100));
            system.addObject(new Star("Star-" + i, 0));
            for (int j = 1; j <= 5; j++) {
                double distance = ThreadLocalRandom.current().nextDouble(1, system.getSize());
                double temperature = ThreadLocalRandom.current().nextDouble(-100, 100);
                boolean hasAtmosphere = ThreadLocalRandom.current().nextBoolean();
                boolean hasOxygen = hasAtmosphere && ThreadLocalRandom.current().nextBoolean();
                boolean hasWater = ThreadLocalRandom.current().nextBoolean();
                boolean hasSolidSurface = ThreadLocalRandom.current().nextBoolean();
                int numOfMoons = ThreadLocalRandom.current().nextInt(0, 6);

                Planet planet = new Planet("Planet-" + i + "-" + j, distance, temperature, hasAtmosphere, hasOxygen, hasWater, hasSolidSurface, numOfMoons);
                system.addObject(planet);
            }
            expedition.addSystem(system);
        }

        // Создаем космические корабли
        for (int i = 1; i <= 5; i++) {
            int jumpCapacity = ThreadLocalRandom.current().nextBoolean() ? 2 : 5;
            int fuel = 50;
            int modules = 12;
            Spaceship spaceship = new Spaceship("Ship-" + i, jumpCapacity, fuel, modules, expedition);
            expedition.addSpaceship(spaceship);
        }

        // Начинаем экспедицию
        expedition.start();
    }
}