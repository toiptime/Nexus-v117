package constants;

import java.util.Calendar;

public class ServerConstants {
    public static final short MAPLE_VERSION = (short) 117;
    public static final String MAPLE_PATCH = "2";

    public static final boolean BLOCK_CS = false;
    public static final boolean OLD_MAPS = false; // Example: It will use old maple event's henesys instead of current one
    public static final boolean LOG_PACKETS = false;
    public static final boolean ADMIN_ONLY = false;

    public static final int USER_LIMIT = 100;
    public static final String EVENT_MESSAGE = "event message";
    public static final byte FLAG = 0;
    public static final int MAX_CHARACTERS = 16;
    public static final String SERVER_NAME = "Project Nexus";
    public static final String HOST = "127.0.0.1";
    public static final int CHANNELS = 4;
    //PVP, GuildQuest, AswanOffSeason
    public static final String EVENTS = "ServerMessage,PinkZakumEntrance,CygnusBattle,ScarTarBattle,VonLeonBattle,Ghost,OrbisPQ,Romeo,Juliet,Pirate,Amoria,Ellin,CWKPQ,DollHouse,BossBalrog_EASY,BossBalrog_NORMAL,HorntailBattle,Nibergen,PinkBeanBattle,ZakumBattle,NamelessMagicMonster,Dunas,Dunas2,2095_tokyo,ZakumPQ,LudiPQ,KerningPQ,ProtectTylus,WitchTower_EASY,WitchTower_Med,WitchTower_Hard,Vergamot,ChaosHorntail,ChaosZakum,CoreBlaze,BossQuestEASY,BossQuestMed,BossQuestHARD,BossQuestHELL,Ravana_EASY,Ravana_HARD,Ravana_MED,Aufhaven,Dragonica,Rex,MonsterPark,Kenta,ArkariumBattle,HillaBattle,SpawnRoom";

    //Rates
    public static final int MESO_RATE = 4;
    public static final int DROP_RATE = 4;
    public static boolean TESPIA = false; // Used for activating GMST, or KMST.
    public static boolean USE_LOCALHOST = false; // true = Packets are logged, false = Others can connect to server

    public static final byte Class_Bonus_EXP(final int job) {
        switch (job) {
            case 501:
            case 530:
            case 531:
            case 532:
            case 2300:
            case 2310:
            case 2311:
            case 2312:
            case 3100:
            case 3110:
            case 3111:
            case 3112:
            case 800:
            case 900:
            case 910:
                return 0;
        }
        return 0;
    }

    public static boolean getEventTime() {
        int time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        switch (Calendar.DAY_OF_WEEK) {
            case 1:
                return time >= 1 && time <= 5;
            case 2:
                return time >= 4 && time <= 9;
            case 3:
                return time >= 7 && time <= 12;
            case 4:
                return time >= 10 && time <= 15;
            case 5:
                return time >= 13 && time <= 18;
            case 6:
                return time >= 16 && time <= 21;
        }
        return time >= 19 && time <= 24;
    }

    public static OperatingSystems getOS() {
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.indexOf("win") >= 0)
            return OperatingSystems.WINDOWS;
        else if (OS.indexOf("mac") >= 0)
            return OperatingSystems.MAC;
        else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0)
            return OperatingSystems.UNIX;
        return null;
    }

    public enum OperatingSystems{
        UNIX,
        MAC,
        WINDOWS
    }


    public enum PlayerGMRank {
        NORMAL('@', 0),
        DONATOR('#', 1),
        INTERN('!', 2),
        GM('!', 3),
        SUPERGM('!', 4),
        ADMIN('!', 5),
        SUDO('!', 6);
        private char commandPrefix;
        private int level;

        PlayerGMRank(char ch, int level) {
            commandPrefix = ch;
            this.level = level;
        }

        public char getCommandPrefix() {
            return commandPrefix;
        }

        public int getLevel() {
            return level;
        }
    }

    public enum CommandType {

        NORMAL(0),
        TRADE(1);
        private int level;

        CommandType(int level) {
            this.level = level;
        }

        public int getType() {
            return level;
        }

    }

}