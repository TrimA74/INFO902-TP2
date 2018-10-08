import com.google.common.eventbus.Subscribe;


public class Process  implements Runnable {
    private Thread thread;
    private EventBusService bus;
    private boolean alive;
    private boolean dead;
    private int horloge;
    private static int nbProcess = 0;
    private int id = Process.nbProcess++;
    private boolean token;
    private boolean wantToken;
    private int nb_thread;

    public Process(String name, int nbThread){

    	this.bus = EventBusService.getInstance();
    	this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.


    	this.thread = new Thread(this);
    	this.thread.setName(name);
    	this.alive = true;
    	this.dead = false;
    	this.horloge = 0;
    	this.token = false;
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
    			
    			if(Thread.currentThread().getName().equals("1")){

    				//broadcast("mon payload");
    				sendTo("mon payload",2);
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
	public void onToken(Token message){
		if(this.wantToken){
			this.token = true;
		} else {
			sendTo("mon payload",Integer.valueOf((this.thread.getName())+1) % this.nb_thread);
		}
	}

	public void release (){

	}

	public void request(){
    	this.wantToken = true;
    	while(!this.token){
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
