import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Simplest form of life.
 * Fun Fact: Mycoplasma are one of the simplest forms of life.  A type of
 * bacteria, they only have 500-1000 genes! For comparison, fruit flies have
 * about 14,000 genes.
 *
 * @author David J. Barnes, Michael Kölling & Jeffery Raphael, extended by Harshraj Patel & Ishab Ahmed
 * @version 2022.01.06 (1)
 */

public class Mycoplasma extends Cell {

    /**
     * Create a new Mycoplasma.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Mycoplasma(Field field, Location location, Color col) {
        super(field, location, col);
    }

    /**
     * This is how the Mycoplasma decides if it's alive or not
     */
    public void act() {
        List<Cell> neighbours = getField().getLivingNeighbours(getLocation());
        neighbours = neighbours.stream().filter(cell -> cell instanceof Mycoplasma).collect(Collectors.toList());

        if (isAlive()) {
           // live if there are 2 or 3 neighbours, otherwise die
           if (neighbours.size() > 1 && neighbours.size() < 4) setNextState(true);
           else setNextState(false);
        }
        
        // if there are exactly 3 neighbours and the cell is dead, revive it
        else if (neighbours.size() == 3) setNextState(true);
    }
}
