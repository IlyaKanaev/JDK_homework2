package server.repository;

// тут все просто до загадочности
public interface Repository<T> {
    void save(T text);
    T load();
}
