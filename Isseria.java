import java.awt.Color;
import java.util.List;
import java.util.Random;

/**
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
    }

    public Isseria(Field field, Location location) {
        super(field, location, Color.MAGENTA);
    }

    /**
     * This is how the Isseria decides if it's alive or not
     */
    public void act(int generation) {
        Random rand = new Random();

        // get all the living neighbours of the same colour
        List<Cell> sameNeighbours = getLivingNeighboursByColour(getColor());

        // if exactly 2 neighbours, there is a chance that the cell is set alive
        if (sameNeighbours.size() == 2) {
            if (rand.nextDouble() < 0.6)
                setNextState(true);
        }
        // if exactly 4 neighbours, there is a chance that the cell is set alive
        else if (sameNeighbours.size() == 4) {
            if (rand.nextDouble() < 0.5)
                setNextState(true);
        // if any other number of neighbours, cell dies or stays dead
        } else {
            setNextState(false);
        }
    }
}
