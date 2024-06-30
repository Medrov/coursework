import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.SwingUtilities;
import Module.*;
import Module.Scan.LargeScanningModule;
import Module.Scan.MediumScanningModule;
import Module.Scan.SmallScanningModule;

class Spaceship implements Runnable {
    public String id;
    public boolean hasReturned;
    public boolean colonized;
    public int jumpCapacity;
    public int remainingJumps;
    public int fuel;
    public int maxModules;
    public int usedModules;
    public boolean isFunctional;
    public Expedition expedition;
    public PlanetarySystem currentSystem;
    public PlanetarySystem targetSystem;
    public GUI gui;
    public String currentAction;

    public List<SpaceshipModule> installedModules;

    public Spaceship(String id, int jumpCapacity, int fuel, int maxModules, Expedition expedition, GUI gui) {
        this.id = id;
        this.jumpCapacity = jumpCapacity;
        this.remainingJumps = jumpCapacity;
        this.fuel = fuel;
        this.maxModules = maxModules;
        this.usedModules = 0;
        this.expedition = expedition;
        this.gui = gui;
        this.hasReturned = false;
        this.colonized = false;
        this.isFunctional = true;
        this.currentSystem = null;
        this.targetSystem = null;
        this.currentAction = "";
        this.installedModules = new ArrayList<>();

        addMandatoryModules();
        fillWithRandomModules();
    }

    private static final Class<? extends SpaceshipModule>[] AVAILABLE_MODULES = new Class[] {
            CommunicationModule.class,
            FuelTankModule.class,
            JumpEngineModule.class,
            LivingModule.class,
            ManeuverEngineModule.class,
            RepairModule.class,
            SolarPanelModule.class,
            LargeScanningModule.class,
            MediumScanningModule.class,
            SmallScanningModule.class,
    };



    private void addMandatoryModules() {
        addModule(createModuleInstance(LivingModule.class));
        addModule(createModuleInstance(CommunicationModule.class));
        addModule(createModuleInstance(SolarPanelModule.class));
        addModule(createModuleInstance(FuelTankModule.class));
        addModule(createModuleInstance(ManeuverEngineModule.class));
        addModule(createModuleInstance(JumpEngineModule.class));
    }

    private void fillWithRandomModules() {
        while (usedModules < maxModules) {
            Class<? extends SpaceshipModule> moduleClass = AVAILABLE_MODULES[ThreadLocalRandom.current().nextInt(AVAILABLE_MODULES.length)];
            addModule(createModuleInstance(moduleClass));
        }
    }

