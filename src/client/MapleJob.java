package client;

public enum MapleJob {
    BEGINNER(0),
    WARRIOR(100),
    FIGHTER(110),
    CRUSADER(111),
    HERO(112),
    PAGE(120),
    WHITEKNIGHT(121),
    PALADIN(122),
    SPEARMAN(130),
    DRAGONKNIGHT(131),
    DARKKNIGHT(132),
    MAGICIAN(200),
    FP_WIZARD(210),
    FP_MAGE(211),
    FP_ARCHMAGE(212),
    IL_WIZARD(220),
    IL_MAGE(221),
    IL_ARCHMAGE(222),
    CLERIC(230),
    PRIEST(231),
    BISHOP(232),
    BOWMAN(300),
    HUNTER(310),
    RANGER(311),
    BOWMASTER(312),
    CROSSBOWMAN(320),
    SNIPER(321),
    MARKSMAN(322),
    THIEF(400),
    ASSASSIN(410),
    HERMIT(411),
    NIGHTLORD(412),
    BANDIT(420),
    CHIEFBANDIT(421),
    SHADOWER(422),
    BLADE_RECRUIT(430),
    BLADE_ACOLYTE(431),
    BLADE_SPECIALIST(432),
    BLADE_LORD(433),
    BLADE_MASTER(434),
    PIRATE(500),
    PIRATE_CS(501),
    JETT1(508),
    BRAWLER(510),
    MARAUDER(511),
    BUCCANEER(512),
    GUNSLINGER(520),
    OUTLAW(521),
    CORSAIR(522),
    CANNONEER(530),
    CANNON_BLASTER(531),
    CANNON_MASTER(532),
    JETT2(570),
    JETT3(571),
    JETT4(572),
    MANAGER(800),
    GM(900),
    SUPERGM(910),
    NOBLESSE(1000),
    DAWNWARRIOR1(1100),
    DAWNWARRIOR2(1110),
    DAWNWARRIOR3(1111),
    DAWNWARRIOR4(1112),
    BLAZEWIZARD1(1200),
    BLAZEWIZARD2(1210),
    BLAZEWIZARD3(1211),
    BLAZEWIZARD4(1212),
    WINDARCHER1(1300),
    WINDARCHER2(1310),
    WINDARCHER3(1311),
    WINDARCHER4(1312),
    NIGHTWALKER1(1400),
    NIGHTWALKER2(1410),
    NIGHTWALKER3(1411),
    NIGHTWALKER4(1412),
    THUNDERBREAKER1(1500),
    THUNDERBREAKER2(1510),
    THUNDERBREAKER3(1511),
    THUNDERBREAKER4(1512),
    LEGEND(2000),
    EVAN_NOOB(2001),
    ARAN1(2100),
    ARAN2(2110),
    ARAN3(2111),
    ARAN4(2112),
    EVAN1(2200),
    EVAN2(2210),
    EVAN3(2211),
    EVAN4(2212),
    EVAN5(2213),
    EVAN6(2214),
    EVAN7(2215),
    EVAN8(2216),
    EVAN9(2217),
    EVAN10(2218),
    MERCEDES(2002),
    MERCEDES1(2300),
    MERCEDES2(2310),
    MERCEDES3(2311),
    MERCEDES4(2312),
    PHANTOM(2003),
    PHANTOM1(2400),
    PHANTOM2(2410),
    PHANTOM3(2411),
    PHANTOM4(2412),
    LUMINOUS(2004),
    LUMINOUS1(2700),
    LUMINOUS2(2710),
    LUMINOUS3(2711),
    LUMINOUS4(2712),
    CITIZEN(3000),
    DEMON_SLAYER(3001),
    DEMON_SLAYER1(3100),
    DEMON_SLAYER2(3110),
    DEMON_SLAYER3(3111),
    DEMON_SLAYER4(3112),
    BATTLE_MAGE_1(3200),
    BATTLE_MAGE_2(3210),
    BATTLE_MAGE_3(3211),
    BATTLE_MAGE_4(3212),
    WILD_HUNTER_1(3300),
    WILD_HUNTER_2(3310),
    WILD_HUNTER_3(3311),
    WILD_HUNTER_4(3312),
    MECHANIC_1(3500),
    MECHANIC_2(3510),
    MECHANIC_3(3511),
    MECHANIC_4(3512),
    NAMELESS_WARDEN(5000),
    MIHILE1(5100),
    MIHILE2(5110),
    MIHILE3(5111),
    MIHILE4(5112),
    ADDITIONAL_SKILLS(9000);
    private final int jobid;

    MapleJob(int id) {
        this.jobid = id;
    }

    public static String getName(MapleJob mjob) {
        return mjob.name();
    }

    public static MapleJob getById(int id) {
        for (MapleJob l : values()) {
            if (l.getId() == id) {
                return l;
            }
        }
        return null;
    }

    public static boolean isExist(int id) {
        for (MapleJob job : values()) {
            if (job.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public int getId() {
        return this.jobid;
    }
}