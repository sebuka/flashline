package ru.sebuka.flashline.gameobjects;

public interface Connectable {
    boolean canConnect(Connectable to);

    void connect(Connectable to) throws Exception;
    void clearConnections() ;
    int getPathsCount() ;

}
