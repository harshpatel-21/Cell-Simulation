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

public class Helicobacter extends Cell {
    // private double r = 222;
    // private double g = 255;
    // private double b = 255;

    /**
     * Create a new Helicobacter.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Helicobacter(Field field, Location location, Color col) {
        super(field, location, col);
    }

    public Helicobacter(Field field, Location location) {
        super(field, location, Color.CYAN);
    }

    /**
     * This is how the Helicobacter decides if it's alive or not
     */
    public Cell act() {
        Random rand = Randomizer.getRandom();

        List<Cell> livingNeighbours = getField().getLivingNeighbours(getLocation());
        List<Cell> sameNeighbours = livingNeighbours.stream().filter(cell -> cell.getClass().equals(this.getClass())).collect(Collectors.toList());

        // TODO: create an attribute which stores the generation counter(how long a cell has been alive for, use to change probability and darkness of blue)
        // if (generation % 100 == 0) plebInfectionRate *= 0.95;
        double plebProduceRate = 0.105;
        
		List<Cell> infectedNeighbours = livingNeighbours.stream().filter(cell -> cell instanceof Plebsiella)
				.collect(Collectors.toList());

		double plebInfectRate = 0.0;

		if (!(isAlive()) && infectedNeighbours.size() >= 1){
			if (rand.nextDouble() < plebInfectRate){
				Cell cell = new Plebsiella(getField(), getLocation());
				cell.setNextState(true);
				return cell;
			}
		}

        // if (isAlive()){
        //     r = Math.max(r-2,52);
        //     g = Math.max(r-2,126);
        //     b = Math.max(r-2,255);
        // }

        // Color color = new Color((int) r,(int) g,(int) b);
        // setColor(color);

        if (!isAlive() && sameNeighbours.size()==3){
            setNextState(true); 
            return null;
        }

        if(sameNeighbours.size()==2) {} // do nothing
        else if(sameNeighbours.size()==1) {setNextState(false);}
        else if(sameNeighbours.size()==3) {setNextState(true);}
        else if(sameNeighbours.size()>3) {setNextState(false);}
        return null;
    }
    
}

