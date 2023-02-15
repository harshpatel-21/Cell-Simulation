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

    public Mycoplasma(Field field, Location location) {
        super(field, location, Color.ORANGE);
    }


    /**
     * This is how the Mycoplasma decides if it's alive or not
     */
    public Cell act(int generation) {
        Random rand = Randomizer.getRandom();

        List<Cell> livingNeighbours = getField().getLivingNeighbours(getLocation());
        
        // get all the living neighbours of the same species
        List<Cell> sameNeighbours = livingNeighbours.stream().filter(cell -> cell.getClass().equals(this.getClass())).collect(Collectors.toList());

        if (isAlive()) {
           // live if there are 2 or 3 neighbours, otherwise die
           if (sameNeighbours.size() > 1 && sameNeighbours.size() < 4) setNextState(true);
           else setNextState(false);
        }
        
        // if there are exactly 3 neighbours and the cell is dead, revive it
        else if (sameNeighbours.size() == 3) setNextState(true);

        return null;
    }

    /**
     * 
     */
    public Cell breedIfPossible(){
        Random rand = new Random();
        List<Cell> neighbours = getField().getLivingNeighbours(getLocation());

        List<Cell> sameNeighbours = neighbours.stream().filter(cell -> cell instanceof Mycoplasma).collect(Collectors.toList());
        List<Cell> isseNeighbours = neighbours.stream().filter(cell -> cell instanceof Isseria).collect(Collectors.toList());

        if (sameNeighbours.size()>=1 && isseNeighbours.size()>=1 && rand.nextDouble()<0.2){
            Plebsiella offspring = new Plebsiella(getField(), getLocation());
            offspring.setNextState(true);
            return offspring;
        }

        else return null;

    }

}
