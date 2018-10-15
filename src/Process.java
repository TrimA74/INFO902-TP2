import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class Process  implements Runnable {
    private Thread thread;
    private EventBusService bus;
    private boolean alive;
    private boolean dead;
    private int horloge;
    private static int nbProcess = 0;
    private int id = Process.nbProcess++;
    private Token token;
    private boolean wantToken;
    private int nb_thread;
    private int synchronizeCheck = 0;
    private boolean isReadyToSynchronize = false;

    public Process(String name, int nbThread){

    	this.bus = EventBusService.getInstance();
    	this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.


    	this.thread = new Thread(this);
    	this.thread.setName(name);
    	this.alive = true;
    	this.dead = false;
    	this.horloge = 0;
    	this.token = null;
    	this.wantToken = false;
    	this.nb_thread = nbThread;
    	this.thread.start();
    }

    // Declaration de la methode de callback invoquee lorsqu'un message de type Bidule transite sur le bus
    /*
	@Subscribe
    public void onTrucSurBus(Message message){
    	System.out.println(Thread.currentThread().getName() + " receives: " + message.getPayload() + " for " + this.thread.getName());
    	this.horloge = Math.max(message.getStamping(),this.horloge) + 1;
    }

    */
	
    public void run(){
		System.out.println(this.thread.getId());
    	int loop = 0;

		System.out.println(Thread.currentThread().getName() + " id :" + this.id);
		
    	while(this.alive){

    		System.out.println(Thread.currentThread().getName() + " Loop : " + loop);
    		try{
				Thread.sleep(500);
				/*
				request();
				System.out.println("je suis en section critique");
				release();
				System.out.println("je suis plus en section critique");
				*/

    			
    			if(Thread.currentThread().getName().equals(Integer.toString(1))){
    				//broadcast("mon payload");
    				//sendTo("mon payload",2);
					//System.out.println("coucou");
					synchronize();

    			}

    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		loop++;
    	}

    	// liberation du bus
    	this.bus.unRegisterSubscriber(this);
    	this.bus = null;
    	System.out.println(Thread.currentThread().getName() + " stoped");
	this.dead = true;
    }

    public void initToken(){
    	int to = (Integer.valueOf(this.thread.getName())+1) % this.nb_thread;
    	System.out.println(this.thread.getName() + " to" + Integer.toString(to));
		Token token = new Token(Integer.toString(to));
		bus.postEvent(token);
	}

	public void synchronize() throws Exception {
		this.synchronizeCheck = 1;
		this.isReadyToSynchronize = true;
		Synchronizer synchronizer = new Synchronizer(this.thread.getName(),"");
		bus.postEvent(synchronizer);
		while(this.synchronizeCheck < this.nb_thread){
			Thread.sleep(500);
		}
		System.out.println("all is synchronized");
	}

	@Subscribe
	public void onSynchronize(Synchronizer synchronizer){
    	if(this.thread.getName().equals(synchronizer.getInitializer())){
    		this.synchronizeCheck++;
		}else {
    		while(!this.isReadyToSynchronize){
    			try {
					Thread.sleep(500);
				}
				catch (Exception e) {

				}
			}
			bus.postEvent(synchronizer);
		}
	}

    public void broadcast(Object payload){
    	this.horloge++;
		BroadcastMessage broadcastMessage = new BroadcastMessage(this.horloge, payload, this.thread.getName());
		System.out.println(Thread.currentThread().getName() + " send : " + broadcastMessage.getPayload());
		bus.postEvent(broadcastMessage);
	}

	@Subscribe
	public void onBroadcast(BroadcastMessage broadcastMessage){
    	if(!broadcastMessage.getSender().equals(this.thread.getName())){
			System.out.println(Thread.currentThread().getName() + " receives: " + broadcastMessage.getPayload() + " for " + this.thread.getName());
			this.horloge = Math.max(broadcastMessage.getStamping(),this.horloge) + 1;
		}
		System.out.println(this.thread.getName() + " stamping : " + this.horloge);
	}

	public void sendTo(Object payload, int to){
		this.horloge++;
    	MessageTo messageTo = new MessageTo(this.horloge,payload, Integer.toString(to));
		System.out.println(Thread.currentThread().getName() + " send : " + messageTo.getPayload());
		bus.postEvent(messageTo);
	}

	@Subscribe
	public void onReceive(MessageTo messageTo){
		if(messageTo.getReceiver().equals(this.thread.getName())){
			System.out.println(Thread.currentThread().getName() + " receives: " + messageTo.getPayload() + " for " + this.thread.getName());
			this.horloge = Math.max(messageTo.getStamping(),this.horloge) + 1;

		}
	}

	@Subscribe
	public void onToken(Token token){
		if(token.getReceiver().equals(this.thread.getName())) {
			if (this.wantToken) {
				this.token = token;
			} else {
				int to = (Integer.valueOf(this.thread.getName())+1) % this.nb_thread;
				System.out.println("sending token to " + to);
				token.setReceiver(Integer.toString(to));
				bus.postEvent(token);
			}
		}
	}

	public void release (){
		this.wantToken = false;
		int to = (Integer.valueOf(this.thread.getName())+1) % this.nb_thread;
		this.token.setReceiver(Integer.toString(to));
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

    public void waitStoped(){
	while(!this.dead){
	    try{
		Thread.sleep(500);
	    }catch(Exception e){
		e.printStackTrace();
	    }
	}
    }
    public void stop(){
    	this.alive = false;
    }
}
