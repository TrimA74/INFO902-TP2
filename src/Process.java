import com.google.common.eventbus.Subscribe;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

/**
 *
 */
public class Process  implements Runnable, Lamport {
	/**
	 * Thread courant
	 */
    private Thread thread;
	/***
	 * Pour savoir si le thread tourne toujours, il s'arrête qu'on a appel stop()
	 */
	private boolean alive;
	/**
	 * Pour savoir si le thread est mort
	 */
    private boolean dead;
	/**
	 * Notre horloge
	 */
	private int horloge;
	/**
	 * Le nombre de thread
	 */
	private int nb_thread;
	/**
	 * Notre middleware
	 */
    private Com myCom;
	/**
	 * Notre dé
	 */
	private int de;
	/**
	 * Stocke la valeur de chaque dé
	 */
    private ArrayList<Integer> otherDe;
	/**
	 * Semaphore pour éviter que l'horloge soit modifier par plusieurs thread en même temps
	 */
	private Semaphore semaphore;


	/**
	 *
	 * @param name Nom du process
	 * @param nbThread Nombre de thread
	 */
	public Process(String name, int nbThread){

    	this.thread = new Thread(this);
    	this.thread.setName(name);
    	this.alive = true;
    	this.dead = false;
    	this.horloge = 0;
    	this.nb_thread = nbThread;
    	this.thread.start();
    	otherDe = new ArrayList<>();
    	for(int i=0; i < this.nb_thread;i++){
    		otherDe.add(i,0);
		}
    	this.semaphore = new Semaphore(1);
    	this.myCom = new Com(this);


    }

	/**
	 * fonction main de notre thread
	 */
    public void run(){
    	try {
			Thread.sleep(3000);
		}catch(Exception e){
			e.printStackTrace();
		}
    	int loop = 0;

		System.out.println("name" + Thread.currentThread().getName());

    	while(this.alive){

    		System.out.println(Thread.currentThread().getName() + " Loop : " + loop);
    		try{

				this.de = 1 + (int)(Math.random() * 6);
				int nbResultReceived = 0;
				myCom.broadcast(this.de);

				while(nbResultReceived<this.nb_thread-1)
				{
					Map<Integer, BroadcastMessage> map = this.myCom.getMessagesByType(BroadcastMessage.class);
					for (Map.Entry<Integer, BroadcastMessage> message : map.entrySet()) {
						this.otherDe.set(message.getValue().getSender(), (Integer) message.getValue().getPayload());
						this.myCom.getBal().remove(message.getKey());
						nbResultReceived++;
					}
					Thread.sleep(500);
				}
				boolean max = true;
				for(int i=0; i<this.otherDe.size(); i++)
				{
					if(this.otherDe.get(i) > this.de)
						max = false;
					else if(this.otherDe.get(i) == this.de && i < this.myCom.getIdProcess())
						max = false;
				};

				if(max){
					this.myCom.requestSC();
					System.out.println("le process " + this.myCom.getIdProcess() + " a la valeur " + this.de);
					PrintWriter writer = new PrintWriter("results.txt", "UTF-8");
					writer.println(Thread.currentThread().getName() + " ecrit dans le fichier : " + this.de);
					writer.close();
					this.myCom.releaseSC();
				}

                for(int i=0; i < this.nb_thread;i++){
                    otherDe.set(i,0);
                }

				this.myCom.synchronize();



    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		loop++;
    	}

    	this.myCom.stop();
    	System.out.println(Thread.currentThread().getName() + " stoped");
	this.dead = true;
    }

	/**
	 * Pour stopper le thread
	 */
    public void stop(){
    	this.alive = false;
    }

	/**
	 *
	 * @return Retourne l'horloge
	 */
	@Override
	public int getClock() {
		return this.horloge;
	}

	/**
	 * Set l'horloge courante à la valeur max en la courante et celle passer en paramètre
	 * @param horloge La valeur de l'horloge
	 */
	@Override
	public void setClock(int horloge) {
		lockClock();
    	this.horloge = Math.max(horloge, this.horloge);
    	unlockClock();
	}

	/**
	 * Bloque l'accès sur l'horloge avec le sémaphore
	 */
	@Override
	public void lockClock() {
    	try {
			semaphore.acquire();
		}catch (Exception e){
			System.out.println(e.toString());
		}
	}

	/**
	 * Libére l'accès sur l'horloge avec le sémaphore
	 */
	@Override
	public void unlockClock() {
		semaphore.release();
	}
}
