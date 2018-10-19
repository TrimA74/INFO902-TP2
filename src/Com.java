import com.google.common.eventbus.Subscribe;

import java.util.List;

public class Com {

    private Lamport lamport;

    private static int nbInstance = 0;
    private int idProcess;

    private EventBusService bus;

    private ProcessToken processToken;

    private List<Message> bal;

    private int synchronizeCheck=0;

    public int getIdProcess() {
        return idProcess;
    }

    public Com(Lamport lamport) {
        this.lamport = lamport;
        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.
        this.idProcess = nbInstance;
        this.nbInstance++;
        this.processToken = new ProcessToken(this);
        if(idProcess == 0){
            processToken.initToken();
        }

    }

    public Com(){

    }

    public void broadcast(Object payload){
        lamport.setClock(lamport.getClock()+1);
        BroadcastMessage broadcastMessage = new BroadcastMessage(lamport.getClock(), payload, this.idProcess);
        System.out.println(Thread.currentThread().getName() + " send : " + broadcastMessage.getPayload());
        bus.postEvent(broadcastMessage);
    }

    public EventBusService getBus() {
        return bus;
    }

    public static int getNbInstance() {
        return nbInstance;
    }

    @Subscribe
    public void onBroadcast(BroadcastMessage broadcastMessage){
        if(broadcastMessage.getSender() != this.idProcess){
            //System.out.println(Thread.currentThread().getName() + " receives: " + broadcastMessage.getPayload() + " for " + this.thread.getName());
            this.lamport.setClock(broadcastMessage.getStamping());
        }
        System.out.println(this.idProcess + " stamping : " + this.lamport.getClock());
    }

    public void requestSC(){
        this.processToken.request();
    }

    public void releaseSC(){
        this.processToken.release();
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






}
