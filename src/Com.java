import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Com {

    private Lamport lamport;

    private static int nbInstance = 0;
    private int idProcess;

    private EventBusService bus;

    private ProcessToken processToken;

    private List<Message> bal;

    private int synchronizeCheck=0;
    private boolean wantToken = false;
    private Token token;
    private boolean isAlive = true;

    public Com(Lamport lamport) {
        this.lamport = lamport;
        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.
        this.idProcess = nbInstance;
        this.nbInstance++;
        this.bal = new ArrayList<>();

        //old version
        /*this.processToken = new ProcessToken(this);
        if(idProcess == 0){
            processToken.initToken();
        }*/

        //new version
        if(idProcess == 0){
            initToken();
        }

    }

    public Com(){

    }

    public int getIdProcess() {
        return idProcess;
    }

    public EventBusService getBus() {
        return bus;
    }

    public static int getNbInstance() {
        return nbInstance;
    }

    public List<Message> getBal() {
        return bal;
    }

    public <T extends Message> Map<Integer, T> getMessagesByType(Class<T> fType) {
        Map<Integer, T> map = new HashMap<>();
        for (int i=0; i<this.bal.size(); i++) {
            if (this.bal.get(i).getClass() ==  fType) {
                map.put(i, fType.cast(this.bal.get(i)));
            }
        }
        return map;
    }



    public void broadcast(Object payload){
        lamport.setClock(lamport.getClock()+1);
        BroadcastMessage broadcastMessage = new BroadcastMessage(lamport.getClock(), payload, this.idProcess);
        System.out.println(this.idProcess + " send : " + broadcastMessage.getPayload());
        bus.postEvent(broadcastMessage);
    }

    @Subscribe
    public void onBroadcast(BroadcastMessage broadcastMessage){
        if(broadcastMessage.getSender() != this.idProcess){
            System.out.println(Thread.currentThread().getName() + " receives: " + broadcastMessage.getPayload() + " for " + this.idProcess);
            this.lamport.setClock(broadcastMessage.getStamping());
            this.bal.add(broadcastMessage);
        }
        //System.out.println(this.idProcess + " stamping : " + this.lamport.getClock());
    }

    public void sendTo(Object payload, int to){
        lamport.setClock(lamport.getClock()+1);
        MessageTo messageTo = new MessageTo(lamport.getClock(), payload, to);
        System.out.println(this.idProcess + " send : " + messageTo.getPayload());
        bus.postEvent(messageTo);
    }

    @Subscribe
    public void onReceive(MessageTo messageTo){
        if(messageTo.getReceiver() == this.idProcess){
            System.out.println(this.idProcess + " receives: " + messageTo.getPayload() + " for " + this.idProcess);
            lamport.setClock(Math.max(messageTo.getStamping(),lamport.getClock()) + 1);
        }
    }


    public void synchronize() throws Exception {
        Synchronizer synchronizer = new Synchronizer(this.idProcess);
        this.bus.postEvent(synchronizer);
        System.out.println(this.idProcess + " à envoyé message de syncro");

        while(this.synchronizeCheck < Com.getNbInstance() - 1) {
            Thread.sleep(500L);
        }

        this.synchronizeCheck = 0;
        System.out.println(this.idProcess + " is synchronized");
    }

    @Subscribe
    public void onSynchronize(Synchronizer synchronizer){
        this.synchronizeCheck++;
    }





    public void initToken(){
        int to = (this.idProcess+1) % this.nbInstance;
        this.token = new Token(to);
        this.bus.postEvent(this.token);
    }

    @Subscribe
    public void onToken(Token token){
        if(token.getReceiver() == this.idProcess) {
            if (this.wantToken) {
                this.token = token;
            } else {
                int to = (this.idProcess+1) % this.nbInstance;
                token.setReceiver(to);
                if(isAlive)
                    this.bus.postEvent(token);
            }
        }
    }

    public void requestSC(){
        //old
        //this.processToken.request();

        //new
        this.wantToken = true;
        while(this.token == null){
            try {
                Thread.sleep(200);
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void releaseSC(){
        //old
        //this.processToken.release();

        //new
        this.wantToken = false;
        int to = (this.idProcess+1) % this.nbInstance;
        this.token.setReceiver(to);
        bus.postEvent(token);
        this.token= null;
    }

    public void stop(){
        // liberation du bus
        isAlive = false;
        this.bus.unRegisterSubscriber(this);
        this.bus = null;
    }

}
