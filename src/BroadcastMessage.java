public class BroadcastMessage extends Message {

    private int sender;
    public BroadcastMessage(int stamping, Object payload, int sender) {
        super(stamping, payload);
        this.sender = sender;
    }

    public int getSender() {
        return sender;
    }
}
