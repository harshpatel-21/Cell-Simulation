import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Iterator;

/**
 * @author Ishab Ahmed, Harshraj Patel
 * @version 2023.02.07
 */

public class Helicobacter extends Cell {

    /**
     * Create a new Helicobacter.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Helicobacter(Field field, Location location, Color col) {
        super(field, location, col);
    }

    /**
     * This is how the Helicobacter decides if it's alive or not
     */
    public void act() {
        List<Cell> neighbours = getField().getLivingNeighbours(getLocation());
        neighbours = neighbours.stream().filter(cell -> cell instanceof Helicobacter).collect(Collectors.toList());

        if (isAlive()) {
           // live if there are 2 or 3 neighbours, otherwise die
           if (neighbours.size() > 1 && neighbours.size() < 4) setNextState(true);
           else setNextState(false);
           
           int row = getLocation().getRow();
           int col = getLocation().getCol();
           
           if (this.getNextAlive()) {
               Random rand = Randomizer.getRandom();
               int revive1 = rand.nextInt(4);
               int revive2 = rand.nextInt(4);
               
               // non-deterministic - 2 *random* diagonal cells will be revived (1/6 probability for each pair of diagonals)
               
               // top right
               if (revive1 == 0 || revive2 == 0) getField().getObjectAt(Math.max(row - 1, 0), Math.min(col + 1, getField().getWidth() - 1)).setNextState(true);
               // bottom right
               if (revive1 == 1 || revive2 == 1) getField().getObjectAt(Math.min(row + 1, getField().getDepth() - 1), Math.min(col + 1, getField().getWidth() - 1)).setNextState(true);
               // top left
               if (revive1 == 2 || revive2 == 2) getField().getObjectAt(Math.max(row - 1, 0), Math.max(col - 1, 0)).setNextState(true);
               // bottom left
               if (revive1 == 3 || revive2 == 3) getField().getObjectAt(Math.min(row + 1, getField().getDepth() - 1), Math.max(col - 1, 0)).setNextState(true);
               
               setNextState(false);
           }
        }
        
        // if there are exactly 3 neighbours and the cell is dead, revive it
        else if (neighbours.size() == 3) setNextState(true);
    }
    
    public Cell[] engulfIfPossible() {
        
        if (!(isAlive())) return null;
        
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        
        while (it.hasNext()) {
            Location where = it.next();
            Cell bacterium = field.getObjectAt(where);
            
            if (bacterium instanceof Isseria && bacterium.isAlive()) {
                Helicobacter heli = new Helicobacter(field, where, Color.CYAN);
                //bacterium.setColor(getColor());
                //bacterium.setNextState(true);
                //field.setObjectAt(where, heli);
                return new Cell[]{heli, bacterium};
            }
        }
        return null;
    }
}

