import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.HashMap;

/**
 * A class representing the shared characteristics of all forms of life
 *
 * @author David J. Barnes, Michael Kölling & Jeffery Raphael extended by
 *         Harshraj Patel & Ishab Ahmed
 * @version 2023.02.20
 */

public abstract class Cell {
    // default colour of cells
    private Color heliColour = new Color(200, 255, 255);
    private Color mycoColour = Color.ORANGE;
    private Color isseColour = Color.MAGENTA;
    private Color infectedColour = Color.RED;
    private Color defaultHeliColor = new Color(200, 255, 255);

    HashMap<Species, Color> speciesColor = new HashMap<>();

    // Whether the cell is alive or not.
    private boolean alive;

    // Whether the cell will be alive in the next generation.
    private boolean nextAlive;

    // The cell's field.
    private Field field;

    // The cell's position in the field.x
    private Location location;

    // The cell's color
    private Color color = Color.white;

    private Color nextColor = color;

    // probability that a cell will get infected
    private double infectRate = 0.10;

    protected Species cellSpecies, nextCellSpecies;

    /**
     * Create a new cell at location in field.
     *
     * @param field    The field currently occupied.
     * @param location The location within the field.
     */
    public Cell(Field field, Location location, Color col) {

        // place species' respective colour in a hashmap to avoid if/switch statements
        // when retrieving a colour for a species
        speciesColor.put(Species.HELICOBACTER, heliColour);
        speciesColor.put(Species.MYCOPLASMA, mycoColour);
        speciesColor.put(Species.ISSERIA, isseColour);
        speciesColor.put(Species.INFECTED, infectedColour);

        this.field = field;

        setLocation(location);

        // set initial colour
        color = col;
        nextColor = col;

        // set initial state
        alive = true;
        nextAlive = false;
    }

    /**
     * Make this cell act - that is: the cell decides it's status in the
     * next generation.
     */
    abstract public void act(int generation);

    /**
     * Check whether the cell is alive or not.
     * 
     * @return true if the cell is still alive.
     */
    protected boolean isAlive() {
        return alive;
    }

    /**
     * Indicate that the cell is no longer alive.
     */
    protected void setDead() {
        alive = false;
    }

    /**
     * Indicate that the cell will be alive or dead in the next generation.
     */
    public void setNextState(boolean value) {
        nextAlive = value;
    }

    /**
     * Changes the state of the cell
     */
    public void updateState() {
        alive = nextAlive;
        color = nextColor;
        cellSpecies = nextCellSpecies;
    }

    /**
     * @return whether the cell is alive in the next iteration
     */
    public boolean getNextAlive() {
        return nextAlive;
    }

    /**
     * Changes the color of the cell
     * 
     * @param col The colour the cell should change to
     */
    public void setColor(Color col) {
        color = col;
    }

    /**
     * Sets the colour the cell should be in the next generation
     * 
     * @param col The colour the cell should change to
     */
    public void setNextColor(Color col) {
        nextColor = col;
    }

    /**
     * Returns the cell's color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Return the cell's location.
     * 
     * @return The cell's location.
     */
    protected Location getLocation() {
        return location;
    }

    /**
     * Set the species of the cell when the Cell is created
     * 
     * @param species the species to set to.
     */
    protected void setInitialSpecies(Species species) {
        nextCellSpecies = species;
        cellSpecies = species;
    }

    public void setState(boolean val) {
        alive = val;
    }

    /**
     * Set the species of the cell for the next generation.
     * 
     * @param species the species to change to.
     */
    protected void setNextSpecies(Species species) {
        nextCellSpecies = species;
        setNextColor(getSpeciesColor(species));
    }

    protected Color getSpeciesColor(Species species) {
        // set the colour of the cell in the next generation according to its species
        return speciesColor.get(species);
    }

    /**
     * set the species of a cell in the current and next generation
     * used mainly when adding new cell using mouse
     * 
     * @param species
     */
    protected void setSpecies(Species species) {
        cellSpecies = species;
        nextCellSpecies = species;
        setNextColor(getSpeciesColor(species));
        setColor(getSpeciesColor(species));
    }

    /**
     * 
     * @return the species of the cell in the current generation.
     */
    protected Species getSpecies() {
        return cellSpecies;
    }

