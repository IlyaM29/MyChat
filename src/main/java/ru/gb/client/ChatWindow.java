package ru.gb.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ChatWindow extends JFrame {
    TextArea textArea = new TextArea();
    TextField textField = new TextField();
    Button button = new Button("Отправить");
    final DefaultListModel<String> dfm = new DefaultListModel<>();
    final JList<String> list = new JList<>(dfm);
    JScrollPane scrollPane = new JScrollPane(list);

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public ChatWindow() {
        ChatClient client = new ChatClient(this);
        visual(client);
    }

    private void visual(ChatClient client) {
        setTitle("Чат");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        int sizeWidth = 400;
        int sizeHeight = 500;
        int locationX = (screenSize.width - sizeWidth) / 2;
        int locationY = (screenSize.height - sizeHeight) / 2;
        setBounds(locationX, locationY, sizeWidth, sizeHeight);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        add(textArea, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.EAST);
        Dimension scrollPanelSize = new Dimension(100, 100);
        scrollPane.setPreferredSize(scrollPanelSize);
        add(panel, BorderLayout.SOUTH);
        panel.add(textField, BorderLayout.CENTER);
        panel.add(button, BorderLayout.EAST);

        textArea.setEditable(false);
        textField.addActionListener(e -> sendMsg(client));
        button.addActionListener(e -> sendMsg(client));
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String nick = list.getSelectedValue();
                    textField.setText("/w " + nick + " " + textField.getText());
                    textField.requestFocus();
                    textField.setCaretPosition(textField.getText().length());
                }
            }
        });

        setVisible(true);
        textField.requestFocus();
    }

    private void sendMsg(ChatClient client) {
        String msg = textField.getText();
        client.sendMessage(msg);
        textField.setText("");
        textField.requestFocus();
    }

    public void addMessage(String msg) {
        textArea.append(msg + "\n");
    }

    public void updateClientList(List<String> clients) {
        dfm.removeAllElements();
        for (String client : clients) {
            dfm.addElement(client);
        }
    }
}
