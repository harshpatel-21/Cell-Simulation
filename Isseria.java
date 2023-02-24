import java.awt.Color;
import java.util.List;
import java.util.Random;

/**
 * Represents a distinct type of bacterium.
 * Fun Fact: The name was inspired by a genus of bacteria called Neisseria.
 * Among the eleven species of Neisseria that colonise humans, only two are 
 * classified as pathogens.
 * 
 * @author Ishab Ahmed, Harshraj Patel
 * @version 2023.02.07
 */

public class Isseria extends Cell {
    /**
     * Create a new Isseria.
     *
     * @param field    The field currently occupied.
     * @param location The location within the field.
     */
    public Isseria(Field field, Location location, Color col) {
        super(field, location, col);
        setInitialSpecies(Species.ISSERIA);
    }

    public Isseria(Field field, Location location) {
        this(field, location, Cell.isseColour);
    }

    /**
     * This is how the Isseria decides if it's alive or not
     */
    @Override
    public void act(int generation) {
        Random rand = new Random();

        // get all the living neighbours of the same colour
        List<Cell> sameNeighbours = getLivingNeighboursBySpecies(getSpecies());

        // if exactly 2 neighbours, there is a chance that the cell is set alive
        if (sameNeighbours.size() == 2) {
            if (rand.nextDouble() < 0.6)
                setNextState(true);
        }
        // if exactly 4 neighbours, there is a chance that the cell is set alive
        else if (sameNeighbours.size() == 4) {
            if (rand.nextDouble() < 0.5)
                setNextState(true);
        } else {
            // if any other number of neighbours, cell dies or stays dead
            setNextState(false);
        }
    }
}
