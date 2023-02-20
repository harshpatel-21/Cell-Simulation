import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * A graphical view of the simulation grid. The view displays a rectangle for
 * each location. Colors for each type of life form can be defined using the
 * setColor method.
 *
 * @author David J. Barnes, Michael Kölling & Jeffery Raphael, Harshraj Patel &
 *         Ishab Ahmed
 * @version 2022.01.06 (1)
 */

public class SimulatorView extends JFrame implements ActionListener, ChangeListener {
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    // Text for generation GUI label
    private final String GENERATION_PREFIX = "Generation: ";

    // Text for population GUI label
    private final String POPULATION_PREFIX = "Population: ";

    // GUI labels
    private JLabel genLabel, population, infoLabel;

    // Extends the multi-line plain text view to be suitable for a single-line
    // editor view. (part of Swing)
    private FieldView fieldView;

    // A statistics object computing and storing simulation information
    private FieldStats stats;

    JSlider speedSlider;
    JButton pauseButton;
    JButton resetButton;

    int sliderUpperBound = 100;
    int defaultSliderValue = 50;
    int currentSliderValue;

    boolean paused;
    boolean reset;
    double speedMultiplier;

    private Container contents = getContentPane();

    /**
     * Create a view of the given width and height.
     * 
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width) {

        stats = new FieldStats();

        setTitle("Life Simulation");
        genLabel = new JLabel(GENERATION_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel(" ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();

        // width of the screen
        int screen_width = (int) size.getWidth();

        // height of the screen
        int screen_height = (int) size.getHeight();

        // center the window
        setLocation((int) (screen_width * 0.5 - (width * 3)), (int) (screen_height * 0.5 - (height * 3)));

        fieldView = new FieldView(height, width);

        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(genLabel, BorderLayout.WEST);
        infoPane.add(infoLabel, BorderLayout.CENTER);

        JPanel bottomPane = createBottomPane();

        // create a grid style layout for positioning main components of the GUI
        contents.setLayout(new GridBagLayout());
        GridBagConstraints mainConstraints = new GridBagConstraints();

        // positionining the different components
        mainConstraints.gridy = 0;
        contents.add(infoPane, mainConstraints);

        mainConstraints.gridy = 1;
        contents.add(fieldView, mainConstraints);

        mainConstraints.gridy = 2;
        contents.add(population, mainConstraints);

        mainConstraints.gridy = 3;
        contents.add(bottomPane, mainConstraints);

        // close the frame if the window is closed
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
        setVisible(true);
    }

    public void enableBottomComponents() {
        speedSlider.setEnabled(true);
        pauseButton.setEnabled(true);
        resetButton.setEnabled(true);
    }

    /**
     * creates the Bottom Panel along with the default values for it. Called when
     * its created and when reset.
     * 
     * @return JPanel containing all the boxes in the bottom Pane
     */
    public JPanel createBottomPane() {
        // default values for the fields
        currentSliderValue = defaultSliderValue;
        paused = reset;
        reset = false;

        // the outer panel which will store our buttons
        JPanel bottomPane = new JPanel();
        bottomPane.setLayout(new GridBagLayout()); // using the GridBagLayout

        // create all the buttons and the speed selection slider
        speedSlider = new JSlider(0, sliderUpperBound + 1, currentSliderValue);
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setSnapToTicks(true);
        speedSlider.setPaintTicks(true);
        speedSlider.setInverted(true); // invert the scale. eg from 0-100 to 100-0
        speedSlider.setEnabled(false);

        pauseButton = new JButton("Pause");
        pauseButton.setEnabled(false);

        resetButton = new JButton("Reset");
        resetButton.setEnabled(false);

        // create an innter panel to group Speed label Text and the Speed Selection
        // Slider
        JPanel speedPanel = new JPanel(new GridBagLayout());
        JLabel speedLabel = new JLabel("Simulation Speed: ", JLabel.CENTER);

        GridBagConstraints speedConstraints = new GridBagConstraints();

        // create the constraints object used to position the buttons on the grid
        GridBagConstraints gridConstraints = new GridBagConstraints();
        gridConstraints.fill = GridBagConstraints.NONE;
        gridConstraints.insets = new Insets(13, 0, 0, 0); // add 13px of vertical padding

        // add the speed panel (includes text and slider) to column 1
        speedConstraints.gridx = 0;
        speedConstraints.gridy = 0;
        speedPanel.add(speedLabel, speedConstraints);

        speedConstraints.gridx = 1;
        speedPanel.add(speedSlider, speedConstraints);

        bottomPane.add(speedPanel, gridConstraints);

        // add the Start button in column 2
        gridConstraints.gridx = 2;
        bottomPane.add(pauseButton, gridConstraints);

        // add the Reset button in column 3
        gridConstraints.gridx = 3;
        bottomPane.add(resetButton, gridConstraints);

        // add action listerners for when the buttons are clicked
        pauseButton.addActionListener(this);
        resetButton.addActionListener(this);
        speedSlider.addChangeListener(this);

        return bottomPane;
    }

    /**
     * reset the fields that are used to determine the state of the simulation
     * to their default values
     */
    public void resetBottomPane() {
        paused = true;
        reset = false;
        currentSliderValue = defaultSliderValue;
        pauseButton.setText("Start");
        speedSlider.setValue(currentSliderValue);
    }

    /**
     * Listen for button presses
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == pauseButton) {
            paused = !paused; // toggle pause state

            // display appropriate text depending on pause state
            if (paused) {
                pauseButton.setText("Resume");
            } else {
                pauseButton.setText("Pause");
            }
        } else if (e.getSource() == resetButton) {
            // set reset to true, and
            reset = true;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == speedSlider) {
            if (!speedSlider.getValueIsAdjusting()) {
                currentSliderValue = speedSlider.getValue();
            }
        }
    }

    public boolean getPause() {
        return paused;
    }

    public boolean getReset() {
        return reset;
    }

    /**
     * @return how far along the slider is.
     */
    public double getDelayMultiplier() {
        return ((double) currentSliderValue / (double) sliderUpperBound);
    }

    public void resetState() {
        resetBottomPane();
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text) {
        infoLabel.setText(text);
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
    private class FieldView extends JPanel {
        private final int GRID_VIEW_SCALING_FACTOR = 6;
        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width) {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
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
