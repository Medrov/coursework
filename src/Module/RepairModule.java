package Module;

public class RepairModule extends SpaceshipModule {
    public RepairModule() {
        super(2); // 2 слота для ремонтного модуля
    }

    @Override
    public String getDescription() {
        return "Repair Module";
    }
}
