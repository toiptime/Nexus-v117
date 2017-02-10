package handling.login;

import constants.GameConstants;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.Triple;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginInformationProvider {

    private final static LoginInformationProvider instance = new LoginInformationProvider();
    protected final List<String> ForbiddenName = new ArrayList<String>();
    // gender, val, job
    protected final Map<Triple<Integer, Integer, Integer>, List<Integer>> makeCharInfo = new HashMap<Triple<Integer, Integer, Integer>, List<Integer>>();
    protected LoginInformationProvider() {
        final MapleDataProvider prov = MapleDataProviderFactory.getDataProvider(new File("wz/Etc.wz"));
        MapleData nameData = prov.getData("ForbiddenName.img");
        for (final MapleData data : nameData.getChildren()) {
            ForbiddenName.add(MapleDataTool.getString(data));
        }
        nameData = prov.getData("Curse.img");
        for (final MapleData data : nameData.getChildren()) {
            ForbiddenName.add(MapleDataTool.getString(data).split(",")[0]);
        }
        final MapleData infoData = prov.getData("MakeCharInfo.img");
        final MapleData data = infoData.getChildByPath("Info");
        for (MapleData dat : data) {
            int val = -1;
            if (dat.getName().endsWith("Female")) { // Comes first..
                val = 1;
            } else if (dat.getName().endsWith("Male")) {
                val = 0;
            }
            final int job = JobType.getByJob(dat.getName()).type;
            for (MapleData da : dat) {
                final Triple<Integer, Integer, Integer> key = new Triple<Integer, Integer, Integer>(val, Integer.parseInt(da.getName()), job);
                List<Integer> our = makeCharInfo.get(key);
                if (our == null) {
                    our = new ArrayList<Integer>();
                    makeCharInfo.put(key, our);
                }
                for (MapleData d : da) {
                    our.add(MapleDataTool.getInt(d, -1));
                }
            }
        }
        if (GameConstants.GMS) { // TODO: LEGEND
            for (MapleData dat : infoData) {
                try {
                    final int type = JobType.getById(Integer.parseInt(dat.getName())).type;
                    for (MapleData d : dat) {
                        int val;
                        if (d.getName().endsWith("female")) {
                            val = 1;
                        } else if (d.getName().endsWith("male")) {
                            val = 0;
                        } else {
                            continue;
                        }
                        for (MapleData da : d) {
                            final Triple<Integer, Integer, Integer> key = new Triple<Integer, Integer, Integer>(val, Integer.parseInt(da.getName()), type);
                            List<Integer> our = makeCharInfo.get(key);
                            if (our == null) {
                                our = new ArrayList<Integer>();
                                makeCharInfo.put(key, our);
                            }
                            for (MapleData dd : da) {
                                our.add(MapleDataTool.getInt(dd, -1));
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        final MapleData uA = infoData.getChildByPath("UltimateAdventurer");
        for (MapleData dat : uA) {
            final Triple<Integer, Integer, Integer> key = new Triple<Integer, Integer, Integer>(-1, Integer.parseInt(dat.getName()), JobType.UltimateAdventurer.type);
            List<Integer> our = makeCharInfo.get(key);
            if (our == null) {
                our = new ArrayList<Integer>();
                makeCharInfo.put(key, our);
            }
            for (MapleData d : dat) {
                our.add(MapleDataTool.getInt(d, -1));
            }
        }
    }
    // 0 = eyes 1 = hair 2 = haircolor 3 = skin 4 = top 5 = bottom 6 = shoes 7 = weapon

    public static LoginInformationProvider getInstance() {
        return instance;
    }

    public static boolean isExtendedSpJob(int jobId) {
        return jobId >= 3100 && jobId <= 3512 || jobId / 100 == 22 || jobId / 100 == 23
                || jobId == 2002 || jobId == 2001 || jobId == 3000 || jobId == 3001
                || jobId == 508 || jobId == 2003 || jobId / 100 == 24 || jobId / 10 == 57 || jobId / 100 == 51 || jobId == 6000 || jobId == 6001;
    }

    public final boolean isForbiddenName(final String in) {
        for (final String name : ForbiddenName) {
            if (in.toLowerCase().contains(name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public final boolean isEligibleItem(final int gender, final int val, final int job, final int item) {
        if (item < 0) {
            return false;
        }
        final Triple<Integer, Integer, Integer> key = new Triple<Integer, Integer, Integer>(gender, val, job);
        final List<Integer> our = makeCharInfo.get(key);
        if (our == null) {
            return false;
        }
        return our.contains(item);
    }

    public enum JobType {

        UltimateAdventurer(-1, "Ultimate", 0, 0),// 100000000
        Resistance(0, "Resistance", 3000, 0),// 931000000
        Adventurer(1, "", 0, 0),
        Cygnus(2, "Premium", 1000, 0),// 130030000
        Aran(3, "Orient", 2000, 0),// 914000000
        Evan(4, "Evan", 2001, 0),// 900090000
        Mercedes(5, "", 2002, 0),// 910150000
        Demon(6, "", 3001, 0),// 931050310
        Phantom(7, "", 2003, 0),// 915000000
        DualBlade(8, "", 0, 0),// 103050900
        Mihile(9, "", 5000, 0),// 913070000
        //Luminous(10, "", 2004, -1),
        //Kaiser(11, "", 6000, -1),
        //AngelicBuster(12, "", 6001, -1),
        //Demon_Avenger(13, "", 3001, -1),
        //Xenon(14, "", 3002, -1),
        Jett(15, "", 0, 0);// 10000
        public int type, id, map;
        public String job;

        JobType(int type, String job, int id, int map) {
            this.type = type;
            this.job = job;
            this.id = id;
            this.map = map;
        }

        public static JobType getByJob(String g) {
            for (JobType e : JobType.values()) {
                if (e.job.length() > 0 && g.startsWith(e.job)) {
                    return e;
                }
            }
            return Adventurer;
        }

        public static JobType getByType(int g) {
            for (JobType e : JobType.values()) {
                if (e.type == g) {
                    return e;
                }
            }
            return Adventurer;
        }

        public static JobType getById(int g) {
            for (JobType e : JobType.values()) {
                if (e.id == g || (g == 508 && e.type == 8)) {
                    return e;
                }
            }
            return Adventurer;
        }
    }
}