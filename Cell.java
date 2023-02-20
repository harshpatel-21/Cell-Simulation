import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * A class representing the shared characteristics of all forms of life
 *
 * @author David J. Barnes, Michael Kölling & Jeffery Raphael extended by
 *         Harshraj Patel & Ishab Ahmed
 * @version 2023.02.20
 */

public abstract class Cell {
	// Whether the cell is alive or not.
	private boolean alive;

	// Whether the cell will be alive in the next generation.
	private boolean nextAlive;

	// The cell's field.
	private Field field;

	// The cell's position in the field.x
	private Location location;

	// The cell's color
	private Color color = Color.white;

	private Color nextColor = color;

	// default colour of cells
	protected Color heliColour = new Color(200, 255, 255);
	protected Color mycoColour = Color.ORANGE;
	protected Color isseColour = Color.MAGENTA;
	protected Color infectedColour = Color.RED;

	// probability that a cell will get infected
	private double infectRate = 0.1025;

	/**
	 * Create a new cell at location in field.
	 *
	 * @param field    The field currently occupied.
	 * @param location The location within the field.
	 */
	public Cell(Field field, Location location, Color col) {
		this.field = field;

		setLocation(location);

		// set initial colour
		color = col;
		nextColor = col;

		// set initial state
		alive = true;
		nextAlive = false;
	}

	/**
	 * Make this cell act - that is: the cell decides it's status in the
	 * next generation.
	 */
	abstract public void act(int generation);

	/**
	 * Check whether the cell is alive or not.
	 * 
	 * @return true if the cell is still alive.
	 */
	protected boolean isAlive() {
		return alive;
	}

	/**
	 * Indicate that the cell is no longer alive.
	 */
	protected void setDead() {
		alive = false;
	}

	/**
	 * Indicate that the cell will be alive or dead in the next generation.
	 */
	public void setNextState(boolean value) {
		nextAlive = value;
	}

	/**
	 * Changes the state of the cell
	 */
	public void updateState() {
		alive = nextAlive;
		color = nextColor;
	}

	/**
	 * @return whether the cell is alive in the next iteration
	 */
	public boolean getNextAlive() {
		return nextAlive;
	}

	/**
	 * Changes the color of the cell
	 * 
	 * @param col The colour the cell should change to
	 */
	public void setColor(Color col) {
		color = col;
	}

	/**
	 * Sets the colour the cell should be in the next generation
	 * 
	 * @param col The colour the cell should change to
	 */
	public void setNextColor(Color col) {
		nextColor = col;
	}

	/**
	 * Returns the cell's color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Return the cell's location.
	 * 
	 * @return The cell's location.
	 */
	protected Location getLocation() {
		return location;
	}

	/**
	 * Place the cell at the new location in the given field.
	 * 
	 * @param location The cell's location.
	 */
	protected void setLocation(Location location) {
		this.location = location;
		field.place(this, location);
	}

	/**
	 * Return the cell's field.
	 * 
	 * @return The cell's field.
	 */
	protected Field getField() {
		return field;
	}

	/**
	 * Cell is engulfed by an infected cell is dead and within the probability.
	 * Cell is engulfed by a Helicobacter if matching the rules.
	 */
	protected void getEngulfedIfPossible() {
		Random rand = Randomizer.getRandom();

		// if the cell is not a Helicobacter
		if (!(getColor().equals(heliColour))) {
			// get number of helicobacter neighbours
			int heliNum = getLivingNeighboursByColour(heliColour).size();

			double engulfProbability = 0.175;

			if (rand.nextDouble() < engulfProbability) {
				// if cell is surrounded by between 1 and 3 Helicobacters
				if (heliNum >= 1 && heliNum <= 3) {
					// there is a probability that the cell is turned in to a Helicobacter
					setNextColor(heliColour);
					setNextState(true);
				}
			}
		}
	}

	/**
	 * Changes the cell becomes a different cell when two different, neighbouring
	 * cells collide
	 */
	protected void breedIfPossible() {
	};

	/**
	 * @return whether the cell is a Heliobacter or not
	 */
	protected boolean isHeli() {
		// needed as Helicobacter's colour is constantly changing
		return getColor() != mycoColour && getColor() != isseColour && getColor() != infectedColour;
	}

	/**
	 * Infects the cell if it is surrounded by infected cells and is within the
	 * probability
	 */
	protected void getInfectedIfPossible() {
		Random rand = new Random();

		// get all the infected neighbours
		List<Cell> infectedNeighbours = getLivingNeighboursByColour(infectedColour);

		// every generation, Helicobacter infection rate decreased by 0.1%
		if (isHeli()) {
			infectRate *= 0.999;
		}

		// if the cell is not infected
		if (!getColor().equals(infectedColour)) {
			// if the cell is surrounded by more than one infected neighbour
			if (infectedNeighbours.size() >= 1) {
				// there is a chance that the cell is infected
				if (rand.nextDouble() < infectRate) {
					setNextColor(infectedColour);
					setNextState(true);
				}
			}
		}

		// if the cell is not alive
		if (!(isAlive()) && !(getColor().equals(heliColour))) {
			// get the number of infected neighbours
			int infectedNum = getLivingNeighboursByColour(infectedColour).size();

			// if there are more than 3 infected neighbours,
			// there is a probability for that cell to become infected
			if (infectedNum > 3 && rand.nextDouble() < 0.11) {
				setNextColor(infectedColour);
				setNextState(true);
			}
		}

	}

	/**
	 * Gets all of the living neighbours of the cell and filters it by the colour
	 * passed in
	 * 
	 * @param colour The colour of the neighbours to be retrieved
	 * @return A list of neighbouring cells of the same colour as the parameter
	 */
	protected List<Cell> getLivingNeighboursByColour(Color colour) {
		return getField().getLivingNeighbours(getLocation()).stream().filter(cell -> cell.getColor().equals(colour))
				.collect(Collectors.toList());
	}

	/**
	 * Changes the colour of the Helicobacter cell based on the current generation
	 * 
	 * @param generation The current generation
	 */
	protected void darkenHeliColour(int generation) {
		int r = (int) Math.max(200 - (0.1 * generation), 52);
		int g = (int) Math.max(255 - (0.1 * generation), 126);
		int b = 255;

		heliColour = new Color(r, g, b);
		// only change Helicobacter cell
		if (isHeli())
			setNextColor(heliColour);
	}

}
