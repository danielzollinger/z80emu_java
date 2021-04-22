package au.com.zollinger.z80_emu;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));

        try {
            MainMenu mainMenu = new MainMenu(reader);
            mainMenu.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
