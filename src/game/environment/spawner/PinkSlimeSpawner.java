package game.environment.spawner;

import game.enemy.Enemy;
import game.enemy.blob.EnemyBlob;
import game.enemy.blob.EnemyRedBlob;
import game.player.Player;
import game.sprite.ImageMask;
import game.util.resource.AnimationLibrary;
import java.util.List;

public class PinkSlimeSpawner extends Spawner {
    
    public PinkSlimeSpawner(Player player, List<Enemy> enemies) {
        super(player,enemies,3000);
    }
    
    public PinkSlimeSpawner(Player player, List<Enemy> enemies, int x, int y) {
        super(player,enemies,x,y,3000);
    }
    
    @Override
    protected void initializeSprite() {
        sprite = AnimationLibrary.PINK_SLIME_PIT.getAnim();
        mask = new ImageMask(sprite.getImage(0),x,y);
        sprite.setDuration(0,1000);
        spriteWidth = sprite.getImage(0).getWidth()*4;
        spriteHeight = sprite.getImage(0).getHeight()*4;
    }
    
    @Override
    protected Enemy getSpawnedEnemy() {
        Enemy e = new EnemyRedBlob(player);
        e.setX(x);
        e.setY(y);
        return e;
    }
    
    @Override
    protected void resolveCollision() {
        if (mask.intersects(player.getCollisionMask()))
            player.resolveHit(x+64,y+64,2);
        for (Enemy e : enemies) {
            if (e instanceof EnemyBlob)
                continue;
            if (mask.intersects(e.getCollisionMask())) {
                e.resolveHit(x,y,attackId,2);
            }
        }
    }
}
