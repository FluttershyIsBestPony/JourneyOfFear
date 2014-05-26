package game.state;

import game.enemy.EnemyPlayer;
import game.error.NetworkException;
import game.map.Area;
import game.network.client.NetworkHandler;
import game.player.Player;
import static game.state.StateSingleplayer.WORLD_SIZE_X;
import static game.state.StateSingleplayer.WORLD_SIZE_Y;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class StateMultiplayer extends StateSingleplayer {
    
    /* IP of server */
    public static String ip = "127.0.0.1";

    /* Port on server */
    public static int port = 9999;
    
    private NetworkHandler network;
    
    private List<EnemyPlayer> enemies;
    
    public StateMultiplayer(int id) {
        super(id);
    }
    
    @Override
    public void enter(GameContainer container, StateBasedGame game) {
        super.enter(container,game);
        enemies = new ArrayList<EnemyPlayer>();
        try {
            network = new NetworkHandler(ip,port,player,enemies);
        } catch (UnknownHostException e) {
            throw new NetworkException(e);
        }
        network.start();
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        super.render(container,game,g);
        
        for (EnemyPlayer e : enemies)
            e.render(container,g);
        
        int posX = (int)(7.5 *VIEW_SIZE_X)/10 + camX;
        int posY = (int)(.75 *VIEW_SIZE_Y)/10 + camY;
        int width = (int)(2.3 *VIEW_SIZE_X)/10;
        int height = (int)(((double)WORLD_SIZE_Y / WORLD_SIZE_X)*(2.3 *VIEW_SIZE_X)/10);
        
        for (EnemyPlayer e : enemies) {
            g.setColor(Color.orange);
            g.fillRect((int)(posX + width*((double)e.getX())/WORLD_SIZE_X), 
                    (int)(posY + height*((double)e.getY())/WORLD_SIZE_Y),3,3);    
        }
    }
    
    @Override
    protected void setupArea(GameContainer container, Player player) {
        currentArea = new Area(WORLD_SIZE_X,WORLD_SIZE_Y,container,player);
        player.setArea(currentArea);
    }
}
