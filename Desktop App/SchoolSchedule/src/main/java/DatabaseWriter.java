package main.java;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabaseWriter {

    private static final String URL = "https://timetabletest.000webhostapp.com/login.php";
    private JFrame jFrame;
    private String path;
    private JFileChooser fileChooser;
    private JTextField usernameText;
    private JPasswordField passwordText;
    private JComboBox schoolList;

    private DatabaseWriter() throws IOException {
        setUpJFrame();
        setUpFileChooser();
        //TODO HANDLE EXCEPTIONS
    }

    private void setUpJFrame() {
        this.jFrame = new JFrame("Училищна Програма");
        this.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.jFrame.setSize(500, 500);
        addPanel();
        this.jFrame.pack();
        this.jFrame.setVisible(true);
    }

    private void addPanel() {
        JPanel jPanel = new JPanel();
        GroupLayout gl = new GroupLayout(jPanel);
        jPanel.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);
        addGroupLayoutComponents(gl);
        this.jFrame.add(jPanel);
    }

    private void addGroupLayoutComponents(GroupLayout gl) {
        JLabel usernameLabel = new JLabel("Потребителско име");
        this.usernameText = new JTextField(40);
        JLabel passwordLabel = new JLabel("Парола");
        this.passwordText = new JPasswordField(40);
        JLabel chooseSchoolLabel = new JLabel("Избери училище");
        this.schoolList = createComboBox();
        JButton button = new JButton("Вмъкни таблица");
        button.addActionListener(e -> {
            setUpOnButtonClick();
        });

        buildLayout(gl, usernameLabel, passwordLabel, chooseSchoolLabel, button);
    }

    private void setUpOnButtonClick() {
        if (checkInput()) {
            String username = usernameText.getText();
            String password = String.valueOf(passwordText.getPassword());
            String databaseName = getConnectionResult(username, password);
            //if the databaseName is not empty then the connection was successful
            if (!databaseName.isEmpty()) {
                //replace the 'success' message so only the database name will remain
                databaseName = databaseName.replace("success", "");
                loadFileChooserAndSetPath();
                if (this.path.contains("PATH DOES NOT EXIST")) {
                    JOptionPane.showMessageDialog(this.jFrame, "Грешна директория."
                            , "Wrong Path", JOptionPane.ERROR_MESSAGE);
                } else {
                    setDatabaseManager(username,password,databaseName);
                }
            }
        }
    }

    private boolean checkInput() {
        //check if the user entered all fields
        if (this.usernameText.getText().isEmpty() || this.passwordText.getPassword().length == 0
                || this.schoolList.getSelectedIndex() == -1) {
            return false;
        }
        return true;
    }

    private String getConnectionResult(String username, String password) {
        String result = "";
        try {
            result = setUpConnection(username, password);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this.jFrame, "Възникна проблем при свързването. Моля, опитайте по - късно"
                    , "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        //check if the connection was successful
        //
        if (result.contains("success")) {
            JOptionPane.showMessageDialog(this.jFrame, "Свързването е успешно! Моля, изберете таблицата за обновяване.",
                    "School Added", JOptionPane.INFORMATION_MESSAGE);
            return result;
        } else {
            JOptionPane.showMessageDialog(this.jFrame, result,
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        return "";
    }

    private String setUpConnection(String username, String password) throws IOException {
        URL u = new URL(URL);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("username", username);
        params.put("password", password);
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
        return sb.toString();
    }

    //TODO CHECK DOT SYNTAX
    private void buildLayout(GroupLayout gl, JLabel usernameLabel, JLabel passwordLabel, JLabel chooseSchoolLabel, JButton button) {
        GroupLayout.SequentialGroup hGroup = gl.createSequentialGroup();

        hGroup.addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER).
                addComponent(usernameLabel).addComponent(this.usernameText).addComponent(passwordLabel).addComponent(this.passwordText));
        hGroup.addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER).
                addComponent(chooseSchoolLabel).addComponent(this.schoolList).addComponent(button));
        gl.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = gl.createSequentialGroup();

        vGroup.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(usernameLabel).addComponent(chooseSchoolLabel));
        vGroup.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(this.usernameText).addComponent(this.schoolList));
        vGroup.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(passwordLabel));
        vGroup.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(this.passwordText).addComponent(button));
        gl.setVerticalGroup(vGroup);
    }

    private JComboBox createComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItem("Математическа гимназия \"Гео Милев\" - Плевен");
        comboBox.addItem("Гимназия с преподаване на чужди езици - Плевен");
        //default value is null so the user must click something to continue
        comboBox.setSelectedIndex(-1);
        return comboBox;
    }

    private void setUpFileChooser() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel files(*.xls;*,*.xlsx;*)", "xls", "xlsx");
        this.fileChooser = new JFileChooser();
        this.fileChooser.setFileFilter(filter);
        this.fileChooser.setAcceptAllFileFilterUsed(false);
    }

    private void loadFileChooserAndSetPath() {
        int option = this.fileChooser.showOpenDialog(this.jFrame);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = this.fileChooser.getSelectedFile();
            if (selectedFile.exists()) {
                this.path = selectedFile.getAbsolutePath();
            } else {
                this.path = "PATH DOES NOT EXIST";
            }
        }
    }

    private void setDatabaseManager(String username,String password, String databaseName) {
        //TODO HANDLE EXCEPTIONS
        Workbook workbook;
        try {
            workbook = WorkbookFactory.create(new File(this.path));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this.jFrame, "Грешка при четенето на данните.Моля, проверете за грешки при " +
                            "въвеждането в таблицата или затворете файла с таблицата,ако сте го отворили.",
                    "Reading Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int indices[] = {0, 1, 2, 3, 4, 5, 6, 7};
        GradesAndRoomsManager manager = new GradesAndRoomsManager(workbook, indices);
        TeachersManager tManager = new TeachersManager(workbook, indices);
        try {
            DatabaseManager databaseManager = new DatabaseManager(manager, tManager, username, password, databaseName);
            databaseManager.updateDatabase();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this.jFrame, "Проблем при свързването със сървъра.Моля, опитайте по - късно.",
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) throws IOException {
        // set view based on operating system
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //run the form
        new DatabaseWriter();
    }
}
