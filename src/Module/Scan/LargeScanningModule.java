package Module.Scan;
import Module.ScanningModule;
class LargeScanningModule extends ScanningModule {
    public LargeScanningModule() {
        super(3, 5); // 3 слота и сканирование всех характеристик
    }

    @Override
    public String getDescription() {
        return "Large Scanning Module (scans all characteristics)";
    }
}

