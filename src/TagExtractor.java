import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TagExtractor extends JFrame
{
    private JTextArea resultTextArea;
    private File selectedFile;
    private Set<String> stopWords;


    public TagExtractor() {

        setTitle("Tag Extractor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initComponents();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        // File selection button
        JButton fileChooserButton = new JButton("Select File");
        fileChooserButton.addActionListener(e -> chooseFile());

        // Stop words selection button
        JButton stopWordsButton = new JButton("Select Stop Words File");
        stopWordsButton.addActionListener(e -> chooseStopWordsFile());

        // Extract tags button
        JButton extractTagsButton = new JButton("Extract Tags");
        extractTagsButton.addActionListener(e -> extractTags());

        // Save tags button
        JButton saveTagsButton = new JButton("Save Tags");
        saveTagsButton.addActionListener(e -> saveTagsToFile());

        // Text area to display results
        resultTextArea = new JTextArea(20, 40);
        resultTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(fileChooserButton);
        buttonPanel.add(stopWordsButton);
        buttonPanel.add(extractTagsButton);
        buttonPanel.add(saveTagsButton);

        // Add components to the frame
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);

        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            resultTextArea.append("Selected File: " + selectedFile.getName() + "\n");
        }
    }

    private void chooseStopWordsFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);

        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            loadStopWords(fileChooser.getSelectedFile());
            resultTextArea.append("Stop Words File Loaded\n");
        }
    }

    private void loadStopWords(File stopWordsFile) {
        stopWords = new TreeSet<>();

        try (Scanner scanner = new Scanner(new FileReader(stopWordsFile))) {
            while (scanner.hasNextLine()) {
                stopWords.add(scanner.nextLine().toLowerCase());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void extractTags() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a file first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<String, Integer> tagFrequency = new HashMap<>();

        try (Scanner scanner = new Scanner(new FileReader(selectedFile))) {
            while (scanner.hasNext()) {
                String word = scanner.next().replaceAll("[^a-zA-Z]", "").toLowerCase();

                if (!stopWords.contains(word)) {
                    tagFrequency.put(word, tagFrequency.getOrDefault(word, 0) + 1);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        displayTags(tagFrequency);
    }

    private void displayTags(Map<String, Integer> tagFrequency) {
        resultTextArea.setText("Tags and Frequencies:\n");

        for (Map.Entry<String, Integer> entry : tagFrequency.entrySet()) {
            resultTextArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }

    private void saveTagsToFile() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a file first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();

            try (PrintWriter writer = new PrintWriter(outputFile)) {
                for (String line : resultTextArea.getText().split("\n")) {
                    writer.println(line);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            resultTextArea.append("Tags saved to file: " + outputFile.getName() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TagExtractor::new);
    }
}

