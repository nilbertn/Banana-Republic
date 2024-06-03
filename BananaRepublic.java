/* Builds Banana Republic Game Uses java swing to make a three panel frame.
Middle panel has a big button with a banana on it, every time you click you get a banana.
Right Panel shows the shop of minions/farms you can buy to produce your bananas.
Left Panel shows all the minions/farms you’ve bought.
Basically a clicker game.
 */

// import java swing and awt database for interface.
// import io and util library for miscellaneous utility

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

// note: in this program, minion refers to any item that generates bananas

// class that defines the entire class for the game
public class BananaRepublic extends JFrame {
    // defines width of one of the three Panels on frame
    private static final int PANEL_WIDTH = 300;
    // defines height of one of the three Panels on frame
    private static final int PANEL_HEIGHT = 600;
    // defines height of one of the several text boxes on frame
    private static final int TEXT_HEIGHT = 70;
    // Height for less significant textboxes
    private static final int SMALL_TEXTBOX_HEIGHT = 20;
    // defines a constant for timer to help increment bananas based on bps
    private static final int TIMER_CONSTANT = 100;
    // counts bananas made/clicked
    private static double bananaCounter = 0;
    // counts total clicks
    private static int clickCounter = 0;
    // counts bananas per second based on minions (producers) bought
    private static double bps = 0;
    // tracks time elapsed since starting program
    private static double timeElapsed = 0;
    // private variable to read quotes from .txt
    private static String quote;
    // private array to store quotes from .txt
    private static String[] quotes = new String[24];
    // initializes a Date to track the starting time
    private Date startTime = new Date();
    // final variable to display screen that shows the minions bought
    private final MinionScreen minionScreen;

    // This initializes several attributes of the entire game Window
    public BananaRepublic() {

        // reads in quotes from .txt file and stores in array
        try {
            Scanner sc = new Scanner(new File("quotes.txt"));
            int i = 0;
            while (sc.hasNext()) {
                quotes[i] = sc.nextLine();
                i++;
            }
        }
        // need this in case of a file not found exception
        catch (FileNotFoundException e) {
            System.out.println(e.getClass());
        }
        for (int i = 0; i < quotes.length; i++)
            System.out.println(quotes[i]);
        // sets initial size of screen
        setSize(960, 540);
        // sets title of the program
        setTitle("BANANA REPUBLIC");
        // sets a simple layout manager to create our buttons and other things
        setLayout(new FlowLayout());
        // this will allow creation of separate "panes" in the game window
        Container container = getContentPane();
        // calls class for pane to track minions
        minionScreen = new MinionScreen();
        // leftmost pane shows minions bought
        container.add(minionScreen);
        // middle pane shows banana clicker, counter, bps, quotes, cps
        BananaScreen bananaScreen = new BananaScreen(this);
        container.add(bananaScreen);
        // right screen to buy minions
        BuyScreen buyScreen = new BuyScreen(this);
        container.add(buyScreen);
        // make sure our program can be seen
        setVisible(true);
        // ensure operation ends when you press close
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    // returns value for bananas per second
    public double getBPS() {
        return bps;
    }

    // adds more bps when you buy a minion
    public void moreBPS(double bPS) {
        bps += bPS;
    }

    // returns amount of bananas you have
    public double getBananaCounter() {
        return bananaCounter;
    }

    // subtracts bananas when you spend in shop
    public void eatBananas(double eat) {
        bananaCounter -= eat;
    }

    // adds minion to pane when you buy
    public void moreMinion(String minion) {
        minionScreen.newMinion(minion);
    }

    // Pane that shows the Banana Clicker and related information
    public class BananaScreen extends JPanel implements ActionListener {
        // Labels for various information to show on this panel, names explain
        private final JLabel bananaText, bpsText, clicks, averageCPS, quoteText;
        // initialized the time to track time for cps
        private Date initialTime = new Date();
        // stores data for project
        private final BananaRepublic data;

        // output information on the Banana panel
        public BananaScreen(BananaRepublic xData) {
            // sets background, stores sent in data, sets size of panel
            getContentPane().setBackground(Color.YELLOW);
            data = xData;
            setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

            // makes new JLabels with their initial values, centers
            bananaText = new JLabel("0 Bananas", SwingConstants.CENTER);
            bpsText = new JLabel("0 Bananas Per Second", SwingConstants.CENTER);
            clicks = new JLabel("0 Clicks", SwingConstants.CENTER);
            averageCPS = new JLabel("0 CPS", SwingConstants.CENTER);
            quoteText = new JLabel("Random Minion Quote: Papaya", SwingConstants.CENTER);

            // sets font of all JLabels based on importance
            bananaText.setFont(new Font(Font.SERIF, Font.BOLD, 23));
            bpsText.setFont(new Font(Font.SERIF, Font.BOLD, 12));
            clicks.setFont(new Font(Font.SERIF, Font.PLAIN, 10));
            averageCPS.setFont(new Font(Font.SERIF, Font.PLAIN, 10));
            quoteText.setFont(new Font(Font.SERIF, Font.PLAIN, 10));

            // sets size of the textboxes based on significance
            bananaText.setPreferredSize(new Dimension(PANEL_WIDTH, TEXT_HEIGHT));
            bpsText.setPreferredSize(new Dimension(PANEL_WIDTH, SMALL_TEXTBOX_HEIGHT));
            clicks.setPreferredSize(new Dimension(PANEL_WIDTH, SMALL_TEXTBOX_HEIGHT));
            averageCPS.setPreferredSize(new Dimension(PANEL_WIDTH, SMALL_TEXTBOX_HEIGHT));
            quoteText.setPreferredSize(new Dimension(PANEL_WIDTH, SMALL_TEXTBOX_HEIGHT));

            // assigns a big yellow banana on the button
            ImageIcon imageIcon = new ImageIcon("./icons/Banana.png");
            JButton bananaClicker = new JButton(imageIcon);
            bananaClicker.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_WIDTH));
            bananaClicker.addActionListener(this);

            // finally, add all the Labels
            add(bananaText);
            add(bpsText);
            add(bananaClicker);
            add(clicks);
            add(averageCPS);
            add(quoteText);

            // make sure to repaint the component
            final JPanel screen = this;
            ActionListener clicked = evt -> screen.repaint();

            // start a timer based on the action listener
            new Timer(TIMER_CONSTANT, clicked).start();
        }

