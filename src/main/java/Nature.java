import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;

/**
 * Created by Anders
 *
 * A special agent that has infinite resources
 * Trades time?
 *
 */
public class Nature extends Agent {

    public static final String RESORUCE_GATHERING = "gather_resources_from_nature";

    public static final String WOOD_GATHERING = "gather_wood";
    public static final String FOOD_GATHERING = "gather_food";
    public static final String METAL_GATHERING = "gather_metal";

    public static final String REQUEST_WOOD = "wood_request";
    public static final String REQUEST_FOOD = "food_request";
    public static final String REQUEST_METAL = "metal_request";

    private static ArrayList<ContainerID> containers = null;

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

        addBehaviour(new ServeWoodRequest());
        addBehaviour(new ServeFoodRequest());
        addBehaviour(new ServeMetalRequest());
    }



    private class ServeWoodRequest extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE), new MessageTemplate(new MessageTemplate.MatchExpression() {
                @Override
                public boolean match(ACLMessage aclMessage) {
                    return aclMessage.getConversationId().startsWith(REQUEST_WOOD);
                }
            })));

            if (msg != null) {
                // check adult and if at woodmill
                AID sender = msg.getSender();

                int amount = Integer.parseInt(msg.getContent());

                ACLMessage reply = msg.createReply();
                reply.setContent(String.valueOf(2));
                send(reply);


            } else {
                block();
            }
        }
    }

    private class ServeFoodRequest extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE), new MessageTemplate(new MessageTemplate.MatchExpression() {
                @Override
                public boolean match(ACLMessage aclMessage) {
                    return aclMessage.getConversationId().startsWith(REQUEST_FOOD);
                }
            })));

            if (msg != null) {
                // check adult and if at woodmill
                AID sender = msg.getSender();

                int amount = Integer.parseInt(msg.getContent());

                ACLMessage reply = msg.createReply();
                reply.setContent(String.valueOf(2));
                send(reply);


            } else {
                block();
            }
        }
    }

    private class ServeMetalRequest extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE), new MessageTemplate(new MessageTemplate.MatchExpression() {
                @Override
                public boolean match(ACLMessage aclMessage) {
                    return aclMessage.getConversationId().startsWith(REQUEST_METAL);
                }
            })));

            if (msg != null) {
                // check adult and if at woodmill
                AID sender = msg.getSender();

                int amount = Integer.parseInt(msg.getContent());

                ACLMessage reply = msg.createReply();
                reply.setContent(String.valueOf(2));
                send(reply);


            } else {
                block();
            }
        }
    }
}
