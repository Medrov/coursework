package Module.Scan;
import Module.ScanningModule;
class MediumScanningModule extends ScanningModule {
    public MediumScanningModule() {
        super(2, 3); // 2 слота и сканирование 3 характеристик
    }

    @Override
    public String getDescription() {
        return "Medium Scanning Module (scans 3 random characteristics)";
    }
}
