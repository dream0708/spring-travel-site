package spring.travel.api.model;

public class Profile {

    private LifeCycle lifecycle;

    private Spending spending;

    private Gender gender;

    public Profile() {
    }

    public Profile(LifeCycle lifecycle, Spending spending, Gender gender) {
        this.lifecycle = lifecycle;
        this.spending = spending;
        this.gender = gender;
    }

    public LifeCycle getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(LifeCycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Spending getSpending() {
        return spending;
    }

    public void setSpending(Spending spending) {
        this.spending = spending;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
