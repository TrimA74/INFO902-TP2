public class MessageTo extends Message {
    public MessageTo(int stamping, Object payload, String receiver) {
        super(stamping, payload);
        this.receiver = receiver;
    }
    private String receiver;

    public String getReceiver() {
        return receiver;
    }
}
