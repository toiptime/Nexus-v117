package client;

import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterStats;
import server.maps.AnimatedMapleMapObject;
import server.maps.MapleMapObjectType;
import server.movement.AbsoluteLifeMovement;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;
import tools.data.MaplePacketLittleEndianWriter;
import tools.packet.CField;
import tools.packet.PacketHelper;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

public class MonsterFamiliar extends AnimatedMapleMapObject implements Serializable {

    private static final long serialVersionUID = 795419937713738569L;
    private int id, familiar, fatigue, characterid;
    private String name;
    private long expiry;
    private short fh = 0;
    private byte vitality;

    public MonsterFamiliar(int characterid, int id, int familiar, long expiry, String name, int fatigue, byte vitality) {
        this.familiar = familiar;
        this.characterid = characterid;
        this.expiry = expiry;
        this.vitality = vitality;
        this.id = id;
        this.name = name;
        this.fatigue = fatigue;
        setStance(0);
        setPosition(new Point(0, 0));
    }

    public MonsterFamiliar(int characterid, int familiar, long expiry) {
        this.familiar = familiar;
        this.characterid = characterid;
        this.expiry = expiry;
        this.fatigue = 0;
        this.vitality = (byte) 1;
        this.name = getOriginalName();
        this.id = Randomizer.nextInt();
    }

    public String getOriginalName() {
        return getOriginalStats().getName();
    }

    public MapleMonsterStats getOriginalStats() {
        return MapleLifeFactory.getMonsterStats(MapleItemInformationProvider.getInstance().getFamiliar(familiar).mob);
    }

    public void addFatigue(MapleCharacter owner) {
        addFatigue(owner, 1);
    }

    public void addFatigue(MapleCharacter owner, int f) {
        //f += ((familiar / 10000) % 10) * f;
        fatigue = Math.min(vitality * 300, Math.max(0, fatigue + f));
        owner.getClient().getSession().write(CField.updateFamiliar(this));
        if (fatigue >= (vitality * 300)) {
            owner.removeFamiliar();
        }
    }

    public int getFamiliar() {
        return familiar;
    }

    public int getId() {
        return id;
    }

    public int getFatigue() {
        return fatigue;
    }

    public void setFatigue(int f) {
        this.fatigue = f;
    }

    public int getCharacterId() {
        return characterid;
    }

    public final String getName() {
        return name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long e) {
        this.expiry = e;
    }

    public byte getVitality() {
        return vitality;
    }

    public void setVitality(int v) {
        this.vitality = (byte) v;
    }

    public short getFh() {
        return fh;
    }

    public void setFh(int f) {
        this.fh = (short) f;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.getSession().write(CField.spawnFamiliar(this, true, false));
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.getSession().write(CField.spawnFamiliar(this, false, false));
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.FAMILIAR;
    }

    public final void updatePosition(final List<LifeMovementFragment> movement) {
        for (final LifeMovementFragment move : movement) {
            if (move instanceof LifeMovement) {
                if (move instanceof AbsoluteLifeMovement) {
                    setFh(((AbsoluteLifeMovement) move).getUnk());
                }
            }
        }
    }

    public void writeRegisterPacket(MaplePacketLittleEndianWriter mplew, boolean chr) {
        mplew.writeInt(getCharacterId()); // Lol
        mplew.writeInt(getFamiliar());
        mplew.writeZeroBytes(13);
        mplew.write(chr ? 1 : 0);
        mplew.writeShort(getVitality());
        mplew.writeInt(getFatigue());
        mplew.writeLong(PacketHelper.getTime(getVitality() >= 3 ? System.currentTimeMillis() : -2));
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.writeLong(PacketHelper.getTime(getExpiry()));
        mplew.writeShort(getVitality());
    }
}