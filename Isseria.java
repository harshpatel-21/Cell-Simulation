import java.awt.Color;
import java.util.List;

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

        // get all the living neighbours of the same colour
        List<Cell> sameNeighbours = getLivingNeighboursByColour(getColor());

        // if exactly 1 isseria neighbour, cell dies
        if (sameNeighbours.size() == 1) {
            setNextState(false);
        // if exactly 3 isseria neighbours, cell is set alive
        } else if (sameNeighbours.size() == 3) {
            setNextState(true);
        // if more than 3 isseria neighbours, cell dies due to overcrowding
        } else if (sameNeighbours.size() > 3) {
            setNextState(false);
        }
    }
}
