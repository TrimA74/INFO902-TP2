import com.google.common.eventbus.Subscribe;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;


public class Process  implements Runnable, Lamport {
    private Thread thread;
    private boolean alive;
    private boolean dead;
    private int horloge;
    private int id;
    private Token token;
    private boolean wantToken;
    private int nb_thread;
    private int synchronizeCheck = 0;
    private boolean isReadyToSynchronize = false;
    private Com myCom;
    private int de;
    private ArrayList<Integer> otherDe;
    private Semaphore semaphore;

    public Process(String name, int nbThread){

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
    	this.semaphore = new Semaphore(1);
    	this.myCom = new Com(this);


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
			/*	this.de = 1 + (int)(Math.random() * 6);
				broadcastDe(this.de);

				while(nbResultReceived<this.nb_thread-1)
				{
					Thread.sleep(500);
				}
				boolean max = true;
				for(int i=0; i<this.otherDe.size(); i++)
				{
					if(this.otherDe.get(i) >= this.de)
						max = false;
				};

				if(max){
					request();
					System.out.println("valuede" + this.de);
					PrintWriter writer = new PrintWriter("results.txt", "UTF-8");
					writer.println(Thread.currentThread().getName() + " ecrit dans le fichier : " + this.de);
					writer.close();
					release();
				}

				synchronize();
            */



    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		loop++;
    	}

    	// liberation du bus
    	//this.bus.unRegisterSubscriber(this);
    	//this.bus = null;
    	System.out.println(Thread.currentThread().getName() + " stoped");
	this.dead = true;
    }







/*
	public void broadcastDe(Object payload){
		this.horloge++;
		DeMessage deMessage = new DeMessage(this.horloge, payload, this.thread.getName());
		System.out.println(Thread.currentThread().getName() + " send deValue : " + deMessage.getPayload());
		bus.postEvent(deMessage);
	}

	@Subscribe
	public void onBroadcastDe(DeMessage deMessage){
		if(!deMessage.getSender().equals(this.thread.getName())){
			this.horloge = Math.max(deMessage.getStamping(),this.horloge) + 1;
			this.otherDe.set(Integer.valueOf(deMessage.getSender()), (Integer)deMessage.getPayload());
			this.nbResultReceived++;
		}
		System.out.println(this.thread.getName() + " stamping : " + this.horloge);
	}

*/




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

	@Override
	public int getClock() {
		return this.horloge;
	}

	@Override
	public void setClock(int horloge) {
		lockClock();
    	this.horloge = Math.max(horlogethis.horloge);
    	unlockClock();
	}

	@Override
	public void lockClock() {
    	try {
			semaphore.acquire();
		}catch (Exception e){
			System.out.println(e.toString());
		}
	}

	@Override
	public void unlockClock() {
		semaphore.release();
	}
}
