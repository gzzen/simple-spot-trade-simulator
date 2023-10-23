package ui;

import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

// graphical user interface for the trading app
public class GraphicalUI extends JFrame {

    private Portfolio portfolio;
    private JsonWriter writer;
    private JsonReader reader;
    private static final String PATH = "./data/portfolio.json";

    private OperationPanel operationPanel;
    private TradingPanel tradingPanel;
    private InfoPanel infoPanel;
    private SecurityList securityList;

    public GraphicalUI() {
        super("Spot-trade simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(13, 13, 13, 13));
        setVisible(true);
        setResizable(false);

        portfolio = new Portfolio(0);
        writer = new JsonWriter(PATH);
        reader = new JsonReader(PATH);
        operationPanel = new OperationPanel();
        tradingPanel = new TradingPanel();
        infoPanel = new InfoPanel();
        securityList = new SecurityList();

        getContentPane().setLayout(new GridLayout(2, 2));
        getContentPane().add(operationPanel);
        getContentPane().add(tradingPanel);
        getContentPane().add(infoPanel);
        getContentPane().add(securityList);

        setShutdownHook();

        pack();
    }

    private void setShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                OnExitPrinter.print();
            }
        });
    }

    // The information panel section of the UI
    private class InfoPanel extends JPanel {

        private JScrollPane scrollSecurityInfo;
        private JScrollPane scrollSystemInfo;
        private JPanel systemInfo;
        private JTextArea securityInfo;

        public InfoPanel() {
            super();
            setLayout(new GridLayout(2, 1));
            systemInfo = new JPanel();
            systemInfo.setLayout(new BoxLayout(systemInfo, BoxLayout.Y_AXIS));
            scrollSystemInfo = new JScrollPane(systemInfo,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollSystemInfo.setPreferredSize(new Dimension(100, 20));
            systemInfo.add(new JLabel("System output goes here..."));

            securityInfo = new JTextArea("Security information goes here...");
            securityInfo.setEditable(false);
            scrollSecurityInfo = new JScrollPane(securityInfo,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            add(scrollSecurityInfo);
            add(scrollSystemInfo);
        }

        // REQUIRES: output is the string representation of a security
        // MODIFIES: this
        // EFFECTS: display the security information at a text field
        public void setSecurityDisplay(String output) {
            securityInfo.setText(output);
            revalidate();
        }

        // REQUIRES: output is an output from the trading app
        // MODIFIED: this
        // EFFECTS: display the system information as a list of labels
        public void setSystemDisplay(String output) {
            systemInfo.add(new JLabel(output));
            revalidate();
        }

    }

    // The trading panel of the UI
    private class TradingPanel extends JPanel {

        private JButton buyButton;
        private JButton sellButton;
        private JButton addCashButton;
        private JTextArea cashDisplay;

        public TradingPanel() {
            super();
            setLayout(new GridLayout(2, 2));

            buyButton = new JButton("Buy");
            sellButton = new JButton("Sell");
            addCashButton = new JButton("Add cash");
            cashDisplay = new JTextArea("Cash balance...");
            cashDisplay.setEditable(false);

            add(addCashButton);
            add(cashDisplay);
            add(buyButton);
            add(sellButton);

            setupButtons();
        }

        // MODIFIES: this
        // EFFECTS: setup functions of the buttons
        private void setupButtons() {
            buyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    buy();
                }
            });

            sellButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sell();
                }
            });
            addCashButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addCash();
                }
            });

        }

        // MODIFIES: this
        // EFFECTS: show a dialog for adding cash
        private void addCash() {
            try {
                int amount = getValidNumber("How much do you want to add?");
                portfolio.addCash(amount);
                infoPanel.setSystemDisplay(amount + "$ added to the portfolio!");
                showCash();
            } catch (DialogClosedException e) {
                infoPanel.setSystemDisplay("Failed: You closed the dialog");
            }
        }

        // MODIFIES: this
        // EFFECTS: display the amount of cash at cash window
        public void showCash() {
            cashDisplay.setText("Cash:\n" + portfolio.getCash() + "$");
            revalidate();
            repaint();
        }

        // MODIFIES: this
        // EFFECTS: perform buying operation in the model
        private void buy() {
            String code;
            int price;
            int quantity;
            try {
                code = getCode();
                price = getValidNumber("Please indicate the price:");
                quantity = getValidNumber("Please indicate the quantity:");
                portfolio.buy(code, price, quantity);
            } catch (DialogClosedException e) {
                infoPanel.setSystemDisplay("Failed: You closed the dialog");
            } catch (InsufficientFundException e) {
                infoPanel.setSystemDisplay("Failed: Insufficient fund");
            }
            infoPanel.setSystemDisplay("Security purchased successfully!");
            securityList.setupSecurityList();
            tradingPanel.showCash();
        }

        // MODIFIES: this
        // EFFECTS: perform selling operations in the model
        private void sell() {
            String code;
            int price;
            int quantity;
            try {
                code = getCode();
                price = getValidNumber("Please indicate the price:");
                quantity = getValidNumber("Please indicate the quantity:");
                infoPanel.setSystemDisplay("Sold successfully with return rate: "
                        + portfolio.sell(code, price, quantity));
            } catch (DialogClosedException e) {
                infoPanel.setSystemDisplay("Failed: You closed the dialog");
            } catch (SecurityNotFoundException e) {
                infoPanel.setSystemDisplay("Failed: security code not found");
            } catch (ExcessQuantityException e) {
                infoPanel.setSystemDisplay("Failed: you don't have that much to sell");
            }
            tradingPanel.showCash();
        }

        // EFFECTS: show an input dialog to get security code
        private String getCode() throws DialogClosedException {
            String code = JOptionPane.showInputDialog("Please indicate the code:");
            checkDialogClosed(code);
            return code;
        }
    }

    // the operation panel for the UI
    private class OperationPanel extends JPanel {

        private JButton loadButton;
        private JButton saveButton;
        private JButton quitButton;
        private JButton goodLuckButton;

        public OperationPanel() {
            super();
            setLayout(new GridLayout(4, 1));
            loadButton = new JButton("Load");
            saveButton = new JButton("Save");
            quitButton = new JButton("Quit");
            goodLuckButton = new JButton("I want good luck!");

            add(loadButton);
            add(saveButton);
            add(quitButton);
            add(goodLuckButton);

            setupButtons();
        }

        // MODIFIES: this
        // EFFECTS: setup functions for the buttons
        private void setupButtons() {
            loadButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    load();
                }
            });
            saveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    save();
                }
            });
            quitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    quit();
                }
            });
            goodLuckButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    goodLuck();
                }
            });
        }

        // MODIFIES: this
        // EFFECTS: update the portfolio status by specified JSON file
        private void load() {
            try {
                portfolio = reader.read();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Unable to read from file " + PATH,
                        "IO error",
                        JOptionPane.ERROR_MESSAGE);
            }
            infoPanel.setSystemDisplay("Successfully loaded!");
            securityList.setupSecurityList();
            tradingPanel.showCash();
        }

        // MODIFIES: this
        // EFFECTS: save the portfolio status as JSON file
        private void save() {
            try {
                writer.open();
                writer.write(portfolio);
                writer.close();
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                        "Persistence file " + PATH + " does not exist",
                        "IO error",
                        JOptionPane.ERROR_MESSAGE);
            }
            infoPanel.setSystemDisplay("Successfully saved!");
        }

        // EFFECTS: quit the program
        private void quit() {
            System.exit(0);
        }

        // MODIFIES: this
        // EFFECTS: show a pop-up window that can give good luck
        private void goodLuck() {
            JFrame imageFrame = new JFrame();
            ImageIcon image = new ImageIcon("./data/goodluck.png");
            JLabel imageLabel = new JLabel(image);
            imageFrame.getContentPane().add(imageLabel);
            imageFrame.pack();
            imageFrame.setResizable(false);
            imageFrame.setVisible(true);
            infoPanel.setSystemDisplay("Good luck!");
        }
    }

    // Panel that contains a list of security at the UI
    private class SecurityList extends JPanel {

        private JPanel securityContainer;
        private JScrollPane scrollSecurityList;

        public SecurityList() {
            super();
            setLayout(new GridLayout(1, 1));

            securityContainer = new JPanel();
            securityContainer.setLayout(new BoxLayout(securityContainer, BoxLayout.Y_AXIS));
            scrollSecurityList = new JScrollPane(securityContainer);
            scrollSecurityList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollSecurityList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            securityContainer.add(new JLabel("Security list goes here..."));

            add(scrollSecurityList);
        }

        // MODIFIES: this
        // EFFECTS: setup the security list by the portfolio
        public void setupSecurityList() {
            securityContainer.removeAll();
            for (Security security : portfolio.getListOfSecurity()) {
                addSecurity(security);
            }
        }

        // MODIFIES: this
        // EFFECTS: add a security button to the container
        private void addSecurity(Security security) {
            securityContainer.add(new SecurityButton(security));
            revalidate();
            repaint();
        }

        // Button object that represents one security
        private class SecurityButton extends JButton {

            private Security security;

            public SecurityButton(Security security) {
                super(security.getCode());
                this.security = security;
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        onClick();
                    }
                });
            }

            // MODIFIES: this
            // EFFECTS: setup the function of clicking the security button
            private void onClick() {
                infoPanel.setSecurityDisplay(security.toString());
            }
        }
    }

    // EFFECTS: force the user to input a valid integer and returns it
    private int getValidNumber(String message) throws DialogClosedException {
        String input = JOptionPane.showInputDialog(message);
        int inputAsInt = 0;
        try {
            checkNumericInput(input);
            checkDialogClosed(input);
            inputAsInt = Integer.parseInt(input);
            checkValidInput(inputAsInt);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please input an integer!",
                    "Non-integer value!",
                    JOptionPane.ERROR_MESSAGE);
            getValidNumber(message);
        } catch (InvalidInputException e) {
            JOptionPane.showMessageDialog(this,
                    "Please input a positive number!",
                    "Negative value!",
                    JOptionPane.ERROR_MESSAGE);
            getValidNumber(message);
        }
        return inputAsInt;
    }

    // EFFECTS: check if an input is valid, if not,
    // throw InvalidInputException
    private void checkValidInput(int input) throws InvalidInputException {
        if (input < 0) {
            throw new InvalidInputException();
        }
    }

    // EFFECTS: check if an input is numeric, if not,
    // throw NumberFormatException
    private void checkNumericInput(String input) {
        if (!input.matches("-?\\d+")) {
            throw new NumberFormatException();
        }
    }

    // EFFECTS: check if the dialog is closed. If so,
    // throw DialogClosedException, otherwise do nothing
    private void checkDialogClosed(String input) throws DialogClosedException {
        if (input == null) {
            throw new DialogClosedException();
        }
    }

}
