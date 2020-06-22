package tim03we.futureplots.provider;

import tim03we.futureplots.utils.Plot;

import java.util.List;

public class MySQLProvider implements DataProvider {

    @Override
    public void connect() {

    }

    @Override
    public void save() {
    }

    @Override
    public void claimPlot(String name, Plot plot) {

    }

    @Override
    public void deletePlot(Plot plot) {

    }

    @Override
    public boolean isHelper(String name, Plot plot) {
        return false;
    }

    @Override
    public boolean isMember(String name, Plot plot) {
        return false;
    }

    @Override
    public boolean isDenied(String name, Plot plot) {
        return false;
    }

    @Override
    public boolean hasOwner(Plot plot) {
        return false;
    }

    @Override
    public void setOwner(String name, Plot plot) {

    }

    @Override
    public String getOwner(Plot plot) {
        return null;
    }

    @Override
    public List<String> getHelpers(Plot plot) {
        return null;
    }

    @Override
    public List<String> getMembers(Plot plot) {
        return null;
    }

    @Override
    public List<String> getDenied(Plot plot) {
        return null;
    }

    @Override
    public void addHelper(String name, Plot plot) {

    }

    @Override
    public void addMember(String name, Plot plot) {

    }

    @Override
    public void addDenied(String name, Plot plot) {

    }

    @Override
    public void removeHelper(String name, Plot plot) {

    }

    @Override
    public void removeMember(String name, Plot plot) {

    }

    @Override
    public void removeDenied(String name, Plot plot) {

    }

    @Override
    public Plot getPlot(String name, Object number, Object level) {
        return null;
    }

    @Override
    public List<String> getPlots(String name, Object level) {
        return null;
    }

    @Override
    public Plot getNextFreePlot(String level) {
        return null;
    }
}
