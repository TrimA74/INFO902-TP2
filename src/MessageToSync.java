public class MessageToSync extends MessageTo {

    private int sender;

    public MessageToSync(int stamping, Object payload, int receiver, int sender) {
        super(stamping, payload, receiver);
        this.sender = sender;
    }

    public int getSender() {
        return sender;
    }
}
