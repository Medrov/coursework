import java.util.concurrent.ThreadLocalRandom;
import javax.swing.SwingUtilities;

class Spaceship implements Runnable {
    private String id;
    private boolean hasReturned;
    private boolean colonized;
    private int jumpCapacity;
    private int remainingJumps;
    private int fuel;
    private int modules;
    private boolean isFunctional;
    private Expedition expedition;
    private PlanetarySystem currentSystem;
    private GUI gui;
    private String currentAction;

    public Spaceship(String id, int jumpCapacity, int fuel, int modules, Expedition expedition, GUI gui) {
        this.id = id;
        this.jumpCapacity = jumpCapacity;
        this.remainingJumps = jumpCapacity;
        this.fuel = fuel;
        this.modules = modules;
        this.expedition = expedition;
        this.gui = gui;
        this.hasReturned = false;
        this.colonized = false;
        this.isFunctional = true;
        this.currentSystem = null;
    }

    public String getId() {
        return id;
    }

    public boolean hasReturned() {
        return hasReturned;
    }

    public boolean isColonized() {
        return colonized;
    }

    public void setReturned(boolean hasReturned) {
        this.hasReturned = hasReturned;
    }

    public void setColonized(boolean colonized) {
        this.colonized = colonized;
    }

    public void setTargetSystem(PlanetarySystem targetSystem) {
        this.currentSystem = targetSystem;
    }

    @Override
    public void run() {
        while (!hasReturned && isFunctional && remainingJumps > 0) {
            // Jump to a new system
            if (remainingJumps > 0) {
                currentSystem = expedition.getRandomSystem();
                remainingJumps--;
                gui.appendLog("Spaceship " + id + " jumped to " + currentSystem.getName());
                updateUI("Jumping to " + currentSystem.getName());
                // Explore the system
                exploreSystem();
                if (colonized) {
                    return;
                }
            }
            // Return to base if out of jumps
            if (remainingJumps == 0) {
                returnToBase();
            }
        }
    }

    private void exploreSystem() {
        // Assume ship jumps to a random point in the system
        double distance = ThreadLocalRandom.current().nextDouble(0, currentSystem.getSize());
        for (AstronomicalObject obj : currentSystem.getObjects()) {
            if (obj instanceof Planet) {
                Planet planet = (Planet) obj;
                if (distance == planet.getDistanceFromCenter()) {
                    gui.appendLog("Spaceship " + id + " found planet " + planet.getName());
                    updateUI("Exploring " + planet.getName());
                    if (planet.isHabitable()) {
                        colonized = true;
                        gui.appendLog("Spaceship " + id + " colonized planet " + planet.getName());
                        updateUI("Colonized " + planet.getName());
                        return;
                    }
                }
            }
        }
        // If no habitable planet found, return to base
        returnToBase();
    }

    public void returnToBase() {
        hasReturned = true;
        gui.appendLog("Spaceship " + id + " returned to base.");
        currentAction = "Returning to base";
        updateUI("Returned to base");
    }

    public void landOnPlanet() {
        // Проверка возможности посадки
        if (currentSystem != null && !colonized) {
            for (AstronomicalObject obj : currentSystem.getObjects()) {
                if (obj instanceof Planet) {
                    Planet planet = (Planet) obj;
                    if (planet.isHabitable()) {
                        colonized = true;
                        gui.appendLog("Spaceship " + id + " has landed on planet " + planet.getName());
                        updateUI("Landed on " + planet.getName());
                        return;
                    }
                }
            }
        }
        gui.appendLog("Spaceship " + id + " could not land on any planet.");
        updateUI("Landing failed");
    }

    public void takeOff() {
        if (colonized) {
            colonized = false;
            gui.appendLog("Spaceship " + id + " has taken off.");
            updateUI("Taken off");
        } else {
            gui.appendLog("Spaceship " + id + " is not on any planet to take off.");
            updateUI("Take off failed");
        }
    }

    public void jumpToSystem(PlanetarySystem targetSystem) {
        if (remainingJumps > 0) {
            currentSystem = targetSystem;
            remainingJumps--;
            gui.appendLog("Spaceship " + id + " jumped to " + targetSystem.getName());
            updateUI("Jumped to " + targetSystem.getName());
            // Reset colonized status on jump
            colonized = false;
        } else {
            gui.appendLog("Spaceship " + id + " has no remaining jumps.");
            updateUI("Jump failed");
        }
    }

    private void updateUI(String message) {
        SwingUtilities.invokeLater(() -> {
            gui.appendLog(message);
            gui.updateShipStatus(this);
        });
    }

    public String getStatus() {
        return "ID: " + id + ", Jumps left: " + remainingJumps + ", Fuel: " + fuel + ", Modules: " + modules + ", Action: " + currentAction;
    }
}
