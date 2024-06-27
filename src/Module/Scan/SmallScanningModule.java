package Module.Scan;
import Module.ScanningModule;

class SmallScanningModule extends ScanningModule {
    public SmallScanningModule() {
        super(1, 2); // 1 слот и сканирование 2 характеристик
    }

    @Override
    public String getDescription() {
        return "Small Scanning Module (scans 2 random characteristics)";
    }
}
