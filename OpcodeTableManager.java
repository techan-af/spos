
// File: OpcodeTableManager.java
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class OpcodeTableManager {
    private static final String FILE_NAME = "opcode_table.txt";
    private Map<String, Opcode> opcodeTable;

    public OpcodeTableManager() {
        opcodeTable = new HashMap<>();
    }

    public void addOpcode(Opcode opcode) {
        opcodeTable.put(opcode.getMnemonic(), opcode);
    }

    public Opcode getOpcode(String mnemonic) {
        return opcodeTable.get(mnemonic);
    }

    public void saveToFile() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(opcodeTable);
        }
    }

    public void loadFromFile() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            opcodeTable = (HashMap<String, Opcode>) ois.readObject();
        }
    }

    public static void main(String[] args) {
        OpcodeTableManager manager = new OpcodeTableManager();

        // Adding some opcodes
        manager.addOpcode(new Opcode("STOP", "00", "IS", 1));
        manager.addOpcode(new Opcode("ADD", "01", "IS", 1));
        manager.addOpcode(new Opcode("SUB", "02", "IS", 1));
        manager.addOpcode(new Opcode("MULTI", "03", "IS", 1));
        manager.addOpcode(new Opcode("MOVER", "04", "IS", 1));
        manager.addOpcode(new Opcode("MOVEM", "05", "IS", 1));
        manager.addOpcode(new Opcode("COMP", "06", "IS", 1));
        manager.addOpcode(new Opcode("BC", "07", "IS", 1));
        manager.addOpcode(new Opcode("DIV", "08", "IS", 1));
        manager.addOpcode(new Opcode("READ", "09", "IS", 1));
        manager.addOpcode(new Opcode("PRINT", "10", "IS", 1));
        manager.addOpcode(new Opcode("START", "01", "AD", 0));
        manager.addOpcode(new Opcode("END", "02", "AD", 0));
        manager.addOpcode(new Opcode("ORIGIN", "03", "AD", 0));
        manager.addOpcode(new Opcode("EQU", "04", "AD", 0));
        manager.addOpcode(new Opcode("DS", "01", "DL", 0));
        manager.addOpcode(new Opcode("DC", "02", "DL", 1));
        manager.addOpcode(new Opcode("AREG", "01", "RG", 0));
        manager.addOpcode(new Opcode("BREG", "02", "RG", 0));
        manager.addOpcode(new Opcode("CREG", "03", "RG", 0));

        try {
            // Saving to file
            manager.saveToFile();
            System.out.println("Opcode table saved successfully.");

            // Loading from file
            manager.loadFromFile();
            System.out.println("Opcode table loaded successfully.");

            // Fetching an opcode
            Opcode opcode = manager.getOpcode("ADD");

            if (opcode != null) {
                System.out.println("Fetched opcode: " + opcode);
            } else {
                System.out.println("Opcode not found.");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
