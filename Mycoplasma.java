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
 * @version 2023.02.18
 */

public class Mycoplasma extends Cell {

    /**
     * Create a new Mycoplasma.
     *
     * @param field    The field currently occupied.
     * @param location The location within the field.
     */
    public Mycoplasma(Field field, Location location, Color col, Species species) {
        super(field, location, col, species);
    }

    public Mycoplasma(Field field, Location location) {
        super(field, location, Color.ORANGE, Species.MYCOPLASMA);
    }

    /**
     * This is how the Mycoplasma decides if it's alive or not
     */
    @Override
    public void act(int generation) {
        // get all the living neighbours of the same colour
        List<Cell> sameNeighbours = getLivingNeighboursBySpecies(getSpecies());

        Random rand = Randomizer.getRandom();

        if (isAlive()) {
            // live on if there are 2 or 3 neighbours
            if (sameNeighbours.size() > 1 && sameNeighbours.size() < 4)
                setNextState(true);
            else {
                // if the cell is infected, there is a probability that is set alive
                // as generations increase, the probability decreases to a minimum of 7%.
                if (getSpecies() == Species.INFECTED && rand.nextDouble() < Math.max(10 / generation, 0.09)) {
                    setNextState(true);
                } 
                else
                    // otherwise cell dies
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
        List<Cell> sameNeighbours = getLivingNeighboursBySpecies(getSpecies());
        // get all the living neighbours that are Isseria type
        List<Cell> isseNeighbours = getLivingNeighboursBySpecies(Species.ISSERIA);

        // if there is more than one Mycoplasma neighbour AND more than one Isseria
        // neighbour
        if (sameNeighbours.size() >= 1 && isseNeighbours.size() >= 1) {
            // probability that the cells becomes infected
            if (rand.nextDouble() < 0.9) {
                setNextSpecies(Species.INFECTED);
            } 
            else {
                // otherwise, make cell Isseria
                setNextSpecies(Species.ISSERIA);
            }
            setNextState(true);
        }
    }

}
