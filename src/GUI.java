import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class GUI extends JFrame {
    private Expedition expedition;
    private List<PlanetarySystem> planetarySystems;
    private List<Spaceship> spaceships;
    private JPanel shipsPanel;
    private JList<String> logList;
    private DefaultListModel<String> logListModel;
    private ExecutorService executor;

    public GUI() {
        setTitle("Space Expedition Control");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenuItem menuItemRestart = new JMenuItem("Restart");
        JMenuItem menuItemExit = new JMenuItem("Exit");
        JMenuItem menuItemInfo = new JMenuItem("System Info");
        menuFile.add(menuItemRestart);
        menuFile.add(menuItemExit);
        menuFile.add(menuItemInfo);
        menuBar.add(menuFile);
        setJMenuBar(menuBar);

        // Center layout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(1, 3, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel systemsPanel = new JPanel();
        systemsPanel.setLayout(new BoxLayout(systemsPanel, BoxLayout.Y_AXIS));
        systemsPanel.setBorder(BorderFactory.createTitledBorder("Planetary Systems"));

        shipsPanel = new JPanel();
        shipsPanel.setLayout(new BoxLayout(shipsPanel, BoxLayout.Y_AXIS));
        shipsPanel.setBorder(BorderFactory.createTitledBorder("Spaceships"));

        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        centerPanel.add(systemsPanel);
        centerPanel.add(shipsPanel);
        centerPanel.add(actionsPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom layout
        logListModel = new DefaultListModel<>();
        logList = new JList<>(logListModel);
        JScrollPane logScrollPane = new JScrollPane(logList);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Log"));
        add(logScrollPane, BorderLayout.SOUTH);

        // Top layout
        JPanel topPanel = new JPanel();
        JButton startButton = new JButton("Start");
        JButton createShipButton = new JButton("Create Ship");
        JButton setupFlightButton = new JButton("Setup Flight");
        JButton startExpeditionButton = new JButton("Start Expedition");
        JButton spaceshipSettingsButton = new JButton("Spaceship settings");
        topPanel.add(startButton);
        topPanel.add(createShipButton);
        topPanel.add(setupFlightButton);
        topPanel.add(startExpeditionButton);
        topPanel.add(spaceshipSettingsButton);
        add(topPanel, BorderLayout.NORTH);

        // Handlers
        startButton.addActionListener(event -> startNewExpedition(systemsPanel));
        createShipButton.addActionListener(event -> createSpaceship());
        startExpeditionButton.addActionListener(event -> startExpedition());
        setupFlightButton.addActionListener(event -> openFlightSetupDialog());
        spaceshipSettingsButton.addActionListener(event -> openSpaceshipSettingsDialog());
        menuItemRestart.addActionListener(event -> restartProgram());
        menuItemExit.addActionListener(event -> System.exit(0));
        menuItemInfo.addActionListener(event -> showSystemInfo());

        setVisible(true);
    }

    private void showSystemInfo() {
        if (planetarySystems == null || planetarySystems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No planetary systems generated yet.", "System Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder info = new StringBuilder();
        for (PlanetarySystem system : planetarySystems) {
            info.append(system.getName()).append(":\n");
            for (AstronomicalObject obj : system.getObjects()) {
                info.append("  ").append(obj.toString()).append("\n");
            }
            info.append("\n");
        }

        JTextArea textArea = new JTextArea(info.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "System Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void startNewExpedition(JPanel systemsPanel) {
        planetarySystems = generatePlanetarySystems();
        expedition = new Expedition();
        planetarySystems.forEach(expedition::addSystem);

        systemsPanel.removeAll();
        systemsPanel.add(new JLabel("Planetary Systems:"));
        planetarySystems.forEach(system -> {
            JLabel systemLabel = new JLabel(system.getName());
            systemLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showSystemDetails(system);
                }
            });
            systemsPanel.add(systemLabel);
        });

        logListModel.addElement("New expedition started.");
        systemsPanel.revalidate();
        systemsPanel.repaint();
    }

    private void showSystemDetails(PlanetarySystem system) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("System: " + system.getName()));
        panel.add(new JLabel("Size: " + system.getSize() + " AU"));
        panel.add(new JLabel("Objects:"));

        for (AstronomicalObject obj : system.getObjects()) {
            JButton objButton = new JButton(obj.getName() + " - " + obj.getClass().getSimpleName());
            objButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showSurfaceDetails(obj);
                }
            });
            panel.add(objButton);

            // Если объект является планетой, добавляем информацию о её спутниках
            if (obj instanceof Planet) {
                Planet planet = (Planet) obj;
                List<Moon> moons = planet.getMoons();
                for (Moon moon : moons) {
                    JButton moonButton = new JButton(" Moon: " + moon.getName() + " - Distance: " + moon.getDistanceFromCenter() + " AU");
                    moonButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            showSurfaceDetails(moon);
                        }
                    });
                    panel.add(moonButton);
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "System Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSurfaceDetails(AstronomicalObject obj) {
        StringBuilder details = new StringBuilder(obj.getName() + " Details:\n");

        if (obj instanceof Planet) {
            Planet planet = (Planet) obj;
            details.append("  Surface Temperature: ").append(planet.getSurfaceTemperature()).append(" °C\n")
                    .append("  Atmosphere: ").append(planet.hasAtmosphere() ? "Yes" : "No").append("\n")
                    .append("  Oxygen: ").append(planet.hasOxygen() ? "Yes" : "No").append("\n")
                    .append("  Water: ").append(planet.hasWater() ? "Yes" : "No").append("\n")
                    .append("  Solid Surface: ").append(planet.hasSolidSurface() ? "Yes" : "No").append("\n");

            // Дополнительные характеристики для пригодной планеты
            details.append("  Ground: ").append(planet.hasGround() ? "Yes" : "No").append("\n")
                    .append("  Average Surface Temperature: ").append(planet.getAverageSurfaceTemperature()).append(" °C\n")
                    .append("  Oxygen Atmosphere: ").append(planet.hasOxygenAtmosphere() ? "Yes" : "No").append("\n");

        } else if (obj instanceof Moon) {
            Moon moon = (Moon) obj;
            details.append("  Distance from Planet: ").append(moon.getDistanceFromCenter()).append(" AU\n");

            // Дополнительные характеристики для спутника
            details.append("  Surface Temperature: ").append(moon.getSurfaceTemperature()).append(" °C\n")
                    .append("  Atmosphere: ").append(moon.hasAtmosphere() ? "Yes" : "No").append("\n")
                    .append("  Oxygen: ").append(moon.hasOxygen() ? "Yes" : "No").append("\n")
                    .append("  Water: ").append(moon.hasWater() ? "Yes" : "No").append("\n")
                    .append("  Solid Surface: ").append(moon.hasSolidSurface() ? "Yes" : "No").append("\n");
        }

        JOptionPane.showMessageDialog(this, details.toString(), obj.getName() + " Surface Details", JOptionPane.INFORMATION_MESSAGE);
    }



    private List<PlanetarySystem> generatePlanetarySystems() {
        List<PlanetarySystem> systems = new ArrayList<>();
        boolean allSystemsValid = false;

        while (!allSystemsValid) {
            systems.clear(); // Очищаем список систем перед генерацией новой

            // Генерируем 15 планетарных систем
            for (int i = 1; i <= 15; i++) {
                PlanetarySystem system = new PlanetarySystem("System-" + i, ThreadLocalRandom.current().nextDouble(10, 100));
                Star centerStar = new Star("Star-" + i, 0);
                system.addObject(centerStar);

                boolean systemValid = true; // Флаг для проверки условий компактности и колонизации
                boolean hasHabitablePlanet = true;

                // Генерируем до 4 планет в каждой системе
                for (int j = 1; j <= 4; j++) {
                    double distance = ThreadLocalRandom.current().nextDouble(1, system.getSize());
                    double temperature = ThreadLocalRandom.current().nextDouble(-100, 100);
                    boolean hasAtmosphere = ThreadLocalRandom.current().nextBoolean();
                    boolean hasOxygen = hasAtmosphere && ThreadLocalRandom.current().nextBoolean();
                    boolean hasWater = ThreadLocalRandom.current().nextBoolean();
                    boolean hasSolidSurface = ThreadLocalRandom.current().nextBoolean();
                    int numOfMoons = ThreadLocalRandom.current().nextInt(0, 6);

                    // Проверяем компактность планеты
                    if (distance < 10 || distance > 100) {
                        systemValid = false;
                        break; // Не добавляем планету в систему, если не удовлетворяет условию компактности
                    }

                    boolean hasGround = ThreadLocalRandom.current().nextBoolean();
                    boolean hasLiquidWater = ThreadLocalRandom.current().nextBoolean();
                    boolean hasOxygenAtmosphere = hasAtmosphere && ThreadLocalRandom.current().nextBoolean();
                    double averageSurfaceTemperature = ThreadLocalRandom.current().nextDouble(0, 25);

                    Planet planet = new Planet("Planet-" + i + "-" + j, distance, temperature, hasAtmosphere, hasOxygen, hasWater,
                            hasSolidSurface, hasGround, hasLiquidWater, hasOxygenAtmosphere, averageSurfaceTemperature, numOfMoons);
                    system.addObject(planet);

                    // Добавление спутников
                    for (int k = 1; k <= numOfMoons; k++) {
                        double distanceMoon = ThreadLocalRandom.current().nextDouble(1, distance / 10); // Дистанция луны от планеты
                        double temperatureMoon = ThreadLocalRandom.current().nextDouble(-10, 10);
                        boolean hasAtmosphereMoon = ThreadLocalRandom.current().nextBoolean();
                        boolean hasOxygenMoon = hasAtmosphereMoon && ThreadLocalRandom.current().nextBoolean();
                        boolean hasWaterMoon = ThreadLocalRandom.current().nextBoolean();
                        boolean hasSolidSurfaceMoon = ThreadLocalRandom.current().nextBoolean();
                        Moon moon = new Moon("Moon-" + i + "-" + j + "-" + k, distanceMoon, temperatureMoon, hasAtmosphereMoon, hasOxygenMoon, hasWaterMoon, hasSolidSurfaceMoon);
                        planet.addMoon(moon);
                    }

                    // Проверяем, есть ли хотя бы одна пригодная для колонизации планета
                    if (!hasHabitablePlanet && planet.isHabitable()) {
                        hasHabitablePlanet = true;
                    }
                }

                // Если система удовлетворяет условиям, добавляем её в список систем
                if (systemValid && hasHabitablePlanet) {
                    systems.add(system);
                } else {
                    // Если система не удовлетворяет условиям, генерируем заново
                    systems.clear();
                    break;
                }
            }

            // Проверяем, что сгенерированы все 15 систем, удовлетворяющих условиям
            if (systems.size() == 15) {
                allSystemsValid = true;
            }
        }

        return systems;
    }


    private void createSpaceship() {
        if (spaceships == null) {
            spaceships = new ArrayList<>();
        }

        String shipId = "Ship-" + (spaceships.size() + 1);
        int jumpCapacity = ThreadLocalRandom.current().nextBoolean() ? 2 : 5;
        int fuel = 50;
        int modules = 12;
        Spaceship spaceship = new Spaceship(shipId, jumpCapacity, fuel, modules, expedition, this);
        spaceships.add(spaceship);
        expedition.addSpaceship(spaceship);

        JLabel shipLabel = new JLabel(shipId);
        shipsPanel.add(shipLabel);
        shipsPanel.revalidate();
        shipsPanel.repaint();
    }

    private void startExpedition() {
        if (spaceships != null) {
            executor = Executors.newFixedThreadPool(spaceships.size());
            for (Spaceship spaceship : spaceships) {
                executor.submit(spaceship);
            }
        }
    }

    public void appendLog(String message) {
        logListModel.addElement(message);
    }

    public void updateShipStatus(Spaceship spaceship) {
        for (Component component : shipsPanel.getComponents()) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                if (label.getText().startsWith(spaceship.getId())) {
                    label.setText(spaceship.getStatus());
                    if (spaceship.hasReturned()) {
                        label.setForeground(Color.GREEN);
                    } else if (spaceship.isColonized()) {
                        label.setForeground(Color.BLUE);
                    } else if (spaceship.getStatus().contains("Jumping") || spaceship.getStatus().contains("Exploring")) {
                        label.setForeground(Color.ORANGE);
                    } else {
                        label.setForeground(Color.RED);
                    }
                }
            }
        }
    }

    private void restartProgram() {
        spaceships.clear();
        shipsPanel.removeAll();
        logListModel.clear();
        expedition = new Expedition();
        planetarySystems = generatePlanetarySystems();
        planetarySystems.forEach(expedition::addSystem);
        logListModel.addElement("Program restarted.");
        revalidate();
        repaint();
    }

    private void openFlightSetupDialog() {
        JDialog dialog = new JDialog(this, "Flight Setup", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2));

        JLabel shipLabel = new JLabel("Select Ship:");
        JLabel systemLabel = new JLabel("Select System:");

        JComboBox<String> shipComboBox = new JComboBox<>();
        JComboBox<String> systemComboBox = new JComboBox<>();

        for (Spaceship ship : spaceships) {
            shipComboBox.addItem(ship.getId());
        }

        for (PlanetarySystem system : planetarySystems) {
            systemComboBox.addItem(system.getName());
        }

        panel.add(shipLabel);
        panel.add(shipComboBox);
        panel.add(systemLabel);
        panel.add(systemComboBox);

        JButton assignButton = new JButton("Assign");
        assignButton.addActionListener(e -> {
            String selectedShipId = (String) shipComboBox.getSelectedItem();
            String selectedSystemName = (String) systemComboBox.getSelectedItem();

            if (selectedShipId != null && selectedSystemName != null) {
                Spaceship selectedShip = null;
                for (Spaceship ship : spaceships) {
                    if (ship.getId().equals(selectedShipId)) {
                        selectedShip = ship;
                        break;
                    }
                }

                PlanetarySystem selectedSystem = null;
                for (PlanetarySystem system : planetarySystems) {
                    if (system.getName().equals(selectedSystemName)) {
                        selectedSystem = system;
                        break;
                    }
                }

                if (selectedShip != null && selectedSystem != null) {
                    selectedShip.setTargetSystem(selectedSystem);
                    appendLog("Assigned " + selectedShipId + " to " + selectedSystemName);
                }
            }
            dialog.dispose();
        });

        JButton jumpButton = new JButton("Jump to System");
        jumpButton.addActionListener(e -> {
            String selectedShipId = (String) shipComboBox.getSelectedItem();
            String selectedSystemName = (String) systemComboBox.getSelectedItem();

            if (selectedShipId != null && selectedSystemName != null) {
                Spaceship selectedShip = null;
                for (Spaceship ship : spaceships) {
                    if (ship.getId().equals(selectedShipId)) {
                        selectedShip = ship;
                        break;
                    }
                }

                PlanetarySystem selectedSystem = null;
                for (PlanetarySystem system : planetarySystems) {
                    if (system.getName().equals(selectedSystemName)) {
                        selectedSystem = system;
                        break;
                    }
                }

                if (selectedShip != null && selectedSystem != null) {
                    selectedShip.jumpToSystem(selectedSystem);
                    appendLog("Spaceship " + selectedShipId + " jumped to " + selectedSystemName);
                }
            }
            dialog.dispose();
        });

        panel.add(assignButton);
        panel.add(jumpButton);
        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void createShipControlButtons(JPanel actionsPanel) {
        JButton landButton = new JButton("Land");
        JButton takeOffButton = new JButton("Take Off");
        JButton jumpButton = new JButton("Jump");

        landButton.addActionListener(e -> {
            String selectedShipId = selectShip();
            if (selectedShipId != null) {
                Spaceship selectedShip = getSpaceshipById(selectedShipId);
                if (selectedShip != null) {
                    selectedShip.landOnPlanet();
                }
            }
        });

        takeOffButton.addActionListener(e -> {
            String selectedShipId = selectShip();
            if (selectedShipId != null) {
                Spaceship selectedShip = getSpaceshipById(selectedShipId);
                if (selectedShip != null) {
                    selectedShip.takeOff();
                }
            }
        });

        jumpButton.addActionListener(e -> openFlightSetupDialog());

        actionsPanel.add(landButton);
        actionsPanel.add(takeOffButton);
        actionsPanel.add(jumpButton);
    }

    private String selectShip() {
        String[] shipIds = spaceships.stream().map(Spaceship::getId).toArray(String[]::new);
        return (String) JOptionPane.showInputDialog(this, "Select Ship:", "Ship Selection", JOptionPane.QUESTION_MESSAGE, null, shipIds, shipIds[0]);
    }

    private Spaceship getSpaceshipById(String shipId) {
        return spaceships.stream().filter(ship -> ship.getId().equals(shipId)).findFirst().orElse(null);
    }

    private void openSpaceshipSettingsDialog() {
        JDialog dialog = new JDialog(this, "Spaceship Settings", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        JButton returnToBaseButton = new JButton("Return to Base");
        JButton landOnPlanetButton = new JButton("Land on Planet");
        JButton takeOffButton = new JButton("Take Off");
        JComboBox<String> shipComboBox = new JComboBox<>();
        JComboBox<String> systemComboBox = new JComboBox<>();

        for (Spaceship ship : spaceships) {
            shipComboBox.addItem(ship.getId());
        }

        for (PlanetarySystem system : planetarySystems) {
            systemComboBox.addItem(system.getName());
        }

        panel.add(systemComboBox);
        panel.add(returnToBaseButton);
        panel.add(landOnPlanetButton);
        panel.add(takeOffButton);

        returnToBaseButton.addActionListener(e -> {
            String selectedShipId = (String) shipComboBox.getSelectedItem();
            if (selectedShipId != null) {
                Spaceship selectedShip = findShipById(selectedShipId);
                if (selectedShip != null) {
                    returnToBase(selectedShip);
                }
            }
        });

        landOnPlanetButton.addActionListener(e -> {
            String selectedShipId = (String) shipComboBox.getSelectedItem();
            if (selectedShipId != null) {
                Spaceship selectedShip = findShipById(selectedShipId);
                if (selectedShip != null) {
                    landOnPlanet(selectedShip);
                }
            }
        });

        takeOffButton.addActionListener(e -> {
            String selectedShipId = (String) shipComboBox.getSelectedItem();
            if (selectedShipId != null) {
                Spaceship selectedShip = findShipById(selectedShipId);
                if (selectedShip != null) {
                    takeOff(selectedShip);
                }
            }
        });

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private Spaceship findShipById(String shipId) {
        for (Spaceship ship : spaceships) {
            if (ship.getId().equals(shipId)) {
                return ship;
            }
        }
        return null;
    }

    private void returnToBase(Spaceship spaceship) {
        spaceship.returnToBase();
        appendLog(spaceship.getId() + " has returned to the base.");
        updateShipStatus(spaceship);
    }

    private void landOnPlanet(Spaceship spaceship) {
        spaceship.landOnPlanet();
        appendLog(spaceship.getId() + " has landed on a planet.");
        updateShipStatus(spaceship);
    }

    private void takeOff(Spaceship spaceship) {
        spaceship.takeOff();
        appendLog(spaceship.getId() + " has taken off.");
        updateShipStatus(spaceship);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}
