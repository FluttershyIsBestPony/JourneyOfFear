package game.network.server;

public class EnemyPlayerData {
    
    private int x;
    private int y;
    private int id;
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getId() { return id; }
    
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setId(int id) { this.id = id; }
    
    public EnemyPlayerData(int id) {
        this.id = id;
    } 
    
    public EnemyPlayerData(int id, int x, int y) {
        this.x = x;
        this.y = y;
        this.id = id;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof EnemyPlayerData))
            return false;
        
        return ((EnemyPlayerData) o).getId() == id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + this.id;
        return hash;
    }
}