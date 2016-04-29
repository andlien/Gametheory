import java.util.ArrayList;

/**
 * Created by Anders on 26.04.2016.
 */
public class Adult extends BanishAgent {

    private double time;

    public Adult() {
        super(100,200,200,50);
    }


    public boolean spendTime(double amount) {
        if (time > amount) {
            time -= amount;
            return true;
        } else {
            return false;
        }
    }

    public void resetTime() {
        time = 100;
    }
}
