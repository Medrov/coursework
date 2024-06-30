package UI;

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

import Model.*;
import Module.SpaceshipModule;

import static Model.Spaceship.findShipById;

public class GUI extends JFrame {
    private Expedition expedition;
    private List<PlanetarySystem> planetarySystems;
    private List<Spaceship> spaceships;
    private JPanel shipsPanel;
    private JPanel systemsPanel;
    private JList<String> logList;
    private DefaultListModel<String> logListModel;
    private ExecutorService executor;
    private boolean isStartButtonClicked = false;
    private boolean isExpeditionStarted = false;

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
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        systemsPanel = new JPanel();
        systemsPanel.setLayout(new BoxLayout(systemsPanel, BoxLayout.Y_AXIS));
        systemsPanel.setBorder(BorderFactory.createTitledBorder("Planetary Systems"));

        shipsPanel = new JPanel();
        shipsPanel.setLayout(new BoxLayout(shipsPanel, BoxLayout.Y_AXIS));
        shipsPanel.setBorder(BorderFactory.createTitledBorder("Spaceships"));

        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        // Add systemsPanel to centerPanel
        gbc.gridx = 0;
        gbc.weightx = 0.25; // 25% width
        centerPanel.add(systemsPanel, gbc);

        // Add shipsPanel to centerPanel
        gbc.gridx = 1;
        gbc.weightx = 0.5; // 50% width
        centerPanel.add(shipsPanel, gbc);

        // Add actionsPanel to centerPanel
        gbc.gridx = 2;
        gbc.weightx = 0.25; // 25% width
        centerPanel.add(actionsPanel, gbc);

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
        JButton startExpeditionButton = new JButton("Start Expedition");
        JButton setupFlightButton = new JButton("Setup Flight");
        JButton spaceshipSettingsButton = new JButton("Spaceship settings");
        JButton setupJumpButton = new JButton("Setup Jump");
        topPanel.add(startButton);
        topPanel.add(createShipButton);
        topPanel.add(setupFlightButton);
        topPanel.add(startExpeditionButton);
        topPanel.add(spaceshipSettingsButton);
        topPanel.add(setupJumpButton);
        add(topPanel, BorderLayout.NORTH);

        // Handlers
        startButton.addActionListener(event -> mainStart(systemsPanel));
        createShipButton.addActionListener(event -> createSpaceship());
        setupFlightButton.addActionListener(event -> openFlightSetupDialog());
        startExpeditionButton.addActionListener(event -> startExpedition());
        spaceshipSettingsButton.addActionListener(event -> openSpaceshipSettingsDialog());
        setupJumpButton.addActionListener(event -> openJumpSetupDialog());
        menuItemRestart.addActionListener(event -> restartProgram());
        menuItemExit.addActionListener(event -> System.exit(0));
        menuItemInfo.addActionListener(event -> showSystemInfo());

