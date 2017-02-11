package tools;

public enum Error {
    FATAL(3), WARNING(2), INFO(1);
    private int value;

    Error(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}