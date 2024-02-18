import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.Naming;


public class ChatClient extends JFrame {
    private ChatService chatService;
    private String username;
    private JTextArea chatArea;
    private JTextField messageField;

    public ChatClient() {
        super("Chat Room");
        try {
            chatService = (ChatService) Naming.lookup("//localhost/ChatServer");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        username = JOptionPane.showInputDialog("Enter your username:");
        if (username == null || username.trim().isEmpty()) {
            System.exit(0);
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JLabel usernameLabel = new JLabel("Logged in as: " + username);
        contentPane.add(usernameLabel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        messageField = new JTextField();
        inputPanel.add(messageField, BorderLayout.CENTER);

        JButton emojiButton = new JButton("\uD83D\uDE00"); 
        emojiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPopupMenu popupMenu = new JPopupMenu();
                String[] emojis = { "\uD83D\uDE00", "\uD83D\uDE01", "\uD83D\uDE02", "\uD83D\uDE03", "\uD83D\uDE04", "\uD83D\uDE05" ,"😒",""}; 
                for (String emoji : emojis) {
                    JMenuItem menuItem = new JMenuItem(emoji);
                    menuItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            messageField.setText(messageField.getText() + emoji);
                        }
                    });
                    popupMenu.add(menuItem);
                }
                popupMenu.show(emojiButton, 0, emojiButton.getHeight());
            }
        });
        inputPanel.add(emojiButton, BorderLayout.EAST);

        JButton sendButton = new JButton("Send");
        inputPanel.add(sendButton, BorderLayout.SOUTH);

        contentPane.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        messageField.addKeyListener(new KeyListener() {

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            sendMessage();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
});

        

        setVisible(true);

        new Thread(() -> {
            while (true) {
                try {
                    String[] messages = chatService.getMessages();
                    chatArea.setText(String.join("\n", messages));
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());
                    Thread.sleep(100);
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }).start();
    }

    public void sendMessage(){
        String message = messageField.getText();
        if (message.trim().isEmpty()){
            JOptionPane.showMessageDialog(ChatClient.this, "Message cannot be empty.");            
        }else{
            try {
                chatService.sendMessage(username, message);
                messageField.setText("");
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
            }
        }
                
    }

    public static void main(String[] args) {
        new ChatClient();
    }
}
