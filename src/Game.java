import java.awt.*;
import java.io.File;
import java.util.*;

public class Game {
    private HashMap<String, Territory> territoryMap;
    private HashMap<String, ArrayList<String>> continentMap = new HashMap<String, ArrayList<String>>();
    private GameComponent gameComponent;
    public HashMap<String, Integer> terrCountMap = new HashMap<String, Integer>();
    public HashMap<String, Integer> reinforcementMap = new HashMap<String, Integer>();
    public Turn currentTurn;

    public Game(String map_file) {
        territoryMap = new HashMap<String, Territory>();
        this.load(new File(map_file));
    }

    public enum State {
        Selection,
        Attack
    }

    /**
     * Loads the specified .map file and reads their entries into the specified Datatypes.
     */
    public void load(File file) {
        try {
            Scanner sc = new Scanner(file);

            while(sc.hasNextLine()) {
                Scanner line = new Scanner(sc.nextLine());
                String nextLine = line.next();
                String landName = "";

                if(nextLine.equals("patch-of")) {
                    ArrayList<Integer> koordinate = new ArrayList<Integer>();
                    while(line.hasNext() && !line.hasNextInt()) {
                        landName += (!landName.equals("") ? " " : "") + line.next();
                    }
                    while(line.hasNextInt()) {
                        koordinate.add(line.nextInt());
                    }
                    this.findTerritory(landName).addPatch(koordinate);
                } else if(nextLine.equals("capital-of")) {
                    while(line.hasNext() && !line.hasNextInt()) {
                        landName += (!landName.equals("") ? " " : "") + line.next();
                    }
                    int x = line.nextInt();
                    int y = line.nextInt();
                    this.findTerritory(landName).setCapital(x,y);
                } else if(nextLine.equals("neighbors-of")) {
                    String currTerr = "";
                    while(line.hasNext() && !(currTerr = line.next()).equals(":")) {
                        landName += (!landName.equals("") ? " " : "") + currTerr;
                    }
                    String neighbour = "";
                    while(line.hasNext()) {
                        String str = line.next();
                        if(str.equals("-")) {
                            Territory current = this.findTerritory(landName);
                            Territory neighbourTerritory = this.findTerritory(neighbour);
                            current.addNeighbour(neighbourTerritory);
                            neighbourTerritory.addNeighbour(current);
                            neighbour = "";
                        } else {
                            if(neighbour.length() > 0) {
                                neighbour += " ";
                            }
                            neighbour += str;
                        }
                    }
                    Territory current = this.findTerritory(landName);
                    Territory neighbourTerritory = this.findTerritory(neighbour);
                    current.addNeighbour(neighbourTerritory);
                    neighbourTerritory.addNeighbour(current); // last Neighbour added !
                } else if(nextLine.equals("continent")) {
                    String continent;
                    int unitBonus;
                    int terrCountPerCont = 0;
                    ArrayList<String> continentTerritories = new ArrayList<String>();

                    while(line.hasNext() && !(continent = line.next()).equals(":")) {
                        landName += (!landName.equals("") ? " " : "") + continent;
                    }

                    unitBonus = Integer.parseInt(String.valueOf(landName.charAt(landName.length() - 1)));
                    landName = landName.substring(0, landName.length() - 2); // -2, weil vor dem int noch ein Leerzeichen steht!
                    reinforcementMap.put(landName, unitBonus);
                    String currContTerr = "";

                    while(line.hasNext()) {
                        String currTerrName = line.next();
                        if (currTerrName.equals("-")) {
                            Territory currTerr = this.findTerritory(currContTerr);
                            currTerr.setContinent(landName);
                            continentTerritories.add(currTerr.name);
                            currContTerr = "";
                        } else {
                            if (currContTerr.length() > 0) {
                                currContTerr += " ";
                            }
                            currContTerr += currTerrName;
                        }
                    }
                    // Add last continent
                    Territory currTerr = this.findTerritory(currContTerr);
                    currTerr.setContinent(landName);
                    continentTerritories.add(currTerr.name);

                    continentMap.put(landName, continentTerritories);
                    terrCountPerCont = continentTerritories.size();
                    terrCountMap.put(landName, terrCountPerCont);
                }
            }
            // Just for testing; Prints Territories belonging to a specific continent
            for (String s : continentMap.keySet()) {
                //System.out.println(s);
            }
            for (ArrayList<String> s : continentMap.values()) {
                //System.out.println(s);
            }
        } catch (Exception ex) {}
    }

    public GameComponent getGameComponent() {
        if(gameComponent == null) {
            gameComponent = new GameComponent(this);
        }
        return gameComponent;
    }

    /**
     * Searches the territoryMap for the specified territory name and adds it if it doesn't exist yet.
     */
    public Territory findTerritory(String name) {
        name = name.trim();
        if(!territoryMap.containsKey(name)) {
            territoryMap.put(name, new Territory(name));
        }
        return territoryMap.get(name);
    }

    /**
     * Returns the clicked territory or null if there was no territory clicked.
     */
    public Territory findTerritoryAt(Point point) {
        for(Territory territory : getTerritories()) {
            if(territory.hasPoint(point)) {
                return territory;
            }
        }
        return null;
    }

    /**
     * Returns all territory objects contained in the territoryMap.
     */
    public Collection<Territory> getTerritories() {
        return this.territoryMap.values();
    }

    /**
     * Returns the current game state.
     */
    public State getState() {
        for(Territory territory : getTerritories()) {
            if(territory.getOwner().equals(Territory.Own.NoOwner)) {
                return State.Selection;
            }
        }
        return State.Attack;
    }

    public HashMap<String, Territory> getTerritoryMap() {
        return territoryMap;
    }

    /**
     * Returns a random territory object for the Computer.
     */
    public Territory getRandomTerritory(Territory.Own owner) {
        ArrayList<String> ownedTerritories = new ArrayList<String>();
        Object[] keys =  territoryMap.keySet().toArray();
        for(Object o : keys) {
            if(territoryMap.get(o).getOwner().equals(owner)){
                ownedTerritories.add((String) o);
            }
        }
        int index = ((int)(Math.random()*100) % ownedTerritories.size());
        return territoryMap.get(ownedTerritories.get(index));
    }

    /**
     * Is used to correctly print the winner (one who owns all territories).
     */
    public Territory.Own getWinner( ) {
        Territory.Own currentOwner = null;
        Object[] keys =  territoryMap.keySet().toArray();
        for(Object o : keys) {
            if(currentOwner == null)
                currentOwner = territoryMap.get(o).getOwner();
            else {
                if(!currentOwner.equals(territoryMap.get(o).getOwner()))
                    return Territory.Own.NoOwner;
            }
        }
        return currentOwner;
    }
}
