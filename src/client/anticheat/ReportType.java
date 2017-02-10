package client.anticheat;

public enum ReportType {
    Advertising(0, "Advertise"),
    Spamming(1, "Spam"),
    Trolling(2, "Troll"),
    Scamming(3, "Scam"),
    Disrespecting(4, "Disrespect"),
    Flamming(5, "Flame"),
    Misusing(6, "Misuse"),
    Racist(7, "Racist"),
    Hacking(8, "Hack");

    public byte i;
    public String theId;

    ReportType(int i, String theId) {
        this.i = (byte) i;
        this.theId = theId;
    }

    public static ReportType getById(int z) {
        for (ReportType t : ReportType.values()) {
            if (t.i == z) {
                return t;
            }
        }
        return null;
    }

    public static ReportType getByString(String z) {
        for (ReportType t : ReportType.values()) {
            if (z.contains(t.theId)) {
                return t;
            }
        }
        return null;
    }
}