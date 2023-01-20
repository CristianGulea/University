public interface IObservable {
    void addObserver(IObserver observer);
    void notifyall();
    void removeObserver(IObserver observer);
}
