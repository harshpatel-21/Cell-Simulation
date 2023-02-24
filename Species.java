import java.util.Arrays;

enum Species{
    HELICOBACTER("Helicobacter"), MYCOPLASMA("Mycoplasma"), ISSERIA("Isseria"), INFECTED("Infected"), TEMPCELL("TempCell");
    private String name;
    
    private Species(String name){
        this.name = name;
    }

    public String getSpeciesName(){
        return name;
    }

    static String[] getAllSpeciesToString(){
        return Arrays.asList(Species.values()).stream().map(sp -> sp.getSpeciesName()).toArray(size -> new String[size]);
    }
}