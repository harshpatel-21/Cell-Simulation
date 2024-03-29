import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A graphical view of the simulation grid. The view displays a rectangle for
 * each location. Colors for each type of life form can be defined using the
 * setColor method.
 *
 * @author David J. Barnes, Michael Kölling & Jeffery Raphael, Harshraj Patel &
 *         Ishab Ahmed
 * @version 2023.02.20
 */

public class SimulatorView extends JFrame implements ActionListener {
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    // Text for generation GUI label
    private final String GENERATION_PREFIX = "Generation: ";

    // Text for population GUI label
    private final String POPULATION_PREFIX = "Population: ";

    // GUI labels
    private JLabel genLabel, population, infoLabel, instructionLabel;

    // Extends the multi-line plain text view to be suitable for a single-line
    // editor view. (part of Swing)
    private FieldView fieldView;

    // A statistics object computing and storing simulation information
    private FieldStats stats;

    // Bottom pane which holds debug components (Start/Reset, etc.)
    private JPanel controlPane;

    // components on the debugging pane
    private JSlider speedSlider; // changes how fast the simulation run
    private JButton toggleSimButton; // button to pause/resume simulation
    private JButton resetButton; // button to reset simulation
    private JButton populateButton;

    // ComboBox which will allow the selection of species
    private JComboBox<Species> speciesSelector;

    // simulation speed slider values
    private int sliderUpperBound = 100;
    private int defaultSliderValue = sliderUpperBound / 2;
    private int currentSliderValue;

    // keep track of state of simulation
    private boolean paused, reset, populateButtonPressed;

    // mouse info to allow for drawing
    private int mouseX, mouseY;
    private boolean isMouseBeingPressed = false;

    // container which whill store components in the running JFrame
    private Container contents = getContentPane();

    /**
     * Create a centred view of the given width and height.
     * Adds different components of the view in a grid layout
     * 
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width) {
        stats = new FieldStats();
        fieldView = new FieldView(height, width);

        setTitle("Life Simulation");
        genLabel = new JLabel(GENERATION_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel(" ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) size.getWidth();
        int screenHeight = (int) size.getHeight();
        int tileSize = fieldView.getViewScalingFactor();
        // centre the window
        setLocation((int) (0.5 * (screenWidth - (width * tileSize))),
                (int) (0.5 * (screenHeight - (height * tileSize))));

        this.setMinimumSize(new Dimension(840, 800));

        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(genLabel, BorderLayout.WEST);
        infoPane.add(infoLabel, BorderLayout.CENTER);

        controlPane = createControlPane();
        // disable all debug components (to only be interactable when simulate() is
        // called)
        toggleDebugComponents(false);

        // create a grid style layout for positioning main components of the GUI
        contents.setLayout(new GridBagLayout());
        GridBagConstraints mainConstraints = new GridBagConstraints();

        // positionining the components vertically
        mainConstraints.gridy = 0;
        contents.add(infoPane, mainConstraints);

        mainConstraints.gridy = 1;
        contents.add(fieldView, mainConstraints);

        mainConstraints.gridy = 2;
        contents.add(population, mainConstraints);

        mainConstraints.gridy = 3;
        contents.add(controlPane, mainConstraints);

        // Add instruction Label and Species selector box
        JPanel bottomPane = new JPanel(new GridBagLayout());
        GridBagConstraints bottomConstraints = new GridBagConstraints();
        bottomConstraints.insets = new Insets(5, 0, 5, 10); // padding

        // default text if a generation number has not been specified
        instructionLabel = new JLabel("You need to start a simulation with a specified number of generations!");

        // position the components horizontolly
        bottomConstraints.gridx = 0;
        bottomPane.add(instructionLabel, bottomConstraints);

        // create the species selector and add it next to instruction label
        bottomConstraints.gridx = 1;
        speciesSelector = new JComboBox<Species>(Species.class.getEnumConstants());

        // disable species selection by default (to only be interactable when simulate
        // is called)
        speciesSelector.setEnabled(false);
        speciesSelector.addActionListener(this);
        bottomPane.add(speciesSelector, bottomConstraints);

        // add bottomPane at the end
        mainConstraints.gridy = 4;
        contents.add(bottomPane, mainConstraints);

        // close the frame if the window is closed
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
        setVisible(true);
    }

    /**
     * Toggle the species selector whilst appropriately changing the
     * instruction label
     * 
     * @param isAllowed boolean of if user is allowed to draw
     */
    public void toggleAllowUserToSelectSpecies(boolean isAllowed) {
        if (isAllowed) {
            instructionLabel.setText("You can click (and drag) the mouse to add cells on the grid!");
        } else {
            instructionLabel.setText("You cannot draw during a simulation or on a pre-populated field");
        }

        // set the status of the selector depending on if @param val is true or false
        speciesSelector.setEnabled(isAllowed);
    }

