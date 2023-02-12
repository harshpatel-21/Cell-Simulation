import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * @author Ishab Ahmed, Harshraj Patel
 * @version 2023.02.07
 */

public class Plebsiella extends Cell {

    /**
     * Create a new Plebsiella.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plebsiella(Field field, Location location, Color col) {
        super(field, location, col);
    }

    public Plebsiella(Field field, Location location) {
        super(field, location, Color.RED);
    }

    /**
     * This is how the Plebsiella decides if it's alive or not
     */
    public Cell act() {
        Random rand = new Random();
        List<Cell> livingNeighbours = getField().getLivingNeighbours(getLocation());
        List<Cell> sameNeighbours = livingNeighbours.stream().filter(cell -> cell instanceof Plebsiella).collect(Collectors.toList());

        List<Cell> deadNeighbours = getField().getDeadNeighbours(getLocation());
        List<Cell> sameDeadNeighbours = deadNeighbours.stream().filter(cell -> cell instanceof Plebsiella).collect(Collectors.toList());

        List<Cell> heliNeighbours = livingNeighbours.stream().filter(cell -> cell instanceof Helicobacter).collect(Collectors.toList());
        
        // TODO: DISCUSS WITH I9 TRAPPY: STORE ENGULF PROBABILITY AS A FIELD, AND AS HELIS BECOME OLDER (DEVELOP IMMUNITY). 
        //       CALCULATE AVERAGE OF NEIGHBOURS, AND THAT WILL BE THE getEngulfedRate probability


        // probability to get engulfed by a helicobacter
        double getEngulfedRate = 0.5;
        if (!isAlive() && heliNeighbours.size()>=3){
            if(rand.nextDouble()< getEngulfedRate){
                Cell cell = new Helicobacter(getField(), getLocation());
                cell.setNextState(true);
                return cell;
            }
        }

        if (isAlive()) {
            // live if there are 2 or 3 neighbours, otherwise die
            if (sameNeighbours.size() > 1 && sameNeighbours.size() < 4) setNextState(true);
            else setNextState(false);
        }
        
        // if there are exactly 3 neighbours and the cell is dead, revive it
        else if (sameNeighbours.size() == 3) setNextState(true);

        else if(sameNeighbours.size()>=2 && rand.nextDouble()<0.02){
            setNextState(true);
        }

        // if the Mycoplasma is dead, then allow the Heli to move inwards
        else {
            if (heliNeighbours.size()==2 && rand.nextDouble() < 0.1){
                Cell heli = new Helicobacter(getField(), getLocation());
                heli.setNextState(true);
                return heli;
            }
        }


        // if (isAlive()) if (neighbours.size() >= 5) setNextState(false);

        return null;
    }

    public List<Cell[]> getEngulfedIfPossible() {
        Random rand = Randomizer.getRandom();
        
        List<Cell[]> cellsToEngulf = new ArrayList<>();

        // get number of living helicobacter cells
        int heliNum = getField().getLivingNeighbours(this.getLocation()).stream().filter(cell -> cell instanceof Helicobacter).collect(Collectors.toList()).size();

        double engulfProbability = 0.45;

        // 60% of the time, don't execute the engulf process. 40% of the time, engulf
        if (rand.nextDouble()>engulfProbability){ return cellsToEngulf; }

        // range of surrounding neighbours needed to engulf (inclusive)
        int lowerBound = 1;
        int upperBound = 2;
        
        // if the number of neighbours is within the range required to be engulfed, engulf the cell
        if (heliNum >= lowerBound && heliNum <= upperBound && isAlive()) {
            Helicobacter predator = new Helicobacter(getField(), this.getLocation());

            predator.setNextState(true); 
            predator.setEngulfed(); // 

            getField().setObjectAt(this.getLocation(), predator);
            cellsToEngulf.add(new Cell[]{this, predator});
        }
        
        return cellsToEngulf;
    }

}

