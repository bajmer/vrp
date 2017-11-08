package core;

import gui.Gui;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Glowna klasa aplikacji
 */
public class Main {

    /**
     * Logger klasy
     */
    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * Uruchamia program i tworzy interfes uzytkownika
     *
     * @param args
     */
    public static void main(String[] args) {
        Gui gui = new Gui();
        gui.setVisible(true);
        logger.info("Application started.");
    }
}
