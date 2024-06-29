import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpaceMissionManager {

    private List<Spaceship> spaceships;
    private ExecutorService executorService;
    private int successfulColonies;
    private int lostShips;

    public SpaceMissionManager(int numSpaceships, int jumpCapacity, int fuel, int maxModulesPerShip, Expedition expedition, GUI gui) {
        this.spaceships = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(numSpaceships);
        this.successfulColonies = 0;
        this.lostShips = 0;

        for (int i = 1; i <= numSpaceships; i++) {
            Spaceship spaceship = new Spaceship("Spaceship-" + i, jumpCapacity, fuel, maxModulesPerShip, expedition, gui);
            spaceships.add(spaceship);
        }
    }

    public void startMission() {
        for (Spaceship spaceship : spaceships) {
            executorService.submit(spaceship);
        }
    }

    public void awaitCompletion() {
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            // Wait for all spaceships to finish their missions
        }
        // After all spaceships are done, determine mission outcome
        determineMissionOutcome();
    }

    private void determineMissionOutcome() {
        int returningShips = 0;
        int colonyCount = 0;
        for (Spaceship spaceship : spaceships) {
            if (spaceship.isReturned()) {
                returningShips++;
            }
            if (spaceship.isColonized()) {
                colonyCount++;
            }
            if (!spaceship.isReturned() && !spaceship.isColonized()) {
                lostShips++;
            }
        }

        if (colonyCount > 0) {
            successfulColonies++;
            System.out.println("Mission successful. Colony established on " + colonyCount + " planet(s).");
        } else if (returningShips > 3 && colonyCount == 0) {
            System.out.println("Mission satisfactory. " + returningShips + " ships returned without colonies.");
        } else {
            System.out.println("Mission failed. More than three ships lost without establishing a colony.");
        }
        System.out.println("Statistics: Successful colonies: " + successfulColonies + ", Lost ships: " + lostShips);
    }

    public static void main(String[] args) {
        Expedition expedition = new Expedition(); // Assuming Expedition class is defined
        GUI gui = new GUI(); // Assuming GUI class is defined

        SpaceMissionManager missionManager = new SpaceMissionManager(5, 10, 100, 5, expedition, gui);
        missionManager.startMission();
        missionManager.awaitCompletion();
    }
}
