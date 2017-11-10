import java.util.ArrayList;
import java.util.HashMap;

public class Turn {
    private int countReinforcements;
    public Territory lastSelectedTerritory;
    public State currentState;
    private HashMap<String, Integer> reinforcementMap;
    private HashMap<String, Integer> terrCountMap;

    public enum State {
        Started,
        Attacking,
        Finished
    }

    public Turn(HashMap<String, Territory> territoryMap, HashMap<String, Integer> reinforcementMap, HashMap<String, Integer> terrCountMap) {
        this.currentState = State.Started;
        this.reinforcementMap = reinforcementMap;
        this.terrCountMap = terrCountMap;
        this.countReinforcements = getReinforcements(territoryMap, Territory.Own.User);
    }

    /**
     * Calculates how many reinforcements are ready for distribution.
     */
    private int getReinforcements(HashMap<String, Territory> territoryMap, Territory.Own currentUser) {
        int cTerr = 0;
        int[] contCount = new int[terrCountMap.size()];
        int bonus = 0;

        for (Territory t : territoryMap.values()) {
            if (t.getOwner() == currentUser) {
                switch (t.continent) {
                    case "Europe":
                        contCount[0]++;
                        break;
                    case "Asia":
                        contCount[1]++;
                        break;
                    case "North America":
                        contCount[2]++;
                        break;
                    case "South America":
                        contCount[3]++;
                        break;
                    case "Africa":
                        contCount[4]++;
                        break;
                    case "Australia":
                        contCount[5]++;
                        break;
                    default:
                        break;
                }
            }
        }
        //System.out.println("Europe: " + contCount[0] + ", Asia: " + contCount[1] + ", North America: " + contCount[2] + ", South America: " + contCount[3] + ", Africa: " + contCount[4] + ", Australia: " + contCount[5]);
        Object[] keys = territoryMap.keySet().toArray();
        for(Object o : keys) {
            if(territoryMap.get((String)o).getOwner().equals(currentUser)) {
                cTerr++;
            }
        }
        if (contCount[0] == terrCountMap.get("Europe")) {
            bonus += reinforcementMap.get("Europe");
        }
        if (contCount[1] == terrCountMap.get("Asia")) {
            bonus += reinforcementMap.get("Asia");
        }
        if (contCount[2] == terrCountMap.get("North America")) {
            bonus += reinforcementMap.get("North America");
        }
        if (contCount[3] == terrCountMap.get("South America")) {
            bonus += reinforcementMap.get("South America");
        }
        if (contCount[4] == terrCountMap.get("Africa")) {
            bonus += reinforcementMap.get("Africa");
        }
        if (contCount[5] == terrCountMap.get("Australia")) {
            bonus += reinforcementMap.get("Australia");
        }
        //System.out.println(bonus);
        return bonus + cTerr/3;
    }

    /**
     * Decrements the current reinforcements if they have been distributed.
     */
    public void useReinforcements() {
        this.countReinforcements--;
    }

    /**
     * Returns the current reinforcements (without bonus) that are ready for distribution
     */
    public int getReinforcementsCount() {
        return this.countReinforcements;
    }

    public State getState() {
        return currentState;
    }

    public void distributeComputerReinforcements(HashMap<String, Territory> territoryMap) {
        int computerReinforcements = getReinforcements(territoryMap, Territory.Own.Computer);
        for(int i = 0; i < computerReinforcements; i++) {
            Territory territory = this.getRandomTerritory(Territory.Own.Computer, territoryMap);
            territory.numberOfArmies++;
        }
    }

    public Territory getRandomTerritory(Territory.Own owner, HashMap<String, Territory> territoryMap) {
        ArrayList<String> ownedTerritories = new ArrayList<String>();
        Object[] keys =  territoryMap.keySet().toArray();
        for(Object o : keys ){
            if(territoryMap.get(o).getOwner().equals(owner)) {
                ownedTerritories.add((String) o);
            }
        }
        int index = ((int)(Math.random()*100) % ownedTerritories.size());
        return territoryMap.get(ownedTerritories.get(index));
    }

    /**
     * Returns all territories owned by the specified owner.
     */
    public ArrayList<String> getOwnersTerritories(Territory.Own owner, HashMap<String, Territory> territoryMap) {
        ArrayList<String> ownedTerritories = new ArrayList<String>();
        Object[] keys =  territoryMap.keySet().toArray();
        for(Object o : keys) {
            if(territoryMap.get(o).getOwner().equals(owner)) {
                ownedTerritories.add((String) o);
            }
        }
        return ownedTerritories;
    }

    /**
     * Returns a number from 1 to 6 to determine the successful attacker/defender.
     */
    public int roll() {
        return ((int)((Math.random()*60) % 6) + 1);
    }

    /**
     * The primary attack method with logic
     */
    public void attack(Territory destination, Territory.Own gameUser) {
        int numberOfAttackingArmies;
        if(lastSelectedTerritory.numberOfArmies >= 4) {
            numberOfAttackingArmies = 3;
            lastSelectedTerritory.numberOfArmies -= 3;
        } else {
            numberOfAttackingArmies = lastSelectedTerritory.numberOfArmies - 1;
            lastSelectedTerritory.numberOfArmies = 1;
        }
        int numberOfDefendingArmies = destination.numberOfArmies;
        while(numberOfDefendingArmies > 0 && numberOfAttackingArmies > 0) {
            int rollOfDefendingArmies = roll();
            int rollOfAttackingArmies = roll();
            if(rollOfAttackingArmies > rollOfDefendingArmies) {
                numberOfDefendingArmies--;
            } else if(rollOfAttackingArmies == rollOfDefendingArmies) { // Assumption !
                numberOfAttackingArmies--;
                numberOfDefendingArmies--;
            } else {
                numberOfAttackingArmies--;
            }
        }
        if(numberOfAttackingArmies > numberOfDefendingArmies) {
            destination.setOwner(gameUser);
            destination.numberOfArmies = numberOfAttackingArmies;
        } else {
            destination.numberOfArmies = numberOfDefendingArmies;
        }
    }

    /**
     *
     * @param game
     * @return
     */
    public boolean attackComputer(Game game) {
        ArrayList<String> territories =  getOwnersTerritories(Territory.Own.Computer, game.getTerritoryMap());
        boolean computerPlayed = false;
        while(territories.size() > 0 && !computerPlayed) {
            int selectedIndex = ((int)((Math.random()*100) % territories.size()));
            Territory selected = game.findTerritory(territories.get(selectedIndex));
            if(attackComputer(selected)) {
                computerPlayed = true;
            } else {
                territories.remove(territories.get(selectedIndex));
            }
        }
        return computerPlayed;
    }

    private boolean attackComputer(Territory selected) {
        lastSelectedTerritory = selected;
        boolean found = false;
        for(Territory neighbour : lastSelectedTerritory.getNeighbours()) {
            if(!found) {
                if (neighbour.getOwner().equals(Territory.Own.User)) {
                    found = true;
                    attack(neighbour, Territory.Own.Computer);
                }
            }
        }
        return found;
    }
}
