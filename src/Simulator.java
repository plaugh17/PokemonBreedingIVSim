import java.util.*;


public class Simulator {

    //Tried to save on memory by using a bunch of static variables
    static final int NO_KNOT = 3;
    static final int KNOT = 5;
    static ArrayList<Integer> unaffectedStats = new ArrayList<>();
    static int[] tempStats = new int[12];
    static int[] newStats = new int[6];
    static boolean firstShiny = true;
    static Random rand = new Random();
    static int num3IV = 0;
    static int num4IV = 0;
    static int num5IV = 0;
    static int num6IV = 0;
    static int shinyCount = 0;
    static int ivCount1 = 0;
    static int ivCount2 = 0;
    static int totalBreeds = 0;

    public static void runBreedingTests(int[] stats1, int[] stats2, int knot, boolean charm, boolean masuda, int numIterations, boolean swaps) {
        ivCount1 = 0;
        ivCount2 = 0;
        for (int i = 0; i < 6; i++) {
            if (stats1[i] == 31) {
                ivCount1++;
            }
            if (stats2[i] == 31) {
                ivCount2++;
            }
        }
        if (swaps) {
            int[] originalStats1 = new int[6];
            System.arraycopy(stats1, 0, originalStats1, 0, 6);
            int[] originalStats2 = new int[6];
            System.arraycopy(stats2, 0, originalStats2, 0, 6);
            int originalIVCount1 = ivCount1;
            int originalIVCount2 = ivCount2;
            int originalCoveredCount = getCoveredCount(stats1, stats2);
            for (int i = 1; i <= numIterations; i++) {
                int coveredCount = originalCoveredCount;
                int j = 0;
                while (num6IV < 2) {
                    coveredCount = runBreedingTest(stats1, stats2, knot, charm, masuda, j, true, coveredCount);
                    totalBreeds++;
                    j++;
                }
                num6IV = 0;
                ivCount1 = originalIVCount1;
                ivCount2 = originalIVCount2;
                System.arraycopy(originalStats1, 0, stats1, 0, 6);
                System.arraycopy(originalStats2, 0, stats2, 0, 6);
            }
        } else {
            for (int i = 1; i <= numIterations; i++) {
                runBreedingTest(stats1, stats2, knot, charm, masuda, i, false, 500);
                totalBreeds++;
            }
        }
    }

    public static int runBreedingTest(int[] stats1, int[] stats2, int knot, boolean charm, boolean masuda, int iterationNum, boolean swaps, int coveredCount) {
        int tempCoveredCount;
        System.arraycopy(stats1, 0, tempStats, 0, 6);
        System.arraycopy(stats2, 0, tempStats, 6, 6);
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

        boolean male = rand.nextBoolean();

        //Swaps done to try to get highest number of max IVs on each pokemon and covering most unique IVs
        if (swaps && !(ivCount1 == 6 && ivCount2 == 6)) {
            if (male) {
                tempCoveredCount = getCoveredCount(newStats, stats2);
                if (tempCoveredCount > coveredCount || (tempCoveredCount == coveredCount && ivCount > ivCount1)) {
                    System.arraycopy(newStats, 0, stats1, 0, 6);
                    System.arraycopy(newStats, 0, tempStats, 0, 6);
                    coveredCount = tempCoveredCount;
                    ivCount1 = ivCount;
                }
            } else {
                tempCoveredCount = getCoveredCount(stats1, newStats);
                if (tempCoveredCount > coveredCount || (tempCoveredCount == coveredCount && ivCount > ivCount2)) {
                    System.arraycopy(newStats, 0, stats2, 0, 6);
                    System.arraycopy(newStats, 0, tempStats, 6, 6);
                    coveredCount = tempCoveredCount;
                    ivCount2 = ivCount;
                }
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
            if (num6IV == 1) {
                //System.out.println("First 6IV Pokemon at iteration number " + iterationNum + "!");
            } else if (num6IV == 2) {
                //System.out.println("Second 6IV Pokemon at iteration number " + iterationNum + "!");
            }
        }

        if (shiny) {
            shinyCount++;
            if (firstShiny) {
                firstShiny = false;
                System.out.println("First shiny Pokemon at iteration number " + (totalBreeds + 1) + "!");
            }
        }
        unaffectedStats.clear();

        return coveredCount;
    }

    public static int getCoveredCount(int[] stats1, int[] stats2) {
        int tempCoveredCount = 0;
        for (int i = 0; i < 6; i++) {
            boolean val = (stats1[i] == 31 || stats2[i] == 31);
            //tempCoveredStats[i] = val;
            if (val) {
                tempCoveredCount++;
            }
        }

        return tempCoveredCount;
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
        int[] stats1 = {31, 31, 6, 31, 31, 31}; //IVs of first pokemon, has to be 6 nums from 0-31
        int[] stats2 = {0, 0, 0, 0, 0, 0}; //IVs of second pokemon. has to be 6 nums from 0-31
        int totalIterations = 10000; //Number of test iteration to run
        boolean shinyCharm = true; //true if using shiny charm, false if not
        boolean masuda = false; //true if using masuda method, false if not
        int knotValue = KNOT; //KNOT if using destiny knot, NO_KNOT if not
        boolean withSwapping = true; //true if you want to swap lowest IV pokemon w/ new pokemon if new pokemon has higher IVs
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
        runBreedingTests(stats1, stats2, knotValue, shinyCharm, masuda, totalIterations, withSwapping);
        long end = System.currentTimeMillis();

        System.out.println("Runtime: " + (end - start) + "ms");
        System.out.println("3IVs: " + num3IV);
        System.out.println("4IVs: " + num4IV);
        System.out.println("5IVs: " + num5IV);
        System.out.println("6IVs: " + num6IV);
        System.out.println("Shinies: " + shinyCount);

        double odds = (double) totalBreeds / 100;
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

        if (withSwapping) {
            int avgBreedingTime = totalBreeds/totalIterations;
            System.out.printf("Average of %d breeds to get two 6IV pokemon\n", avgBreedingTime);
        }
    }
}