    /**
     * Place the cell at the new location in the given field.
     * 
     * @param location The cell's location.
     */
    protected void setLocation(Location location) {
        this.location = location;
        field.place(this, location);
    }

    /**
     * Return the cell's field.
     * 
     * @return The cell's field.
     */
    protected Field getField() {
        return field;
    }

    /**
     * Gets all of the living neighbours of the cell and filters it by the species
     * passed in
     * 
     * @param species The species of the neighbours to be filtered by
     * @return A list of neighbouring cells of the same species as the parameter
     */
    protected List<Cell> getLivingNeighboursBySpecies(Species species) {
        List<Cell> livingNeighbours = field.getLivingNeighbours(getLocation());
        Stream<Cell> sameNeighbours = livingNeighbours.stream().filter(cell -> cell.getSpecies() == species);
        return sameNeighbours.collect(Collectors.toList());
    }

    /**
     * Cell is engulfed by an infected cell is dead and within the probability.
     * Cell is engulfed by a Helicobacter if matching the rules.
     */
    protected void getEngulfedIfPossible() {
        Random rand = Randomizer.getRandom();

        // if the cell is not a Helicobacter
        if (getSpecies() != Species.HELICOBACTER && (getSpecies() != Species.MYCOPLASMA && !isAlive())) {
            // get number of helicobacter neighbours
            int heliNum = getLivingNeighboursBySpecies(Species.HELICOBACTER).size();

            double engulfProbability = 0.135;

            if (getSpecies() == Species.INFECTED){
                engulfProbability = 0.2;
            }

            if (getSpecies() == Species.EMPTYCELL){
                engulfProbability = 0.009;
            }

            // there is a probability that the cell is turned in to a Helicobacter if it is
            // surrounded by 1 to 3 (inclusive) Helicobacter cells
            if ((heliNum >= 1 && heliNum <= 3) && rand.nextDouble() < engulfProbability) {
                setNextState(true);
                setNextSpecies(Species.HELICOBACTER);
            }
        }
    }

    /**
     * Infects the cell if it is surrounded by infected cells and is within the
     * probability
     */
    protected void getInfectedIfPossible() {
        Random rand = new Random();

        // get all the infected neighbours
        List<Cell> infectedNeighbours = getLivingNeighboursBySpecies(Species.INFECTED);

        // every generation, Helicobacter infection rate decreased by 0.1%
        if (getSpecies() == Species.HELICOBACTER) {
            infectRate *= 0.999;
        }

        // if the cell is not infected
        if (getSpecies() != Species.INFECTED) {
            // if the cell is surrounded by more than one infected neighbour
            if (infectedNeighbours.size() >= 1) {
                // there is a chance that the cell is infected
                if (rand.nextDouble() < infectRate) {
                    setNextState(true);
                    setNextSpecies(Species.INFECTED);
                }
            }
        }

        // Allow infected cells to devour dead cells which aren't Helicobacters
        if (!(isAlive()) && getSpecies() != Species.HELICOBACTER) {
            // get the number of infected neighbours
            int infectedNum = getLivingNeighboursBySpecies(Species.INFECTED).size();

            // if there are more than 3 infected neighbours,
            // there is a probability for that cell to become infected
            if (infectedNum > 3 && rand.nextDouble() < 0.11) {
                setNextState(true);
                setNextSpecies(Species.INFECTED);
            }
        }

    }

    /**
     * Changes the cell becomes a different cell when two different, neighbouring
     * cells collide
     */
    protected void breedIfPossible() {
    };

    /**
     * Changes the colour of the Helicobacter cell based on the current generation
     * 
     * @param generation The current generation
     */
    protected void darkenHeliColour(int generation) {
        // get the current RGB values of Helicobacter color
        int r, g, b;

        r = defaultHeliColor.getRed();
        g = defaultHeliColor.getGreen();
        b = defaultHeliColor.getBlue();

        // calculate new values for darker Helicobacter color

        r = (int) Math.max(r - (0.1 * generation), 52);
        g = (int) Math.max(g - (0.1 * generation), 126);
        b = 255;

        // set the new Helicobacter's static color
        heliColour = new Color(r, g, b);
        // Species.setColor(Species.HELICOBACTER, heliColor);

        // change the colour of the cell if it's a Helicobacter
        if (getSpecies() == Species.HELICOBACTER)
            setNextColor(heliColour);

    }

}
