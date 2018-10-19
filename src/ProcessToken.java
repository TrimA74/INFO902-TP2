import com.google.common.eventbus.Subscribe;

public class ProcessToken implements Runnable {

    private Thread thread;
    private Com com;
    private Token token;
    private boolean wantToken;
    private EventBusService bus;

    public ProcessToken(Com com) {
        this.thread = new Thread(this);
        this.com = com;

        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.

        this.thread.setName("tokenThread");
        this.wantToken = false;
    }

    @Override
    public void run() {

    }

    public void initToken(){
        int to = (this.com.getIdProcess()+1) % Com.getNbInstance();
        Token token = new Token(to);
        this.bus.postEvent(token);
    }

    @Subscribe
    public void onToken(Token token){
        if(token.getReceiver() == this.com.getIdProcess()) {
            if (this.wantToken) {
                this.token = token;
            } else {
                int to = (this.com.getIdProcess()+1) % Com.getNbInstance();
                token.setReceiver(to);
                this.bus.postEvent(token);
            }
        }
    }

    public void release (){
        this.wantToken = false;
        int to = (this.com.getIdProcess()+1) % Com.getNbInstance();
        this.token.setReceiver(to);
        bus.postEvent(token);
        this.token= null;
    }

    public void request(){
        this.wantToken = true;
        while(this.token == null){
            try {
                Thread.sleep(200);
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }


}
