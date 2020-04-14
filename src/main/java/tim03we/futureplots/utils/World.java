package tim03we.futureplots.utils;

public class World {

    private String levelName;

    public World(String levelName) {
        this.levelName = levelName;
    }

    public boolean exists() {
        return Settings.levels.contains(levelName);
    }
}
