package synth;

public class ListItem {
    private final int id;
    private final String name;
    private int font;
    private int channel;
    private int bank;
    private int program;

    public ListItem(int id, String name) {
        this.id = id;
        this.name = name;
        this.font = 0;
        this.channel = 0;
        this.bank = 0;
        this.program = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getFont() {
        return font;
    }

    public int getChannel() {
        return channel;
    }

    public int getBank() {
        return bank;
    }

    public int getProgram() {
        return program;
    }

    public void setFont(int font) {
        this.font = font;
    }

    public void setChannel(int channel) {
        if (channel <= 0) {
            this.channel = 0;
        } else if (channel > 16) {
            this.channel = 15;
        } else {
            this.channel = channel;
        }
    }

    public void setBank(int bank) {
        this.bank = bank;
    }

    public void setProgram(int program) {
        this.program = program;
    }

    public void setBankProgram(String bank, String program) {
        this.bank = Integer.parseInt(bank);
        this.program = Integer.parseInt(program);
    }

    @Override
    public String toString() {
        return name; // отображается в ListView
    }
}
