package client.ui;

import client.domain.ClientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Класс описывающий работу графического интерфейса приложения.
public class ClientGUI extends JFrame implements ClientView {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private JTextArea log;
    private JTextField tfIPAddress, tfPort, tfLogin, tfMessage; // tfIPAddress и tfPort не используются
    private JPasswordField password; // не используется
    private JButton btnLogin, btnSend;
    private JPanel headerPanel;

    /**
     * Контроллер, описывающий реакцию на различные события.
     * Когда что-то происходит, например нажата какая-то кнопка на экране, то обращаемся
     * к контроллеру и вызываем нужный метод
     */
    private ClientController clientController;

    public ClientGUI() {
        setting();
        createPanel();

        setVisible(true);
    }

    //Сеттер
    @Override
    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }

    // Настройка основных параметров GUI
    private void setting() {
        setSize(WIDTH, HEIGHT); // размеры окна
        setResizable(false); // неизменяемость окошка
        setTitle("Chat client"); // заголовок
        setDefaultCloseOperation(HIDE_ON_CLOSE); // основное окно, закрытие которого - остановит программу
    }

    //Метод вывода текста на экран GUI. Вызывается из контроллера
    @Override
    public void showMessage(String msg) {
        log.append(msg); // log - это поле в окошке для текста
    }

    // Метод, описывающий отключение клиента от сервера со стороны сервера
    // Вообще не понял. Вроде бы этот метод только делает видимой панель виджетов...
    @Override
    public void disconnectedFromServer(){
        hideHeaderPanel(true);
    }

    /**
     * Метод, описывающий отключение клиента от сервера со стороны клиента
     */
    public void disconnectFromServer(){
        clientController.disconnectFromServer();
    }

    /**
     * Метод изменения видимости верхней панели экрана, на которой виджеты для авторизации (например кнопка логин)
     * @param visible true, если надо сделать панель видимой
     */
    public void hideHeaderPanel(boolean visible){
        headerPanel.setVisible(visible);
    }

    /**
     * Метод, срабатывающий при нажатии кнопки авторизации
     * Отправляет серверу текст и скрывает видимость панели виджетов
     */
    public void login(){
        if (clientController.connectToServer(tfLogin.getText())){
            headerPanel.setVisible(false);
        }
    }

    // Метод для отправки сообщения. Используется при нажатии на кнопку send
    private void message(){
        clientController.message(tfMessage.getText());
        tfMessage.setText("");
    }

    // Метод добавления виджетов на экран
    private void createPanel() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createLog());
        add(createFooter(), BorderLayout.SOUTH);
    }

    // Метод создания панели авторизации
    private Component createHeaderPanel() {
        headerPanel = new JPanel(new GridLayout(2, 3)); // кнопок много, но используются только две
        tfIPAddress = new JTextField("127.0.0.1"); // текст "от балды"
        tfPort = new JTextField("8189"); // тоже текст-заглушка
        tfLogin = new JTextField("Ivan Ivanovich");
        password = new JPasswordField("123456"); // пароль так себе...
        btnLogin = new JButton("login");
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        headerPanel.add(tfIPAddress);
        headerPanel.add(tfPort);
        headerPanel.add(new JPanel());
        headerPanel.add(tfLogin);
        headerPanel.add(password);
        headerPanel.add(btnLogin);

        return headerPanel;
    }

    // Метод создания центральной панели, на которой отображается история сообщений
    private Component createLog() {
        log = new JTextArea();
        log.setEditable(false);
        return new JScrollPane(log);
    }

    // Метод создания панели отправки сообщений
    // Непростая магия, надо бы запомнить на будущее
    private Component createFooter() {
        JPanel panel = new JPanel(new BorderLayout());
        tfMessage = new JTextField();
        tfMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    message();
                }
            }
        });
        btnSend = new JButton("send");
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                message();
            }
        });
        panel.add(tfMessage);
        panel.add(btnSend, BorderLayout.EAST);
        return panel;
    }

    // Метод срабатывающий при важных событиях связанных с графическим окном (например окно в фокусе)
    // фокусы-покусы... когда же я так научусь... (вздыхает...)
    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING){
            disconnectFromServer();
        }
    }
}

