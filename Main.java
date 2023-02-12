public class Main{
    public static void main(String[] args){
        Simulator simulator = new Simulator();
        simulator.simulate(4_000);

        // for (int i = 0; i < 20; i++) 
        //     try {
        //         Thread.sleep(1000);
        //         simulator.simOneGeneration();
        //     } catch (InterruptedException ie) {}
        // }
    }
}