package server;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.inventory.MapleInventoryIdentifier;
import client.messages.CommandProcessor;
import constants.ServerConstants;
import constants.WorldConstants;
import database.DatabaseConnection;
import handling.MapleServerHandler;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginInformationProvider;
import handling.login.LoginServer;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.guild.MapleGuild;
import server.Timer.*;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.MobSkillFactory;
import server.life.PlayerNPC;
import server.maps.MapleMapFactory;
import server.quest.MapleQuest;
import tools.Logger;
import tools.MapleAESOFB;
import tools.MockIOSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Start {

    public static final Start instance = new Start();
    public static long startTime = System.currentTimeMillis();

    public static void main(final String args[]) throws InterruptedException {
        instance.run();
    }

    public void run() throws InterruptedException {
        if (isPortInUse(LoginServer.PORT) || isPortInUse(CashShopServer.PORT)){
            Logger.println("Server is already running somewhere....");
            System.exit(0);
        }

        if (ServerConstants.ADMIN_ONLY || ServerConstants.USE_LOCALHOST)
            Logger.println("Maintenance is currently active.");
        if (ServerConstants.LOG_PACKETS)
            Logger.println("Logging Packets.");

        try {
            final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new RuntimeException("Runtime Exception - Check if the SQL Database is connected.");
        }

        World.init();

        Logger.println("Server Rates: " +
                "Exp " + WorldConstants.Servers.Scania.getExp() +
                " / Meso " + WorldConstants.Servers.Scania.getMeso() +
                " / Drop " + WorldConstants.Servers.Scania.getDrop());

        boolean encryptionfound = false;
        for (MapleAESOFB.EncryptionKey encryptkey : MapleAESOFB.EncryptionKey.values()) {
            if (("V" + ServerConstants.MAPLE_VERSION).equals(encryptkey.name())) {
                encryptionfound = true;
                break;
            }
        }
        if (!encryptionfound)
            Logger.println("System could not locate encryption for the current version, so it is using the latest Encryption");

        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        CloneTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        PingTimer.getInstance().start();

        int i = 0;
        updateProgress(i += 5,"Guild Rankings");
        MapleGuildRanking.getInstance().load();

        updateProgress(i += 5, "Guilds");
        MapleGuild.loadAll();

        updateProgress(i += 5, "Family");
        MapleFamily.loadAll();

        updateProgress(i += 5, "Quests");
        MapleLifeFactory.loadQuestCounts();

        updateProgress(i += 5, "Quest Init");
        MapleQuest.initQuests();

        updateProgress(i += 5, "ETC Items");
        MapleItemInformationProvider.getInstance().runEtc();

        updateProgress(i += 5, "Monsters");
        MapleMonsterInformationProvider.getInstance().load();

        updateProgress(i += 5, "Items");
        MapleItemInformationProvider.getInstance().runItems();

        updateProgress(i += 5, "Skill Factory");
        SkillFactory.load();

        updateProgress(i += 5, "Login Information Provider");
        LoginInformationProvider.getInstance();

        updateProgress(i += 5, "Random Rewards");
        RandomRewards.load();

        updateProgress(i += 5, "Maple OX Quiz Factory");
        MapleOxQuizFactory.getInstance();

        updateProgress(i += 5, "Maple Carnival");
        MapleCarnivalFactory.getInstance();

        updateProgress(i += 5, "Character Card Factory");
        CharacterCardFactory.getInstance().initialize();

        updateProgress(i += 5, "Monster Skills");
        MobSkillFactory.getInstance();

        updateProgress(i += 5, "Speed Runner");
        SpeedRunner.loadSpeedRuns();

        updateProgress(i += 5, "MTS Storage Handler");
        MTSStorage.load();

        updateProgress(i += 5, "Inventory Identifier");
        MapleInventoryIdentifier.getInstance();

        updateProgress(i += 5, "Map Factory");
        MapleMapFactory.loadCustomLife();

        updateProgress(i += 5, "Complete");
        Logger.println("\n");

        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("DELETE FROM `moonlightachievements` where achievementid > 0;");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
        }
        CashItemFactory.getInstance().initialize();
        MapleServerHandler.initiate();

        LoginServer.run_startup_configurations();
        ChannelServer.startChannel_Main();

        CashShopServer.run_startup_configurations();
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        World.registerRespawn();
        server.ShutdownServer.registerMBean();
        PlayerNPC.loadAll();
        MapleMonsterInformationProvider.getInstance().addExtra();
        LoginServer.setOn();
        RankingWorker.run();

        //Set up fake user to use commands
        final byte ivRecv[] = new byte[]{(byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255)};
        final byte ivSend[] = new byte[]{(byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255)};

        final MapleClient c = new MapleClient(
                new MapleAESOFB(ivSend, (short) (0xFFFF - ServerConstants.MAPLE_VERSION)), // Sent Cypher
                new MapleAESOFB(ivRecv, ServerConstants.MAPLE_VERSION), // Recv Cypher
                new MockIOSession());
        //c.setChannel(1);
        MapleCharacter chr = MapleCharacter.loadCharFromDB(1, c, false);//Character ID 1 is the system
        c.setPlayer(chr);
        chr.setMap(180000001);
        Logger.println("\nProject Nexus was launched successfully in " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.print('$');
                String commandLine = br.readLine();
                CommandProcessor.processCommand(c, commandLine, ServerConstants.CommandType.NORMAL);
            }catch(IOException e) {
                Logger.println("Command error");
            }
        }
    }

    public static class Shutdown implements Runnable {
        @Override
        public void run() {
            server.ShutdownServer.getInstance().run();
            server.ShutdownServer.getInstance().run();
        }
    }

    public static void updateProgress(float progressPercentage) {
        updateProgress(progressPercentage, "");
    }
    public static void updateProgress(float progressPercentage, String message) {
        int p = (int) (progressPercentage / 2);

        System.out.print("\r[");
        for (int i = 0; i <= p; i++)
            System.out.print("=");

        for (; p < 50; p++)
            System.out.print(" ");
        System.out.print("] " + (byte) progressPercentage + "% (" + message + ")");
    }

    private boolean isPortInUse(int port) {
        boolean result = false;

        try {
            (new Socket("localhost", port)).close();
            result = true;
        } catch(IOException e) {
        }
        return result;
    }
}