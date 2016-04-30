import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ProposeResponder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Anders on 30.04.2016.
 */
public class Manager extends Inhabitant {

    public static final String REQUEST_WORK = "requesting_work_here";
    public static final String FIRED_MSG = "you_are_fired";
    private Resource produce;

    private DefaultListModel<AID> workers;

    private static int startX = 0;
    private static int startY = 0;

    private JList<AID> aids;

    @Override
    protected void setup() {
        super.setup();

        Object[] args = getArguments();

        if (args.length != 1) {
            System.err.println("Manager started without resource!");
            doDelete();
        }
        try {
            produce = Resource.valueOf((String)args[0]);
        } catch (IllegalArgumentException e) {
            System.err.println("Wrong resource-enum");
            doDelete();
        }

        // Managers need start capital
        resources.put(Resource.WEALTH, resources.get(Resource.WEALTH) + 1000.0);

        DFAgentDescription description = new DFAgentDescription();
        description.setName(getAID());
        ServiceDescription service = new ServiceDescription();
        service.setType(WORK_TYPE);
        service.setName(produce.toString() + "_WORK");
        description.addServices(service);

        try {
            DFService.register(this, description);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        addBehaviour(new RegisterWorker());
    }

    @Override
    protected void setupGUI() {
        synchronized (Manager.class) {
            JFrame frame = new JFrame(getLocalName());

            JPanel outer = new JPanel(new GridLayout(2,1));

            JPanel p = setupResourceView();
            outer.add(p);

            JPanel company = new JPanel(new GridLayout(1,2));

            workers = new DefaultListModel<>();
            workers.addElement(this.getAID());
            aids = new JList<>(workers);
            aids.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            aids.setMaximumSize(new Dimension(200, 200));
            aids.setLayoutOrientation(JList.VERTICAL);
            JScrollPane pane = new JScrollPane(aids);
            pane.setMaximumSize(new Dimension(200,200));
            company.add(pane);

            JButton fire = new JButton("Fire!");
            fire.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            });
            company.add(fire);

            outer.add(company);

            frame.getContentPane().add(outer, BorderLayout.CENTER);

            frame.addWindowListener(new	WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    doDelete();
                }
            } );

            frame.setResizable(true);

            frame.pack();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            startX = (int) (screenSize.getWidth() - frame.getWidth());
            frame.setLocation(startX, startY);

            startY += frame.getHeight();
            if (startY > screenSize.getHeight()) {
                startY = 0;
            }

            frame.setVisible(true);
        }
    }

    @Override
    protected void findWork() {

    }

    private boolean positionsAvailable() {
        return workers.size() < 5;
    }

    private class FireWorker extends OneShotBehaviour {

        @Override
        public void action() {
            ACLMessage fire_msg = new ACLMessage(ACLMessage.INFORM);
            fire_msg.setContent(FIRED_MSG);
            fire_msg.addReceiver(aids.getSelectedValue());
            send(fire_msg);

            System.out.println("Fired agent " + aids.getSelectedValue().getLocalName() + " from " + produce.toString());
        }
    }

    private class RegisterWorker extends ProposeResponder {

        public RegisterWorker() {
            super(Manager.this, MessageTemplate.and(
                    MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                    MessageTemplate.MatchContent(REQUEST_WORK)
            ));
        }
        @Override
        protected ACLMessage prepareResponse(ACLMessage propose) throws NotUnderstoodException, RefuseException {
            ACLMessage response = propose.createReply();
            if (positionsAvailable() && !workers.contains(propose.getSender())) {
                workers.addElement(propose.getSender());
                System.out.println("Manager " + getAID().getLocalName() + "(" + produce.toString() + ") hired agent" + propose.getSender().getLocalName());
                response.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            } else {
                System.out.println("Manager " + getAID().getLocalName() + "(" + produce.toString() + ") could not hire " + propose.getSender().getLocalName());
                response.setPerformative(ACLMessage.REJECT_PROPOSAL);
            }
            return response;
        }
    }



}
