public class MessageTo extends Message {
    public MessageTo(int stamping, Object payload, int receiver) {
        super(stamping, payload);
        this.receiver = receiver;
    }
    private int receiver;

    public int getReceiver() {
        return receiver;
    }
}