    private SpaceshipModule createModuleInstance(Class<? extends SpaceshipModule> moduleClass) {
        try {
            if (moduleClass == CommunicationModule.class) {
                return new CommunicationModule();
            } else if (moduleClass == FuelTankModule.class) {
                return new FuelTankModule(1);
            } else if (moduleClass == JumpEngineModule.class) {
                return new JumpEngineModule(1, 1);
            } else if (moduleClass == LivingModule.class) {
                return new LivingModule();
            } else if (moduleClass == ManeuverEngineModule.class) {
                return new ManeuverEngineModule(1, 10   , 1);
            } else if (moduleClass == RepairModule.class) {
                return new RepairModule();
            } else if (moduleClass == SolarPanelModule.class) {
                return new SolarPanelModule();
            } else if (moduleClass == LargeScanningModule.class) {
                return new LargeScanningModule();
            } else if (moduleClass == MediumScanningModule.class) {
                return new MediumScanningModule();
            } else if (moduleClass == SmallScanningModule.class) {
                return new SmallScanningModule();
            } else {
                throw new IllegalArgumentException("Unsupported module class: " + moduleClass.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getId() {
        return id;
    }

    public boolean isReturned() {
        return hasReturned;
    }

    public boolean isColonized() {
        return colonized;
    }

    public PlanetarySystem getTargetSystem() {
        return targetSystem;
    }

    public void setReturned(boolean hasReturned) {
        this.hasReturned = hasReturned;
    }

    public void setColonized(boolean colonized) {
        this.colonized = colonized;
    }

    public void setTargetSystem(PlanetarySystem targetSystem) {
        this.targetSystem = targetSystem;
    }

    public boolean addModule(SpaceshipModule module) {
        if (usedModules + module.getSlotsOccupied() <= maxModules) {
            installedModules.add(module);
            usedModules += module.getSlotsOccupied();
            if (module instanceof FuelTankModule) {
                fuel += ((FuelTankModule) module).getFuelCapacity();
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean hasModule(Class<? extends SpaceshipModule> moduleClass) {
        return installedModules.stream().anyMatch(module -> moduleClass.isInstance(module));
    }

    @Override
    public void run() {
        while (!hasReturned && isFunctional && remainingJumps > 0) {
            checkAndRepairModules();
            // Jump to a new system
            if (remainingJumps > 0 && isFunctional) {
                remainingJumps--;
                gui.appendLog("Spaceship " + id + " jumped to " + targetSystem.getName());
                updateUI("Jumping to " + targetSystem.getName());
                // Explore the system
                exploreSystem();
                if (colonized) {
                    return;
                }
            }
            // Return to base if out of jumps
            if (remainingJumps == 0 && isFunctional) {
                //returnToBase();
            }
            // Sleep for 1 second to simulate passage of time
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //gui.appendLog("Spaceship " + id + " has ended its mission.");
    }

    private void exploreSystem() {
        double distance = ThreadLocalRandom.current().nextDouble(0, currentSystem.getSize());
        boolean habitablePlanetFound = false;
        for (AstronomicalObject obj : currentSystem.getObjects()) {
            if (obj instanceof Planet) {
                Planet planet = (Planet) obj;
                if (distance >= planet.getDistanceFromCenter()) {
                    gui.appendLog("Spaceship " + id + " found planet " + planet.getName());
                    updateUI("Exploring " + planet.getName());

                    // Scan planet logic
                    if (scanPlanet(planet)) {
                        colonized = true;
                        gui.appendLog("Spaceship " + id + " colonized planet " + planet.getName());
                        updateUI("Colonized " + planet.getName());
                        currentAction = "Colonized";
                        return;
                    }
                }
            }
        }
        if (!habitablePlanetFound) {
            //returnToBase();
        }
    }

    private boolean scanPlanet(Planet planet) {
        if (hasModule(SolarPanelModule.class)) {
            for (SpaceshipModule module : installedModules) {
                if (module instanceof ScanningModule) {
                    ScanningModule scanningModule = (ScanningModule) module;
                    int scanCapacity = scanningModule.getScanCapacity();
                    boolean habitable = planet.isHabitable();
                    // Scan logic based on scanCapacity
                    // For simplicity, assume we scan randomly selected characteristics here
                    if (habitable) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void checkAndRepairModules() {
        for (SpaceshipModule module : installedModules) {
            // 2% chance to fail each second
            if (!(module instanceof FuelTankModule) && !(module instanceof LivingModule) && !(module instanceof RepairModule)) {
                double failureChance = ThreadLocalRandom.current().nextDouble(0, 1);
                if (failureChance <= 0.02) {
                    // SpaceshipModule failed, attempt repair
                    boolean repaired = attemptRepair(module);
                    if (!repaired) {
                        handleModuleFailure(module);
                    }
                }
            }
        }
    }

    private boolean attemptRepair(SpaceshipModule module) {
        if (hasModule(RepairModule.class)) {
            gui.appendLog("Spaceship " + id + " is repairing module: " + module.getDescription());
            try {
                Thread.sleep(3000); // Repair time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gui.appendLog("Spaceship " + id + " has repaired module: " + module.getDescription());
            return true;
        }
        return false;
    }

    private void handleModuleFailure(SpaceshipModule module) {
        if (module instanceof CommunicationModule) {
            gui.appendLog("Spaceship " + id + " has lost communication with command. Mission lost.");
            currentAction = "Lost communication";
            isFunctional = false;
        } else if (module instanceof SolarPanelModule) {
            gui.appendLog("Spaceship " + id + " has lost energy. Mission lost.");
            currentAction = "Lost energy";
            isFunctional = false;
        } else if (module instanceof LivingModule) {
            gui.appendLog("Spaceship " + id + " has no more crew members. Mission lost.");
            currentAction = "Lost crew";
            isFunctional = false;
        } else if (module instanceof RepairModule) {
            gui.appendLog("Spaceship " + id + " cannot perform repairs. Mission lost.");
            currentAction = "Repair failure";
            isFunctional = false;
        } else if (module instanceof ScanningModule) {
            gui.appendLog("Spaceship " + id + " cannot scan planets. Landing will be blind.");
            currentAction = "No scanning";
        } else if (module instanceof ManeuverEngineModule) {
            gui.appendLog("Spaceship " + id + " cannot maneuver in planetary system. Mission compromised.");
            currentAction = "No maneuvering";
        } else if (module instanceof JumpEngineModule) {
            if (remainingJumps == 0) {
                gui.appendLog("Spaceship " + id + " has no remaining jumps. Mission lost.");
                currentAction = "No jumps left";
                isFunctional = false;
            } else {
                gui.appendLog("Spaceship " + id + " cannot perform jumps. Mission compromised.");
                currentAction = "Jump failure";
            }
        } else if (module instanceof FuelTankModule) {
            if (fuel == 0 && remainingJumps == 0) {
                gui.appendLog("Spaceship " + id + " has no fuel and cannot return to base. Mission lost.");
                currentAction = "Fuel depleted";
                isFunctional = false;
            } else if (fuel == 0) {
                gui.appendLog("Spaceship " + id + " has no fuel. Movement halted.");
                currentAction = "No fuel";
                isFunctional = false;
            } else {
                gui.appendLog("Spaceship " + id + " has lost its fuel tank. Mission compromised.");
                currentAction = "Fuel tank failure";
            }
        }
    }
    public void startExpedition() {
        currentAction = "Started expedition";
    }
    public void returnToBase() {
        setReturned(true);
        gui.appendLog("Spaceship " + id + " returned to base.");
        currentAction = "Returning to base";
        updateUI("Returned to base");
    }

    public void landOnPlanet(Spaceship spaceship) {
        System.out.println("1. " + spaceship.currentSystem + " 2. " + !colonized + " 3. " + fuel);
        if (currentSystem != null && !colonized && fuel >= 10) {

            for (AstronomicalObject obj : currentSystem.getObjects()) {
                if (obj instanceof Planet) {
                    Planet planet = (Planet) obj;
                    if (planet.isHabitable()) {
                        colonized = true;
                        fuel -= 10; // Fuel consumption for landing
                        gui.appendLog("Spaceship " + id + " has landed on planet " + planet.getName());
                        updateUI("Landed on " + planet.getName());
                        return;
                    }
                }
            }
        }
        gui.appendLog("Spaceship " + id + " could not land on any habitable planet or insufficient fuel.");
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
        if (remainingJumps > 0 && isFunctional) {
            currentSystem = targetSystem;
            remainingJumps--;
            gui.appendLog("Spaceship " + id + " jumped to " + targetSystem.getName());
            updateUI("Jumped to " + targetSystem.getName());
            colonized = false;
        } else {
            gui.appendLog("Spaceship " + id + " has no remaining jumps or is not functional.");
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
        return "ID: " + id + ", Jumps left: " + remainingJumps + ", Fuel: " + fuel + ", Modules: " + usedModules + "/" + maxModules + ", Action: " + currentAction;
    }
}
