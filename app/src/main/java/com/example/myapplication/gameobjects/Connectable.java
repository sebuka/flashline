package com.example.myapplication.gameobjects;

public interface Connectable {
    boolean canConnect(Connectable to);

    void connect(Connectable to) throws Exception;
    void clearConnections() ;
    int getPathsCount() ;

    int getColor(int posType);
    void setColor(int color, int posType);
}
