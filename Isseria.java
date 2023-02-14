import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * @author Ishab Ahmed, Harshraj Patel
 * @version 2023.02.07
 */

public class Isseria extends Cell {
    /**
     * Create a new Isseria.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Isseria(Field field, Location location, Color col) {
        super(field, location, col);
    }

    public Isseria(Field field, Location location) {
        super(field, location, new Color(242,98,255));
    }

    /**
     * This is how the Isseria decides if it's alive or not
     */
    public Cell act() {
        Random rand = Randomizer.getRandom();

        List<Cell> livingNeighbours = getField().getLivingNeighbours(getLocation());
        
        // get all the living neighbours of the same species
        List<Cell> sameNeighbours = livingNeighbours.stream().filter(cell -> cell.getClass().equals(this.getClass())).collect(Collectors.toList());
        
        // get all the neighbours, which were engulfed (meaning that they were previously Isseria cells)
        List<Cell> engulfedNeighbours = livingNeighbours.stream().filter(cell -> cell.getEngulfed()).collect(Collectors.toList());
        
        // checks for engulfed neighbouring cells (previously isseria but now helicobacter)
        // 50% chance of spawning Isseria from remnent DNA of engulfed Issera which are now Helis
        
        // check if it can get eaten
        // Cell engulfedCell = getInfectedIfPossible(Helicobacter.class);
        // Cell engulfedCell = getEngulfedIfPossible();
        // if (engulfedCell != null) return engulfedCell;

        if (engulfedNeighbours.size()==2 && !(isAlive()) && rand.nextDouble()<0.85){setNextState(true); return null;}

        
        else if(sameNeighbours.size()==2) {;} // do nothing
        else if(sameNeighbours.size()==1) {setNextState(false);}
        else if(sameNeighbours.size()==3) {setNextState(true);}
        else if(sameNeighbours.size()>3) {setNextState(false);}
        
        return null;
    }


    public List<Cell[]> getEngulfedIfPossible() {
        Random rand = Randomizer.getRandom();
        
        List<Cell[]> cellsToEngulf = new ArrayList<>();

        // get number of living helicobacter cells
        int heliNum = getField().getLivingNeighbours(this.getLocation()).stream().filter(cell -> cell instanceof Helicobacter).collect(Collectors.toList()).size();

        // set the probability of engulfsion
        double engulfProbability = 0.24;

        // if the number chosen is greater than the probability, return (reduce indents)
        if (rand.nextDouble()>engulfProbability){ return cellsToEngulf; }

        // range of surrounding neighbours needed to engulf (inclusive)
        int lowerBound = 1;
        int upperBound = 3;
        
        // if the number of neighbours is within the range required to be engulfed, engulf the cell
        if (heliNum >= lowerBound && heliNum <= upperBound && isAlive()) {
            Helicobacter newHeliCell = new Helicobacter(getField(), this.getLocation());

            newHeliCell.setNextState(true); 
            newHeliCell.setEngulfed(); // 

            getField().setObjectAt(this.getLocation(), newHeliCell);
            cellsToEngulf.add(new Cell[]{this, newHeliCell});
        }
        
        return cellsToEngulf;
    }
}

