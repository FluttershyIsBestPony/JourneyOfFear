package game.enemy.slime;

import game.enemy.Enemy;
import game.player.Player;
import game.sprite.EntitySprite;
import game.state.StateMultiplayer;
import game.util.resource.AnimationLibrary;
import game.util.resource.SoundLibrary;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;

public class EnemyRedSlime extends Enemy implements EnemySlime {
        
    public EnemyRedSlime(Player player) {
        super(player);
        this.x = (int)(Math.random()*StateMultiplayer.WORLD_SIZE_X);
        this.y = (int)(Math.random()*StateMultiplayer.WORLD_SIZE_Y);
        this.speed = 0.0625;
        this.animationSpeed = 332;
        this.health = 15;
        this.minimapColor = new Color(255,128,128);
    }
    
    @Override
    protected void initializeSprite() {
        sprite = new EntitySprite(4);
        Animation[] animList = {
            AnimationLibrary.BLOB_RIGHT.getAnim(),
            AnimationLibrary.BLOB_UP.getAnim(),
            AnimationLibrary.BLOB_LEFT.getAnim(),
            AnimationLibrary.BLOB_DOWN.getAnim(),
        };
        sprite.setAnimations(animList);
        initializeMask();
    }
    
    @Override
    public void move(int delta) {
        if (stunTimer>0) {
            sprite.getAnim(spritePointer).setCurrentFrame(0);
            sprite.getAnim(spritePointer).stop();
            x+=(knockbackDX*stunTimer)/(KNOCKBACK_DISTANCE*KNOCKBACK_MULTIPLIER);
            y+=(knockbackDY*stunTimer)/(KNOCKBACK_DISTANCE*KNOCKBACK_MULTIPLIER);
            return;
        }
        
        sprite.getAnim(spritePointer).start();
        if (Math.random()*20<1) {
            spritePointer = (int)(Math.random()*4);
        }
        
        int dx = 0;
        int dy = 0;
        
        if (spritePointer == 0)
            dx+=speed*delta;
        else if (spritePointer == 1)
            dy-=speed*delta;
        else if (spritePointer == 2)
            dx-=speed*delta;
        else if (spritePointer == 3)
            dy+=speed*delta;
        
        x += dx;
        y += dy;
    }
    
    @Override
    public void resolveHit(int ox, int oy, int attackId, int damage, double mult) {
        if (attackId != lastAttackId) {
            lastAttackId = attackId;
            initializeKnockback(x-ox,y-oy,mult);
            health-=damage;
            SoundLibrary.SWORD_HIT.play();
        }
    }
}
