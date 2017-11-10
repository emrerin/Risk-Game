import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GameComponent extends JComponent implements MouseListener {
    private Game game;

    public GameComponent(Game game) {
        super();
        this.addMouseListener(this);
        this.game = game;
        this.load();
    }

    public void load(){
        for(Component component : this.getComponents()) {
            if(component instanceof GameComponent ) {
                this.remove(component);
            }
        }

        for(Territory territory : game.getTerritories()) {
            this.add(new TerritoryComponent(territory));
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.blue);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point clicked_point = e.getPoint();
        Territory territory = this.game.findTerritoryAt(clicked_point);
        if(territory != null) {
            if(SwingUtilities.isLeftMouseButton(e)) {
                if (game.getState().equals(Game.State.Selection)) {
                    if (territory.getOwner().equals(Territory.Own.NoOwner)) {
                        territory.setOwner(Territory.Own.User);
                        territory.incrementNumberOfArmy();
                        System.out.println("User selected territory: " + territory.name);
                        territory = this.game.getRandomTerritory(Territory.Own.NoOwner);
                        territory.setOwner(Territory.Own.Computer);
                        territory.incrementNumberOfArmy();
                        System.out.println("Computer selected territory: " + territory.name);
                    } else if (territory.getOwner().equals(Territory.Own.User)) {
                        System.out.println("This territory is already occupied by You!");
                    } else {
                        System.out.println("This territory is occupied by the Computer!");
                    }
                    if (game.getState().equals(Game.State.Attack)) {
                        this.game.currentTurn = new Turn(game.getTerritoryMap(), game.reinforcementMap, game.terrCountMap);
                        System.out.println("Reinforcements ready for distribution: " + this.game.currentTurn.getReinforcementsCount());
                    }
                } else {
                    if (game.currentTurn.currentState.equals(Turn.State.Started)) {
                        if (territory.getOwner().equals(Territory.Own.User)) {
                            territory.incrementNumberOfArmy();
                            this.game.currentTurn.useReinforcements();
                            System.out.println("Reinforcements remaining: " + this.game.currentTurn.getReinforcementsCount());
                            if (this.game.currentTurn.getReinforcementsCount() == 0) {
                                this.game.currentTurn.distributeComputerReinforcements(this.game.getTerritoryMap());
                                System.out.println("Computer is distributing reinforcements");
                                System.out.println("Select a country for attacking!");
                                game.currentTurn.currentState = Turn.State.Attacking;
                            }
                        } else {
                            System.out.println("This territory belongs to the Computer!");
                        }
                    } else if (game.currentTurn.currentState.equals(Turn.State.Finished)) {
                        this.game.currentTurn = new Turn(game.getTerritoryMap(), game.reinforcementMap, game.terrCountMap);
                        System.out.println("You earned " + this.game.currentTurn.getReinforcementsCount() + " reinforcements.");
                    } else {
                        if (territory.getOwner().equals(Territory.Own.User)) {
                            this.game.currentTurn.lastSelectedTerritory = territory;
                            System.out.println(territory.name + " is selected!");
                            System.out.println("Right click to attack a neighbouring country!");
                        } else {
                            System.out.println("This territory belongs to the Computer!");
                        }
                    }
                }
            } else if(SwingUtilities.isRightMouseButton(e)) {
                if(this.game.getState().equals(Game.State.Attack) && this.game.currentTurn.getState().equals(Turn.State.Attacking)) {
                    if (territory.getOwner().equals(Territory.Own.User)) {
                        if (this.game.currentTurn.lastSelectedTerritory.isNeighbour(territory)) {
                            if (this.game.currentTurn.lastSelectedTerritory.decrementNumberOfArmy()) {
                                territory.incrementNumberOfArmy();
                            } else {
                                System.out.println("There must be at least one army in a territory!");
                            }
                        } else {
                            System.out.println("This territory is not a neighbour of the selected territory!");
                        }
                    } else {
                        if(this.game.currentTurn.lastSelectedTerritory.isNeighbour(territory)) {
                            this.game.currentTurn.attack(territory, Territory.Own.User);
                            Territory.Own winner = game.getWinner();
                            if(winner.equals(Territory.Own.NoOwner)) {
                                this.game.currentTurn.attackComputer(this.game);
                            }
                            if(winner.equals(Territory.Own.Computer)) {
                                System.out.println("You lost!");
                            } else if(winner.equals(Territory.Own.User)) {
                                System.out.println("You win!");
                            }
                            game.currentTurn.currentState = Turn.State.Finished;
                        } else {
                            System.out.println("This territory is not a neighbour of the selected territory!");
                        }
                    }
                }
            }
            game.getGameComponent().repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e){}

    @Override
    public void mouseReleased(MouseEvent e){}

    @Override
    public void mouseEntered(MouseEvent e){}

    @Override
    public void mouseExited(MouseEvent e){}

}
