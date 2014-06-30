package spring.travel.api.model;

public class Loyalty {

    private Group group;

    private int points;

    public Loyalty() {
    }

    public Loyalty(Group group, int points) {
        this.group = group;
        this.points = points;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
