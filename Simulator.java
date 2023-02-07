import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A Life (Game of Life) simulator, first described by British mathematician
 * John Horton Conway in 1970.
 *
 * @author David J. Barnes, Michael Kölling & Jeffery Raphael
 * @version 2022.01.06 (1)
 */

public class Simulator {
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 140;

    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 110;

    // The probability that a Mycoplasma is alive
    private static final double MYCOPLASMA_ALIVE_PROB = 0.15;
    
    // The probability that a Mycoplasma is alive
    private static final double HELICOBACTER_ALIVE_PROB = 0.125;
    
    // The probability that a Mycoplasma is alive
    private static final double ISSERIA_ALIVE_PROB = 0.09;

    // List of cells in the field.
    private List<Cell> cells;

    // The current state of the field.
    private Field field;

    // The current generation of the simulation.
    private int generation;

    // A graphical view of the simulation.
    private SimulatorView view;

    /**
     * Execute simulation
     */
    public static void main(String arg1) {
      Simulator sim = new Simulator();
      sim.simulate(100);
    }

    /**
     * Construct a simulation field with default size.
     */
    public Simulator() {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        if (width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        cells = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 generations).
     */
    public void runLongSimulation() {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of
     * generations.  Stop before the given number of generations if the
     * simulation ceases to be viable.
     * @param numGenerations The number of generations to run for.
     */
    public void simulate(int numGenerations) {
        for (int gen = 1; gen <= numGenerations && view.isViable(field); gen++) {
            simOneGeneration();
            delay(10);
        }
    }

    /**
     * Run the simulation from its current state for a single generation.
     * Iterate over the whole field updating the state of each life form.
     */
    public void simOneGeneration() {
        generation++;
        List<Cell[]> newCells = new ArrayList<>();
        
        for (Iterator<Cell> it = cells.iterator(); it.hasNext(); ) {
            Cell cell = it.next();
            cell.act();
            Cell[] newCell = cell.engulfIfPossible();
            if (newCell != null) newCells.add(newCell);
        }

        for (Cell cell : cells) {
          cell.updateState();
        }
        
        for (Cell[] cell : newCells) {
            cells.add(cell[0]);
            cells.remove(cell[1]);
        }
        view.showStatus(generation, field);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        generation = 0;
        cells.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(generation, field);
    }

    /**
     * Randomly populate the field live/dead life forms
     */
    private void populate() {
        Random rand = Randomizer.getRandom();
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                populateSingleLocation(location);
            }
        }
    }
    
    private void populateSingleLocation(Location location) {
        Random rand = Randomizer.getRandom();
        int row = location.getRow();
        int col = location.getCol();
        
        if (rand.nextDouble() <= MYCOPLASMA_ALIVE_PROB && row > DEFAULT_DEPTH/2) {
            Mycoplasma myco = new Mycoplasma(field, location, Color.ORANGE);
            cells.add(myco);
        }
        else if (rand.nextDouble() <= HELICOBACTER_ALIVE_PROB && col >= DEFAULT_WIDTH/2 && row <= DEFAULT_DEPTH/2) {
            Helicobacter heli = new Helicobacter(field, location, Color.CYAN);
            cells.add(heli);
        }
        else if (rand.nextDouble() <= ISSERIA_ALIVE_PROB && col <= DEFAULT_WIDTH/2 && row <= DEFAULT_DEPTH/2) {
            Isseria isse = new Isseria(field, location, Color.MAGENTA);
            cells.add(isse);
        }
        else createDeadCell(location);
    }
    
    private void createDeadCell(Location location) {
        Random rand = Randomizer.getRandom();
        int cellProb = rand.nextInt(3);
        if (cellProb == 0) {
            Mycoplasma myco = new Mycoplasma(field, location, Color.ORANGE);
            myco.setDead();
            cells.add(myco);
        }
        else if (cellProb == 1) {
            Helicobacter heli = new Helicobacter(field, location, Color.CYAN);
            heli.setDead();
            cells.add(heli);
        }
        else if (cellProb == 2) {
            Isseria isse = new Isseria(field, location, Color.MAGENTA);
            isse.setDead();
            cells.add(isse);
        }
        
        int row = location.getRow();
        int col = location.getCol();
        
        if (col <= DEFAULT_WIDTH/2 && row <= DEFAULT_DEPTH/2) {
            Isseria isse = new Isseria(field, location, Color.MAGENTA);
            isse.setDead();
            cells.add(isse);
        } else if (col >= DEFAULT_WIDTH/2 && row <= DEFAULT_DEPTH/2) {
            Helicobacter heli = new Helicobacter(field, location, Color.CYAN);
            heli.setDead();
            cells.add(heli);
        } else {
            Mycoplasma myco = new Mycoplasma(field, location, Color.ORANGE);
            myco.setDead();
            cells.add(myco);
        }
    }

    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec) {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
