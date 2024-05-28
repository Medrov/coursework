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

    public Spaceship(String id, int jumpCapacity, int fuel, int modules, Expedition expedition) {
        this.id = id;
        this.jumpCapacity = jumpCapacity;
        this.remainingJumps = jumpCapacity;
        this.fuel = fuel;
        this.modules = modules;
        this.expedition = expedition;
        this.hasReturned = false;
        this.colonized = false;
        this.isFunctional = true;
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

    public void run() {
        while (!hasReturned && isFunctional && remainingJumps > 0) {
            // Jump to a new system
            if (remainingJumps > 0) {
                currentSystem = expedition.getRandomSystem();
                remainingJumps--;
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
                    System.out.println("Spaceship " + id + " found planet " + planet.getName());
                    if (planet.isHabitable()) {
                        colonized = true;
                        System.out.println("Spaceship " + id + " colonized planet " + planet.getName());
                        return;
                    }
                }
            }
        }
        // If no habitable planet found, return to base
        returnToBase();
    }

    private void returnToBase() {
        hasReturned = true;
        System.out.println("Spaceship " + id + " returned to base.");
    }
}