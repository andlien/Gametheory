import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ProposeInitiator;
import jade.proto.ProposeResponder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EnumMap;
import java.util.Random;

/**
 * Created by Anders on 26.04.2016.
 */
public class Inhabitant extends Agent {

    protected static final String WORK_TYPE = "work_key";

    private static int startX = 0;
    private static int startY = 0;

    protected EnumMap<Resource, Double> resources;

    protected EnumMap<Resource, JLabel> jlabels = new EnumMap<>(Resource.class);

    private AID nature;

    @Override
    protected void setup() {
        resources = new EnumMap<Resource, Double>(Resource.class) {
            @Override
            public Double put(Resource key, Double value) {
                Double a = super.put(key, value);
                updateResourceModel(key);
                return a;
            }
        };
        resources.put(Resource.WOOD, 100.0);
        resources.put(Resource.FOOD, 100.0);
        resources.put(Resource.WEALTH, 100.0);
        resources.put(Resource.METAL, 100.0);
        resources.put(Resource.TIME, 100.0);

        setupGUI();
        
        DFAgentDescription t = new DFAgentDescription();
        ServiceDescription s = new ServiceDescription();
        s.setType(Nature.RESORUCE_GATHERING);
        t.addServices(s);

        try {
            DFAgentDescription[] res = DFService.search(this, t);
            if (res.length !=  1) {
                System.err.println("Nature not found!");
            } else {
                nature = res[0].getName();
            }

        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    protected void setupGUI() {
        synchronized (Inhabitant.class) {
            JFrame frame = new JFrame(getLocalName());

            JPanel p = setupResourceView();

            JButton find_work_btn = new JButton("Find me some work!");
            find_work_btn.addActionListener(e -> findWork());
            p.add(find_work_btn);

            frame.getContentPane().add(p, BorderLayout.CENTER);

            frame.addWindowListener(new	WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    doDelete();
                }
            } );

            frame.setResizable(true);

            frame.pack();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation(startX, startY);

            startY += frame.getHeight();
            if (startY > screenSize.getHeight()) {
                startY = 0;
                startX += frame.getWidth();
            }

            frame.setVisible(true);
        }

    }

    private void updateResourceModel(Resource r) {
        System.out.println("UPDATING " + r.toString() + "!");
        if (jlabels.get(r) != null) {
            jlabels.get(r).setText(String.valueOf(resources.get(r)));
        }
    }

    public boolean spendTime(double amount) {
        if (resources.get(Resource.TIME) > amount) {
            resources.put(Resource.TIME, resources.get(Resource.TIME) - amount);
            return true;
        } else {
            return false;
        }
    }

    public void resetTime() {
        resources.put(Resource.TIME, 100.0);
    }

    public boolean hasTime(double amount) {
        return resources.get(Resource.TIME) > amount;
    }

    protected void findWork() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription serviceTemp = new ServiceDescription();
        serviceTemp.setType(Manager.WORK_TYPE);
        template.addServices(serviceTemp);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            DFAgentDescription chosen = result[new Random().nextInt(result.length)];
            addBehaviour(new ApplyForJob((ServiceDescription) chosen.getAllServices().next(), chosen.getName()));
        } catch (FIPAException e) {
            e.printStackTrace();
        }

    }

    protected JPanel setupResourceView() {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(8, 2));

        for (Resource r : Resource.values()) {
            p.add(new JLabel(r.toString()));

            JLabel resValue = new JLabel(String.valueOf(resources.get(r)));
            jlabels.put(r, resValue);
            p.add(resValue);
        }
        return p;
    }

    private static class MACLMessage extends ACLMessage {
        private static final String REQUEST_RES = "request_res_key";
        private static final String REQUEST_AMOUNT = "request_amount_key";
        private static final String TRADING_RES = "trade_res_key";
        private static final String TRADING_AMOUNT = "trade_amount_key";

        // used in trading
        public MACLMessage(Resource requestRes, double amount, Resource tradingRes, double amount2) {
            super(PROPOSE);
            setConversationId("trade" + System.currentTimeMillis());
            addUserDefinedParameter(REQUEST_RES, requestRes.toString());
            addUserDefinedParameter(REQUEST_AMOUNT, String.valueOf(amount));
            addUserDefinedParameter(TRADING_RES, tradingRes.toString());
            addUserDefinedParameter(TRADING_AMOUNT, String.valueOf(amount2));
            // get reciever by searching df
//            addReceiver(reciver);
        }

        // used in job-applying
        public MACLMessage(ServiceDescription job, AID reciever) {
            super(PROPOSE);
            setConversationId("apply-for-job:" + job.getName() + System.currentTimeMillis());
            setContent(Manager.REQUEST_WORK);
            addReceiver(reciever);
        }
    }

    private class ApplyForJob extends ProposeInitiator {

        private Resource res;

        public ApplyForJob(ServiceDescription job, AID manager) {
            super(Inhabitant.this, new MACLMessage(job, manager));
            System.out.println("Applying for job " + job.getName());
            res = Resource.valueOf(job.getName().split("_")[0]);
        }

        @Override
        protected void handleAcceptProposal(ACLMessage accept_proposal) {
            System.out.println(getAID().getName() + " says: I have a job in " + res.toString());
        }
    }

    protected class TradeResourceInitiator extends ProposeInitiator {

        Resource requestRes;
        double requestAmount;
        Resource tradeRes;
        double tradeAmount;

        public TradeResourceInitiator(Resource requestRes, double amount, Resource tradingRes, double amount2) {
            super(Inhabitant.this, new MACLMessage(requestRes, amount, tradingRes, amount2));
            this.requestRes = requestRes;
            this.requestAmount = amount;
            this.tradeRes = tradingRes;
            this.tradeAmount = amount2;
        }

        @Override
        protected void handleAcceptProposal(ACLMessage accept_proposal) {
            resources.put(requestRes, resources.get(requestRes) + requestAmount);
            resources.put(tradeRes, resources.get(tradeRes) - tradeAmount);
        }
    }

    private class TradeResourceResponder extends ProposeResponder {

        public TradeResourceResponder(Agent a, MessageTemplate mt) {
            super(a, mt);
        }
    }

}
