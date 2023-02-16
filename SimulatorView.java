import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A graphical view of the simulation grid. The view displays a rectangle for
 * each location. Colors for each type of life form can be defined using the
 * setColor method.
 *
 * @author David J. Barnes, Michael Kölling & Jeffery Raphael
 * @version 2022.01.06 (1)
 */

public class SimulatorView extends JFrame implements ActionListener{
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

    JComboBox<String> speedBox;
    JButton pauseButton;
    JButton resetButton;
    JPanel bottomPane;

    boolean paused = false;
    boolean reset = false;
    double speedMultiplier;


    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width) {
        stats = new FieldStats();

        setTitle("Life Simulation");
        genLabel = new JLabel(GENERATION_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        
        // width will store the width of the screen
        int screen_width = (int)size.getWidth();
        
        // height will store the height of the screen
        int screen_height = (int)size.getHeight();

        // center the window to the center of the screen
        setLocation((int) (screen_width*0.5 - (width*3)), (int) (screen_height*0.5 - (height*3)));

        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        // create the panel that is to go at the bottom
        createBottomPane();

        JPanel infoPane = new JPanel(new BorderLayout());

        infoPane.add(genLabel, BorderLayout.WEST);
        infoPane.add(infoLabel, BorderLayout.CENTER);

        contents.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.gridx=0;
        c.gridy=0;
        contents.add(infoPane, c);
        c.gridx=0;
        c.gridy=1;
        contents.add(fieldView, c);
        c.gridx=0;
        c.gridy=2;
        contents.add(population,c);
        c.gridx=0;
        c.gridy=3;
        contents.add(bottomPane, c);
        System.out.println(pauseButton.getHeight()+" "+pauseButton.getWidth());

        pack();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        if (e.getSource() == pauseButton){
            paused = !paused;
            if (paused) {
                pauseButton.setText("Resume");
            }
            else {
                pauseButton.setText("Pause");
            };
        }
        else if (e.getSource() == resetButton){
            reset=true;
        }
        else if (e.getSource() == speedBox){
            speedMultiplier = (double) Double.parseDouble(speedBox.getSelectedItem().toString());
        }
    } 

    public boolean getPause(){
        return paused;
    }

    public boolean getReset(){
        return reset;
    }

    public double getSpeed(){
        return speedMultiplier;
    }

    public void resetState(){
        resetBottomPane();
    }

    /**
     * creates the Bottom Panel along with the default values for it. Called when its created and when reeset.
     */
    public void createBottomPane(){
        // default values for the fields
        speedMultiplier = 1;
        paused = reset;
        reset = false; 

        // the outer panel which will store our buttons
        bottomPane = new JPanel();
        bottomPane.setLayout(new GridBagLayout()); // using the GridBagLayout

        // create all the buttons and the speed selection box
        String[] speedMultipliers = {"0.1","0.5","1","10","100","1000"};
        speedBox = new JComboBox<String>(speedMultipliers);
        pauseButton = new JButton("Pause");
        resetButton = new JButton("Reset");

        // create an innter panel to group Speed label Text and the Speed Selection box
        JPanel speedPanel = new JPanel(new GridBagLayout());
        JLabel speedLabel = new JLabel("Speed Multiplier: ",JLabel.CENTER);
        GridBagConstraints speedConstraints = new GridBagConstraints();
        
        // create the constraints object used to position the buttons on the grid
        GridBagConstraints gridConstraints = new GridBagConstraints();
        gridConstraints.fill = GridBagConstraints.NONE;
        gridConstraints.insets = new Insets(13,0,0,0); // add 13px of vertical padding

        // add the speed panel (includes text and box) to column 1
        gridConstraints.gridx = 1;
        speedConstraints.gridx = 0;
        speedConstraints.gridy = 0;
        speedPanel.add(speedLabel,speedConstraints);
        
        speedConstraints.gridx = 1;
        speedConstraints.gridy = 0;
        speedPanel.add(speedBox,speedConstraints);

        speedBox.setSelectedIndex(2); // default speed is at index 1, which is the multiplier 1
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
        speedBox.addActionListener(this);
    }

    public void resetBottomPane(){
        paused = true;
        reset=false;
        speedMultiplier=1;

        pauseButton.setText("Start");
        speedBox.setSelectedIndex(2); // default speed is at index 1, which is the multiplier 1

    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text) {
        infoLabel.setText(text);
    }

    /**
     * Show the current status of the field.
     * @param generation The current generation.
     * @param field The field whose status is to be displayed.
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
          }
          else {
            fieldView.drawMark(col, row, EMPTY_COLOR);
          }
        }
      }

      stats.countFinished();
      population.setText(POPULATION_PREFIX + "  "+stats.getPopulationDetails(field));
      fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
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
            if (!size.equals(getSize())) {  // if the size has changed...
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
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
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
                }
                else {
                    // Rescale the previous image.
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
