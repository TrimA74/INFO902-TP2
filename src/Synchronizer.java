public class Synchronizer {
    private String initializer;
    private String responder;

    public Synchronizer(String initializer, String responder) {
        this.initializer = initializer;
        this.responder = responder;
    }

    public String getInitializer() {
        return initializer;
    }

    public void setInitializer(String initializer) {
        this.initializer = initializer;
    }

    public String getResponder() {
        return responder;
    }

    public void setResponder(String responder) {
        this.responder = responder;
    }
}
