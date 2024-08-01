package client.domain;

import client.ui.ClientView;
import server.domain.ServerController;

// Класс содержащий логику работы клиента
public class ClientController {
    private boolean connected; // вкл-выкл
    private String name; // то, что юзер сам вписывает при авторизации
    private ClientView clientView; // абстракция графического интерфейса
    private ServerController serverController; // объект для связи с сервером

    // в конструкторе - две зависимости, от серверной логики и от интерфейса графики
    public ClientController(ClientView clientView, ServerController serverController) {
        this.clientView = clientView;
        this.serverController = serverController;
        clientView.setClientController(this);
    }

    public boolean connectToServer(String name) {
        this.name = name; // имя пользователя, которым будем подписывать исходящие сообщения
        if (serverController.connectUser(this)){
            showOnWindow("Вы успешно подключились!\n");
            connected = true;
            String log = serverController.getHistory(); // юзер сразу получает весь текст чата
            if (log != null){
                showOnWindow(log);
            }
            return true;
        } else {
            showOnWindow("Подключение не удалось");
            return false;
        }
    }

    // Метод, с помощью которого сервер передает клиенту сообщения
    public void answerFromServer(String text) {
        showOnWindow(text);
    }

    // Метод отключения от сервера инициализированное сервером (неиспользуемый)
    // Кроме собственно отключения, меняет состояние поля connected и делает сообщение в текстовом поле окна
    public void disconnectedFromServer() {
        if (connected) {
            connected = false;
            clientView.disconnectedFromServer();
            showOnWindow("Вы были отключены от сервера!");
        }
    }

//   Метод отключения от сервера инициализированное клиентом (например закрыто GUI)
    public void disconnectFromServer() {
        serverController.disconnectUser(this);
    }

    // Метод для передачи сообщения на сервер
    public void message(String text) {
        if (connected) {
            if (!text.isEmpty()) {
                serverController.message(name + ": " + text);
            }
        } else {
            showOnWindow("Нет подключения к серверу");
        }
    }

    // Геттер (не используется)
    public String getName() {
        return name;
    }

    // Метод вывода сообщения на GUI
    private void showOnWindow(String text) {
        clientView.showMessage(text + "\n");
    }
}

