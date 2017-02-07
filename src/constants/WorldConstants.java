/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package constants;

/**
 * @author Itzik
 */
public class WorldConstants {

    public static final int defaultserver = Servers.Scania.getWorld(); // Not really being used anymore
    public static final int gmserver = -1; // 티탄 (Titan) -1 = no gm server
    //Note that for gmserver better not to enable show or else it will duplicate

    public static String getNameById(int serverid) {
        switch (serverid) {
            case 0:
                return "Scania";
            case 1:
                return "Bera";
            case 2:
                return "Broa";
            case 3:
                return "Windia";
            case 4:
                return "Khaini";
            case 5:
                return "Bellocan";
            case 6:
                return "Mardia";
            case 7:
                return "Kradia";
            case 8:
                return "Yellonde";
            case 9:
                return "Demethos";
            case 10:
                return "Galicia";
            case 11:
                return "El Nido";
            case 12:
                return "Zenith";
            case 13:
                return "Arcania";
            case 14:
                return "Chaos";
            case 15:
                return "Nova";
            case 16:
                return "Regenades";
            case 17:
                return "Azwan";
            case 18:
                return "Croa";
            case 19:
                return "Judis";
            case 20:
                return "Kastia";
            case 21:
                return "Aster";
            case 22:
                return "Cosmo";
            case 23:
                return "Androa";
            case 33:
                return "카오스";
            case 34:
                return "티탄";
            case 35:
                return "Legends";
            case 36:
                return "Elf";
            case 37:
            case 39:
                return "저스티스";
            case 38:
                return "래이븐";
        }
        return "불명"; //불명 = Unknown
    }

    public static String getTespiaNameById(String serverid) {
        switch (serverid) {
            case "t0":
                return "Tespia";
            case "t1":
                return "스카니아";
            case "t2":
                return "배라";
            case "t3":
                return "브로아";
            case "t30":
                return "Nova";
            case "t31":
                return "Cosmo";
            case "t32":
                return "Androa";
            case "t33":
                return "카오스";
            case "t34":
                return "티탄";
            case "t35":
                return "래잔드";
            case "t36":
                return "앨프";
            case "t37":
                return "저스티스";
            case "t38":
                return "래이븐";
        }
        return "불명"; // 불명 = Unknown
    }

    public enum Servers {

        // EXAMPLE:
        // Format: WorldName(world id, exp rate, meso rate, drop rate, flag, show, available to enter)
        // GMS:
        Scania(0, 5, 10, 2, (byte) 0, true, true),
        Bera(1, 9, 6, 3, (byte) 1, false, false),
        Broa(2, 1, 1, 1, (byte) 0, false, false),
        Windia(3, 1, 1, 1, (byte) 0, false, false),
        Khaini(4, 1, 1, 1, (byte) 0, false, false),
        Bellocan(5, 1, 1, 1, (byte) 0, false, false),
        Mardia(6, 1, 1, 1, (byte) 0, false, false),
        Kradia(7, 1, 1, 1, (byte) 0, false, false),
        Yellonde(8, 1, 1, 1, (byte) 0, false, false),
        Demethos(9, 1, 1, 1, (byte) 0, false, false),
        Galicia(10, 1, 1, 1, (byte) 0, false, false),
        ElNido(11, 1, 1, 1, (byte) 0, false, false),
        Zenith(12, 1, 1, 1, (byte) 0, false, false),
        Arcania(13, 1, 1, 1, (byte) 0, false, false),
        Chaos(14, 1, 1, 1, (byte) 0, false, false),
        Nova(15, 1, 1, 1, (byte) 0, false, false),
        Regenades(16, 1, 1, 1, (byte) 0, false, false),
        Azwan(17, 1, 1, 1, (byte) 0, false, false),
        Croa(18, 1, 1, 1, (byte) 0, false, false),
        Judis(19, 1, 1, 1, (byte) 0, false, false),
        Kastia(20, 1, 1, 1, (byte) 0, false, false),
        Aster(21, 1, 1, 1, (byte) 0, false, false),
        Cosmo(22, 1, 1, 1, (byte) 0, false, false),
        Androa(23, 1, 1, 1, (byte) 0, false, false),
        // Things that are being translated from KMS (in GMS wz):
        카오스(33, 1, 1, 1, (byte) 0, false, false), //Translation: Chaos
        티탄(34, 1, 1, 1, (byte) 0, false, false), //Translation: Titan
        Legends(35, 1, 1, 1, (byte) 0, false, false),
        Elf(36, 1, 1, 1, (byte) 0, false, false),
        저스티스(37, 1, 1, 1, (byte) 0, false, false), //Translation: Justice
        래이븐(38, 1, 1, 1, (byte) 0, false, false), //Translation: Raven
        저스티스2(39, 1, 1, 1, (byte) 0, false, false); //Translation: Justice
        public static final byte recommended = (byte) Scania.getWorld(); // -1 = no recommended
        public static final String recommendedmsg = getById(recommended).toString() + " is our current recommended world.";
        //(Yes, there are 2 justices idk why)
        private int world, exp, meso, drop;
        private byte flag;
        private boolean show, available;

