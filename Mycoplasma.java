import java.awt.Color;
import java.util.List;
import java.util.Random;

/**
 * Simplest form of life.
 * Fun Fact: Mycoplasma are one of the simplest forms of life. A type of
 * bacteria, they only have 500-1000 genes! For comparison, fruit flies have
 * about 14,000 genes.
 *
 * @author David J. Barnes, Michael Kölling & Jeffery Raphael, extended by
 *         Harshraj Patel & Ishab Ahmed
 * @version 2022.01.06 (1)
 */

public class Mycoplasma extends Cell {

    /**
     * Create a new Mycoplasma.
     *
     * @param field    The field currently occupied.
     * @param location The location within the field.
     */
    public Mycoplasma(Field field, Location location, Color col) {
        super(field, location, col);
    }

    public Mycoplasma(Field field, Location location) {
        super(field, location, Color.ORANGE);
    }

    /**
     * This is how the Mycoplasma decides if it's alive or not
     */
    public void act(int generation) {
        // get all the living neighbours of the same colour
        List<Cell> sameNeighbours = getLivingNeighboursByColour(getColor());

        Random rand = Randomizer.getRandom();

        if (isAlive()) {
            // live on if there are 2 or 3 neighbours
            if (sameNeighbours.size() > 1 && sameNeighbours.size() < 4)
                setNextState(true);
            else {
                // if the cell is infected, there is a probability that is set alive
                if (getColor().equals(infectedColour) && rand.nextDouble() < Math.max(10 / generation, 0.08)) {
                    setNextState(true);
                // otherwise cell dies
                } else
                    setNextState(false);
            }
        }
        // if the cell is dead and has exactly 3 neighbours, revive it
        else if (sameNeighbours.size() == 3)
            setNextState(true);
    }

    /**
     * Cell becomes Isseria when Mycoplasma and Isseria collide, with a chance of
     * becoming infected during breeding
     */
    @Override
    public void breedIfPossible() {
        Random rand = new Random();

        // get all the living neighbours of the same colour
        List<Cell> sameNeighbours = getLivingNeighboursByColour(getColor());
        // get all the living neighbours that are Isseria type
        List<Cell> isseNeighbours = getLivingNeighboursByColour(isseColour);

        // if there is more than one Mycoplasma neighbour AND more than one Isseria
        // neighbour
        if (sameNeighbours.size() >= 1 && isseNeighbours.size() >= 1) {
            // probability that the cells becomes infected
            if (rand.nextDouble() < 0.9) {
                setNextColor(infectedColour);
                // otherwise, make cell Isseria
            } else {
                setNextColor(isseColour);
            }
            setNextState(true);
        }
    }

}
