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

    // Flag to check if the simulator is empty or not.
    private boolean populatedWithCells;

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
        view = new SimulatorView(depth, width);

        // Setup a valid starting point.
        reset(false);
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
        view.toggleDebugComponents(true);

        // initially, start off with blank canvas and allow user to draw
        view.toggleAllowUserToSelectSpecies(true);

        // while true used to ensure simulation GUI persists even when board is empty
        // (as it is in start state), instead of closing when 0 alive cells
        while (true) {
            view.isViable(field);

            // simulate while the simulator is not paused and the generation limit has not
            // been reached
            if (!view.getPause() && generation < numGenerations) {
                // whilst simulation is being ran, don't allow modification of field
                view.toggleAllowUserToSelectSpecies(false);

                simOneGeneration();

                // delay to control the speed of the simulation
                delay((int) (350 * view.getDelayMultiplier()));
            }

            // if in drawing canvas mode
            else if (!populatedWithCells) {

                // check to see if a user is adding cells onto the grid and add if possible
                if (view.getIsMouseBeingPressed()) {
                    view.toggleDebugComponents(true); // allow the bottom components to be interactable
                    drawCell(view.getMouseLocation());
                }

                // toggle the ability to draw cells depending on whether simulation is paused.
                view.toggleAllowUserToSelectSpecies(view.getPause());

            }

            // if the populate button was pressed, reset the field with pre-populated cells
            if (view.getPopulateButtonPressed()) {
                populatedWithCells = true; // since field is now populated with cells
                reset(true);
                view.resetComponents();
                view.toggleAllowUserToSelectSpecies(false);
            }

            // reset simulation to default empty field
            if (view.getReset()) {
                reset(false);
                view.resetComponents();
                populatedWithCells = false; // since field is empty, its not populated with cells
            }
        }
    }

    /**
     * Draws a cell to the field at a given location
     * 
     * @param location
     */
    public void drawCell(Location location) {
        Species speciesSelected = view.getSpeciesSelected();

        Cell cellToDraw = field.getObjectAt(location);

        // set location to selected species of cell
        switch (speciesSelected) {

            case HELICOBACTER:
                cellToDraw.setSpecies(Species.HELICOBACTER);
                break;

            case MYCOPLASMA:
                cellToDraw.setSpecies(Species.MYCOPLASMA);
                break;

            case ISSERIA:
                cellToDraw.setSpecies(Species.ISSERIA);
                break;

            case INFECTED:
                cellToDraw.setSpecies(Species.INFECTED);
                break;
        }

        // an empty cell is just a dead cell
        if (speciesSelected == Species.EMPTYCELL) {
            cellToDraw.setState(false);
        } else {
            cellToDraw.setState(true);
        }

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
     * Reset the field to initial state.
     * 
     * @param prePopulate boolean determining whether field is pre-populated or
     *                    empty when reset
     */
    public void reset(boolean prePopulate) {
        generation = 0;

        cells.clear();
        populate(prePopulate);

        // Show the starting state in the view
        view.showStatus(generation, field);
    }

    /**
     * Populates entire field with cells.
     * 
     * @param prePopulate determines whether the field is pre-populated with living
     *                    cells, or the board is cleared
     */
    private void populate(boolean prePopulate) {
        field.clear();
        // iterates over each location in the field
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);

                // either field will be pre-populated with cells
                if (prePopulate) {
                    populateSingleLocation(location);
                } else {
                    // or made empty, ready for the user to draw
                    createDeadCell(location);
                }
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
