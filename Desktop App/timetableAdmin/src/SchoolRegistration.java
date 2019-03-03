import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Security;
import java.util.LinkedHashMap;
import java.util.Map;

public class SchoolRegistration {

    private static final String URL = "https://schooltimetable.site/add_school_admin.php";
    private JFrame jFrame;
    private JTextField schoolNameText;
    private JTextField schoolUsernameText;
    private JTextField schoolPasswordText;
    private JTextField schoolDatabaseText;

    private SchoolRegistration() {
        loadJFrame();
    }

    private void loadJFrame() {
        this.jFrame = new JFrame("Училищна Програма");
        this.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addPanel();
        this.jFrame.pack();
        this.jFrame.setVisible(true);
    }

    private void addPanel() {
        JPanel jPanel = new JPanel();
        BoxLayout layout = new BoxLayout(jPanel, BoxLayout.Y_AXIS);
        jPanel.setLayout(layout);
        // Set border for the panel
        jPanel.setBorder(new EmptyBorder(new Insets(15, 15, 15, 15)));
        addComponentsToPanel(jPanel);
        this.jFrame.add(jPanel);
    }

    private void addComponentsToPanel(JPanel jPanel) {
        JLabel schoolNameLabel = new JLabel("Име на училище");
        this.schoolNameText = new JTextField(40);
        JLabel schoolUsernameLabel = new JLabel("Потребителско име");
        this.schoolUsernameText = new JTextField(40);
        JLabel schoolPasswordLabel = new JLabel("Парола");
        this.schoolPasswordText = new JTextField(40);
        JLabel schoolDatabaseLabel = new JLabel("Име на базата");
        this.schoolDatabaseText = new JTextField(40);
        JButton addButton = new JButton("Добави училище");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkInput()) {
                    addSchoolToDatabase();
                }
            }
        });

        // Add buttons to the frame (and spaces between buttons)
        jPanel.add(schoolNameLabel);
        jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        jPanel.add(schoolNameText);
        jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        jPanel.add(schoolUsernameLabel);
        jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        jPanel.add(schoolUsernameText);
        jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        jPanel.add(schoolPasswordLabel);
        jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        jPanel.add(schoolPasswordText);
        jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        jPanel.add(schoolDatabaseLabel);
        jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        jPanel.add(schoolDatabaseText);
        jPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        jPanel.add(addButton);
    }

    private boolean checkInput() {
        if (this.schoolNameText.getText().isEmpty() || this.schoolUsernameText.getText().isEmpty()
                || this.schoolPasswordText.getText().isEmpty() || this.schoolDatabaseText.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this.jFrame,
                    "Моля, попълнете всички полета!",
                    "Empty Fields",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void addSchoolToDatabase() {
        String name = schoolNameText.getText().trim();
        String username = schoolUsernameText.getText().trim();
        String password = schoolPasswordText.getText().trim();
        String databaseName = schoolDatabaseText.getText().trim();
        String logoURLFormat = "https://schooltimetable.site/school_logos/%s.png";
        String logoURL = String.format(logoURLFormat, username);
        manageConnection(name, username, password, databaseName, logoURL);
    }

    private void manageConnection(String name, String username, String password, String databaseName, String logoURL) {
        String result = "";
        try {
            result = setUpConnection(name, username, password, databaseName, logoURL);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this.jFrame, "Възникна проблем при свързването. Моля, опитайте по - късно"
                    , "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        //check if the connection was successful
        if (result.contains("успешно")) {
            JOptionPane.showMessageDialog(this.jFrame, result,
                    "School Added", JOptionPane.INFORMATION_MESSAGE);
        }
        else{
            JOptionPane.showMessageDialog(this.jFrame,result,"Connection Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private String setUpConnection(String name, String username, String password, String databaseName, String logoURL) throws IOException {

        URL u = new URL(SchoolRegistration.URL);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", name);
        params.put("username", username);
        params.put("password", password);
        params.put("db_name", databaseName);
        params.put("logo_url", logoURL);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) {
                postData.append('&');
            }
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0; ) {
            sb.append((char) c);
        }
        conn.getInputStream().close();
        return sb.toString();
    }

    public static void main(String[] args) {
        // set view based on operating system
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //run the form
        new SchoolRegistration();
    }
}

