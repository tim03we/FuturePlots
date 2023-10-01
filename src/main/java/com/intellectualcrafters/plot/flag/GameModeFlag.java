package com.intellectualcrafters.plot.flag;

import com.intellectualcrafters.plot.util.PlotGameMode;

public class GameModeFlag extends Flag<PlotGameMode> {

    public GameModeFlag(String name) {
        super(name);
    }

    @Override
    public String valueToString(Object value) {
        return ((PlotGameMode) value).getName();
    }

    @Override
    public PlotGameMode parseValue(String value) {
        switch (value.toLowerCase()) {
            case "survival":
            case "s":
            case "0":
                return PlotGameMode.SURVIVAL;
            case "creative":
            case "c":
            case "1":
                return PlotGameMode.CREATIVE;
            case "adventure":
            case "a":
            case "2":
                return PlotGameMode.ADVENTURE;
            case "spectator":
            case "3":
                return PlotGameMode.SPECTATOR;
            default:
                return PlotGameMode.NOT_SET;
        }
    }

    @Override
    public String getValueDescription() {
        return "Flag value must be a gamemode: 'survival', 'creative', 'adventure' or 'spectator'";
    }
}
