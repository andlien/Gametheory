import jade.core.Agent;

/**
 * Created by Anders
 *
 * Super-class for our banish agents
 */
public abstract class BanishAgent extends Agent {

    private double wood;
    private double food;
    private double wealth;
    private double iron;


    public BanishAgent(double wood, double food, double wealth, double iron) {
        this.wood = wood;
        this.food = food;
        this.wealth = wealth;
        this.iron = iron;
    }

    public boolean giveWood(BanishAgent otherAgent, double amount) {
        if (this.wood > amount) {
            this.wood -= amount;
            otherAgent.wood += amount;
            return true;
        } else {
            return false;
        }
    }

    public boolean giveFood(BanishAgent otherAgent, double amount) {
        if (this.food > amount) {
            this.food -= amount;
            otherAgent.food += amount;
            return true;
        } else {
            return false;
        }
    }

    public boolean giveWealth(BanishAgent otherAgent, double amount) {
        if (this.wealth > amount) {
            this.wealth -= amount;
            otherAgent.wealth += amount;
            return true;
        } else {
            return false;
        }
    }

    public boolean giveIron(BanishAgent otherAgent, double amount) {
        if (this.iron > amount) {
            this.iron -= amount;
            otherAgent.iron += amount;
            return true;
        } else {
            return false;
        }
    }


}
