import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

/**
 * Created by Anders
 *
 * A special agent that has infinite resources
 * Trades time?
 *
 */
public class Nature extends BanishAgent {

    private static final String RESORUCE_GATHERING = "gather_resources_from_nature";

    public static final String WOOD_GATHERING = "gather_wood";
    public static final String FOOD_GATHERING = "gather_food";
    public static final String METAL_GATHERING = "gather_metal";

    public Nature() {
        super(Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
    }

    @Override
    protected void setup() {
        DFAgentDescription template = new DFAgentDescription();
        template.setName(getAID());
        ServiceDescription wood_description = new ServiceDescription();
        wood_description.setType(RESORUCE_GATHERING);
        wood_description.setName(WOOD_GATHERING);
        ServiceDescription food_description = new ServiceDescription();
        food_description.setType(RESORUCE_GATHERING);
        food_description.setName(FOOD_GATHERING);
        ServiceDescription metal_description = new ServiceDescription();
        metal_description.setType(RESORUCE_GATHERING);
        metal_description.setName(FOOD_GATHERING);
        template.addServices(wood_description);
        template.addServices(food_description);
        template.addServices(metal_description);

        try {
            DFService.register(this, template);
        } catch (FIPAException e) {
            e.printStackTrace();
        }



    }

    private static class ServeResourcesBehaviour extends Behaviour {

    }
}
