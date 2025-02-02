package org.controller.modules;

import org.controller.codetemplates.Testable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class ArgumentsHandler implements Testable {
    public static String argSymbol = "-";
    public HashMap<String, String> programArgs = new HashMap<>();
    public List<String> options = new ArrayList<>();

    private void loadArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            try {
                if (arg.startsWith(argSymbol + argSymbol)) {
                    options.add(arg.toLowerCase().replace(argSymbol + argSymbol, ""));
                } else if (arg.startsWith(argSymbol)) {
                    programArgs.put(
                            arg.toLowerCase().replace(argSymbol, ""), args[i + 1]
                    );
                }
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                
            }
        }
    }

    public ArgumentsHandler(String[] args) {
        this.loadArgs(args);
    }

    public boolean hasArg(String key) {
        return programArgs.containsKey(key);
    }

    public String getArg(String key) {
        return programArgs.get(key);
    }

    public boolean hasOption(String key) {
        return options.contains(key);
    }

    public void test() {
        this.programArgs = new HashMap<>();
        this.loadArgs( new String[]{"main.jar", "-arg1", "some", "-arg2", "info"} );
        System.out.println(programArgs);
    }
}
