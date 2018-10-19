public abstract class Message {
    private int stamping;

    public Message(int stamping, Object payload) {
        this.stamping = stamping;
        this.payload = payload;
    }

    public int getStamping() {
        return stamping;
    }

    public Object getPayload() {
        return payload;
    }

    private Object payload;
}
