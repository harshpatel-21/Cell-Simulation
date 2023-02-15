import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * A class representing the shared characteristics of all forms of life
 *
 * @author David J. Barnes, Michael Kölling & Jeffery Raphael
 * @version 2022.01.06 (1)
 */

public abstract class Cell {
	private int generationCreated;
	// Whether the cell was engulfed or not
	private boolean engulfed;

	// Whether the cell is alive or not.
	private boolean alive;

	// Whether the cell will be alive in the next generation.
	private boolean nextAlive;

	// The cell's field.
	private Field field;

	// The cell's position in the field.
	private Location location;

	// The cell's color
	private Color color = Color.white;

	/**
	 * Create a new cell at location in field.
	 *
	 * @param field    The field currently occupied.
	 * @param location The location within the field.
	 */
	public Cell(Field field, Location location, Color col) {
		alive = true;
		nextAlive = false;
		this.field = field;

		setLocation(location);
		setColor(col);
	}

	public int getGenerationCreated() {
		return generationCreated;
	}

	/**
	 * Make this cell act - that is: the cell decides it's status in the
	 * next generation.
	 */
	abstract public Cell act(int generation);

	protected List<Cell[]> getEngulfedIfPossible() {
		return new ArrayList<>();
	}

	protected Cell breedIfPossible() {
		return null;
	}

	protected Cell getInfectedIfPossible() {
		Random rand = new Random();

		List<Cell> livingNeighbours = getField().getLivingNeighbours(getLocation());

		List<Cell> infectedNeighbours = livingNeighbours.stream().filter(cell -> cell instanceof Plebsiella)
				.collect(Collectors.toList());

		double plebInfectRate = 0.1025;

		if (this instanceof Plebsiella){return null;}

		if (infectedNeighbours.size() >= 1){
			if (rand.nextDouble() < plebInfectRate){
				Cell cell = new Plebsiella(getField(), getLocation());
				cell.setNextState(true);
				return cell;
			}
		}

		return null;
	}

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

	protected void setEngulfed() {
		engulfed = true;
	}

	protected boolean getEngulfed() {
		return engulfed;
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
	}

	/**
	 * @return whether the cell is alive in the next iteration
	 */
	public boolean getNextAlive() {
		return nextAlive;
	}

	/**
	 * Changes the color of the cell
	 */
	public void setColor(Color col) {
		color = col;
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

	protected Cell getTopNeighbour() {
		int[] coords = getTopNeighbourLocation();
		return field.getObjectAt(coords[0], coords[1]);
	}

	protected Cell getBottomNeighbour() {
		int[] coords = getBottomNeighbourLocation();
		return field.getObjectAt(coords[0], coords[1]);
	}

	protected Cell getLeftNeighbour() {
		int[] coords = getLeftNeighbourLocation();
		return field.getObjectAt(coords[0], coords[1]);
	}

	protected Cell getRightNeighbour() {
		int[] coords = getRightNeighbourLocation();
		return field.getObjectAt(coords[0], coords[1]);
	}

	protected Cell getTopLeftNeighbour() {
		int[] coords = getTopLeftNeighbourLocation();
		return field.getObjectAt(coords[0], coords[1]);
	}

	protected Cell getTopRightNeighbour() {
		int[] coords = getTopRightNeighbourLocation();
		return field.getObjectAt(coords[0], coords[1]);
	}

	protected Cell getBottomLeftNeighbour() {
		int[] coords = getBottomLeftNeighbourLocation();
		return field.getObjectAt(coords[0], coords[1]);
	}

	protected Cell getBottomRightNeighbour() {
		int[] coords = getBottomRightNeighbourLocation();
		return field.getObjectAt(coords[0], coords[1]);
	}

	protected int[] getTopNeighbourLocation() {
		return new int[] { Math.max(0, location.getRow() - 1), location.getCol() };
	}

	protected int[] getBottomNeighbourLocation() {
		return new int[] { Math.min(location.getRow() + 1, field.getDepth() - 1), location.getCol() };
	}

	protected int[] getLeftNeighbourLocation() {
		return new int[] { location.getRow(), Math.max(location.getCol() - 1, 0) };
	}

	protected int[] getRightNeighbourLocation() {
		return new int[] { location.getRow(), Math.min(location.getCol() + 1, field.getWidth() - 1) };
	}

	protected int[] getTopLeftNeighbourLocation() {
		return new int[] { Math.max(location.getRow() - 1, 0), Math.max(location.getCol() - 1, 0) };
	}

	protected int[] getTopRightNeighbourLocation() {
		return new int[] { Math.max(location.getRow() - 1, 0),Math.min(location.getCol() + 1, getField().getWidth() - 1) };
	}

	protected int[] getBottomLeftNeighbourLocation() {
		return new int[] { Math.min(location.getRow() + 1, getField().getDepth() - 1),
				Math.max(location.getCol() - 1, 0) };
	}

	protected int[] getBottomRightNeighbourLocation() {
		return new int[] { Math.min(location.getRow() + 1, getField().getDepth() - 1),
				Math.min(location.getCol() + 1, getField().getWidth() - 1) };
	}
}
