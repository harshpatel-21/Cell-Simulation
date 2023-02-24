import java.awt.Color;
import java.util.List;

/**
 * Represents a distinct type of bacteria.
 * Fun Fact: The name comes from the Helicobacter pylori bacterium. The bacterium 
 * is the most widespread infection in the world, with at least half of the world's
 * population being infected by it.
 * 
 * @author Ishab Ahmed, Harshraj Patel
 * @version 2023.02.07
 */

public class Helicobacter extends Cell {
    /**
     * Create a new Helicobacter.
     *
     * @param field    The field currently occupied.
     * @param location The location within the field.
     */
    public Helicobacter(Field field, Location location, Color col) {
        super(field, location, col);
        setInitialSpecies(Species.HELICOBACTER);
    }

    public Helicobacter(Field field, Location location) {
        this(field, location, Species.getColor(Species.HELICOBACTER));
    }

    /**
     * This is how the Helicobacter decides if it's alive or not
     */
    @Override
    public void act(int generation) {
        // get all the living neighbours of the same colour
        List<Cell> sameNeighbours = getLivingNeighboursBySpecies(getSpecies());

        // if the cell is dead, and has exactly 3 neighbours, dead cell is set alive
        if (!isAlive() && sameNeighbours.size() == 3) {
            setNextState(true);
        }

        if (isAlive()){
            // if exactly 1 helicobacter neighbour, cell dies
            if (sameNeighbours.size() == 1) {
                setNextState(false);
                // if exactly 3 helicobacter neighbours, cell is set alive
            } else if (sameNeighbours.size() == 3) {
                setNextState(true);
                // if more than 3 helicobacter neighbours, cell dies due to overcrowding
            } else if (sameNeighbours.size() > 3) {
                setNextState(false);
            }
        }
    }
}
