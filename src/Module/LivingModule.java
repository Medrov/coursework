package Module;

public class LivingModule extends SpaceshipModule {
    public LivingModule() {
        super(1); // 1 слот для жилого модуля
    }

    @Override
    public String getDescription() {
        return "Living Module";
    }
}

