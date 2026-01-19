package gui;

import controller.Controller;
import gui.util.RoundedPanel;
import gui.util.Utility;
import modello.Priorita;
import modello.Tipo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FIltraIssue extends JDialog {

    public interface FiltroListener {
        void applicaFiltri(Map<String, Object> filtri);
    }

    private JPanel mainPanel;
    private JPanel botPanel;
    private JButton indietroButton;
    private JTextField idField;
    private JComboBox<String> prioritaComboBox;
    private JTextField paroleChiaveFIeld;
    private JComboBox<String> tipoComboBox;
    private JComboBox<String> risoltoComboBox;
    private JComboBox<String> creatoreComboBox;
    private JComboBox<String> assegnatarioComboBox;
    private JComboBox<String> dataCreazioneComboBox;
    private JPanel filtraPanel;
    private JPanel innerPanel;
    private JButton filtraButton;
    private FiltroListener listener;

    public FIltraIssue(JFrame owner, FiltroListener listener) {
        this(owner, listener, false);
    }

    public FIltraIssue(JFrame owner, FiltroListener listener, boolean disableAssegnatario) {
        super(owner, "Filtra Issue", true); 
        this.listener = listener;
        
        setContentPane(mainPanel);
        setSize(600, 800);
        setLocationRelativeTo(owner);
        setResizable(false);

        filtraPanel.setBorder(new RoundedPanel("pannello"));
        innerPanel.setBorder(new RoundedPanel("finestra"));

        Image icon = Utility.getIconaApplicazione();
        if (icon != null) {
            setIconImage(icon);
        }

        botPanel.setBorder(new RoundedPanel("pannello"));

        popolaComboBox();

        if (disableAssegnatario) {
            assegnatarioComboBox.setEnabled(false);
            assegnatarioComboBox.setToolTipText("Filtro disabilitato in questa vista");
        }

        filtraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applicaFiltri();
            }
        });

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        setVisible(true); 
    }

    private void popolaComboBox() {
        prioritaComboBox.addItem("Tutte");
        for (Priorita p : Priorita.values()) {
            prioritaComboBox.addItem(p.name());
        }

        tipoComboBox.addItem("Tutti");
        for (Tipo t : Tipo.values()) {
            tipoComboBox.addItem(t.name());
        }

        risoltoComboBox.addItem("Tutti");
        risoltoComboBox.addItem("Sì");
        risoltoComboBox.addItem("No");

        creatoreComboBox.addItem("Tutti");
        assegnatarioComboBox.addItem("Tutti");
        dataCreazioneComboBox.addItem("Tutte");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<Map<String, Object>> utenti = Controller.getInstance().getUtenti();
                List<Map<String, Object>> issues = Controller.getInstance().getAllIssues();
                
                SwingUtilities.invokeLater(() -> {
                    for (Map<String, Object> u : utenti) {
                        creatoreComboBox.addItem((String) u.get("nomeutente"));
                        assegnatarioComboBox.addItem((String) u.get("nomeutente"));
                    }

                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    
                    List<String> date = issues.stream()
                            .map(i -> {
                                String d = (String) i.get("datacreazione");
                                if (d == null) return null;
                                try {
                                    if (d.contains(".")) d = d.substring(0, d.indexOf('.'));
                                    LocalDateTime dt = LocalDateTime.parse(d);
                                    return dt.format(outputFormatter);
                                } catch (Exception e) {
                                    return null;
                                }
                            })
                            .filter(d -> d != null)
                            .distinct()
                            .sorted()
                            .collect(Collectors.toList());
                            
                    for (String d : date) {
                        dataCreazioneComboBox.addItem(d);
                    }
                });
                return null;
            }
        };
        worker.execute();
    }

    private void applicaFiltri() {
        Map<String, Object> filtri = new HashMap<>();

        String id = idField.getText().trim();
        if (!id.isEmpty()) filtri.put("id", id);

        String parole = paroleChiaveFIeld.getText().trim();
        if (!parole.isEmpty()) filtri.put("parole", parole);

        if (prioritaComboBox.getSelectedIndex() > 0) 
            filtri.put("priorita", prioritaComboBox.getSelectedItem());

        if (tipoComboBox.getSelectedIndex() > 0) 
            filtri.put("tipo", tipoComboBox.getSelectedItem());

        if (risoltoComboBox.getSelectedIndex() > 0) 
            filtri.put("risolto", risoltoComboBox.getSelectedItem());

        if (creatoreComboBox.getSelectedIndex() > 0) 
            filtri.put("creatore", creatoreComboBox.getSelectedItem());

        if (assegnatarioComboBox.isEnabled() && assegnatarioComboBox.getSelectedIndex() > 0)
            filtri.put("assegnatario", assegnatarioComboBox.getSelectedItem());
            
        if (dataCreazioneComboBox.getSelectedIndex() > 0)
            filtri.put("data", dataCreazioneComboBox.getSelectedItem());

        if (listener != null) {
            listener.applicaFiltri(filtri);
        }
        dispose();
    }
}