    /**
     * Toggle state of components set them as interactable or not
     */
    public void toggleDebugComponents(boolean value) {
        speedSlider.setEnabled(value);
        toggleSimButton.setEnabled(value);
        resetButton.setEnabled(value);
        populateButton.setEnabled(value);
    }

    /**
     * Creates the debug pane which is responsible for toggling the simulation and
     * affecting its speed. The components are initially disabled.
     * 
     * @return controlPane a JPanel object containing all of the components
     */
    public JPanel createControlPane() {
        // default values for the fields
        currentSliderValue = defaultSliderValue;
        paused = true;
        reset = false;

        // pane to hold components
        JPanel controlPane = new JPanel();
        controlPane.setLayout(new GridBagLayout()); // using the GridBagLayout

        // create all the buttons and the speed selection slider
        speedSlider = new JSlider(0, sliderUpperBound, currentSliderValue);
        speedSlider.setMinorTickSpacing(10);
        speedSlider.setSnapToTicks(true);
        speedSlider.setPaintTicks(true);
        speedSlider.setInverted(true); // invert the scale. eg from 0-100 to 100-0

        toggleSimButton = new JButton("Start");
        resetButton = new JButton("Reset");
        populateButton = new JButton("Populate field");

        // pane used to contain both the slider component and accompanying label
        JLabel speedLabel = new JLabel("Simulation Speed: ", JLabel.CENTER);
        JPanel speedPane = new JPanel(new GridBagLayout());

        GridBagConstraints speedConstraints = new GridBagConstraints();

        // create the constraints object used to position the buttons on the grid
        GridBagConstraints gridConstraints = new GridBagConstraints();
        gridConstraints.fill = GridBagConstraints.NONE;
        gridConstraints.insets = new Insets(5, 5, 0, 5); // add padding

        // add components to speed pane horizontally
        speedPane.add(speedLabel, speedConstraints);
        speedConstraints.gridx = 1;
        speedPane.add(speedSlider, speedConstraints);

        // add components to main pane horizontally
        controlPane.add(speedPane, gridConstraints);

        gridConstraints.gridx = 1;
        controlPane.add(toggleSimButton, gridConstraints);

        gridConstraints.gridx = 2;
        controlPane.add(resetButton, gridConstraints);

        gridConstraints.gridx = 3;
        controlPane.add(populateButton, gridConstraints);

        // add listeners for when the buttons are clicked
        toggleSimButton.addActionListener(this);
        resetButton.addActionListener(this);
        populateButton.addActionListener(this);

        return controlPane;
    }

    /**
     * Reset state of certain components to the defaults
     */
    public void resetComponents() {
        paused = true;
        reset = false;

        populateButtonPressed = false;
        populateButton.setEnabled(true);

        toggleSimButton.setText("Start");

        currentSliderValue = defaultSliderValue;
        speedSlider.setValue(currentSliderValue);

        speciesSelector.setSelectedIndex(0);
    }

    /**
     * Change state of fields on button press
     * 
     * @param event the event that occured
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == toggleSimButton) {
            paused = !paused; // toggle pause state
            // display appropriate text depending on pause state
            if (paused) {
                toggleSimButton.setText("Resume");
            } else {
                toggleSimButton.setText("Pause");
            }
        } else if (event.getSource() == resetButton) {
            reset = true;

        } else if (event.getSource() == populateButton) {
            populateButtonPressed = true;
            populateButton.setEnabled(false);
        }
    }

    /**
     * @return the species selected in the species selector JComboBox
     */
    public Species getSpeciesSelected() {
        // getSelectedItem returns generic object, so casting needed
        return (Species) speciesSelector.getSelectedItem();
    }

