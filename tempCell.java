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

public class tempCell extends Cell {
    /**
     * Create a new Isseria.
     *
     * @param field    The field currently occupied.
     * @param location The location within the field.
     */
    public tempCell(Field field, Location location, Color col) {
        super(field, location, col);
        setInitialSpecies(Species.TEMPCELL);
    }

    public tempCell(Field field, Location location) {
        this(field, location, Color.BLACK);
    }


    /**
     * This is how the Mycoplasma decides if it's alive or not
     */
    @Override
    public void act(int generation) {
        // get all the living neighbours of the same colour
        // List<Cell> sameNeighbours = getLivingNeighboursBySpecies(getSpecies());
        List<Cell> neighbours = getField().getLivingNeighbours(getLocation());

        Random rand = Randomizer.getRandom();

        if (isAlive()) {
            // live on if there are 2 or 3 neighbours
            if (neighbours.size() > 1 && neighbours.size() < 4)
                setNextState(true);
            else {
                setNextState(false);
            }
        }
        // if the cell is dead and has exactly 3 neighbours, revive it
        else if (neighbours.size() >= 2)
            setNextState(true);
    }
}
