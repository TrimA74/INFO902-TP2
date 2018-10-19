public class DeMessage {
    private int stamping;
    private int sender;
    public DeMessage(int stamping, Object payload, int sender) {
        this.stamping = stamping;
        this.payload = payload;
        this.sender = sender;
    }
    public int getStamping() {
        return stamping;
    }

    public Object getPayload() {
        return payload;
    }

    private Object payload;

    public int getSender() {
        return sender;
    }
}