        setVisible(true);
    }

    private void mainStart(JPanel systemsPanel) {
        if (!isStartButtonClicked) {
            startNewCompanyExpedition(systemsPanel);
            isStartButtonClicked = true;
            JOptionPane.showMessageDialog(this, "The expedition company has started!", "Start", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "The expedition company has already started.", "Start", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void startNewCompanyExpedition(JPanel systemsPanel) {
        planetarySystems = PlanetarySystem.generatePlanetarySystems();
        expedition = new Expedition();
        planetarySystems.forEach(expedition::addSystem);

        systemsPanel.removeAll();
        systemsPanel.add(new JLabel("Planetary Systems:"));
        planetarySystems.forEach(system -> {
            JLabel systemLabel = new JLabel(system.getName());
            if (system.hasHabitablePlanets()) {
                systemLabel.setForeground(Color.GREEN);
            }
            systemLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showSystemDetails(system);
                }
            });
            systemsPanel.add(systemLabel);
        });

        logListModel.addElement("New expedition company started.");
        systemsPanel.revalidate();
        systemsPanel.repaint();
    }

    private void startExpedition() {
        if (!isExpeditionStarted) {
            isExpeditionStarted = true;
            if (spaceships != null) {
                executor = Executors.newFixedThreadPool(spaceships.size());
                for (Spaceship spaceship : spaceships) {
                    executor.submit(spaceship);
                    spaceship.currentSystem = spaceship.targetSystem;
                    spaceship.startExpedition();
                    updateShipStatus(spaceship);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No spaceships available to start the expedition.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "The expedition has already started.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void createSpaceship() {
        if (expedition == null) {
            JOptionPane.showMessageDialog(this, "The expedition has not started.", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
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
            shipLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showShipModules(spaceship);
                }
            });
            shipsPanel.add(shipLabel);
            shipsPanel.revalidate();
            shipsPanel.repaint();
        }
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

    private void showSystemDetails(PlanetarySystem system) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("System: " + system.getName()));
        panel.add(new JLabel("Size: " + system.getSize() + " AU"));
        panel.add(new JLabel("Objects:"));

        for (AstronomicalObject obj : system.getObjects()) {
            JButton objButton = new JButton(obj.getName() + " - " + obj.getClass().getSimpleName() + " - Distance: " + obj.getDistanceFromCenter() + " AU");
            objButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showSurfaceDetails(obj);
                }
            });

            // Если объект является планетой, проверяем, обитаема ли она, и добавляем информацию о её спутниках
            if (obj instanceof Planet) {
                Planet planet = (Planet) obj;
                if (planet.isHabitable()) {
                    objButton.setForeground(Color.black);
                    objButton.setBackground(Color.GREEN);
                }
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

            panel.add(objButton);
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

    private void showShipModules(Spaceship spaceship) {
        StringBuilder moduleList = new StringBuilder();
        moduleList.append("Modules of ").append(spaceship.getId()).append(":").append("\n");
        moduleList.append("----------------------------").append("\n");

        for (SpaceshipModule module : spaceship.installedModules) {
            moduleList.append("- ").append(module.getDescription()).append("\n");
        }

        JOptionPane.showMessageDialog(this, moduleList.toString(), "Modules of " + spaceship.getId(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void openFlightSetupDialog() {
        if (spaceships == null) {
            JOptionPane.showMessageDialog(this, "The spaceships not created.", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            JDialog dialog = new JDialog(this, "Flight Setup", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(300, 200);
            dialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel(new GridLayout(4, 2));

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
                        System.out.println(selectedShip.getTargetSystem());
                        appendLog("Assigned " + selectedShipId + " to " + selectedSystemName);
                    }
                }
                dialog.dispose();
            });

            JButton autoAssignButton = new JButton("Auto Assign");
            autoAssignButton.addActionListener(e -> {
                int shipCount = spaceships.size();

                for (int i = 0; i < shipCount; i++) {
                    Spaceship ship = spaceships.get(i);
                    PlanetarySystem system = planetarySystems.get(i);
                    ship.setTargetSystem(system);
                    appendLog("Assigned " + ship.getId() + " to " + system.getName());
                }
                System.out.println("All ships have been assigned to systems.");
                dialog.dispose();
            });

            panel.add(assignButton);
            panel.add(autoAssignButton);
            dialog.add(panel, BorderLayout.CENTER);
            dialog.setVisible(true);
        }
    }

    private void openJumpSetupDialog() {
        if (spaceships == null) {
            JOptionPane.showMessageDialog(this, "The spaceships not created.", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            JDialog dialog = new JDialog(this, "Jump Setup", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(300, 200);
            dialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JPanel inputPanel = new JPanel(new GridLayout(3, 2));
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

            inputPanel.add(shipLabel);
            inputPanel.add(shipComboBox);
            inputPanel.add(systemLabel);
            inputPanel.add(systemComboBox);

            panel.add(inputPanel);

            JButton jumpButton = new JButton("Jump to System");
            jumpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
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
                        System.out.println(selectedShip.getTargetSystem());
                        appendLog("Spaceship " + selectedShipId + " jumped to " + selectedSystemName);
                    }
                }
                dialog.dispose();
            });

            panel.add(Box.createVerticalStrut(20)); // Добавить промежуток перед кнопкой
            panel.add(jumpButton);
            panel.add(Box.createVerticalStrut(20)); // Добавить промежуток после кнопки

            dialog.add(panel, BorderLayout.CENTER);
            dialog.setVisible(true);
        }
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

        for (Spaceship ship : spaceships) {
            shipComboBox.addItem(ship.getId());
        }

        panel.add(shipComboBox);
        panel.add(returnToBaseButton);
        panel.add(landOnPlanetButton);
        panel.add(takeOffButton);

        returnToBaseButton.addActionListener(e -> {
            String selectedShipId = (String) shipComboBox.getSelectedItem();
            if (selectedShipId != null) {
                Spaceship selectedShip = findShipById(selectedShipId, spaceships);
                if (selectedShip != null) {
                    selectedShip.returnToBase();
                    updateShipStatus(selectedShip);
                }
            }
            dialog.dispose();
        });

        landOnPlanetButton.addActionListener(e -> {
            String selectedShipId = (String) shipComboBox.getSelectedItem();
            if (selectedShipId != null) {
                Spaceship selectedShip = findShipById(selectedShipId, spaceships);
                if (selectedShip != null) {
                    selectedShip.landOnPlanet(selectedShip);
                }
            }
            dialog.dispose();
        });

        takeOffButton.addActionListener(e -> {
            String selectedShipId = (String) shipComboBox.getSelectedItem();
            if (selectedShipId != null) {
                Spaceship selectedShip = findShipById(selectedShipId, spaceships);
                if (selectedShip != null) {
                    selectedShip.takeOff();
                }
            }
            dialog.dispose();
        });

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    public void appendLog(String message) {
        logListModel.addElement(message);
    }

    public void updateShipStatus(Spaceship spaceship) {
        for (Component component : shipsPanel.getComponents()) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                if (label.getText().contains(spaceship.getId())) {
                    label.setText(spaceship.getStatus());
                    if (spaceship.isReturned()) {
                        label.setForeground(Color.CYAN);
                    } else if (spaceship.isColonized()) {
                        label.setForeground(Color.ORANGE);
                    } else if (spaceship.getStatus().contains("Started") || spaceship.getStatus().contains("Exploring")) {
                        label.setForeground(Color.GREEN);
                    } else {
                        label.setForeground(Color.RED);
                    }
                }
            }
        }
    }

    private void restartProgram() {
        isStartButtonClicked = false;
        isExpeditionStarted = false;
        planetarySystems = null;
        expedition = null;
        spaceships = null;

        // Удаляем элементы из панелей
        shipsPanel.removeAll();
        systemsPanel.removeAll(); // Добавляем очистку панели систем

        // Очищаем лог
        logListModel.clear();

        // Обновляем UI.GUI
        shipsPanel.revalidate();
        shipsPanel.repaint();
        systemsPanel.revalidate(); // Обновляем также панель систем
        systemsPanel.repaint();

        revalidate();
        repaint();

        logListModel.addElement("Program restarted.");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}
