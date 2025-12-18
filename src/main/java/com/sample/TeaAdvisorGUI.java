package com.sample;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class TeaAdvisorGUI extends JFrame {
    private KieSession kSession;
    private JPanel contentPanel;
    private JTextArea historyArea;
    private Map<String, String> translations;
    private FactHandle questionHandle;
    
    public TeaAdvisorGUI(KieSession kSession) {
        this.kSession = kSession;
        this.translations = loadTranslations();
        
        kSession.setGlobal("gui", this);
        
        setTitle("What's your blend?");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private Map<String, String> loadTranslations() {
        Map<String, String> trans = new HashMap<>();
        
        trans.put("Q_TEMP", "Do you want something hot or cold?");
        trans.put("HOT", "HOT");
        trans.put("COLD", "COLD");
        trans.put("Q_TEA_HOT", "How about some tea?");
        trans.put("YES_TEA_HOT", "YES");
        trans.put("NO_TEA_HOT", "NO");
        trans.put("Q_TEA_COLD", "How about an iced tea?");
        trans.put("YES_TEA_COLD", "YES");
        trans.put("NO_TEA_COLD", "NO");
        trans.put("Q_TEA_MILK", "How about a milk tea?");
        trans.put("YES_TEA_MILK", "YES");
        trans.put("NO_TEA_MILK", "NO");
        trans.put("Q_MILK", "Milk based?");
        trans.put("YES_MILK", "YES");
        trans.put("NO_MILK", "NO");
        trans.put("Q_FLAVOR", "Do you want a flavour shot?");
        trans.put("YES_FLAVOR", "YES");
        trans.put("NO_FLAVOR", "NO");
        trans.put("Q_TEA_BASE", "Do you prefer light or dark teas?");
        trans.put("LIGHT", "LIGHT");
        trans.put("DARK", "DARK");
        trans.put("CANT_DECIDE", "CAN'T DECIDE");
        trans.put("Q_BLENDED_ICED", "Blended or iced?");
        trans.put("BLENDED", "BLENDED");
        trans.put("ICED", "ICED");
        trans.put("Q_MILK_BLENDED", "Milk based?");
        trans.put("REC_BLACK_TEA", "You should get Black Tea");
        trans.put("REC_GREEN_TEA", "You should get Green Tea");
        trans.put("REC_OOLONG_TEA", "You should get Oolong Tea");
        trans.put("REC_GREEN_MILK_TEA", "You should get a Green Milk Tea");
        trans.put("REC_BLACK_MILK_TEA", "You should get a Black Milk Tea");
        trans.put("REC_OOLONG_MILK_TEA", "You should get an Oolong Milk Tea");
        trans.put("REC_SMOOTHIE", "You should get a smoothie");
        trans.put("DESC_SMOOTHIE", "Order example: Taro Smoothie");
        trans.put("REC_SLUSH", "You should get a Slush");
        trans.put("DESC_SLUSH", "Order example: Strawberry Slush");
        trans.put("REC_LEMON_GREEN_TEA", "Green tea works best with fruit and flower flavours.");
        trans.put("DESC_LEMON_GREEN_TEA", "Order example: Lemon Green Tea.");
        trans.put("DESC_ICED_LEMON_GREEN_TEA", "Order example: Iced Lemon Green Tea.");
        trans.put("REC_ICED_GREEN_TEA", "You should get an Iced Green Tea");
        trans.put("REC_PINEAPPLE_OOLONG", "Oolong tea is a versatile tea.");
        trans.put("DESC_PINEAPPLE_OOLONG", "Order example: Pineapple Oolong Tea");
        trans.put("DESC_ICED_PINEAPPLE_OOLONG", "Order example: Iced Pineapple Oolong Tea");
        trans.put("REC_ICED_OOLONG_TEA", "You should get an Iced Oolong Tea");
        trans.put("REC_CHAI_BLACK_TEA", "Black tea works best with herb and spice flavours.");
        trans.put("DESC_CHAI_BLACK_TEA", "Order example: Chai Black Tea");
        trans.put("DESC_ICED_CHAI_BLACK_TEA", "Order example: Iced Chai Black Tea");
        trans.put("REC_ICED_BLACK_TEA", "You should get an Iced Black Tea");
        trans.put("TRY_SOMETHING_COLD", "Maybe try something cold.");
        trans.put("TRY_SOMETHING_HOT", "Maybe try something hot.");
        
        return trans;
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("WHAT'S YOUR BLEND?", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton resetButton = new JButton("Try again");
        resetButton.addActionListener(e -> resetSession());
        topPanel.add(resetButton, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        historyArea = new JTextArea(8, 40);
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setBorder(BorderFactory.createTitledBorder("History"));
        bottomPanel.add(historyScroll, BorderLayout.CENTER);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    public void askQuestion(Object question) {
        if (questionHandle != null) {
            kSession.delete(questionHandle);
        }
        questionHandle = kSession.insert(question);
        kSession.fireAllRules();
    }
    
    public void displayQuestion(String questionKey, java.util.List<String> options, boolean multiSelect) {
        SwingUtilities.invokeLater(() -> {
            contentPanel.removeAll();
            
            String questionText = translations.getOrDefault(questionKey, questionKey);
            
            JLabel questionLabel = new JLabel("<html><div style='width:700px;'><b>" + 
                questionText + "</b></div></html>");
            questionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(questionLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            
            if (multiSelect) {
                java.util.List<JCheckBox> checkBoxes = new ArrayList<>();
                
                for (String option : options) {
                    String optionText = translations.getOrDefault(option, option);
                    JCheckBox cb = new JCheckBox(optionText);
                    cb.setFont(new Font("Arial", Font.PLAIN, 14));
                    cb.setAlignmentX(Component.LEFT_ALIGNMENT);
                    checkBoxes.add(cb);
                    contentPanel.add(cb);
                    contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
                
                contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                JButton submitButton = new JButton("Confirm");
                submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                submitButton.addActionListener(e -> {
                    java.util.List<String> selected = new ArrayList<>();
                    for (int i = 0; i < checkBoxes.size(); i++) {
                        if (checkBoxes.get(i).isSelected()) {
                            selected.add(options.get(i));
                        }
                    }
                    handleMultiAnswer(questionKey, selected);
                });
                contentPanel.add(submitButton);
            } else {
                ButtonGroup group = new ButtonGroup();
                
                for (String option : options) {
                    String optionText = translations.getOrDefault(option, option);
                    JRadioButton rb = new JRadioButton(optionText);
                    rb.setFont(new Font("Arial", Font.PLAIN, 14));
                    rb.setAlignmentX(Component.LEFT_ALIGNMENT);
                    group.add(rb);
                    contentPanel.add(rb);
                    contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                    
                    rb.addActionListener(e -> handleAnswer(questionKey, option));
                }
            }
            
            contentPanel.revalidate();
            contentPanel.repaint();
        });
    }
    
    private void handleAnswer(String questionKey, String answer) {
        String questionText = translations.getOrDefault(questionKey, questionKey);
        String answerText = translations.getOrDefault(answer, answer);
        addToHistory("Q: " + questionText + "\nA: " + answerText + "\n");
        
        kSession.insert(answer);
        kSession.fireAllRules();
    }
    
    private void handleMultiAnswer(String questionKey, java.util.List<String> answers) {
        String questionText = translations.getOrDefault(questionKey, questionKey);
        StringBuilder sb = new StringBuilder("Q: " + questionText + "\nA: ");
        
        for (int i = 0; i < answers.size(); i++) {
            String answerText = translations.getOrDefault(answers.get(i), answers.get(i));
            sb.append(answerText);
            if (i < answers.size() - 1) sb.append(", ");
        }
        sb.append("\n");
        addToHistory(sb.toString());
        
        for (String answer : answers) {
            kSession.insert(answer);
        }
        kSession.fireAllRules();
    }
    
    public void displayRecommendation(String recommendation, String description) {
        SwingUtilities.invokeLater(() -> {
            contentPanel.removeAll();
            
            String recText = translations.getOrDefault(recommendation, recommendation);
            String descText = translations.getOrDefault(description, description);
            
            JLabel resultLabel = new JLabel("<html><div style='width:700px;'><h2>Your recommendation:</h2></div></html>");
            resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(resultLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            
            JLabel recLabel = new JLabel("<html><div style='width:700px;'><b style='font-size:18px; color:#2E7D32;'>" + 
                recText + "</b></div></html>");
            recLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(recLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            
            JLabel descLabel = new JLabel("<html><div style='width:700px;'>" + descText + "</div></html>");
            descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(descLabel);
            
            contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
            JButton restartButton = new JButton("Try again");
            restartButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            restartButton.addActionListener(e -> resetSession());
            contentPanel.add(restartButton);
            
            contentPanel.revalidate();
            contentPanel.repaint();
            
            addToHistory("\n=== RECOMMENDATION ===\n" + recText + "\n" + descText + "\n");
        });
    }
    
    private void addToHistory(String text) {
        historyArea.append(text + "\n");
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }
    
    private void resetSession() {
        kSession.dispose();
        
        KieSession newSession = kSession.getKieBase().newKieSession();
        newSession.setGlobal("gui", this);
        this.kSession = newSession;
        this.questionHandle = null;
        
        contentPanel.removeAll();
        historyArea.setText("");
        
        contentPanel.revalidate();
        contentPanel.repaint();
        
        kSession.fireAllRules();
    }
}