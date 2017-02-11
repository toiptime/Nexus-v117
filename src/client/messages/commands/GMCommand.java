/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.messages.commands;

import client.*;
import client.inventory.*;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import handling.channel.ChannelServer;
import handling.world.World;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleShopFactory;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.StringUtil;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.InventoryPacket;

import java.util.Arrays;
import java.util.List;

/**
 * @author Emilyx3
 */
public class GMCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.GM;
    }

    public static class warn extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().print(0, "[Syntax] !warning [name] [reason]");
                return 0;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            String reason = StringUtil.joinStringFrom(splitted, 2);
            //victim.giveWarning(victim, reason);
            //victim.getMap().startMapEffect(victim.getName() + " has been warned for " + reason + ". Warnings: " + victim.getWarnings(victim) + "/3", 5120041);
            victim.getMap().startMapEffect(victim.getName() + " has been warned for " + reason + ".", 5120041);
            return 1;
        }
    }

    public static class warpoxtop extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(6, "Top Warped Out.");
            for (MapleMapObject wrappedPerson : c.getPlayer().getMap().getCharactersAsMapObjects()) {
                MapleCharacter person = (MapleCharacter) wrappedPerson;
                if (person.getPosition().y <= -206 && !person.isGM()) {
                    person.changeMap(person.getMap().getReturnMap(), person.getMap().getReturnMap().getPortal(0));
                    person.changeMap(910000000, 0);
                }
            }
            return 1;
        }
    }

    public static class warpoxleft extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(6, "Left Warped Out.");
            for (MapleMapObject wrappedPerson : c.getPlayer().getMap().getCharactersAsMapObjects()) {
                MapleCharacter person = (MapleCharacter) wrappedPerson;
                if (person.getPosition().y > -206 && person.getPosition().y <= 334 && person.getPosition().x >= -952 && person.getPosition().x <= -308 && !person.isGM()) {
                    //person.changeMap(person.getMap().getReturnMap(), person.getMap().getReturnMap().getPortal(0));
                    person.changeMap(910000000, 0);
                }
            }
            return 1;
        }
    }

    public static class warpox extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getMap().getId() == 109020001) {
                MapleMap map1 = c.getPlayer().getMap();
                List<MapleMapObject> players1 = map1.getMapObjectsInRange(c.getPlayer().getPosition(), (double) 1000, Arrays.asList(MapleMapObjectType.PLAYER));

                for (MapleMapObject closeplayers : players1) {
                    MapleCharacter playernear = (MapleCharacter) closeplayers;
                    if (playernear.isGM() && playernear.isAlive()) ;
                    else {
                        if (splitted[1].equalsIgnoreCase("hene")) {
                            playernear.changeMap(c.getChannelServer().getMapFactory().getMap(Integer.valueOf(100000000)));
                        } else {
                            playernear.changeMap(c.getChannelServer().getMapFactory().getMap(Integer.valueOf(splitted[1])));
                        }
                        playernear.print(5, "You have lost the event, you will be warped out.");
                    }
                }
            } else {
                c.getPlayer().print(5, "You are not in the OX event map!");
            }
            return 1;
        }
    }

    public static class warpoxright extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(6, "Right Warped Out.");
            for (MapleMapObject wrappedPerson : c.getPlayer().getMap().getCharactersAsMapObjects()) {
                MapleCharacter person = (MapleCharacter) wrappedPerson;
                if (person.getPosition().y > -206 && person.getPosition().y <= 334 && person.getPosition().x >= -142 && person.getPosition().x <= 502 && !person.isGM()) {
                    //person.changeMap(person.getMap().getReturnMap(), person.getMap().getReturnMap().getPortal(0));
                    person.changeMap(910000000, 0);
                }
            }
            return 1;
        }
    }

    public static class warpoxmiddle extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(6, "Middle Warped Out.");
            for (MapleMapObject wrappedPerson : c.getPlayer().getMap().getCharactersAsMapObjects()) {
                MapleCharacter person = (MapleCharacter) wrappedPerson;
                if (person.getPosition().y > -206 && person.getPosition().y <= 274 && person.getPosition().x >= -308 && person.getPosition().x <= -142 && !person.isGM()) {
                    // person.changeMap(person.getMap().getReturnMap(), person.getMap().getReturnMap().getPortal(0));
                    person.changeMap(910000000, 0);
                }
            }
            return 1;
        }
    }

    /* public static class Bombmap extends CommandExecute {

     @Override
     public int execute(MapleClient c, String[] splitted) {
     for (MapleCharacter chr : c.getPlayer().getMap().getCharacters()) {
     for (int i = 0; i < 250; i += 50) {
     c.getPlayer().getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(9300166), new Point(chr.getPosition().x - i, chr.getPosition().y));
     c.getPlayer().getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(9300166), new Point(chr.getPosition().x + i, chr.getPosition().y));
     }
     }
     c.getPlayer().print(5, "Planted bombs around the map");
     return 0;

     }
     }

     public static class Bomb extends CommandExecute {

     @Override
     public int execute(MapleClient c, String[] splitted) {
     int bomb;
     if (splitted.length < 2) {
     bomb = 1;
     } else {
     bomb = Integer.parseInt(splitted[1]);
     }
     for (int i = 0; i < bomb; i++) {
     c.getPlayer().getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(9300166), c.getPlayer().getPosition());
     }
     c.getPlayer().print(6, "Planted " + bomb + " bomb(s)");
     return 1;
     }
     }*/
    public static class ChatType extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getChatType()) {
                c.getPlayer().setChatType(true);
            } else {
                c.getPlayer().setChatType(false);
            }
            c.getPlayer().print(0, c.getPlayer().getChatType() ? "Text color is White now." : "Text color is Black now.");
            return 1;
        }
    }

    public static class Emotion extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (splitted.length < 3) {
                c.getPlayer().print(0, splitted[0] + " [victim] [emote] [duration]");
                return 0;
            }
            victim.getMap().broadcastMessage(victim, CField.facialExpression2(Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3])), false);
            return 1;
        }
    }

    public static class givepet extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 6) {
                c.getPlayer().print(0, splitted[0] + " [character name] [petid] [petname] [petlevel] [petcloseness][petfullness]");
                return 0;
            }
            MapleCharacter petowner = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            int id = Integer.parseInt(splitted[2]);
            String name = splitted[3];
            int level = Integer.parseInt(splitted[4]);
            int closeness = Integer.parseInt(splitted[5]);
            int fullness = Integer.parseInt(splitted[6]);
            long period = 20000;
            short flags = 0;
            if (id >= 5001000 || id < 5000000) {
                c.getPlayer().print(0, "Invalid Pet ID.");
                return 0;
            }
            if (level > 30) {
                level = 30;
            }
            if (closeness > 30000) {
                closeness = 30000;
            }
            if (fullness > 100) {
                fullness = 100;
            }
            if (level < 1) {
                level = 1;
            }
            if (closeness < 0) {
                closeness = 0;
            }
            if (fullness < 0) {
                fullness = 0;
            }
            try {
                MapleInventoryManipulator.addById(petowner.getClient(), id, (short) 1, "", MaplePet.createPet(id, name, level, closeness, fullness, MapleInventoryIdentifier.getInstance(), id == 5000054 ? (int) period : 0, flags), 45, null);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
            return 1;
        }
    }

    public static class OpenNpc extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, Integer.parseInt(splitted[1]), splitted.length > 2 ? StringUtil.joinStringFrom(splitted, 2) : splitted[1]);
            return 1;
        }
    }

    public static class OpenShop extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleShopFactory.getInstance().getShop(Integer.parseInt(splitted[1]));
            return 1;
        }
    }

    public static class ClearDrops extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().clearDrops(c.getPlayer(), true);
            return 1;
        }
    }

    public static class GetSkill extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            Skill skill = SkillFactory.getSkill(Integer.parseInt(splitted[1]));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);

            if (level > skill.getMaxLevel()) {
                level = (byte) skill.getMaxLevel();
            }
            if (masterlevel > skill.getMaxLevel()) {
                masterlevel = (byte) skill.getMaxLevel();
            }
            c.getPlayer().changeSingleSkillLevel(skill, level, masterlevel);
            return 1;
        }
    }

    public static class Fame extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                c.getPlayer().print(6, "[Syntax] !fame [name] [amount]");
                return 0;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            int fame = 0;
            try {
                fame = Integer.parseInt(splitted[2]);
            } catch (NumberFormatException nfe) {
                c.getPlayer().print(6, "Invalid Number...");
                return 0;
            }
            if (victim != null && player.allowedToTarget(victim)) {
                victim.addFame(fame);
                victim.updateSingleStat(MapleStat.FAME, victim.getFame());
            }
            return 1;
        }
    }

    public static class Invincible extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (player.isInvincible()) {
                player.setInvincible(false);
                player.print(6, "Invincibility deactivated.");
            } else {
                player.setInvincible(true);
                player.print(6, "Invincibility activated.");
            }
            return 1;
        }
    }

    public static class SP extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setRemainingSp(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLESP, 0); // We don't care the value here
            return 1;
        }
    }

    public static class Job extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int jobid = Integer.parseInt(splitted[1]);
            if (!MapleJob.isExist(jobid)) {
                c.getPlayer().print(5, "Invalid Job");
                return 0;
            }
            c.getPlayer().changeJob((short) jobid);
            c.getPlayer().setSubcategory(c.getPlayer().getSubcategory());
            return 1;
        }
    }

    /*public static class JobPerson extends CommandExecute {

     @Override
     public int execute(MapleClient c, String[] splitted) {
     MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
     if (!MapleJob.isExist(Integer.parseInt(splitted[2]))) {
     c.getPlayer().print(5, "Invalid Job");
     return 0;
     }
     victim.changeJob((short) Integer.parseInt(splitted[2]));
     c.getPlayer().setSubcategory(c.getPlayer().getSubcategory());
     return 1;
     }
     }*/
    public static class Shop extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleShopFactory shop = MapleShopFactory.getInstance();
            int shopId = Integer.parseInt(splitted[1]);
            if (shop.getShop(shopId) != null) {
                shop.getShop(shopId).sendShop(c);
            }
            return 1;
        }
    }

    public static class LevelUp extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().gainExp(GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) - c.getPlayer().getExp(), true, false, true);
            return 1;
        }
    }

    public static class LevelUpTill extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            //for (int i = 0; i < Integer.parseInt(splitted[1]) - c.getPlayer().getLevel(); i++) {
            while (c.getPlayer().getLevel() < Integer.parseInt(splitted[1])) {
                if (c.getPlayer().getLevel() < 255) {
                    c.getPlayer().levelUp();
                }
            }
            //}
            return 1;
        }
    }

    /*public static class LevelUpPersonTill extends CommandExecute {

     @Override
     public int execute(MapleClient c, String[] splitted) {
     MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
     //for (int i = 0; i < Integer.parseInt(splitted[2]) - victim.getLevel(); i++) {
     while (victim.getLevel() < Integer.parseInt(splitted[2])) {
     if (victim.getLevel() < 255) {
     victim.levelUp();
     }
     }
     //}
     return 1;
     }
     }*/
    public static class ITEM extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final int itemId = Integer.parseInt(splitted[1]);
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);

            if (!c.getPlayer().isAdmin()) {
                for (int i : GameConstants.itemBlock) {
                    if (itemId == i) {
                        c.getPlayer().print(5, "Sorry but this command is blocked for your Game Master level.");
                        return 0;
                    }
                }
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (!ii.itemExists(itemId)) {
                c.getPlayer().print(5, itemId + " does not exist");
            } else {
                Item item;
                short flag = (short) ItemFlag.LOCK.getValue();

                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    item = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                } else {
                    item = new client.inventory.Item(itemId, (byte) 0, quantity, (byte) 0);

                }
                //if (!c.getPlayer().isSuperGM()) {
                //    item.setFlag(flag);
                //}
                if (!c.getPlayer().isAdmin()) {
                    item.setGMLog(c.getPlayer().getName() + " used !getitem");
                    item.setOwner(c.getPlayer().getName());
                }
                MapleInventoryManipulator.addbyItem(c, item);
            }
            return 1;
        }
    }

    public static class character extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final StringBuilder builder = new StringBuilder();
            final MapleCharacter other = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (other == null) {
                builder.append(splitted[1] + " does not exist");
                c.getPlayer().print(6, builder.toString());
                return 0;
            }
            if (other.getClient().getLastPing() <= 0) {
                other.getClient().sendPing();
            }
            builder.append(MapleClient.getLogMessage(other, ""));
            builder.append(" X, Y (").append(other.getPosition().x);
            builder.append(", ").append(other.getPosition().y);
            builder.append(")");

            builder.append("\r\n HP: ");
            builder.append(other.getStat().getHp());
            builder.append(" /");
            builder.append(other.getStat().getCurrentMaxHp());

            builder.append(" || MP: ");
            builder.append(other.getStat().getMp());
            builder.append(" /");
            builder.append(other.getStat().getCurrentMaxMp(other.getJob()));

            builder.append(" || Battleship HP: ");
            builder.append(other.currentBattleshipHP());

            builder.append("\r\n || WATK: ");
            builder.append(other.getStat().getTotalWatk());
            builder.append(" || MATK: ");
            builder.append(other.getStat().getTotalMagic());
            builder.append(" || MAX DAMAGE : ");
            builder.append(other.getStat().getCurrentMaxBaseDamage());
            builder.append(" || DAMAGE%: ");
            builder.append(other.getStat().dam_r);
            builder.append(" || BOSS DAMAGE%: ");
            builder.append(other.getStat().bossdam_r);
            builder.append(" || CRIT CHANCE: ");
            builder.append(other.getStat().passive_sharpeye_rate());
            builder.append(" || CRIT DAMAGE: ");
            builder.append(other.getStat().passive_sharpeye_percent());

            builder.append("\r\n STR: ");
            builder.append(other.getStat().getStr()).append(" + (").append(other.getStat().getTotalStr() - other.getStat().getStr()).append(")");
            builder.append(" || DEX: ");
            builder.append(other.getStat().getDex()).append(" + (").append(other.getStat().getTotalDex() - other.getStat().getDex()).append(")");
            builder.append(" || INT: ");
            builder.append(other.getStat().getInt()).append(" + (").append(other.getStat().getTotalInt() - other.getStat().getInt()).append(")");
            builder.append(" || LUK: ");
            builder.append(other.getStat().getLuk()).append(" + (").append(other.getStat().getTotalLuk() - other.getStat().getLuk()).append(")");

            builder.append("\r\n EXP: ");
            builder.append(other.getExp());
            builder.append(" || MESOS: ");
            builder.append(other.getMeso());

            builder.append("\r\n || Party: ");
            builder.append(other.getParty() == null ? -1 : other.getParty().getId());

            builder.append(" || Has Trade: ");
            builder.append(other.getTrade() != null);
            builder.append(" || Latency: ");
            builder.append(other.getClient().getLatency());
            builder.append(" || PING: ");
            builder.append(other.getClient().getLastPing());
            builder.append(" || PONG: ");
            builder.append(other.getClient().getLastPong());
            other.getClient().DebugMessage(builder);
            builder.append("\r\n Remote Address: " + other.getClient().getSessionIPAddress());
            c.getPlayer().print(6, builder.toString());
            return 1;
        }
    }

    public static class PotentialItem extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final int itemId = Integer.parseInt(splitted[1]);
            if (!c.getPlayer().isAdmin()) {
                for (int i : GameConstants.itemBlock) {
                    if (itemId == i) {
                        c.getPlayer().print(5, "Sorry but this command is blocked for your Game Master level.");
                        return 0;
                    }
                }
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (itemId >= 2000000) {
                c.getPlayer().print(5, "You can only get equips.");
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().print(5, itemId + " does not exist.");
            } else {
                Equip equip;
                equip = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                equip.setPotential1(Integer.parseInt(splitted[2]));
                equip.setPotential2(Integer.parseInt(splitted[3]));
                equip.setPotential3(Integer.parseInt(splitted[4]));
                equip.setPotential4(Integer.parseInt(splitted[5]));
                equip.setPotential5(Integer.parseInt(splitted[6]));
                equip.setOwner(c.getPlayer().getName());
                MapleInventoryManipulator.addbyItem(c, equip);
            }
            return 1;
        }
    }

    public static class Level extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setLevel(Short.parseShort(splitted[1]));
            c.getPlayer().updateSingleStat(MapleStat.LEVEL, Integer.parseInt(splitted[1]));
            if (c.getPlayer().getExp() < 0) {
                c.getPlayer().gainExp(-c.getPlayer().getExp(), false, false, true);
            }
            return 1;
        }
    }

    /*public static class LevelPerson extends CommandExecute {

     @Override
     public int execute(MapleClient c, String[] splitted) {
     MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
     victim.setLevel(Short.parseShort(splitted[2]));
     victim.updateSingleStat(MapleStat.LEVEL, Integer.parseInt(splitted[2]));
     if (victim.getExp() < 0) {
     victim.gainExp(-c.getPlayer().getExp(), false, false, true);
     }
     return 1;
     }
     }*/
    public static class Event extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getClient().getChannelServer().eventOn == false) {
                int mapid = c.getPlayer().getMapId();
                c.getPlayer().getClient().getChannelServer().eventOn = true;
                c.getPlayer().getClient().getChannelServer().eventMap = mapid;
                try {
                    World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[Event] " + c.getPlayer().getName() + " has started an Event in Channel " + c.getChannel() + ". Please use the command @event to participate!"));
                } catch (Exception ex) {

                }
            } else {
                c.getPlayer().getClient().getChannelServer().eventOn = false;
                try {
                    World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[Event] Entry to the event has been closed."));
                } catch (Exception e) {
                }

            }
            return 1;

        }
    }

    public static class RemoveItem extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().print(6, "[Syntax] !removeitem [name] [itemid]");
                return 0;
            }
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chr == null) {
                c.getPlayer().print(6, "This player does not exist");
                return 0;
            }
            chr.removeAll(Integer.parseInt(splitted[2]), false);
            c.getPlayer().print(6, "All items with the ID " + splitted[2] + " has been removed from the inventory of " + splitted[1] + ".");
            return 1;

        }
    }

    public static class LockItem extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().print(6, "[Syntax] !lockitem [name] [itemid]");
                return 0;
            }
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chr == null) {
                c.getPlayer().print(6, "This player does not exist");
                return 0;
            }
            int itemid = Integer.parseInt(splitted[2]);
            MapleInventoryType type = GameConstants.getInventoryType(itemid);
            for (Item item : chr.getInventory(type).listById(itemid)) {
                item.setFlag((byte) (item.getFlag() | ItemFlag.LOCK.getValue()));
                chr.getClient().getSession().write(InventoryPacket.updateSpecialItemUse(item, type.getType(), item.getPosition(), true, chr));
            }
            if (type == MapleInventoryType.EQUIP) {
                type = MapleInventoryType.EQUIPPED;
                for (Item item : chr.getInventory(type).listById(itemid)) {
                    item.setFlag((byte) (item.getFlag() | ItemFlag.LOCK.getValue()));
                    //chr.getClient().getSession().write(CField.updateSpecialItemUse(item, type.getType()));
                }
            }
            c.getPlayer().print(6, "All items with the ID " + splitted[2] + " has been locked from the inventory of " + splitted[1] + ".");
            return 1;
        }
    }

    public static class KillMap extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter map : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (map != null && !map.isGM()) {
                    map.getStat().setHp((short) 0, map);
                    map.getStat().setMp((short) 0, map);
                    map.updateSingleStat(MapleStat.HP, 0);
                    map.updateSingleStat(MapleStat.MP, 0);
                }
            }
            return 1;
        }
    }

    public static class Smega extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            World.Broadcast.broadcastSmega(CWvsContext.serverNotice(3, c.getPlayer() == null ? c.getChannel() : c.getPlayer().getClient().getChannel(), c.getPlayer() == null ? c.getPlayer().getName() : c.getPlayer().getName() + " : " + StringUtil.joinStringFrom(splitted, 1), true));
            /*if (splitted.length < 2) {
             c.getPlayer().print(0, "!smega <itemid> <message>");
             return 0;
             }
             final List<String> lines = new LinkedList<>();
             for (int i = 0; i < 4; i++) {
             final String text = StringUtil.joinStringFrom(splitted, 2);
             if (text.length() > 55) {
             continue;
             }
             lines.add(text);
             }
             final boolean ear = true;
             World.Broadcast.broadcastSmega(CWvsContext.getAvatarMega(c.getPlayer(), c.getChannel(), Integer.parseInt(splitted[1]), lines, ear)); */
            return 1;
        }
    }

    public static class SpeakMega extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                c.getPlayer().print(0, "The person isn't login, or doesn't exists.");
                return 0;
            }
            World.Broadcast.broadcastSmega(CWvsContext.serverNotice(3, victim.getClient().getChannel(), victim.getName() + " : " + StringUtil.joinStringFrom(splitted, 2), true));
            /* 
             if (splitted.length < 2) {
             c.getPlayer().print(0, "!smega <itemid> <victim> <message>");
             return 0;
             }
             final List<String> lines = new LinkedList<>();
             for (int i = 0; i < 4; i++) {
             final String text = StringUtil.joinStringFrom(splitted, 3);
             if (text.length() > 55) {
             continue;
             }
             lines.add(text);
             }
             final boolean ear = true;
             World.Broadcast.broadcastSmega(CWvsContext.getAvatarMega(victim, victim.getClient().getChannel(), Integer.parseInt(splitted[1]), lines, ear));
             */
            return 1;
        }
    }

    public static class SpeakAll extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (mch == null) {
                    return 0;
                } else {
                    mch.getMap().broadcastMessage(CField.getChatText(mch.getId(), StringUtil.joinStringFrom(splitted, 1), mch.isGM(), 0));
                }
            }
            return 1;
        }
    }

    public static class Speak extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                c.getPlayer().print(5, "unable to find '" + splitted[1]);
                return 0;
            } else {
                victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 2), victim.isGM(), 0));
            }
            return 1;
        }
    }

    public static class DiseaseMap extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().print(6, "!disease [type] [level] (Type = seal / darkness / weaken / stun / curse / poison / slow / seduce / reverse / zombify / potion / shadow / blind / freeze / potential)");
                return 0;
            }
            int type = 0;
            if (splitted[1].equalsIgnoreCase("SEAL")) {
                type = 120;
            } else if (splitted[1].equalsIgnoreCase("DARKNESS")) {
                type = 121;
            } else if (splitted[1].equalsIgnoreCase("WEAKEN")) {
                type = 122;
            } else if (splitted[1].equalsIgnoreCase("STUN")) {
                type = 123;
            } else if (splitted[1].equalsIgnoreCase("CURSE")) {
                type = 124;
            } else if (splitted[1].equalsIgnoreCase("POISON")) {
                type = 125;
            } else if (splitted[1].equalsIgnoreCase("SLOW")) {
                type = 126;
            } else if (splitted[1].equalsIgnoreCase("SEDUCE")) { // 24 and 29 are cool.
                type = 128;
            } else if (splitted[1].equalsIgnoreCase("REVERSE")) {
                type = 132;
            } else if (splitted[1].equalsIgnoreCase("ZOMBIFY")) {
                type = 133;
            } else if (splitted[1].equalsIgnoreCase("POTION")) {
                type = 134;
            } else if (splitted[1].equalsIgnoreCase("SHADOW")) {
                type = 135;
            } else if (splitted[1].equalsIgnoreCase("BLIND")) {
                type = 136;
            } else if (splitted[1].equalsIgnoreCase("FREEZE")) {
                type = 137;
            } else if (splitted[1].equalsIgnoreCase("POTENTIAL")) {
                type = 138;
            } else {
                c.getPlayer().print(6, "[Syntax] !disease [type] [level] (Type = seal / darkness / weaken / stun / curse / poison / slow / seduce / reverse / zombify / potion / shadow / blind / freeze / potential)");
                return 0;
            }
            for (MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (mch.getMapId() == c.getPlayer().getMapId()) {
                    if (splitted.length == 4) {
                        if (mch == null) {
                            c.getPlayer().print(5, "Not found.");
                            return 0;
                        }
                        mch.disease(type, CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1));
                    } else {
                        mch.disease(type, CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1));
                    }
                }
            }
            return 1;
        }
    }

    public static class Disease extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().print(6, "!disease [type] [name] [level] (Type = seal / darkness / weaken / stun / curse / poison / slow / seduce / reverse / zombify / potion / shadow / blind / freeze / potential)");
                return 0;
            }
            int type = 0;
            if (splitted[1].equalsIgnoreCase("SEAL")) {
                type = 120;
            } else if (splitted[1].equalsIgnoreCase("DARKNESS")) {
                type = 121;
            } else if (splitted[1].equalsIgnoreCase("WEAKEN")) {
                type = 122;
            } else if (splitted[1].equalsIgnoreCase("STUN")) {
                type = 123;
            } else if (splitted[1].equalsIgnoreCase("CURSE")) {
                type = 124;
            } else if (splitted[1].equalsIgnoreCase("POISON")) {
                type = 125;
            } else if (splitted[1].equalsIgnoreCase("SLOW")) {
                type = 126;
            } else if (splitted[1].equalsIgnoreCase("SEDUCE")) {
                type = 128;
            } else if (splitted[1].equalsIgnoreCase("REVERSE")) {
                type = 132;
            } else if (splitted[1].equalsIgnoreCase("ZOMBIFY")) {
                type = 133;
            } else if (splitted[1].equalsIgnoreCase("POTION")) {
                type = 134;
            } else if (splitted[1].equalsIgnoreCase("SHADOW")) {
                type = 135;
            } else if (splitted[1].equalsIgnoreCase("BLIND")) {
                type = 136;
            } else if (splitted[1].equalsIgnoreCase("FREEZE")) {
                type = 137;
            } else if (splitted[1].equalsIgnoreCase("POTENTIAL")) {
                type = 138;
            } else {
                c.getPlayer().print(6, "[Syntax] !disease [type] [name] [level] (Type = seal / darkness / weaken / stun / curse / poison / slow / seduce / reverse / zombify / potion / shadow / blind / freeze / potential)");
                return 0;
            }
            if (splitted.length == 4) {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[2]);
                if (victim == null) {
                    c.getPlayer().print(5, "Not found.");
                    return 0;
                }
                victim.disease(type, CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1));
            } else {
                for (MapleCharacter victim : c.getPlayer().getMap().getCharactersThreadsafe()) {
                    victim.disease(type, CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1));
                }
            }
            return 1;
        }
    }

    public static class CloneMe extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().cloneLook();
            return 1;
        }
    }

    public static class DisposeClones extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(6, c.getPlayer().getCloneSize() + " clones disposed.");
            c.getPlayer().disposeClones();
            return 1;
        }
    }

    public static class SetInstanceProperty extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            EventManager em = c.getChannelServer().getEventSM().getEventManager(splitted[1]);
            if (em == null || em.getInstances().size() <= 0) {
                c.getPlayer().print(5, "None");
            } else {
                em.setProperty(splitted[2], splitted[3]);
                for (EventInstanceManager eim : em.getInstances()) {
                    eim.setProperty(splitted[2], splitted[3]);
                }
            }
            return 1;
        }
    }

    public static class ListInstanceProperty extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            EventManager em = c.getChannelServer().getEventSM().getEventManager(splitted[1]);
            if (em == null || em.getInstances().size() <= 0) {
                c.getPlayer().print(5, "None");
            } else {
                for (EventInstanceManager eim : em.getInstances()) {
                    c.getPlayer().print(5, "Event " + eim.getName() + ", Event Name: " + em.getName() + " Iprops: " + eim.getProperty(splitted[2]) + ", Eprops: " + em.getProperty(splitted[2]));
                }
            }
            return 0;
        }
    }

    public static class LeaveInstance extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getEventInstance() == null) {
                c.getPlayer().print(5, "You are not in one");
            } else {
                c.getPlayer().getEventInstance().unregisterPlayer(c.getPlayer());
            }
            return 1;
        }
    }

    public static class WhosThere extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            StringBuilder builder = new StringBuilder("Players On Map: ").append(c.getPlayer().getMap().getCharactersThreadsafe().size()).append(", ");
            for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (builder.length() > 150) { // wild guess :o
                    builder.setLength(builder.length() - 2);
                    c.getPlayer().print(6, builder.toString());
                    builder = new StringBuilder();
                }
                builder.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                builder.append(", ");
            }
            builder.setLength(builder.length() - 2);
            c.getPlayer().print(6, builder.toString());
            return 1;
        }
    }

    public static class StartInstance extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getEventInstance() != null) {
                c.getPlayer().print(5, "You are in one");
            } else if (splitted.length > 2) {
                EventManager em = c.getChannelServer().getEventSM().getEventManager(splitted[1]);
                if (em == null || em.getInstance(splitted[2]) == null) {
                    c.getPlayer().print(5, "Not exist");
                } else {
                    em.getInstance(splitted[2]).registerPlayer(c.getPlayer());
                }
            } else {
                c.getPlayer().print(5, "[Syntax] !startinstance [eventmanager] [eventinstance]");
            }
            return 1;

        }
    }

    public static class ResetMobs extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().killAllMonsters(false);
            return 1;
        }
    }

    public static class KillMonsterByOID extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            int targetId = Integer.parseInt(splitted[1]);
            MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.killMonster(monster, c.getPlayer(), false, false, (byte) 1);
            }
            return 1;
        }
    }

    public static class removenpc extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().resetNPCs();
            return 1;
        }
    }

    public static class whitechat extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter all : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                all.print(-6, StringUtil.joinStringFrom(splitted, 1));
            }
            return 1;
        }
    }

    public static class Notice extends CommandExecute {

        protected static int getNoticeType(String typestring) {
            switch (typestring) {
                case "1":
                    return -1;
                case "2":
                    return -2;
                case "3":
                    return -3;
                case "4":
                    return -4;
                case "5":
                    return -5;
                case "6":
                    return -6;
                case "7":
                    return -7;
                case "8":
                    return -8;
                case "n":
                    return 0;
                case "p":
                    return 1;
                case "l":
                    return 2;
                case "nv":
                    return 5;
                case "v":
                    return 5;
                case "b":
                    return 6;
            }
            return -1;
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int joinmod = 1;
            int range = -1;
            if (splitted[1].equals("m")) {
                range = 0;
            } else if (splitted[1].equals("c")) {
                range = 1;
            } else if (splitted[1].equals("w")) {
                range = 2;
            }

            int tfrom = 2;
            if (range == -1) {
                range = 2;
                tfrom = 1;
            }
            int type = getNoticeType(splitted[tfrom]);
            if (type == -1) {
                type = 0;
                joinmod = 0;
            }
            StringBuilder sb = new StringBuilder();
            if (splitted[tfrom].equals("nv")) {
                sb.append("[Notice]");
            } else {
                sb.append("");
            }
            joinmod += tfrom;
            sb.append(StringUtil.joinStringFrom(splitted, joinmod));

            byte[] packet = CWvsContext.serverNotice(type, sb.toString());
            if (range == 0) {
                c.getPlayer().getMap().broadcastMessage(packet);
            } else if (range == 1) {
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else if (range == 2) {
                World.Broadcast.broadcastMessage(packet);
            }
            return 1;
        }
    }

    public static class Yellow extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int range = -1;
            if (splitted[1].equals("m")) {
                range = 0;
            } else if (splitted[1].equals("c")) {
                range = 1;
            } else if (splitted[1].equals("w")) {
                range = 2;
            }
            if (range == -1) {
                range = 2;
            }
            byte[] packet = CWvsContext.yellowChat((splitted[0].equals("!y") ? ("[" + c.getPlayer().getName() + "] ") : "") + StringUtil.joinStringFrom(splitted, 2));
            if (range == 0) {
                c.getPlayer().getMap().broadcastMessage(packet);
            } else if (range == 1) {
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else if (range == 2) {
                World.Broadcast.broadcastMessage(packet);
            }
            return 1;
        }
    }

    public static class Y extends Yellow {
    }

    public static class WhatsMyIP extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().print(5, "IP: " + c.getSession().getRemoteAddress().toString().split(":")[0]);
            return 1;
        }
    }

    public static class TDrops extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().toggleDrops();
            return 1;
        }
    }
}