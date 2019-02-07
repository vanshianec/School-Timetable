import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DatabaseWriter {

    private JFrame jFrame;
    private String path;
    private JFileChooser fileChooser;
    private DatabaseManager databaseManager;

    public DatabaseWriter() throws IOException {
        setUpJFrame();
        setUpFileChooser();
        loadFileChooserAndGetPath();
        //TODO HANDLE EXCEPTIONS
        setDatabaseManager();
        loadJFrame();
    }

    private void setUpJFrame() {
        this.jFrame = new JFrame("TITLE");
        this.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.jFrame.setSize(600, 600);
    }

    private void setUpFileChooser() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel files(*.xls;*,*.xlsx;*)", "xls", "xlsx");
        this.fileChooser = new JFileChooser();
        this.fileChooser.setFileFilter(filter);
        this.fileChooser.setAcceptAllFileFilterUsed(false);
    }

    private void loadFileChooserAndGetPath() {
        int option = this.fileChooser.showOpenDialog(this.jFrame);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = this.fileChooser.getSelectedFile();
            if (selectedFile.exists()) {
                this.path = selectedFile.getAbsolutePath();
            } else {
                this.path = "DOES NOT EXIST";
            }
        }
    }

    private void setDatabaseManager() throws IOException {
        //TODO HANDLE EXCEPTIONS
        Workbook workbook = WorkbookFactory.create(new File(this.path));
        int indices[] = {0, 1, 2, 3, 4, 5, 6, 7};
        GradesAndRoomsManager manager = new GradesAndRoomsManager(workbook, indices);
        TeachersManager tManager = new TeachersManager(workbook, indices);
        this.databaseManager = new DatabaseManager(manager, tManager);
        //databaseManager.updateDatabase();
    }

    private void loadJFrame() {
        //TODO REMOVE FIRST LINE
        JPanel jPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(jPanel);
        //set the scrolling speed
        scrollPane.getVerticalScrollBar().setUnitIncrement(33);
        this.jFrame.add(scrollPane);

        Map<Integer, String> teacherIdAndName = this.databaseManager.getTeacherIdAndName();
        JTextField[] fields = new JTextField[teacherIdAndName.size()];
        JLabel[] labels = new JLabel[teacherIdAndName.size()];
        GroupLayout layout = new GroupLayout(jPanel);
        jPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        GroupLayout.ParallelGroup parallel = layout.createParallelGroup();
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(parallel));
        GroupLayout.SequentialGroup sequential = layout.createSequentialGroup();
        layout.setVerticalGroup(sequential);
        Font labelFont = new Font("Serif", Font.BOLD, 19);
        Font textFont = new Font("Helvetica", Font.ITALIC, 18);

        for (int i = 0; i < teacherIdAndName.size(); i++) {
            JLabel label = new JLabel(String.valueOf(i + 1), JLabel.RIGHT);
            label.setFont(labelFont);
            labels[i] = label;
            JTextField textField = new JTextField(teacherIdAndName.get(i + 1));
            textField.setFont(textFont);
            fields[i] = textField;
            labels[i].setLabelFor(fields[i]);
            parallel.addGroup(layout.createSequentialGroup().
                    addComponent(labels[i]).addComponent(fields[i]));
            sequential.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                    addComponent(labels[i]).addComponent(fields[i]));
            //make the components have the same size
            layout.linkSize(SwingConstants.HORIZONTAL, labels[i], labels[0]);
        }

        this.jFrame.setVisible(true);
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
