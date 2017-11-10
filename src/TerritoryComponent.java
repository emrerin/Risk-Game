import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

public class TerritoryComponent extends JComponent {
    public Territory territory;

    public TerritoryComponent(Territory territory){
        super();
        this.setSize(this.getMaximumSize());
        this.territory = territory;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        Iterator<Polygon> iter = territory.polygons.iterator();

        while (iter.hasNext()) {
            Polygon polygon = iter.next();
            g2.setColor(Color.black);
            g2.setStroke(new BasicStroke(3));
            g2.drawPolygon(polygon);

            if (territory.getOwner().equals(Territory.Own.Computer)) {
                g2.setColor(Color.red);
            }
            else if (territory.getOwner().equals(Territory.Own.User)) {
                g2.setColor(Color.green);
            }
            else if (territory.getOwner().equals(Territory.Own.NoOwner)){
                g2.setColor(Color.lightGray);
            }
            g2.fillPolygon(polygon);
        }
        g2.setColor(Color.black);
        g2.drawString(territory.numberOfArmies + "", territory.capital.xpoints[0], territory.capital.ypoints[0]);
    }
}
