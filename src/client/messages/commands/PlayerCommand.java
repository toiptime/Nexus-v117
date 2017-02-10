package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.messages.commands.CommandExecute.TradeExecute;
import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import handling.channel.ChannelServer;
import handling.world.World;
import scripting.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.RankingWorker;
import server.RankingWorker.RankingInformation;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.Logger;
import tools.StringUtil;
import tools.packet.CWvsContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Emilyx3
 */
public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }

    public static class depositapbank extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            int input = Integer.parseInt(splitted[1]);
            int ap = c.getPlayer().getRemainingAp();
            if (input <= ap) {
                c.getPlayer().addaptobank(input);
                c.getPlayer().setRemainingAp(ap - input);
                c.getPlayer().print(5, "You now have " + c.getPlayer().getapinbank() + " ability points in your bank and " + c.getPlayer().getRemainingAp() + " ability points left. (Your ability points will show correctly when you relog)");
                return 1;
            }
            c.getPlayer().print(5, "Please make sure that you have enough ability points to add into the bank.");
            return 0;

        }
    }

    public static class withdrawapbank extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            int input = Integer.parseInt(splitted[1]);
            int inbank = c.getPlayer().getapinbank();
            int ap = c.getPlayer().getRemainingAp();
            int final_ = ap + input;
            if (input <= inbank && input <= 32767 && final_ <= 32767) {
                c.getPlayer().setapbank(inbank - input);
                c.getPlayer().setRemainingAp(ap + input);
                c.getPlayer().print(5, "You now have " + c.getPlayer().getapinbank() + " ability points in your bank and " + c.getPlayer().getRemainingAp() + " ability points left. (Your ability points will show correctly when you relog)");
                return 1;
            }
            c.getPlayer().print(5, "Please make sure that you have enough ability points and not greater than 32767 to add into the bank.");
            return 0;

        }
    }

    public static class leet extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().isInBlockedMap()) {
                c.getPlayer().print(5, "You may not use this command in the current map.");
                return 0;
            }
            if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                c.getPlayer().print(5, "You may not use this command while you are in jail.");
                return 0;
            }

            if (c.getPlayer().getMeso() >= 500) { // 500 Mesos
                c.getPlayer().gainMeso(-500, true);
                c.getPlayer().toggleLeetness();
                return 1;

            } else {
                if (c.getPlayer().getLeetness() == true) {
                    c.getPlayer().toggleLeetness();
                } else {
                    c.getPlayer().print(6, "You don't have enough mesos to use the leet tool.");
                }
                return 0;
            }
        }
    }

    public static class codex extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int[] cards = {2380004, 2381006, 2381097, 2382095, 2382133, 2383075, 2384020, 2384077, 2384095, 2385072,
                    2386012, 2386054, 2387039, 2387056, 2387087, 2387109, 2388091, 2388152};
            for (int i = 0; i < cards.length; i++) {
                if (c.getPlayer().canHold(cards[i])) {
                    MapleInventoryManipulator.addById(c, cards[i], (short) 1, null);
                } else {
                    c.getPlayer().print(5, "Please leave more space for your inventory.");
                    return 0;
                }
            }
            return 1;
        }
    }

    public static class chalk extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (StringUtil.joinStringFrom(splitted, 1).length() <= 40) {
                c.getPlayer().setChalkboard(StringUtil.joinStringFrom(splitted, 1));
                return 1;
            }
            c.getPlayer().print(6, "Your chalkboard must be less than or equal to 40 characters.");
            return 0;
        }
    }

    // public static class Online extends CommandExecute {

    //    @Override
    //    public int execute(MapleClient c, String[] splitted) {
    //        for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
    //            c.getPlayer().print(6, "Channel" + i + ": " + ChannelServer.getInstance(i).getPlayerStorage().getAllCharacters().size());
    //            c.getPlayer().print(6, ChannelServer.getInstance(i).getPlayerStorage().getOnlinePlayers(true));
    //        }
    //        return 1;
    //     }
    // }

    public static class dispose extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write(CWvsContext.enableActions());
            c.getPlayer().print(6, "[Notice] You have been disposed.");
            return 1;
        }
    }

    public static class expfix extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setExp(c.getPlayer().getExp() - GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) >= 0 ? GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) : 0);
            c.getPlayer().fakeRelog();
            return 1;
        }
    }

    public static class go extends CommandExecute {

        private static final HashMap<String, Integer> gotomaps = new HashMap<>();

        static {
            gotomaps.put("ellin", 300000000);
            gotomaps.put("edel", 310000000);
            gotomaps.put("haunted", 682000000);
            gotomaps.put("crimson", 610030010);
            gotomaps.put("nlc", 600000000);
            gotomaps.put("fm", 910000000);
            gotomaps.put("dojo", 925020000);
            gotomaps.put("monster", 951000000);
            gotomaps.put("home", 800000000);
            gotomaps.put("showa", 801000000);
            gotomaps.put("golden", 809060000);
            gotomaps.put("amherst", 1000000);
            gotomaps.put("south", 2000000);
            gotomaps.put("orbis", 200000000);
            gotomaps.put("el", 211000000);
            gotomaps.put("lion1", 211060100);
            gotomaps.put("lion2", 211060300);
            gotomaps.put("lion3", 211060500);
            gotomaps.put("lion4", 211060700);
            gotomaps.put("lion5", 211060900);
            gotomaps.put("ludi", 220000000);
            gotomaps.put("omega", 221000000);
            gotomaps.put("korean", 222000000);
            gotomaps.put("aqua", 230000000);
            gotomaps.put("leafre", 240000000);
            gotomaps.put("horntail", 240060200);
            gotomaps.put("chaoshorntail", 240060201);
            gotomaps.put("mu", 250000000);
            gotomaps.put("herb", 251000000);
            gotomaps.put("pirate", 251010404);
            gotomaps.put("ariant", 260000200);
            gotomaps.put("magatia", 261000000);
            gotomaps.put("romeo", 261000011);
            gotomaps.put("juliet", 261000021);
            gotomaps.put("chryse", 200100000);
            gotomaps.put("three", 270000000);
            gotomaps.put("pinkbean", 270050100);
            gotomaps.put("tera", 240070000);
            gotomaps.put("zakum", 280030000);
            gotomaps.put("chaoszakum", 280030001);
            gotomaps.put("arkarium", 272030400);
            gotomaps.put("singapore", 540000000);
            gotomaps.put("malaysia", 550000000);
            gotomaps.put("spooky", 551030200);
            gotomaps.put("henesys", 100000000);
            gotomaps.put("ellinia", 101000000);
            gotomaps.put("elluel", 101050000);
            gotomaps.put("peion", 102000000);
            gotomaps.put("kerning", 103000000);
            gotomaps.put("lith", 104000000);
            gotomaps.put("six", 104020000);
            gotomaps.put("sleepy", 105000000);
            gotomaps.put("pirate", 120000000);
            gotomaps.put("florina", 120030000);
            gotomaps.put("ereve", 130000000);
            gotomaps.put("mushroom", 106020000);
            gotomaps.put("rien", 140000000);
            gotomaps.put("square", 103040000);
            gotomaps.put("amoria", 680000000);
            gotomaps.put("boss", 689010000);
            gotomaps.put("kampung", 551000000);
            gotomaps.put("boat", 541000000);
            gotomaps.put("vonleon", 211070100);
            gotomaps.put("cygnus", 271040100);
            gotomaps.put("alien", 610040200);
            gotomaps.put("skeleton", 682010201);
            gotomaps.put("scarecrow", 682010202);
            gotomaps.put("clown", 682010203);
            gotomaps.put("guild", 200000301);
            gotomaps.put("pap", 220080001);
            gotomaps.put("pianus", 230040410);
            gotomaps.put("female", 801040003);
            gotomaps.put("man", 801040100);
            gotomaps.put("jaguar", 931000500);
            gotomaps.put("haze", 300030100);
            gotomaps.put("lord", 251010404);
            gotomaps.put("kenta", 923040000);
            gotomaps.put("hilla", 262030300);
            gotomaps.put("hillas", 262031300);
            gotomaps.put("ardent", 910001000);
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (int i : GameConstants.blockedMaps) {
                if (c.getPlayer().getMapId() == i) {
                    c.getPlayer().print(5, "You may not use this command in the current map.");
                    return 0;
                }
            }
            if (splitted.length < 2) {
                c.getPlayer().print(6, "[Syntax] @go [map]. To view a list of locations that are available, use @town / @mob / @pq / @mob / @boss / @misc");
            } else {
                if (gotomaps.containsKey(splitted[1])) {
                    MapleMap target = c.getChannelServer().getMapFactory().getMap(gotomaps.get(splitted[1]));
                    if (target == null) {
                        c.getPlayer().print(6, "Map does not exist.");
                        return 0;
                    }
                    MaplePortal targetPortal = target.getPortal(0);
                    c.getPlayer().changeMap(target, targetPortal);
                } else {
                    c.getPlayer().print(6, "To view a list of locations that are available, use @town / @mob / @pq / @boss / @misc");
                }
            }
            return 1;
        }
    }

    public static class town extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(5, ".::::::::::: Project Nexus Town Maps :::::::::::.");
            c.getPlayer().print(5, "| ellin | edel | nlc | fm | home | showa |");
            c.getPlayer().print(5, "| golden | amherst | south | orbis | el |");
            c.getPlayer().print(5, "| ludi | omega | korean | aqua | leafre |");
            c.getPlayer().print(5, "| mu | herb | pirate | ariant | magatia |");
            c.getPlayer().print(5, "| singapore | malaysia | henesys | ellinia |");
            c.getPlayer().print(5, "| elluel | perion | kerning | lith | sleepy |");
            c.getPlayer().print(5, "| pirate | florina | ereve | rien | square |");
            c.getPlayer().print(5, "| amoria | kampung | boat |");
            return 1;
        }
    }

    public static class pq extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(5, ".:::::::::::::::: Project Nexus Party Quest Maps ::::::::::::::::.");
            c.getPlayer().print(5, "| dojo | monster | crimson | romeo | juliet | boss |");
            c.getPlayer().print(5, "| haze | lord | kenta |");
            return 1;
        }
    }

    public static class mob extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(5, ".:::::::::: Project Nexus Monster Maps ::::::::::.");
            c.getPlayer().print(5, "| skeleton | scarecrow | clown | alien |");
            c.getPlayer().print(5, "| lion1 | lion2 | lion3 | lion4 | lion5 |");
            return 1;
        }
    }

    public static class boss extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(5, ".::::::::::::::::::::::: Project Nexus Boss Maps :::::::::::::::::::::::.");
            c.getPlayer().print(5, "| pianus | pap | female | man | spooky | zakum | chaoszakum | vonleon |");
            c.getPlayer().print(5, "| hilla | horntail | chaoshorntail | arkarium | cygnus | hillas | pinkbean |");
            return 1;
        }
    }

    public static class misc extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(5, ".:::: Project Nexus Miscellaneous Maps ::::.");
            c.getPlayer().print(5, "| chryse | mushroom | haunted | ardent |");
            c.getPlayer().print(5, "| six | guild | three | tera | jaguar |");
            return 1;
        }
    }

    public static class npc extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(5, ".::::::::::::::::::::: Project Nexus NPC :::::::::::::::::::::.");
            c.getPlayer().print(6, "@nx - Opens the NX Item NPC. [Aramia]");
            c.getPlayer().print(6, "@trophy - Opens the event trophy exchanger NPC. [Agent W]");
            c.getPlayer().print(6, "@chair - Opens the Chair Gachapon NPC. [Chair Gachapon]");
            c.getPlayer().print(6, "@jq - Opens the Jumping Quest NPC. [Duey]");
            c.getPlayer().print(6, "@daily - Opens the Daily Reward NPC. [Santa]");
            c.getPlayer().print(6, "@craft - Opens the item NPC Crafter. [Cody]");
            c.getPlayer().print(6, "@exchanger - Opens the item Exchanger NPC. [Papulatus]");
            c.getPlayer().print(6, "@donor - Opens the Donor NPC. [Agent P]");
            c.getPlayer().print(6, "@msi - Opens the MSI Maker NPC. [Agent Meow]");
            c.getPlayer().print(6, "@jqp - Opens the Jumping Point Exchanger NPC. [Mr. Sandman]");
            c.getPlayer().print(6, "@buynx - Opens the NX Seller NPC. [Ardin]");
            c.getPlayer().print(6, "@bank - Opens the Bank NPC. [Eunice]");
            c.getPlayer().print(6, "@stylist - Opens the all-in-one styler NPC. [KIN]");
            c.getPlayer().print(6, "@job - Opens the job advancer NPC. [CreditUnion]");
            c.getPlayer().print(6, "@shop - Opens the all-in-one common equip seller NPC. [Conor]");
            c.getPlayer().print(6, "@skills - Opens the skills NPC. [Agatha]");
            c.getPlayer().print(6, "@reward - Opens the Reborn Reward NPC. [Lime Balloon]");
            c.getPlayer().print(6, "@vote - Opens the Vote Point Exchanger NPC. [Aqua Balloon]");
            return 1;
        }
    }

    public static class resetexp extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setExp(0);
            return 1;
        }
    }

    public static class str extends DistributeStatCommands {

        public str() {
            stat = MapleStat.STR;
        }
    }

    public static class dex extends DistributeStatCommands {

        public dex() {
            stat = MapleStat.DEX;
        }
    }

    public static class INT extends DistributeStatCommands {

        public INT() {
            stat = MapleStat.INT;
        }
    }

    public static class luk extends DistributeStatCommands {

        public luk() {
            stat = MapleStat.LUK;
        }
    }

    public static class cleardrops extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().clearDrops(c.getPlayer(), true);
            return 1;
        }
    }

    public abstract static class DistributeStatCommands extends CommandExecute {

        private static int statLim = 32767;
        protected MapleStat stat = null;

        private void setStat(MapleCharacter player, int amount) {
            switch (stat) {
                case STR:
                    player.getStat().setStr((short) amount, player);
                    player.updateSingleStat(MapleStat.STR, player.getStat().getStr());
                    break;
                case DEX:
                    player.getStat().setDex((short) amount, player);
                    player.updateSingleStat(MapleStat.DEX, player.getStat().getDex());
                    break;
                case INT:
                    player.getStat().setInt((short) amount, player);
                    player.updateSingleStat(MapleStat.INT, player.getStat().getInt());
                    break;
                case LUK:
                    player.getStat().setLuk((short) amount, player);
                    player.updateSingleStat(MapleStat.LUK, player.getStat().getLuk());
                    break;
            }
        }

        private int getStat(MapleCharacter player) {
            switch (stat) {
                case STR:
                    return player.getStat().getStr();
                case DEX:
                    return player.getStat().getDex();
                case INT:
                    return player.getStat().getInt();
                case LUK:
                    return player.getStat().getLuk();
                default:
                    throw new RuntimeException(); // Will never happen.
            }
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().print(5, "The integer you entered is invalid.");
                return 0;
            }
            int change = 0;
            try {
                change = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException nfe) {
                c.getPlayer().print(5, "The integer you entered is invalid.");
                return 0;
            }
            if (change <= 0) {
                c.getPlayer().print(5, "You must enter an integer that is greater than 0.");
                return 0;
            }
            if (c.getPlayer().getRemainingAp() < change) {
                c.getPlayer().print(5, "You don't have enough ability points to do this action.");
                return 0;
            }
            if (getStat(c.getPlayer()) + change > statLim) {
                c.getPlayer().print(5, "The stat limititation is " + statLim + ".");
                return 0;
            }
            setStat(c.getPlayer(), getStat(c.getPlayer()) + change);
            c.getPlayer().setRemainingAp((short) (c.getPlayer().getRemainingAp() - change));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
            c.getPlayer().print(5, StringUtil.makeEnumHumanReadable(stat.name()) + " has been raised by " + change + ".");
            return 1;
        }
    }

    public static class monster extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMonster mob = null;
            for (final MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                if (mob.isAlive()) {
                    c.getPlayer().print(6, "Monster " + mob.toString());
                    break; //only one
                }
            }
            if (mob == null) {
                c.getPlayer().print(6, "No monster was found.");
            }
            return 1;
        }
    }

    public abstract static class OpenNPCCommand extends CommandExecute {

        private static int[] npcs = { // Ish yur job to make sure these are in order and correct ;(
                9270035,// 0 Bank
                9900000,// 1 Stylist
                9900002,// 2 Job NPC
                9270026,// 3 Sixx Vote Point Exchanger [Unused Currently]
                2012000, // 4 Agatha @skills - Max All Skills / Max All Skills By Job / Clear Skills / Reset Skill Point To 0
                9010040, // 5 All In One NPC Seller
                9040008, // 6 Check Guild Rank
                9900000, // 7 Drop Cash
                9000055, // 8 Aramia NX
                9010001, // 9 Monster Drop
                1012121, // 10 Android Stylist
                9000039, // 11 Agent W Event Trophy Exchanger
                9110009, // 12 Chair Gachapon
                9010009, // 13 Duey Jumping Quest
                9310058, // 14 Daily Coin Giver
                9200000, // 15 Cody Crafter
                2043000, // 16 Pap Crafter
                9000035, // 17 Donor NPC
                9000037, // 18 Agent Meow MSI
                9201042, // 19 Jumping Quest Point Exchanger
                2101003, // 20 Buy NX Ardin
                9000055, // 21 Aramia NX
                2040039, // 22 Reborn Reward NPC
                2040041, // 23 Vote Point Exchanger NPC
        };
        protected int npc = -1;

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, npcs[npc], npc == 1 ? "dropcash" : null);
            return 1;
        }
    }

    public static class bank extends OpenNPCCommand {

        public bank() {
            npc = 0;
        }
    }
    
    /*public static class dropcash extends OpenNPCCommand {

        public dropcash() {
            npc = 1;
        }
    }*/

    public static class job extends OpenNPCCommand {

        public job() {
            npc = 2;
        }
    }

    public static class skills extends OpenNPCCommand {

        public skills() {
            npc = 4;
        }
    }

    public static class shop extends OpenNPCCommand {

        public shop() {
            this.npc = 5;
        }
    }

    public static class guildrank extends OpenNPCCommand {

        public guildrank() {
            this.npc = 6;
        }
    }

    public static class stylist extends OpenNPCCommand {

        public stylist() {
            npc = 7;
        }
    }

    public static class nx extends OpenNPCCommand {

        public nx() {
            npc = 21;
        }
    }

    public static class monsterdrops extends OpenNPCCommand {

        public monsterdrops() {
            npc = 9;
        }
    }

    public static class androidstylist extends OpenNPCCommand {

        public androidstylist() {
            npc = 10;
        }
    }

    public static class trophy extends OpenNPCCommand {

        public trophy() {
            npc = 11;
        }
    }

    public static class chair extends OpenNPCCommand {

        public chair() {
            npc = 12;
        }
    }

    public static class jq extends OpenNPCCommand {

        public jq() {
            npc = 13;
        }
    }

    public static class daily extends OpenNPCCommand {

        public daily() {
            npc = 14;
        }
    }

    public static class craft extends OpenNPCCommand {

        public craft() {
            npc = 15;
        }
    }

    public static class exchanger extends OpenNPCCommand {

        public exchanger() {
            npc = 16;
        }
    }

    public static class donor extends OpenNPCCommand {

        public donor() {
            npc = 17;
        }
    }

    public static class msi extends OpenNPCCommand {

        public msi() {
            npc = 18;
        }
    }

    public static class jqp extends OpenNPCCommand {

        public jqp() {
            npc = 19;
        }
    }

    public static class buynx extends OpenNPCCommand {

        public buynx() {
            npc = 20;
        }
    }

    public static class reward extends OpenNPCCommand {

        public reward() {
            npc = 22;
        }
    }

    public static class vote extends OpenNPCCommand {

        public vote() {
            npc = 23;
        }
    }

    public static class Tag extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getMapId() != 800020400) {
                c.getPlayer().print(5, "You may not use command outside of the Tag Battle Map");
                return 0;
            }
            MapleMap map = c.getPlayer().getMap();
            List<MapleMapObject> players = map.getMapObjectsInRange(c.getPlayer().getPosition(), (double) 7000, Arrays.asList(MapleMapObjectType.PLAYER));
            for (MapleMapObject closeplayers : players) {
                MapleCharacter playernear = (MapleCharacter) closeplayers;
                if (playernear.isAlive() && playernear != c.getPlayer() && !playernear.isGM()) {
                    if (playernear.isAlive() && playernear != c.getPlayer() && !playernear.isGM()) {
                        playernear.updateSingleStat(MapleStat.HP, 0);
                        playernear.print(5, "You have been tagged by " + c.getPlayer().getName());
                        MapleMap target = c.getChannelServer().getMapFactory().getMap(910000000);
                        playernear.changeMap(target);
                    }
                }
            }
            return 0;
        }
    }

    public static class save extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().saveToDB(false, false);
            c.getPlayer().print(6, "[Notice] Your progress has been saved.");
            return 1;
        }
    }

    public static class smega extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setSmega();
            return 1;
        }
    }

    public static class ranking extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 4) { // Job start end
                c.getPlayer().print(5, "[Syntax] @ranking [job] [start number] [end number]");
                final StringBuilder builder = new StringBuilder("Job: ");
                for (String b : RankingWorker.getJobCommands().keySet()) {
                    builder.append(b);
                    builder.append(" ");
                }
                c.getPlayer().print(5, builder.toString());
            } else {
                int start = 1, end = 10;
                try {
                    start = Integer.parseInt(splitted[2]);
                    end = Integer.parseInt(splitted[3]);
                } catch (NumberFormatException e) {
                    c.getPlayer().print(5, "You didn't specify the start and end number correctly, the default values of 1 and 10 will be used.");
                }
                if (end < start || end - start > 10) {
                    c.getPlayer().print(5, "End number must be greater, and end number must be within a range of 10 from the start number.");
                } else {
                    final Integer job = RankingWorker.getJobCommand(splitted[1]);
                    if (job == null) {
                        c.getPlayer().print(5, "Please use @ranking to check the job name lists.");
                    } else {
                        final List<RankingInformation> ranks = RankingWorker.getRankingInfo(job.intValue());
                        if (ranks == null || ranks.size() <= 0) {
                            c.getPlayer().print(5, "Please try again later.");
                        } else {
                            int num = 0;
                            for (RankingInformation rank : ranks) {
                                if (rank.rank >= start && rank.rank <= end) {
                                    if (num == 0) {
                                        c.getPlayer().print(6, "Rankings For " + splitted[1] + " - From " + start + " To " + end);
                                        c.getPlayer().print(6, "--------------------------------------");
                                    }
                                    c.getPlayer().print(6, rank.toString());
                                    num++;
                                }
                            }
                            if (num == 0) {
                                c.getPlayer().print(5, "No ranking was returned.");
                            }
                        }
                    }
                }
            }
            return 1;
        }
    }


    public static class check extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            String type;

            if (splitted.length < 2) {
                type = c.getPlayer().getName().toString();
            } else {
                type = splitted[1];
            }

            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(type);
            if (victim != null) {
                c.getPlayer().print(5, victim.getName().toString() + " Stats Are :");
                c.getPlayer().print(5, "Level: " + victim.getLevel());
                c.getPlayer().print(6, "Reborns: " + c.getPlayer().getReborns() + "");
                c.getPlayer().print(5, "Fames: " + victim.getFame() + " || Jumping Quest Points: " + victim.getJQPoints());
                c.getPlayer().print(5, "STR: " + victim.getStat().getStr() + " || DEX: " + victim.getStat().getDex() + " || INT: " + victim.getStat().getInt() + " || LUK: " + victim.getStat().getLuk());
                c.getPlayer().print(5, "Mesos: " + victim.getMeso() + " || Mesos In Bank: " + victim.getMesosInBank());
                c.getPlayer().print(5, "Donor Points: " + victim.getPoints());
                return 1;
            } else {
                c.getPlayer().print(5, type + " does not exist, please make sure that the character is in the same channel as you.");
                return 0;
            }
        }
    }

    public static class saveall extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                mch.saveToDB(false, false);
            }
            c.getPlayer().print(6, "All characters data has been saved!");
            Logger.println(c.getPlayer().getName().toString() + " used @saveall to save all characters.");
            return 1;
        }
    }

    public static class help extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(6, ".::::::::::::::::::::::::::::::: Project Nexus Player Commands ::::::::::::::::::::::::::::::::.");
            c.getPlayer().print(6, "@str / dex / int / luk [amount] - Adds the amount of stats to STR / DEX / INT / LUK.");
            c.getPlayer().print(6, "@check - Checks your information.");
            c.getPlayer().print(6, "@check [name] - Checks the character information that you requested.");
            c.getPlayer().print(6, "@monster - Displays the information on the closest monster for you.");
            c.getPlayer().print(6, "@monsterdrops - Checks the monster drop information that is closest to you that you requested.");
            c.getPlayer().print(6, "@cleardrops - Clears the drops on your current map.");
            c.getPlayer().print(6, "@apbank - Shows more information about the ability points bank.");
            c.getPlayer().print(6, "@online - Shows the online amount and connected characters.");
            c.getPlayer().print(6, "@uptime - Checks Project Nexus Uptime.");
            c.getPlayer().print(6, "@dispose - This helps to unstuck your character.");
            c.getPlayer().print(6, "@codex - Fixes the annoying codex in new tab. [Use the cards]");
            c.getPlayer().print(6, "@resetexp - Reset your exp to 0. [Use it only when necessary]");
            c.getPlayer().print(6, "@expfix - Fix your exp when it is stucked. [Use it only when necessary]");
            c.getPlayer().print(6, "@cancelbuff - Cancels your current active skills.");
            c.getPlayer().print(6, "@leet - Toggles leet chat to on / off mode for 500 mesos each time.");
            c.getPlayer().print(6, "@message [message] - Messages a Project Nexus staff with your enquires.");
            c.getPlayer().print(6, "@chalk [message] - Displays text on a chalkboard that you input. [40 Characters Maximum]");
            c.getPlayer().print(6, "@smega - Toggles super megaphone to on / off mode.");
            c.getPlayer().print(6, "@event - Join an event if there's one.");
            c.getPlayer().print(6, "@race - Join an race event if there's one.");
            c.getPlayer().print(6, "@eventrules - Check the rules of the event.");
            c.getPlayer().print(6, "@rebornhelp - View the reborn commands.");
            c.getPlayer().print(6, "@go [map] - Go to the map that you requested.");
            c.getPlayer().print(6, "@npc - Shows the lists of NPC that are available while using command.");
            c.getPlayer().print(6, "@town - Shows the list of Town maps that are available.");
            c.getPlayer().print(6, "@mob - Shows the list of Monster maps that are available.");
            c.getPlayer().print(6, "@pq - Shows the list of Party Quest maps that are available.");
            c.getPlayer().print(6, "@boss - Shows the list of Boss maps that are available.");
            c.getPlayer().print(6, "@misc - Shows the list of miscellaneous maps that are available.");
            // c.getPlayer().print(6, "@hardcore - You get 2x exp in exchange for your character being deleted when you die.");

            return 1;
        }
    }
    /* public static class hardcore extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setHardcore(1);
            return 1;
        }
    }*/

    public static class apbank extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(5, "Ability Point Bank System");
            c.getPlayer().print(5, "The ability point bank system acts as a bank for your ability points. You are able to deposit and withdraw ability points from your remaining ability points into the ability points bank and vice versa.");
            c.getPlayer().print(5, "Once you reached the ability points capacity of 32767, you can simply deposit into your ability points bank and continue with your training.");
            c.getPlayer().print(5, "If you would like to use the ability points, you can withdraw the ability points from your ability points bank.");
            c.getPlayer().print(5, "Note: The ability points bank can hold up to " + Integer.MAX_VALUE + " ability points.");
            c.getPlayer().print(5, "@depositapbank - Deposits the desired value from your remaining ability points into your ability points bank.");
            c.getPlayer().print(5, "@withdrawapbank - Withdraws the desired value from your ability points bank into your remaining ability points.");
            return 1;
        }
    }
    
     /* public static class light extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            short level = c.getPlayer().getLevel();
            int mesos = c.getPlayer().getMeso();
            int price = 1000000;
            if(level > 10 && mesos > 999999) {
               c.getPlayer().setLight();
                c.getPlayer().gainMeso(-price, true);
                return 1;
            }
                c.getPlayer().print(5, "Please make sure you are above level 10 and have 1,000,000 mesos to join the light side!");
                return 0;
            }
        }
        public static class dark extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            short level = c.getPlayer().getLevel();
            int mesos = c.getPlayer().getMeso();
            int price = 1000000;
            if(level > 10 && mesos > 999999) {
                c.getPlayer().setDark();
                c.getPlayer().gainMeso(-price, true);
                return 1;
            } 
                c.getPlayer().print(5, "Please make sure you are above level 10 and have 1,000,000 mesos to join the dark side!");
                return 0;
            }
        }*/

    /**
     * public static class side extends CommandExecute {
     *
     * @Override public int execute(MapleClient c, String[] splitted) {
     * String dd = "Side System";
     * String ee = "In Project Nexus, we offer the ability to pick a side, light or dark to players.";
     * String ff = "Each of these sides offer players different benefits.";
     * String gg = "Dark Side: Coming soon.";
     * String hh = "Light Side: Coming soon.";
     * String vv = "It will cost you 2,000,000 mesos per change and you must be above level 10.";
     * String ii = "Use @light to pick the light side.";
     * String jj = "Use @dark to pick the dark side.";
     * c.getPlayer().print(5, dd);
     * c.getPlayer().print(5, ee);
     * c.getPlayer().print(5, ff);
     * c.getPlayer().print(5, gg);
     * c.getPlayer().print(5, hh);
     * c.getPlayer().print(5, vv);
     * c.getPlayer().print(5, ii);
     * c.getPlayer().print(5, jj);
     * return 1;
     * }
     * }
     */
    public static class TradeHelp extends TradeExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(-2, "[System] @offerequip / @offeruse / @offersetup / @offeretc / @offercash [quantity] [name of the item]");
            return 1;
        }
    }

    public abstract static class OfferCommand extends TradeExecute {

        protected int invType = -1;

        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().print(-2, "[Syntax] [quantity] [name of item]");
            } else if (c.getPlayer().getLevel() < 30) {
                c.getPlayer().print(-2, "[Error] : Only players that are greater than level 30 may use this command.");
            } else {
                int quantity = 1;
                try {
                    quantity = Integer.parseInt(splitted[1]);
                } catch (Exception e) { // Swallow and just use 1
                }
                String search = StringUtil.joinStringFrom(splitted, 2).toLowerCase();
                Item found = null;
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                for (Item inv : c.getPlayer().getInventory(MapleInventoryType.getByType((byte) invType))) {
                    if (ii.getName(inv.getItemId()) != null && ii.getName(inv.getItemId()).toLowerCase().contains(search)) {
                        found = inv;
                        break;
                    }
                }
                if (found == null) {
                    c.getPlayer().print(-2, "[Error] No such item was found (" + search + ")");
                    return 0;
                }
                if (GameConstants.isPet(found.getItemId()) || GameConstants.isRechargable(found.getItemId())) {
                    c.getPlayer().print(-2, "[Error] You may not trade this item while using this command.");
                    return 0;
                }
                if (quantity > found.getQuantity() || quantity <= 0 || quantity > ii.getSlotMax(found.getItemId())) {
                    c.getPlayer().print(-2, "[Error] Invalid quantity.");
                    return 0;
                }
                if (!c.getPlayer().getTrade().setItems(c, found, (byte) -1, quantity)) {
                    c.getPlayer().print(-2, "[Error] This item could not be placed.");
                    return 0;
                } else {
                    c.getPlayer().getTrade().chatAuto("[System] : " + c.getPlayer().getName() + " offered " + ii.getName(found.getItemId()) + " x " + quantity);
                }
            }
            return 1;
        }
    }

    public static class OfferEquip extends OfferCommand {

        public OfferEquip() {
            invType = 1;
        }
    }

    public static class OfferUse extends OfferCommand {

        public OfferUse() {
            invType = 2;
        }
    }

    public static class OfferSetup extends OfferCommand {

        public OfferSetup() {
            invType = 3;
        }
    }

    public static class OfferEtc extends OfferCommand {

        public OfferEtc() {
            invType = 4;
        }
    }

    public static class OfferCash extends OfferCommand {

        public OfferCash() {
            invType = 5;
        }
    }

    public static class event extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                c.getPlayer().print(5, "You may not use this command while in the jail.");
                return 0;
            }
            if (c.getPlayer().getClient().getChannelServer().eventOn == false) {
                c.getPlayer().print(6, "There is no event available currently.");
            } else {
                MapleMap EventMap = c.getChannelServer().getMapFactory().getMap(c.getPlayer().getClient().getChannelServer().eventMap);
                MaplePortal EventPortal = EventMap.getPortal(0);
                c.getPlayer().changeMap(EventMap, EventPortal);
                c.getPlayer().print(6, "Welcome to the event! Please wait for further instruction from a Staff.");
            }
            return 1;
        }
    }

    public static class message extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                c.getPlayer().print(5, "You may not use this command while in the jail.");
                return 0;
            }
            if (splitted[1].length() == 0) {
                c.getPlayer().print(6, "Use words, silly.");
            }
            if (!c.getPlayer().getCheatTracker().GMSpam(60000, 3)) { // 1 Minute.
                World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, "Channel " + c.getPlayer().getClient().getChannel() + " //Player " + c.getPlayer().getName() + ": " + StringUtil.joinStringFrom(splitted, 1)));
                c.getPlayer().print(5, "Message has been sent");
            } else {
                c.getPlayer().print(6, "Please wait for another minute to use the @message command again.");
            }
            return 1;
        }
    }

    public static class race extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getEntryNumber() < 1) {
                if (c.getPlayer().getMapId() == 100000000) {
                    if (c.getChannelServer().getWaiting() || c.getPlayer().isGM()) { // TODO: Test
                        c.getPlayer().setEntryNumber(c.getChannelServer().getCompetitors() + 1);
                        c.getChannelServer().setCompetitors(c.getChannelServer().getCompetitors() + 1);
                        SkillFactory.getSkill(c.getPlayer().getGender() == 1 ? 80001006 : 80001005).getEffect(1).applyTo(c.getPlayer());
                        c.getPlayer().print(0, "You have successfully joined the race! Your entry number is " + c.getPlayer().getEntryNumber() + ".");
                        c.getPlayer().print(1, "If you cancel the mount buff, you will automatically leave the race.");
                    } else {
                        c.getPlayer().print(0, "There is no event currently taking place.");
                        return 0;
                    }
                } else {
                    c.getPlayer().print(0, "You are not at Henesys.");
                    return 0;
                }
            } else {
                c.getPlayer().print(0, "You have already joined this race.");
                return 0;
            }
            return 1;
        }
    }

    public static class eventrules extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getChannelServer().getWaiting() || c.getChannelServer().getRace()) {
                c.getPlayer().print(0, "The Official Rules And Regulations Of The Great Victoria Island Race :");
                c.getPlayer().print(0, "-------------------------------------------------------------------------------------------");
                c.getPlayer().print(0, "To win, you must race from Henesys all the way to Henesys going Eastward.");
                c.getPlayer().print(0, "Rule #1: No cheating. You can't use any warping commands, or you'll be disqualified.");
                c.getPlayer().print(0, "Rule #2: You may use any form of transportation. This includes Teleport, Flash Jump and Mounts.");
                c.getPlayer().print(0, "Rule #3: You are not allowed to kill any monsters in your way. They are obstacles.");
                c.getPlayer().print(0, "Rule #4: You may start from anywhere in Henesys, but moving on to the next map before the start won't work.");
            } else {
                c.getPlayer().print(0, "There is no event currently taking place.");
                return 0;
            }
            return 1;
        }
    }

    public static class online extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            java.util.Map<Integer, Integer> connected = World.getConnected();
            StringBuilder conStr = new StringBuilder("Connected Clients: \r\n\r\n");
            boolean first = true;
            for (int i : connected.keySet()) {
                if (!first) {
                    conStr.append(", ");
                } else {
                    first = false;
                }
                if (i == 0) {
                    conStr.append("Total: \r\n");
                    conStr.append(connected.get(i));
                } else {
                    conStr.append("Channel \r\n");
                    conStr.append(i);
                    conStr.append(": ");
                    conStr.append(connected.get(i));
                }
            }
            c.getPlayer().print(6, conStr.toString());
            return 1;
        }
    }

    public static class Reborn extends CommandExecute {
        protected int job = 0;

        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (player.getLevel() >= 200) {
                player.updateReborns();
                player.changeJob((short) job);
                player.setLevel((short) 1);// Level 1 16
                player.setExp(0);
                player.updateSingleStat(MapleStat.LEVEL, 1);
                player.updateSingleStat(MapleStat.EXP, 0);
                player.print(5, "You now have " + player.getReborns() + " reborns!");
            } else {
                player.print(5, "You need to be at least level 200 to reborn.");
            }
            return 0;
        }
    }

    public static class RebornCygnus extends Reborn {
        public RebornCygnus() {
            job = 1000;
        }
    }

    public static class RebornDualblade extends Reborn {
        public RebornDualblade() {
            job = 430;
        }
    }

    public static class RebornPhantom extends Reborn {
        public RebornPhantom() {
            job = 2400;
        }
    }

    public static class RebornJett extends Reborn {
        public RebornJett() {
            job = 508;
        }
    }

    public static class RebornDemonSlayer extends Reborn {
        public RebornDemonSlayer() {
            job = 3100;
        }
    }

    public static class RebornMercedes extends Reborn {
        public RebornMercedes() {
            job = 2300;
        }
    }

    public static class RebornAran extends Reborn {
        public RebornAran() {
            job = 2100;
        }
    }

    public static class RebornCannoneer extends Reborn {
        public RebornCannoneer() {
            job = 501;
        }
    }

    public static class RebornBattleMage extends Reborn {
        public RebornBattleMage() {
            job = 3200;
        }
    }

    public static class RebornWildHunter extends Reborn {
        public RebornWildHunter() {
            job = 3300;
        }
    }

    public static class RebornMechanic extends Reborn {
        public RebornMechanic() {
            job = 3500;
        }
    }

    public static class RebornMihile extends Reborn {
        public RebornMihile() {
            job = 5100;
        }
    }

    public static class CancelBuffs extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().cancelAllBuffs();
            return 1;
        }
    }

    public static class Uptime extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(6, "Project Nexus has been up for " + StringUtil.getReadableMillis(ChannelServer.serverStartTime, System.currentTimeMillis()));
            return 1;
        }
    }

    public static class sell extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 3 || player.hasBlockedInventory()) {
                c.getPlayer().print(5, "@sell [equip / use / setup / etc] [starting slot] [ending slot]");
                return 0;
            } else {
                MapleInventoryType type;
                if (splitted[1].equalsIgnoreCase("equip")) {
                    type = MapleInventoryType.EQUIP;
                } else if (splitted[1].equalsIgnoreCase("use")) {
                    type = MapleInventoryType.USE;
                } else if (splitted[1].equalsIgnoreCase("setup")) {
                    type = MapleInventoryType.SETUP;
                } else if (splitted[1].equalsIgnoreCase("etc")) {
                    type = MapleInventoryType.ETC;
                } else {
                    c.getPlayer().print(5, "Invalid Format. Use @sell [equip / use / setup / etc> <starting slot] [ending slot]");
                    return 0;
                }
                MapleInventory inv = c.getPlayer().getInventory(type);
                byte start = Byte.parseByte(splitted[2]);
                byte end = Byte.parseByte(splitted[3]);
                int totalMesosGained = 0;
                for (byte i = start; i <= end; i++) {
                    if (inv.getItem(i) != null) {
                        MapleItemInformationProvider iii = MapleItemInformationProvider.getInstance();
                        int itemPrice = (int) iii.getPrice(inv.getItem(i).getItemId());
                        totalMesosGained += itemPrice;
                        player.gainMeso(itemPrice < 0 ? 0 : itemPrice, true);
                        MapleInventoryManipulator.removeFromSlot(c, type, i, inv.getItem(i).getQuantity(), true);
                    }
                }
                c.getPlayer().print(5, "You have sold your inventory item from slots " + start + " to " + end + ", and gained " + totalMesosGained + " mesos.");
            }
            return 1;
        }
    }
}