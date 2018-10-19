public interface Lamport {
    int getClock();
    void setClock(int horloge);
    void lockClock();
    void unlockClock();
}
