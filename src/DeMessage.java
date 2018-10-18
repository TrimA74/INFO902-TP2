public class DeMessage {
    private int stamping;
    private String sender;
    public DeMessage(int stamping, Object payload, String sender) {
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

    public String getSender() {
        return sender;
    }
}