        Servers(int world, int exp, int meso, int drop, byte flag, boolean show, boolean available) {
            this.world = world;
            this.exp = exp;
            this.meso = meso;
            this.drop = drop;
            this.flag = flag;
            this.show = show;
            this.available = available;
        }

        public static Servers getById(int g) {
            for (Servers e : Servers.values())
                if (e.world == g)
                    return e;
            return null;
        }

        public static Servers getByName(String g) {
            for (Servers e : Servers.values())
                if (e.toString().equals(g))
                    return e;
            return null;
        }

        public int getWorld() {
            return world;
        }

        public int getExp() { return exp; }

        public int getMeso() {
            return meso;
        }

        public int getDrop() {
            return drop;
        }

        public byte getFlag() {
            return flag;
        }

        public boolean show() {
            return show;
        }

        public boolean isAvailable() {
            return available;
        }
    }

    public enum TespiaServers {

        // EXAMPLE:
        // WorldName(world id, exp rate, meso rate, drop rate, flag, show, available to enter)
        // GMS Tespia: servers start with t tespia is t0 (weird)
        Tespia("t0", 1, 1, 1, (byte) 0, true, false),
        // Things that are being translated from KMS (in GMS wz):
        스카니아("t1", 1, 1, 1, (byte) 0, false, false), // Translation: Scania
        배라("t2", 1, 1, 1, (byte) 0, false, false), // Translation: Bera
        브로아("t3", 1, 1, 1, (byte) 0, false, false), // Translation: Broa
        Nova("t30", 1, 1, 1, (byte) 0, false, false),
        Cosmo("t31", 1, 1, 1, (byte) 0, false, false),
        Androa("t32", 1, 1, 1, (byte) 0, false, false),
        카오스("t33", 1, 1, 1, (byte) 0, false, false),
        티탄("t34", 1, 1, 1, (byte) 0, false, false),
        래잔드("t35", 1, 1, 1, (byte) 0, false, false), // Translaition: Legend
        앨프("t36", 1, 1, 1, (byte) 0, false, false), // Translaition: Elf
        저스티스("t37", 1, 1, 1, (byte) 0, false, false), // Translaition: Justice
        래이븐("t38", 1, 1, 1, (byte) 0, false, false); // Translaition: Raven
        //Not sure if Tespia has recommended worlds
        public static final String recommended = "t0";
        public static final String recommendedmsg = getById(recommended).toString() + " is our current recommended world.";
        private int exp, meso, drop;
        private byte flag;
        private String world;
        private boolean show, available;

        TespiaServers(String world, int exp, int meso, int drop, byte flag, boolean show, boolean available) {
            this.world = world;
            this.exp = exp;
            this.meso = meso;
            this.drop = drop;
            this.flag = flag;
            this.show = show;
            this.available = available;
        }

        public static TespiaServers getById(String g) {
            for (TespiaServers e : TespiaServers.values()) {
                if (e.world.equals(g)) {
                    return e;
                }
            }
            return null;
        }

        public static TespiaServers getByName(String g) {
            for (TespiaServers e : TespiaServers.values()) {
                if (e.name().equals(g)) {
                    return e;
                }
            }
            return null;
        }

        public String getWorld() {
            return world;
        }

        public int getExp() {
            return exp;
        }

        public int getMeso() {
            return meso;
        }

        public int getDrop() {
            return drop;
        }

        public byte getFlag() {
            return flag;
        }

        public boolean show() {
            return show;
        }

        public boolean isAvailable() {
            return available;
        }
    }
}