        // actually paint in the updated components
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            // calculate time spent between the time now and time we started with
            Date nowTime = new Date();
            long timeSpent = nowTime.getTime() - initialTime.getTime();
            initialTime = nowTime;
            // based on time, add Bananas to counter based on bps
            bananaCounter += data.getBPS() * (timeSpent / 1000.0);
            // updates banana counter text
            bananaText.setText((int) bananaCounter + " Bananas");
            // updates bps text
            bpsText.setText(String.format("%.3f Bananas per second", bps));
            // updates click counting text
            clicks.setText(clickCounter + " Clicks");
            // calculate averageCPS
            averageCPS.setText("Average CPS: " + clickCounter / (timeElapsed / 1000.0));
            // output a random minion quote
            quoteText.setText("Random Minion Quote: " + " " + quote + " ");
        }

        // when button pressed, do these things
        public void actionPerformed(ActionEvent e) {
            // random object to select a random quote
            Random rand = new Random();
            int i = rand.nextInt(24);
            quote = quotes[i];
            // this time tracker will track the CPS
            Date clickTime = new Date();
            timeElapsed = clickTime.getTime() - startTime.getTime();
            // add to bananas and clicks per click
            bananaCounter += 1.0;
            clickCounter += 1;
            // remember to repaint!
            repaint();
        }
    }

    // Panel to show minions bought
    public class MinionScreen extends JPanel {
        // Symbol table tracks the minions owned
        private final ST<String, MinionOwned> minionStorage = new ST<String, MinionOwned>();

        // simply sets the panel dimensions of the minion panel
        public MinionScreen() {
            setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        }

        // instance where you own a minion
        public class MinionOwned extends JPanel {
            // name of the minion
            private final String name;

            // assigns name to minion and dimensions for showing them in panel
            public MinionOwned(String minionName) {
                name = minionName;
                setPreferredSize(new Dimension(PANEL_WIDTH, TEXT_HEIGHT));
            }

            // this will add the minion physically to the screen with an image
            public void update() {
                ImageIcon imageIcon = new ImageIcon("./icons/" + name + ".png");
                JLabel label = new JLabel(imageIcon);
                add(label);
            }
        }

        // void function that will see if minion owned yet
        public void newMinion(String label) {
            // if we don't own it update the ST key and screen, else just update screen
            if (!minionStorage.contains(label)) {
                MinionOwned panel = new MinionOwned(label);
                panel.update();
                minionStorage.put(label, panel);
                add(panel);
                for (String key : minionStorage)
                    System.out.println(minionStorage.get(key));
            }
            else {
                minionStorage.get(label).update();
                for (String key : minionStorage)
                    System.out.println(minionStorage.get(key));
            }
        }
    }

    // Minion to buy in the shop
    public class ShopItem {
        // name of the minion
        private final String minionName;
        // cost to buy specific minion
        private double cost;
        // variable for bps
        private double bananaPerSecond;
        // button to buy a minion
        private final JButton buyMinion;

        // sets attributes of the shop item
        public ShopItem(String name, double theCost, double bps, final BananaRepublic data) {
            // assigns inputted variables to private variables
            minionName = name;
            cost = theCost;
            bananaPerSecond = bps;
            // creates button with name and cost of minion, sets size of button
            buyMinion = new JButton(minionName + " " + (int) cost);
            buyMinion.setPreferredSize(new Dimension(PANEL_WIDTH, 40));
            // once button pressed...
            ActionListener aL = evt -> {
                // if we can afford it, buy minion
                if (cost <= data.getBananaCounter()) {
                    // adds to bps, subtracts cost, updates expensiveness
                    data.moreBPS(bananaPerSecond);
                    data.eatBananas((int) cost);
                    cost = cost * 1.3;
                    bananaPerSecond = bananaPerSecond * 1.1;
                    // sets updated cost
                    buyMinion.setText(minionName + " " + (int) cost);
                    data.moreMinion(minionName);
                }
            };
            // uses timer for button click
            buyMinion.addActionListener(aL);
            ActionListener timedAL = evt -> {
                // simply enables you to buy if you can afford, disables if cannot
                double bananas = data.getBananaCounter();
                if (Math.max(bananas, cost) == bananas) {
                    if (!buyMinion.isEnabled()) {
                        buyMinion.setEnabled(true);
                        buyMinion.repaint();
                    }
                }
                else {
                    if (buyMinion.isEnabled()) {
                        buyMinion.setEnabled(false);
                        buyMinion.repaint();
                    }
                }
            };
            new Timer(TIMER_CONSTANT, timedAL).start();
        }

        // returns cost of item
        public long getPrice() {
            return (long) cost;
        }

        // returns button for minions
        public JButton getButton() {
            return buyMinion;
        }
    }

    // Actual panel to buy minion
    public class BuyScreen extends JPanel {
        // stores data
        private final BananaRepublic data;

        // sets initial attributes of panel
        public BuyScreen(BananaRepublic xData) {
            // inputs data from input
            data = xData;
            // sets dimensions, title of shop, font and size of textbox, and adds
            setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
            JLabel shopText = new JLabel("MINION SHOP", SwingConstants.CENTER);
            shopText.setFont(new Font(Font.SERIF, Font.BOLD, 25));
            shopText.setPreferredSize(new Dimension(PANEL_WIDTH, 75));
            add(shopText);

            // manually add specific products, prices, and bps
            minionProduct("Cursor", 10, 0.2);
            minionProduct("Tree", 30, 3);
            minionProduct("Minion", 100, 7);
            minionProduct("Plantation", 800, 13);
            minionProduct("Factory", 3000, 40);
            minionProduct("Lab", 10000, 100);
            minionProduct("Trophy", 100000000, -100000);
        }

        // will add the specified attributes to button
        public void minionProduct(String name, double price, double cps) {
            ShopItem item = new ShopItem(name, price, cps, data);
            add(item.getButton());
        }
    }
}
