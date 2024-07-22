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

    public boolean hasOpcode(String mnemonic) {
        return opcodeTable.containsKey(mnemonic);
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
}
