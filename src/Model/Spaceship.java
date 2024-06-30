package Model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.*;

import Module.*;
import Module.Scan.LargeScanningModule;
import Module.Scan.MediumScanningModule;
import Module.Scan.SmallScanningModule;
import UI.GUI;

public class Spaceship implements Runnable {
    public String id;
    public boolean isReturned;
    public boolean isColonized;
    public int jumpCapacity;
    public int remainingJumps;
    public int fuel;
    public int maxModules;
    public int usedModules;
    public boolean isFunctional;
    public int distanceTraveled;
    public Expedition expedition;
    public PlanetarySystem currentSystem;
    public PlanetarySystem targetSystem;
    public GUI gui;
    public String currentAction;

    public List<SpaceshipModule> installedModules;
    Set<PlanetarySystem> visitedSystems;

    public Spaceship(String id, int jumpCapacity, int fuel, int maxModules, Expedition expedition, GUI gui) {
        this.id = id;
        this.jumpCapacity = jumpCapacity;
        this.remainingJumps = jumpCapacity;
        this.fuel = fuel;
        this.maxModules = maxModules;
        this.usedModules = 0;
        this.expedition = expedition;
        this.gui = gui;
        this.isReturned = false;
        this.isColonized = false;
        this.isFunctional = true;
        this.currentSystem = null;
        this.targetSystem = null;
        this.currentAction = "";
        this.distanceTraveled = 0;
        this.installedModules = new ArrayList<>();
        this.visitedSystems = new HashSet<>();
        addMandatoryModules();
        fillWithRandomModules();
    }

    public void startExpedition() {
        currentAction = "Started expedition";
    }
    private void exploreSystem() {
        double distance = ThreadLocalRandom.current().nextDouble(0, targetSystem.getSize());
        boolean habitablePlanetFound = false;

        gui.appendLog("Spaceship " + id + " is exploring " + targetSystem.getName() + " and will travel " + distance + " astronomical units.");

        travelDistance(distance);

        for (AstronomicalObject obj : targetSystem.getObjects()) {
            if (obj instanceof Planet) {
                Planet planet = (Planet) obj;
                if (distance >= planet.getDistanceFromCenter()) {
                    // Scan planet logic
                    if (scanPlanet(planet)) {
                        isColonized = true;
                        gui.appendLog("Spaceship " + id + " colonized planet " + planet.getName());
                        logUI("Colonized " + planet.getName());
                        showPlanetFoundDialog(planet);
                        return;
                    }
                }
            }
        }
        if (!habitablePlanetFound) {
            returnToBase();
        }
    }

    private void travelDistance(double distance) {
        for (int i = 0; i < distance; i++) {
            distanceTraveled++;
            try {
                Thread.sleep(500); // 0,5 секунд на каждую астрономическую единицу
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void showPlanetFoundDialog(Planet planet) {
        JOptionPane.showMessageDialog(gui, "Spaceship " + id + " has found a habitable planet: " + planet.getName(),
                "Planet Found", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void run() {
        while (!isReturned && isFunctional && remainingJumps > 0) {
            checkAndRepairModules();
            // Jump to a new system
            if (!visitedSystems.contains(targetSystem)) {
                visitedSystems.add(targetSystem);
                if (remainingJumps > 0 && isFunctional) {
                    remainingJumps--;
                    gui.appendLog("Spaceship " + id + " jumped to " + targetSystem.getName());
                    logUI("Jumping to " + targetSystem.getName());
                    exploreSystem();
                    if (isColonized) {
                        return;
                    }
                }
            } else {
                gui.appendLog("Spaceship " + id + " has already visited " + targetSystem.getName());
            }
            // Return to base if out of jumps
            if (remainingJumps == 0 && isFunctional) {
                returnToBase();
            }
            // Sleep for 1 second to simulate passage of time
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        gui.appendLog("Spaceship " + id + " has ended its mission.");
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

    public void returnToBase() {
        this.isReturned = true;
        gui.appendLog("Spaceship " + id + " returned to base.");
        currentAction = "Returning to base";
        logUI("Returned to base");
    }

    public void landOnPlanet(Spaceship spaceship) {
        if (currentSystem != null && !isColonized && fuel >= 10) {
            for (AstronomicalObject obj : currentSystem.getObjects()) {
                if (obj instanceof Planet) {
                    Planet planet = (Planet) obj;
                    if (planet.isHabitable()) {
                        isColonized = true;
                        fuel -= 10; // Fuel consumption for landing
                        gui.appendLog("Spaceship " + id + " has landed on planet " + planet.getName());
                        logUI("Landed on " + planet.getName());
                        return;
                    }
                }
            }
        }
        gui.appendLog("Spaceship " + id + " could not land on any habitable planet or insufficient fuel.");
        logUI("Landing failed");
    }

    public void takeOff() {
        if (isColonized) {
            isColonized = false;
            gui.appendLog("Spaceship " + id + " has taken off.");
            logUI("Taken off");
        } else {
            gui.appendLog("Spaceship " + id + " is not on any planet to take off.");
            logUI("Take off failed");
        }
    }

    public void jumpToSystem(PlanetarySystem targetSystem) {
        if (remainingJumps > 0 && isFunctional) {
            currentSystem = targetSystem;
            remainingJumps--;
            gui.appendLog("Spaceship " + id + " jumped to " + targetSystem.getName());
            logUI("Jumped to " + targetSystem.getName());
            isColonized = false;
        } else {
            gui.appendLog("Spaceship " + id + " has no remaining jumps or is not functional.");
            logUI("Jump failed");
        }
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
    public static Spaceship findShipById(String shipId, List<Spaceship> spaceships) {
        for (Spaceship ship : spaceships) {
            if (ship.id.equals(shipId)) {
                return ship;
            }
        }
        return null;
    }
    private void logUI(String message) {
        SwingUtilities.invokeLater(() -> {
            gui.appendLog(message);
            gui.updateShipStatus(this);
        });
    }

    public String getStatus() {
        return "ID: " + id + ", Jumps left: " + remainingJumps + ", Fuel: " + fuel + ", Modules: " + usedModules + "/" + maxModules +  ", Distance: " + distanceTraveled + ", Action: " + currentAction;
    }
}
