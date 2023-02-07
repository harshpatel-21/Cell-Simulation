import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

    /**
     * This is how the Isseria decides if it's alive or not
     */
    public void act() {
        List<Cell> neighbours = getField().getLivingNeighbours(getLocation());
        neighbours = neighbours.stream().filter(cell -> cell instanceof Isseria).collect(Collectors.toList());

        if (isAlive()) {
           // live if there are 2 or 3 neighbours, otherwise die
           if (neighbours.size() > 1 && neighbours.size() < 4) setNextState(true);
           else setNextState(false);
           
           int row = getLocation().getRow();
           int col = getLocation().getCol();
           
           // if both the row and column are odd
           if (row % 2 != 0 && col % 2 != 0) {
               // set the cell to the left alive
               getField().getObjectAt(row, Math.max(col - 1, 0)).setNextState(true); // left 
               //getField().getObjectAt(row, Math.min(col + 1, getField().getWidth() - 1)).setNextState(true); // right
               //getField().getObjectAt(Math.max(row-1, 0), col).setNextState(true); // top
               Random rand = Randomizer.getRandom();
               
               // pi% of the time the top right cell is set to alive
               if (rand.nextDouble() <= 0.3141592) getField().getObjectAt(Math.max(row - 1, 0), Math.min(col + 1, getField().getWidth() - 1)).setNextState(true); // toprigh
               
               setNextState(false); // kill the current cell
           }
           
           // the 3 cells below this current cell
           Cell c1 = getField().getObjectAt(Math.min(row + 1, getField().getDepth() - 1), col);
           Cell c2 = getField().getObjectAt(Math.min(row + 2, getField().getDepth() - 1), col);
           Cell c3 = getField().getObjectAt(Math.min(row + 3, getField().getDepth() - 1), col);
           
           // cell to the left
           Cell c4 = getField().getObjectAt(row, Math.max(col - 1, 0));
           // cell to the right
           Cell c5 = getField().getObjectAt(row, Math.min(col + 1, getField().getWidth() - 1));
           
           if (isAlive() && c1.isAlive() && c2.isAlive() && c3.isAlive() && ((c1 != c2) && (c2 != c3))) c1.setNextState(false); c2.setNextState(false); c3.setNextState(false);
           if (isAlive() && c4.isAlive() && c5.isAlive()) setNextState(false);
        }
        
        // if there are exactly 3 neighbours and the cell is dead, revive it
        else if (neighbours.size() == 3) setNextState(true);
    }
}

