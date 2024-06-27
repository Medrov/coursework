package Module;

public class ManeuverEngineModule extends SpaceshipModule {
    private int fuelConsumptionPerAU;
    private int fuelConsumptionForLanding;

    public ManeuverEngineModule(int fuelConsumptionPerAU, int fuelConsumptionForLanding, int slotsOccupied) {
        super(slotsOccupied);
        this.fuelConsumptionPerAU = fuelConsumptionPerAU;
        this.fuelConsumptionForLanding = fuelConsumptionForLanding;
    }

    @Override
    public String getDescription() {
        return "Maneuver Engine: Fuel consumption per AU: " + fuelConsumptionPerAU + ", Fuel consumption for landing: " + fuelConsumptionForLanding;
    }
}
