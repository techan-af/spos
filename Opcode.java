
import java.io.Serializable;

public class Opcode implements Serializable {
    private String mnemonic;
    private String opcode;
    private String type;
    private int length;

    public Opcode(String mnemonic, String opcode, String type, int length) {
        this.mnemonic = mnemonic;
        this.opcode = opcode;
        this.type = type;
        this.length = length;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public String getOpcode() {
        return opcode;
    }

    public String getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "Opcode{" +
                "mnemonic='" + mnemonic + '\'' +
                ", opcode='" + opcode + '\'' +
                ", type='" + type + '\'' +
                ", length=" + length +
                '}';
    }
}
