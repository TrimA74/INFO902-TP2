import com.google.common.eventbus.Subscribe;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


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
    private int de;
    private ArrayList<Integer> otherDe;
    private int nbResultReceived;

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
    	otherDe = new ArrayList<>();
    	for(int i=0; i < this.nb_thread;i++){
    		otherDe.add(i,0);
		}
		this.nbResultReceived = 0;

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
		
    	while(this.alive && loop < 3){

    		System.out.println(Thread.currentThread().getName() + " Loop : " + loop);
    		try{
				Thread.sleep(500);
				this.de = 1 + (int)(Math.random() * 6);
				broadcastDe(this.de);

				while(nbResultReceived<this.nb_thread-1)
				{
					Thread.sleep(500);
				}
				nbResultReceived = 0;

				//affichage otherDe
				System.out.println("affichage de otherDe dans " +this.thread.getName());
				for(int i=0; i<this.otherDe.size(); i++)
				{
					System.out.println(this.otherDe.get(i));
				}
				System.out.println("fin affichage de otherDe dans " +this.thread.getName());


				boolean max = true;
				for(int i=0; i<this.otherDe.size(); i++)
				{
					if(this.otherDe.get(i) >= this.de)
						max = false;
				};

				if(max){
					request();
					System.out.println("value max est de" + this.de);
					PrintWriter writer = new PrintWriter("results.txt", "UTF-8");
					writer.println(Thread.currentThread().getName() + " ecrit dans le fichier : " + this.de);
					writer.close();
					release();
				}

				synchronize();
				Thread.sleep(1000);
				System.out.println("------------------------------------------------------------------");
				Thread.sleep(1000);


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
		Token token = new Token(Integer.toString(to));
		bus.postEvent(token);
	}

	public void synchronize() throws Exception {
		Synchronizer synchronizer = new Synchronizer(this.thread.getName(),"");
		bus.postEvent(synchronizer);
		System.out.println(this.thread.getName() + " à envoyé message de syncro");
		while(this.synchronizeCheck < this.nb_thread-1){
			Thread.sleep(500);
		}
		this.synchronizeCheck = 0;
		System.out.println(this.thread.getName() + " is synchronized");
	}

	@Subscribe
	public void onSynchronize(Synchronizer synchronizer){
		System.out.println(this.thread.getName() + " a recu message de syncro" );
    	this.synchronizeCheck++;
	}

    public void broadcast(Object payload){
    	this.horloge++;
		BroadcastMessage broadcastMessage = new BroadcastMessage(this.horloge, payload, this.thread.getName());
		System.out.println(Thread.currentThread().getName() + " send : " + broadcastMessage.getPayload());
		bus.postEvent(broadcastMessage);
	}

	public void broadcastDe(Object payload){
		this.horloge++;
		DeMessage deMessage = new DeMessage(this.horloge, payload, this.thread.getName());
		System.out.println(Thread.currentThread().getName() + " send deValue : " + deMessage.getPayload());
		bus.postEvent(deMessage);
	}

	@Subscribe
	public void onBroadcast(BroadcastMessage broadcastMessage){
    	if(!broadcastMessage.getSender().equals(this.thread.getName())){
			//System.out.println(Thread.currentThread().getName() + " receives: " + broadcastMessage.getPayload() + " for " + this.thread.getName());
			this.horloge = Math.max(broadcastMessage.getStamping(),this.horloge) + 1;
		}
		//System.out.println(this.thread.getName() + " stamping : " + this.horloge);
	}

	@Subscribe
	public void onBroadcastDe(DeMessage deMessage){
		if(!deMessage.getSender().equals(this.thread.getName())){
			this.horloge = Math.max(deMessage.getStamping(),this.horloge) + 1;
			this.otherDe.set(Integer.valueOf(deMessage.getSender()), (Integer)deMessage.getPayload());
			this.nbResultReceived++;
		}
		//System.out.println(this.thread.getName() + " stamping : " + this.horloge);
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
				token.setReceiver(Integer.toString(to));
				//juste pour pas que ça aille trop vite (j'espere ça evitera des lag)
				try {
					Thread.sleep(200);
				}catch (Exception e){
					e.printStackTrace();
				}
				if(!this.dead){
					bus.postEvent(token);
				}
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
