package Module;

public class JumpEngineModule extends SpaceshipModule {
    private int jumpCapacity;

    public JumpEngineModule(int jumpCapacity, int slotsOccupied) {
        super(slotsOccupied);
        this.jumpCapacity = jumpCapacity;
    }

    @Override
    public String getDescription() {
        return "Jump Engine Level " + jumpCapacity + ": Allows " + jumpCapacity + " jumps.";
    }
}
