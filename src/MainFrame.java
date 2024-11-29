// MainFrame.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class MainFrame extends JFrame {
    private DrawingPanel drawingPanel;
    private JComboBox<String> shapeSelector;
    private JButton colorButton;
    private Color currentColor;
    private String currentShape;
    private JToggleButton editModeButton;
    private JComboBox<String> editActionSelector;

    public MainFrame() {
        setTitle("Mini Paint");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        currentColor = Color.BLACK;
        currentShape = "Freehand";

        drawingPanel = new DrawingPanel();
        createToolbar();

        setLayout(new BorderLayout());
        add(createToolbar(), BorderLayout.NORTH);
        add(drawingPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        shapeSelector = new JComboBox<>(new String[]{"Freehand", "Rectangle", "Circle", "Triangle", "Eraser"});
        shapeSelector.addActionListener(e -> {
            currentShape = (String)shapeSelector.getSelectedItem();
            drawingPanel.setCurrentShape(currentShape);
            editModeButton.setSelected(false);
            drawingPanel.setEditMode(false);
        });

        colorButton = new JButton();
        colorButton.setBackground(currentColor);
        colorButton.setForeground(Color.WHITE);
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choisir une couleur", currentColor);
            if (newColor != null) {
                currentColor = newColor;
                colorButton.setBackground(currentColor);
                drawingPanel.setCurrentColor(currentColor);
            }
        });

        editModeButton = new JToggleButton("Mode édition");
        editActionSelector = new JComboBox<>(new String[]{"Déplacer", "Supprimer"});
        editActionSelector.setEnabled(false);

        editModeButton.addActionListener(e -> {
            boolean isEditMode = editModeButton.isSelected();
            editActionSelector.setEnabled(isEditMode);
            shapeSelector.setEnabled(!isEditMode);
            drawingPanel.setEditMode(isEditMode);
            if (isEditMode) {
                drawingPanel.setEditAction((String)editActionSelector.getSelectedItem());
            }
        });

        editActionSelector.addActionListener(e -> {
            drawingPanel.setEditAction((String)editActionSelector.getSelectedItem());
        });

        toolbar.add(new JLabel(" Dessin: "));
        toolbar.add(shapeSelector);
        toolbar.addSeparator(new Dimension(10, 0));
        toolbar.add(new JLabel(" Couleur: "));
        toolbar.add(colorButton);
        toolbar.addSeparator(new Dimension(10, 0));
        toolbar.add(editModeButton);
        toolbar.add(editActionSelector);
        toolbar.addSeparator(new Dimension(10, 0));

        JButton saveButton = new JButton("Sauvegarder");
        JButton loadButton = new JButton("Charger");

        saveButton.addActionListener(e -> saveDrawing());
        loadButton.addActionListener(e -> loadDrawing());

        toolbar.add(saveButton);
        toolbar.add(loadButton);

        return toolbar;
    }

    private void saveDrawing() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".paint");
            }
            public String getDescription() {
                return "Fichiers Paint (*.paint)";
            }
        });

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".paint")) {
                file = new File(file.getPath() + ".paint");
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(drawingPanel.getShapes());
                JOptionPane.showMessageDialog(this, "Dessin sauvegardé avec succès!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la sauvegarde: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadDrawing() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".paint");
            }
            public String getDescription() {
                return "Fichiers Paint (*.paint)";
            }
        });

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileChooser.getSelectedFile()))) {
                @SuppressWarnings("unchecked")
                ArrayList<Shape> loadedShapes = (ArrayList<Shape>) ois.readObject();
                drawingPanel.setShapes(loadedShapes);
                JOptionPane.showMessageDialog(this, "Dessin chargé avec succès!");
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainFrame();
        });
    }
}