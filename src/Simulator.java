import java.util.*;


public class Simulator {

    //Tried to save on memory by using a bunch of static variables
    static final int NO_KNOT = 3;
    static final int KNOT = 5;
    static ArrayList<Integer> unaffectedStats = new ArrayList<>();
    static int[] tempStats = new int[12];
    static int[] newStats = new int[6];
    static boolean first6IV = true;
    static boolean firstShiny = true;
    static Random rand = new Random();
    static int num3IV = 0;
    static int num4IV = 0;
    static int num5IV = 0;
    static int num6IV = 0;
    static int shinyCount = 0;

    public static void runBreedingTests(int[] stats1, int[] stats2, int knot, boolean charm, boolean masuda, int numIterations) {
        System.arraycopy(stats1, 0, tempStats, 0, 6);
        System.arraycopy(stats2, 0, tempStats, 6, 6);
        for (int iterationNum = 1; iterationNum <= numIterations; iterationNum++) {
            for (int i = 0; i <= 11; i++) {
                unaffectedStats.add(i);
            }
            for (int i = 0; i < knot; i++) {
                Integer statNum = unaffectedStats.get(rand.nextInt(unaffectedStats.size()));
                newStats[statNum % 6] = tempStats[statNum];
                unaffectedStats.remove(statNum);
                if (statNum >= 6) {
                    unaffectedStats.remove(Integer.valueOf(statNum - 6));
                } else {
                    unaffectedStats.remove(Integer.valueOf(statNum + 6));
                }
            }
            int lastStat = unaffectedStats.get(0);
            newStats[lastStat] = rand.nextInt(32);

            int ivCount = 0;
            for (int i = 0; i < 6; i++) {
                if (newStats[i] == 31) {
                    ivCount++;
                }
            }

            boolean shiny = determineShininess(charm, masuda);

            if (ivCount == 3) {
                num3IV++;
            } else if (ivCount == 4) {
                num4IV++;
            } else if (ivCount == 5) {
                num5IV++;
            } else if (ivCount == 6) {
                num6IV++;
                if (first6IV) {
                    first6IV = false;
                    System.out.println("First 6IV Pokemon at iteration number " + iterationNum + "!");
                }
            }

            if (shiny) {
                shinyCount++;
                if (firstShiny) {
                    firstShiny = false;
                    System.out.println("First shiny Pokemon at iteration number " + iterationNum + "!");
                }
            }
            unaffectedStats.clear();
        }
    }

    //Not how shininess is actually determined in game but odds are the same
    public static boolean determineShininess(boolean charm, boolean masuda) {
        int iterations = 1;
        if (charm) {
            iterations += 2;
        }
        if (masuda) {
            iterations += 5;
        }
        for (int i = 1; i <= iterations; i++) {
            if (rand.nextInt(4096) == 0) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        int[] stats1 = {31, 31, 31, 4, 31, 31}; //IVs of first pokemon, has to be 6 nums from 0-31
        int[] stats2 = {6, 31, 31, 31, 31, 31}; //IVs of second pokemon. has to be 6 nums from 0-31
        int totalIterations = 1000000; //Number of test iteration to run
        boolean shinyCharm = true; //true if using shiny charm, false if not
        boolean masuda = false; //true if using masuda method, false if not
        int knotValue = KNOT; //KNOT if using destiny knot, NO_KNOT if not
        //Temporarily nixed out IO until further notice
        /* Scanner in = new Scanner(System.in);
        boolean goodResponse = false;
        int knotVal = 0;
        while (!goodResponse) {
            try {
                System.out.print("Using Destiny Knot? (y/n): ");
                String knotted = in.nextLine();
                if (knotted.toLowerCase().equals("y")) {
                    knotVal = KNOT;
                    goodResponse = true;
                } else if (knotted.toLowerCase().equals("n")) {
                    knotVal = NO_KNOT;
                    goodResponse = true;
                } else {
                    System.out.println("Please enter a y or n");
                }
            } catch (Exception e) {
                System.out.println("Error! Please try again.");
            }
        }

        goodResponse = false;
        while (!goodResponse) {
            try {
                System.out.print("Enter IVs of first Pokemon (ex: 31 0 31 31 0 31): ");
                String ivs1 = in.nextLine();
                int ivCount = 0;
                for (String s : ivs1.split(" ")) {
                    ivCount++;
                    int iv = Integer.parseInt(s);
                    if (iv > 31 || iv < 0) {
                        System.out.println("Please enter only number from 0-31!");
                        ivCount = 6;
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter only numbers!");
            } catch (Exception e) {
                System.out.println("Error! Please try again.");
            }
        } */
        long start = System.currentTimeMillis();
        runBreedingTests(stats1, stats2, knotValue, shinyCharm, masuda, totalIterations);
        long end = System.currentTimeMillis();

        System.out.println("Runtime: " + (end - start) + "ms");
        System.out.println("3IVs: " + num3IV);
        System.out.println("4IVs: " + num4IV);
        System.out.println("5IVs: " + num5IV);
        System.out.println("6IVs: " + num6IV);
        System.out.println("Shinies: " + shinyCount);

        double odds = (double) totalIterations / 100;
        double threeOdds = num3IV/odds;
        double fourOdds = num4IV/odds;
        double fiveOdds = num5IV/odds;
        double sixOdds = num6IV/odds;
        double shinyOdds = shinyCount/odds;
        System.out.printf("3IV Odds: %.2f%%\n", threeOdds);
        System.out.printf("4IV Odds: %.2f%%\n", fourOdds);
        System.out.printf("5IV Odds: %.2f%%\n", fiveOdds);
        System.out.printf("6IV Odds: %.2f%%\n", sixOdds);

        int shinyOddNum = -1;
        for (int i = 5000; i > 1; i--) {
            double testOdds = 1.0/i;
            double testShinyOdds = shinyOdds / 100;
            if (testOdds > testShinyOdds) {
                shinyOddNum = i;
                break;
            }
        }
        System.out.printf("Shiny Odds: %.2f%% (Roughly 1 in %d)\n", shinyOdds, shinyOddNum);
    }
}
