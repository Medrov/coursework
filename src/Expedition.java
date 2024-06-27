import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Expedition {
    private List<PlanetarySystem> systems;
    private List<Spaceship> spaceships;

    public Expedition() {
        this.systems = new ArrayList<>();
        this.spaceships = new ArrayList<>();
    }

    public void addSystem(PlanetarySystem system) {
        systems.add(system);
    }

    public void addSpaceship(Spaceship spaceship) {
        spaceships.add(spaceship);
    }

    public PlanetarySystem getRandomSystem() {
        return systems.get(ThreadLocalRandom.current().nextInt(systems.size()));
    }

    public void start() {
        ExecutorService executor = Executors.newFixedThreadPool(spaceships.size());
        for (Spaceship spaceship : spaceships) {
            executor.execute(spaceship);
        }
        executor.shutdown();
    }
}