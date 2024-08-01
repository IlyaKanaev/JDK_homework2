package server.ui;

import server.domain.ServerController;

// что бизнес-части нужно от GUI? Выслушать и соединиться
public interface ServerView {
    void showMessage(String message);
    void setServerController(ServerController serverController);
}
