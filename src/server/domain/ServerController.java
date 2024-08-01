package server.domain;

import client.domain.ClientController;
import server.repository.Repository;
import server.ui.ServerView;

import java.util.ArrayList;
import java.util.List;

public class ServerController {
    private boolean work; // флажок. у клиентов - аналог connected
    private ServerView serverView; // репозиторий "мягко" соединяющий логику с графикой
    private List<ClientController> clientControllerList; // всех подключаемых к чату загоняем в List
    private Repository<String> repository; // база у нас - просто текст

    public ServerController(ServerView serverView, Repository<String> repository) {
        this.serverView = serverView;
        this.repository = repository;
        clientControllerList = new ArrayList<>();
        serverView.setServerController(this);
    }

    public void start(){
        if (work){
            showOnWindow("Сервер уже был запущен");
        } else {
            work = true;
            showOnWindow("Сервер запущен!");
        }
    }

    public void stop(){
        if (!work){
            showOnWindow("Сервер уже был остановлен");
        } else {
            work = false;
            while (!clientControllerList.isEmpty()){ // поштучное отключение клиентов от чата начиная с конца List
                disconnectUser(clientControllerList.get(clientControllerList.size() - 1));
            }
            showOnWindow("Сервер остановлен!");
        }
    }

    public void disconnectUser(ClientController clientController){
        clientControllerList.remove(clientController); // удаляем конкретного клиента из чата
        if (clientController != null){
            clientController.disconnectFromServer(); // и отключаем клиента
            showOnWindow(clientController.getName() + " отключился от беседы"); // соообщаем остальным о дизертире
        }
    }

    public boolean connectUser(ClientController clientController){
        if (!work){ // проверяем наличие присутствия
            return false;
        }
        clientControllerList.add(clientController); // подключаем
        showOnWindow(clientController.getName() + " подключился к беседе"); // сообщаем
        return true; // ставим галочку
    }

    public void message(String text){
        if (!work){
            return;
        }
        showOnWindow(text); // пишем текст в текстовом поле окошка сервера
        answerAll(text); // рассылаем сообщение всем подключенным к чату
        saveInHistory(text); // сохраняем текст в отельном текстовом файле (т.н. База Данных)
    }

    public String getHistory() {
        return repository.load();
    } // это для всех подключившихся выкатывается все написанное

    private void answerAll(String text){
        for (ClientController clientController : clientControllerList){ // рассылка спама
            clientController.answerFromServer(text);
        }
    }

    private void showOnWindow(String text){
        serverView.showMessage(text + "\n");
    } // пишем автора и его сообщение

    private void saveInHistory(String text){
        repository.save(text);
    } // метод для работы с БД
}