    /**
     * @return whether the simulation is paused or not
     */
    public boolean getPause() {
        return paused;
    }

    /**
     * @return whether the simulation needs to be reset or not
     */
    public boolean getReset() {
        return reset;
    }

    /**
     * @return the percentage of how far along the slider is
     */
    public float getDelayMultiplier() {
        // casting to increase accuracy (otherwise it becomes an integer)
        return (float) currentSliderValue / sliderUpperBound;
    }

    /**
     * 
     * @return if the population button has been pressed or not
     */
    public boolean getPopulateButtonPressed() {
        return populateButtonPressed;
    }

    /**
     * Update the currentSliderValue field with the value of the slider
     */
    public void updateSliderValue() {
        currentSliderValue = speedSlider.getValue();
    }

    /**
     * 
     * @return whether the mouse button is being pressed
     */
    public boolean getIsMouseBeingPressed() {
        return isMouseBeingPressed;
    }

    /**
     * Calculates the corresponding field Location that the mouse is at
     * 
     * @return Location at the row and column of the mouse click
     */
    public Location getMouseLocation() {
        // x-coordinate / tile width == column
        int gridx = (int) (mouseX / fieldView.getViewScalingFactor());
        // y-cordinate / tile height == row
        int gridy = (int) (mouseY / fieldView.getViewScalingFactor());

        // restrict xy-coordinates to the JPanel size
        gridx = Math.max(Math.min(gridx, fieldView.gridWidth - 1), 0);
        gridy = Math.max(Math.min(gridy, fieldView.gridHeight - 1), 0);

        return new Location(gridy, gridx);
    }

    /**
     * Show the current status of the field.
     * 
     * @param generation The current generation.
     * @param field      The field whose status is to be displayed.
     */
    public void showStatus(int generation, Field field) {
        if (!isVisible()) {
            setVisible(true);
        }

        genLabel.setText(GENERATION_PREFIX + generation);
        stats.reset();
        fieldView.preparePaint();

        updateSliderValue();

        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Cell cell = field.getObjectAt(row, col);

                if (cell != null && cell.isAlive()) {
                    stats.incrementCount(cell.getClass());
                    fieldView.drawMark(col, row, cell.getColor());
                } else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }

        stats.countFinished();
        population.setText(POPULATION_PREFIX + "  " + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * 
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field) {
        return stats.isViable(field);
    }

    /**
     * Provide a graphical view of a rectangular field. This is
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this
     * for your project if you like.
     */
    private class FieldView extends JPanel implements MouseMotionListener {
        private final int GRID_VIEW_SCALING_FACTOR = 6;
        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * 
         * @return the size of the tile
         */
        public int getViewScalingFactor() {
            return GRID_VIEW_SCALING_FACTOR;
        }

        /*
         * Update the mouse coordinates
         */
        public void updateMouseCoords(MouseEvent e) {
            mouseX = e.getX();
            mouseY = e.getY();
        }

        /**
         * Updates the location of the mouse when it is moved
         */
        @Override
        public void mouseMoved(MouseEvent event) {
            updateMouseCoords(event);
        }

        /**
         * If the mouse is pressed and moved, update mouse coordinates and set pressed
         * flag to true
         */
        @Override
        public void mouseDragged(MouseEvent event) {
            isMouseBeingPressed = true;
            updateMouseCoords(event);
        }

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width) {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);

            // add motion listener for when mouse is clicked and dragged
            addMouseMotionListener(this);

            // add normal listener for when it is simply clicked
            this.addMouseListener(new MouseAdapter() {

                /**
                 * set the mouse press flag to true once its been released and update the
                 * coordinates of where it was pressed
                 */
                @Override
                public void mousePressed(MouseEvent event) {
                    updateMouseCoords(event);
                    isMouseBeingPressed = true;
                }

                /**
                 * set the mouse press flag to false once its been released
                 */
                @Override
                public void mouseReleased(MouseEvent e) {
                    isMouseBeingPressed = false;
                }
            });
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize() {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                    gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint() {
            if (!size.equals(getSize())) { // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if (xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if (yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }

        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color) {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g) {
            if (fieldImage != null) {
                Dimension currentSize = getSize();
                if (size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                } else {
                    // Rescale the previous image.
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
