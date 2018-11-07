public class BroadcastMessageSync extends Message {

    private int sender;
    private int receiver;

    public BroadcastMessageSync(int stamping, Object payload, int sender, int receiver) {
        super(stamping, payload);
        this.sender = sender;
        this.receiver = receiver;
    }

    public int getReceiver(){
        return receiver;
    }
    public int getSender() {
        return sender;
    }
}
