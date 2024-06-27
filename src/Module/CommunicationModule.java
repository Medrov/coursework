package Module;

public class CommunicationModule extends SpaceshipModule {
    public CommunicationModule() {
        super(1); // 1 слот для модуля связи
    }

    @Override
    public String getDescription() {
        return "Communication Module (requires energy)";
    }

    public boolean requiresEnergy() {
        return true;
    }
}

