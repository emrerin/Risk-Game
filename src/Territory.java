import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Territory {

    public ArrayList<Polygon> polygons = new ArrayList<Polygon>();

    private ArrayList<Territory> neighbours;

    public Polygon capital;

    public int numberOfArmies;

    public String name;

    public String continent;

    private Own owner;

    public enum Own{
        User,
        Computer,
        NoOwner
    }

    public Territory(String name){
        this.name = name;
        numberOfArmies = 0;
        owner = Own.NoOwner;
        neighbours = new ArrayList<Territory>();
    }

    public String getContinent() {
        return this.continent;
    }

    public void setContinent(String name) {
        this.continent = name;
    }

    public void setOwner(Own o){
        owner = o;
    }

    public Own getOwner(){
        return owner;
    }

    public void addPatch(ArrayList<Integer> koordinate) {
        Polygon polygon = new Polygon();
        Iterator<Integer> iter = koordinate.iterator();

        while (iter.hasNext()) {
            polygon.addPoint(iter.next(), iter.next());
        }
        polygons.add(polygon);
    }

    public void setCapital(int x, int y){
        Polygon polygon = new Polygon();
        polygon.addPoint(x, y);
        capital = polygon;
    }

    public boolean hasPoint(Point point){
        for(Polygon polygon : polygons){
            if(polygon.contains(point)){
                return true;
            }
        }
        return false;
    }

    public void incrementNumberOfArmy(){
        numberOfArmies++;
    }

    public boolean decrementNumberOfArmy() {
        if(numberOfArmies > 1){
            numberOfArmies--;
            return true;
        }
        return false;
    }

    public void addNeighbour(Territory territory){
        if(!isNeighbour(territory)){ // dublicate control !
            neighbours.add(territory);
        }
    }

    public boolean isNeighbour(Territory territory){
        for(Territory currentNeighbour : neighbours){
            if(currentNeighbour.name.equals(territory.name)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Territory> getNeighbours(){
        return neighbours;
    }
}
