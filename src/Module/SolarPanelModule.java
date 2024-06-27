package Module;

public class SolarPanelModule extends SpaceshipModule {
    public SolarPanelModule() {
        super(1); // 1 слот для модуля солнечных батарей
    }

    @Override
    public String getDescription() {
        return "Solar Panel Module (provides energy)";
    }

    public boolean providesEnergy() {
        return true;
    }
}

