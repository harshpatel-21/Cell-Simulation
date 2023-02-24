import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A Life (Game of Life) simulator, first described by British mathematician
 * John Horton Conway in 1970.
 *
 * @author David J. Barnes, Michael Kölling & Jeffery Raphael, extended by
 *         Harshraj Patel & Ishab Ahmed
 * @version 2023.02.20
 */

public class Simulator {
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 140;

    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 110;

    // The probability that a Mycoplasma is alive
    private static final double MYCOPLASMA_ALIVE_PROB = 0.2;

    // The probability that a Mycoplasma is alive
    private static final double HELICOBACTER_ALIVE_PROB = 0.2;

    // The probability that a Mycoplasma is alive
    private static final double ISSERIA_ALIVE_PROB = 0.15;

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
     * 
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
        view = new SimulatorView(depth, width, this);

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
     * generations. Stop before the given number of generations if the
     * simulation ceases to be viable.
     * 
     * @param numGenerations The number of generations to run for.
     */
    public void simulate(int numGenerations) {
        view.toggleDebugComponents(true); // allow the bottom components to be interactable

        while (view.isViable(field)) {
            // simulate while the simulator is not paused and the generation limit has not
            // been reached
            if (!view.getPause() && generation < numGenerations) {
                simOneGeneration();
                // delay to control the speed of the simulation
                delay((int) (350 * view.getDelayMultiplier()));
            }
            
            // reset simulation
            if (view.getReset()) {
                reset();
                view.resetBottomPane();
            }

            // check to see if a cell is to be added onto the grid if the view is paused.
            if (view.getIsMouseBeingPressed() && view.getPause()){
                Location gridCoords = view.getMouseCoords();
                addCellIfPossible(gridCoords);
            }

            
        }
    }

    /**
     * add a cell in a given location
     * @param location
     */
    public void addCellIfPossible(Location location){
        Species speciesSelected = view.getSpeciesSelected();
        Cell cellToAdd = null;

        // dont continue if no species was selected
        if (speciesSelected == null){
            return;
        }

        // create the appropriate cell depending on the selected species 
        switch (speciesSelected){
            case HELICOBACTER:
                cellToAdd = new Helicobacter(field, location);
                break;

            case MYCOPLASMA:
                cellToAdd = new Mycoplasma(field, location);
                break;

            case ISSERIA:
                cellToAdd = new Isseria(field, location);
                break;
            
            case TEMPCELL:
                cellToAdd = new tempCell(field, location);
                break;

            case INFECTED:
                cellToAdd = new Helicobacter(field, location);
                cellToAdd.setSpecies(Species.INFECTED);
                break;
        }

        // if the class of the selected species exists, add the cell to the field
        if (cellToAdd == null){
            return;
        }
        field.place(cellToAdd, location);
        cells.add(cellToAdd);
        
        // update field
        view.showStatus(generation, field);
    
    }

    /**
     * Run the simulation from its current state for a single generation.
     * Iterate over the whole field updating the state of each life form.
     */
    public void simOneGeneration() {
        generation++;

        for (Iterator<Cell> it = cells.iterator(); it.hasNext();) {
            Cell cell = it.next();
            cell.act(generation);

            // if the cell is not infected, it can breed and/or get infected
            if (cell.getColor() != Color.RED) {
                cell.breedIfPossible();
                cell.getInfectedIfPossible();
            }

            cell.getEngulfedIfPossible();
        }

        for (Cell cell : cells) {
            cell.updateState();
            cell.darkenHeliColour(generation);
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
        field.clear();
        // iterates over each location in the field
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                populateSingleLocation(location);
            }
        }
    }

    /**
     * For each cell in the field, populate it with a random cell, depending on the
     * probability that it is alive, and the region on the field
     * 
     * @param location The current location of the grid being populated
     */
    private void populateSingleLocation(Location location) {
        Random rand = Randomizer.getRandom();

        int row = location.getRow();
        int col = location.getCol();

        // spawn Mycoplasma in bottom half if random number within probability
        if (rand.nextDouble() <= MYCOPLASMA_ALIVE_PROB && row > DEFAULT_DEPTH / 2) {
            Mycoplasma myco = new Mycoplasma(field, location);
            cells.add(myco);
            // spawn Helicobacter in top right quadrant if random number within probability
        } else if (rand.nextDouble() <= HELICOBACTER_ALIVE_PROB && col >= DEFAULT_WIDTH / 2
                && row <= DEFAULT_DEPTH / 2) {
            Helicobacter heli = new Helicobacter(field, location);
            cells.add(heli);
            // spawn Isseria in top left quadrant if random number within probability
        } else if (rand.nextDouble() <= ISSERIA_ALIVE_PROB && col <= DEFAULT_WIDTH / 2 && row <= DEFAULT_DEPTH / 2) {
            Isseria isse = new Isseria(field, location);
            cells.add(isse);
            // otherwise, create a dead cell at that location
        } else
            createDeadCell(location);
    }

    /**
     * Populate the location with a dead cell. The cell type is dependent on the
     * region on the field that the location is
     * 
     * @param location The current location of the grid being populated
     */
    private void createDeadCell(Location location) {
        int row = location.getRow();
        int col = location.getCol();

        // spawn dead Isseria in top left quadrant
        if (col <= DEFAULT_WIDTH / 2 && row <= DEFAULT_DEPTH / 2) {
            Isseria isse = new Isseria(field, location);
            isse.setDead();
            cells.add(isse);
            // spawn dead Helicobacter in top right quadrant
        } else if (col >= DEFAULT_WIDTH / 2 && row <= DEFAULT_DEPTH / 2) {
            Helicobacter heli = new Helicobacter(field, location);
            heli.setDead();
            cells.add(heli);
            // spawn dead Mycoplasma in bottom half
        } else {
            Mycoplasma myco = new Mycoplasma(field, location);
            myco.setDead();
            cells.add(myco);
        }
    }

    /**
     * Pause for a given time.
     * 
     * @param millisec The time to pause for, in milliseconds
     */
    private void delay(int millisec) {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException ie) {
            // wake up
        }
    }
}
