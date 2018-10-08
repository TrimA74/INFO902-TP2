public class BroadcastMessage extends Message {

    private String sender;
    public BroadcastMessage(int stamping, Object payload, String sender) {
        super(stamping, payload);
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }
}
