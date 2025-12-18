package com.sample;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                KieServices ks = KieServices.Factory.get();
                KieContainer kContainer = ks.getKieClasspathContainer();
                KieSession kSession = kContainer.newKieSession("ksession-rules");
                
                TeaAdvisorGUI gui = new TeaAdvisorGUI(kSession);
                gui.setVisible(true);
                
                kSession.fireAllRules();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Błąd inicjalizacji systemu: " + e.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}