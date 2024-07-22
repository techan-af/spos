import java.io.*;
import java.util.*;

public class PassOneAssembler {
    private OpcodeTableManager opcodeTableManager;
    private Map<String, Integer> symbolTable;
    private List<String> literalTable;
    private List<Integer> poolTable;
    private List<String> intermediateCode;
    private int locationCounter;

    public PassOneAssembler() {
        opcodeTableManager = new OpcodeTableManager();
        symbolTable = new HashMap<>();
        literalTable = new ArrayList<>();
        poolTable = new ArrayList<>();
        intermediateCode = new ArrayList<>();
        locationCounter = 0;
        loadOpcodes();
    }

    private void loadOpcodes() {
        // Add opcodes to the table (assuming they are not loaded from file for
        // simplicity)
        opcodeTableManager.addOpcode(new Opcode("STOP", "00", "IS", 1));
        opcodeTableManager.addOpcode(new Opcode("ADD", "01", "IS", 1));
        opcodeTableManager.addOpcode(new Opcode("SUB", "02", "IS", 1));
        opcodeTableManager.addOpcode(new Opcode("MULTI", "03", "IS", 1));
        opcodeTableManager.addOpcode(new Opcode("MOVER", "04", "IS", 1));
        opcodeTableManager.addOpcode(new Opcode("MOVEM", "05", "IS", 1));
        opcodeTableManager.addOpcode(new Opcode("COMP", "06", "IS", 1));
        opcodeTableManager.addOpcode(new Opcode("BC", "07", "IS", 1));
        opcodeTableManager.addOpcode(new Opcode("DIV", "08", "IS", 1));
        opcodeTableManager.addOpcode(new Opcode("READ", "09", "IS", 1));
        opcodeTableManager.addOpcode(new Opcode("PRINT", "10", "IS", 1));
        opcodeTableManager.addOpcode(new Opcode("START", "01", "AD", 0));
        opcodeTableManager.addOpcode(new Opcode("END", "02", "AD", 0));
        opcodeTableManager.addOpcode(new Opcode("ORIGIN", "03", "AD", 0));
        opcodeTableManager.addOpcode(new Opcode("EQU", "04", "AD", 0));
        opcodeTableManager.addOpcode(new Opcode("DS", "01", "DL", 1));
        opcodeTableManager.addOpcode(new Opcode("DC", "02", "DL", 1));
        opcodeTableManager.addOpcode(new Opcode("AREG", "01", "RG", 0));
        opcodeTableManager.addOpcode(new Opcode("BREG", "02", "RG", 0));
        opcodeTableManager.addOpcode(new Opcode("CREG", "03", "RG", 0));
    }

    public void processCode(String inputFileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            int lineCounter = 0;

            while ((line = reader.readLine()) != null) {
                lineCounter++;
                processLine(line.trim(), lineCounter);
            }
        }

        handleLiterals();
        saveTables();
        printTables();
    }

    private void processLine(String line, int lineCounter) {
        if (line.isEmpty() || line.startsWith(";")) {
            return;
        }

        String[] tokens = line.split("\\s+");
        String label = null;
        String mnemonic;
        String operand = null;

        if (opcodeTableManager.hasOpcode(tokens[0])) {
            mnemonic = tokens[0];
            if (tokens.length > 1) {
                operand = tokens[1];
            }
        } else {
            label = tokens[0];
            mnemonic = tokens[1];
            if (tokens.length > 2) {
                operand = tokens[2];
            }
            symbolTable.put(label, locationCounter);
        }

        Opcode opcode = opcodeTableManager.getOpcode(mnemonic);
        if (opcode != null) {
            if (operand != null) {
                if (operand.startsWith("=")) {
                    addLiteral(operand);
                } else {
                    symbolTable.putIfAbsent(operand, -1);
                }
            }
            intermediateCode
                    .add(String.format("%d\t%s\t%s", locationCounter, mnemonic, operand != null ? operand : ""));
            locationCounter += opcode.getLength();
        } else {
            System.err.println("Error: Invalid mnemonic " + mnemonic + " at line " + lineCounter);
        }

        // Handle assembly directives like START, END, ORIGIN, EQU, DS, DC
        switch (mnemonic) {
            case "START":
                locationCounter = operand != null ? Integer.parseInt(operand) : 0;
                break;
            case "END":
                handleLiterals();
                break;
            case "ORIGIN":
                locationCounter = evaluateExpression(operand);
                break;
            case "EQU":
                if (label != null) {
                    symbolTable.put(label, evaluateExpression(operand));
                }
                break;
            case "DS":
                locationCounter += Integer.parseInt(operand);
                break;
            case "DC":
                locationCounter += 1; // Assuming DC takes 1 memory unit
                break;
        }
    }

    private void addLiteral(String literal) {
        if (!literalTable.contains(literal)) {
            literalTable.add(literal);
        }
    }

    private int evaluateExpression(String expression) {
        // Simple expression evaluator (assuming no complex expressions for now)
        if (expression.contains("+")) {
            String[] parts = expression.split("\\+");
            return symbolTable.get(parts[0]) + Integer.parseInt(parts[1]);
        } else if (expression.contains("-")) {
            String[] parts = expression.split("-");
            return symbolTable.get(parts[0]) - Integer.parseInt(parts[1]);
        } else {
            return Integer.parseInt(expression);
        }
    }

    private void handleLiterals() {
        poolTable.add(locationCounter);
        for (String literal : literalTable) {
            intermediateCode.add(String.format("%d\t%s\t%s", locationCounter, "LITERAL", literal));
            locationCounter += 1; // Assuming each literal takes 1 memory unit
        }
    }

    private void saveTables() throws IOException {
        saveToFile("intermediate_code.txt", intermediateCode);
        saveToFile("symbol_table.txt", symbolTable);
        saveToFile("literal_table.txt", literalTable);
        saveToFile("pool_table.txt", poolTable);
    }

    private <T> void saveToFile(String fileName, T table) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(table.toString());
        }
    }

    private void printTables() {
        System.out.println("Intermediate Code:");
        intermediateCode.forEach(System.out::println);

        System.out.println("\nSymbol Table:");
        symbolTable.forEach((k, v) -> System.out.println(k + " : " + v));

        System.out.println("\nLiteral Table:");
        for (int i = 0; i < literalTable.size(); i++) {
            System.out.println(i + " : " + literalTable.get(i));
        }

        System.out.println("\nPool Table:");
        for (int i = 0; i < poolTable.size(); i++) {
            System.out.println(i + " : " + poolTable.get(i));
        }
    }

    public static void main(String[] args) {
        PassOneAssembler assembler = new PassOneAssembler();

        try {
            assembler.processCode("input_code.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
