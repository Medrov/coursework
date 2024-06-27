package Module;

public class FuelTankModule extends SpaceshipModule {
    private int fuelCapacity;

    public FuelTankModule(int fuelCapacity) {
        super(1); // 1 слот для топливного модуля
        this.fuelCapacity = fuelCapacity;
    }

    @Override
    public String getDescription() {
        return "Fuel Tank Module with capacity of " + fuelCapacity + " liters";
    }

    public int getFuelCapacity() {
        return fuelCapacity;
    }
}

