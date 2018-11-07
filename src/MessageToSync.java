public class MessageToSync extends Message {

    private int sender;

    private int receiver;



    public MessageToSync(int stamping, Object payload, int receiver, int sender) {
        super(stamping, payload);
        this.receiver = receiver;
        this.sender = sender;
    }

    public int getReceiver() {
        return receiver;
    }

    public int getSender() {
        return sender;
    }
}
