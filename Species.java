import java.awt.Color;
import java.util.HashMap;

public enum Species {
    HELICOBACTER, MYCOPLASMA, ISSERIA, INFECTED;

    static HashMap<Species, Color> speciesColor = new HashMap<>();

    static {
        setUpdefaultColours();
    }

    public static void setUpdefaultColours() {
        speciesColor.put(HELICOBACTER, new Color(200, 255, 255));
        speciesColor.put(MYCOPLASMA, Color.ORANGE);
        speciesColor.put(ISSERIA, Color.MAGENTA);
        speciesColor.put(INFECTED, Color.RED);
    }

    /**
     * 
     * @return the color of the current species
     */
    public Color getColor() {
        return speciesColor.get(this);
    }

    /**
     * 
     * @param species
     * @return the color of the species specified
     */
    public static Color getColor(Species species) {
        return speciesColor.get(species);
    }

    /**
     * Set a new colour for an existing species. ie when the Helicobacter gets
     * darker update its colour
     * 
     * @param species
     * @param color
     */
    public static void setColor(Species species, Color color) {
        speciesColor.put(species, color);
    }
}
