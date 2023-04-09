package veinminer.objects;
public class Coordinate {
    public int x;
    public int y;
    public int gameID;

    public Coordinate(int x, int y, int gameID) {
        this.x = x;
        this.y = y;
        this.gameID = gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) 
            return true;

        if (!(o instanceof Coordinate)) 
            return false;

        Coordinate vector2 = (Coordinate) o;
        return x == vector2.x && y == vector2.y && gameID == vector2.gameID;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}