package Module;

public abstract class ScanningModule extends SpaceshipModule {
    protected int scanCapacity;

    public ScanningModule(int slotsOccupied, int scanCapacity) {
        super(slotsOccupied);
        this.scanCapacity = scanCapacity;
    }

    public abstract String getDescription();

    public int getScanCapacity() {
        return scanCapacity;
    }
}
