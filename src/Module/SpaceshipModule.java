package Module;

public abstract class SpaceshipModule {
    protected int slotsOccupied;

    public SpaceshipModule(int slotsOccupied) {
        this.slotsOccupied = slotsOccupied;
    }

    public abstract String getDescription();

    public int getSlotsOccupied() {
        return slotsOccupied;
    }
}
