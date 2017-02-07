package client;

import constants.GameConstants;
import provider.MapleData;
import provider.MapleDataTool;
import server.MapleStatEffect;
import server.Randomizer;
import server.life.Element;
import tools.Pair;

import java.util.ArrayList;
import java.util.List;

public class Skill {

    private final List<MapleStatEffect> effects = new ArrayList<MapleStatEffect>();
    private final List<Pair<Integer, Byte>> requiredSkill = new ArrayList<Pair<Integer, Byte>>();
    private String name = "";
    private List<MapleStatEffect> pvpEffects = null;
    private List<Integer> animation = null;
    private Element element = Element.NEUTRAL;
    private int id, animationTime = 0, masterLevel = 0, maxLevel = 0, delay = 0, trueMax = 0, eventTamingMob = 0, skillTamingMob = 0, skillType = 0; // 4 is alert
    private boolean invisible = false, chargeskill = false, timeLimited = false, combatOrders = false, pvpDisabled = false, magic = false, casterMove = false, pushTarget = false, pullTarget = false;

    public Skill(final int id) {
        super();
        this.id = id;
    }

    public static final Skill loadFromData(final int id, final MapleData data, final MapleData delayData) {
        Skill ret = new Skill(id);

        boolean isBuff = false;
        final int skillType = MapleDataTool.getInt("skillType", data, -1);
        final String elem = MapleDataTool.getString("elemAttr", data, null);
        if (elem != null) {
            ret.element = Element.getFromChar(elem.charAt(0));
        }
        ret.skillType = skillType;
        ret.invisible = MapleDataTool.getInt("invisible", data, 0) > 0;
        ret.timeLimited = MapleDataTool.getInt("timeLimited", data, 0) > 0;
        ret.combatOrders = MapleDataTool.getInt("combatOrders", data, 0) > 0;
        ret.masterLevel = MapleDataTool.getInt("masterLevel", data, 0);
        if ((id == 22111001 || id == 22140000 || id == 22141002)) {
            ret.masterLevel = 5; // Hack
        }
        ret.eventTamingMob = MapleDataTool.getInt("eventTamingMob", data, 0);
        ret.skillTamingMob = MapleDataTool.getInt("skillTamingMob", data, 0);
        final MapleData inf = data.getChildByPath("info");
        if (inf != null) {
            ret.pvpDisabled = MapleDataTool.getInt("pvp", inf, 1) <= 0;
            ret.magic = MapleDataTool.getInt("magicDamage", inf, 0) > 0;
            ret.casterMove = MapleDataTool.getInt("casterMove", inf, 0) > 0;
            ret.pushTarget = MapleDataTool.getInt("pushTarget", inf, 0) > 0;
            ret.pullTarget = MapleDataTool.getInt("pullTarget", inf, 0) > 0;
        }
        final MapleData effect = data.getChildByPath("effect");
        if (skillType == 2) {
            isBuff = true;
        } else if (skillType == 3) { // Final attack
            ret.animation = new ArrayList<Integer>();
            ret.animation.add(0);
            isBuff = effect != null;
        } else {
            MapleData action_ = data.getChildByPath("action");
            final MapleData hit = data.getChildByPath("hit");
            final MapleData ball = data.getChildByPath("ball");

            boolean action = false;
            if (action_ == null) {
                if (data.getChildByPath("prepare/action") != null) {
                    action_ = data.getChildByPath("prepare/action");
                    action = true;
                }
            }
            isBuff = effect != null && hit == null && ball == null;
            if (action_ != null) {
                String d = null;
                if (action) { // Prepare
                    d = MapleDataTool.getString(action_, null);
                } else {
                    d = MapleDataTool.getString("0", action_, null);
                }
                if (d != null) {
                    isBuff |= d.equals("alert2");
                    final MapleData dd = delayData.getChildByPath(d);
                    if (dd != null) {
                        for (MapleData del : dd) {
                            ret.delay += Math.abs(MapleDataTool.getInt("delay", del, 0));
                        }
                        if (ret.delay > 30) { // Then, faster(2) = (10+2)/16 which is basically 3/4
                            ret.delay = (int) Math.round(ret.delay * 11.0 / 16.0); // fastest(1) lolol
                            ret.delay -= (ret.delay % 30); // Round to 30ms
                        }
                    }
                    if (SkillFactory.getDelay(d) != null) { // This should return true always
                        ret.animation = new ArrayList<Integer>();
                        ret.animation.add(SkillFactory.getDelay(d));
                        if (!action) {
                            for (MapleData ddc : action_) {
                                if (!MapleDataTool.getString(ddc, d).equals(d)) {
                                    String c = MapleDataTool.getString(ddc);
                                    if (SkillFactory.getDelay(c) != null) {
                                        ret.animation.add(SkillFactory.getDelay(c));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            switch (id) {
                case 2301002: // Heal is alert2 but not overtime...
                case 2111003: // Poison mist
                case 12111005: // Flame Gear
                case 22161003:
                case 32121006:
                case 11076:
                case 2111002: // Explosion
                case 4211001: // Chakra
                case 2121001: // Big bang
                case 2221001: // Big bang
                case 2321001: // Big bang
                case 1076:
                    isBuff = false;
                    break;
                case 4111009:
                case 14111007:
                case 3121002:
                case 3221002:
                case 5311004:
                case 5121015:
                case 5211009:
                case 24111002:
                case 1004: // Monster riding
                case 20031209:
                case 20031210:
                case 10001004:
                case 20001004:
                case 20011004:
                case 80001000:
                case 1026:
                case 10001026:
                case 20001026:
                case 20011026:
                case 20021026:
                case 30001026:
                case 30011026:
                case 50001026:
                case 93:
                case 10000093:
                case 20000093:
                case 20010093:
                case 20020093:
                case 30000093:
                case 30010093:
                case 9101004: // Hide is a buff -.- atleast for us o.o"
                case 1111002: // Combo
                case 4211003: // Pickpocket
                case 4111001: // Mesoup
                case 15111002: // Super Transformation
                case 5111005: // Transformation
                case 5121003: // Super Transformation
                case 13111005: // Alabtross
                case 21000000: // Aran Combo
                case 21101003: // Body Pressure
                case 5211001: // Pirate octopus summon
                case 5211002:
                case 5220002: // Wrath of the octopi
                case 5001005: // Dash
                case 15001003:
                case 5211006: // Homing beacon
                case 5220011: // Bullseye
                case 5110001: // Energy charge
                case 15100004:
                case 5121009: // Speed infusion
                case 15111005:

                case 22121001: // Element reset
                case 22131001: // Magic shield
                case 22141002: // Magic booster
                case 2311006: // Magic booster
                case 22151002: // Killer wing
                case 22151003: // Magic resist
                case 22161002: // Imprint
                case 22171000: // Maple warrior
                case 22171004: // Hero will
                case 5721002: // Hero will
                case 22181000: // Onyx blessing
                case 22181004:
                case 22161004:
                case 22181003: // Soul stone
                    //case 22121000:
                    //case 22141003:
                    //case 22151001:
                    //case 22161002:
                case 4331003: // Owl spirit
                case 15101006: // Spark
                case 15111006: // Spark
                case 4321000: // Tornado spin
                case 1320009: // Beholder's buff.. passive
                case 35120000:
                case 35001002: // TEMP. mech
                case 9001004: // Hide
                case 4341002:

                case 32001003: // Dark aura
                case 32120000:
                case 32111012: // Blue aura
                case 32110000:
                case 32101003: // Yellow aura
                case 32120001:
                case 35101007: // Perfect armor
                case 35121006: // Satellite safety
                case 35001001: // Flame
                case 35101009:
                case 35121005: // Missile
                case 35121013:
                case 35111004: // Siege
                case 33111003: // Puppet ?
                case 1211009:
                case 1111007:
                case 51111005: // Mihile's magic crash
                case 1311007: // Magic, armor, atk crash
                case 32121003: // Twister
                case 5111007:
                case 5211007:
                case 15111011:// Dice
                case 5311005:
                case 5320007:
                case 5720005:
                case 35111013: // Dice
                case 5711011: // Dice
                case 32111006:
                case 5120011:
                case 5220012:
                case 1220013:
                case 33101006: // Jaguar oshi
                case 32110007:
                case 32110008:
                case 32110009:
                case 32111005:
                case 31121005:
                case 35121003:
                case 35121009:
                case 35121010:
                case 35111005:
                case 35111001:
                case 35111010:
                case 35111009:
                case 35111011:
                case 35111002:
                case 35101005:
                case 3120006:
                case 3220005:
                case 2121009:
                case 2120010:
                case 2221009:
                case 2220010:
                case 23101003:// Spirit surge
                case 2321010:
                case 2320011:
                case 5321003:
                case 5321004:
                case 80001089:

                case 24101005: // Booster
                case 5701005: // Gun booster
                case 24121009: // Hero's will
                case 24121008: // Maple warrior
                case 5711001: // Turret
                    isBuff = true;
                    break;
            }
        }
        ret.chargeskill = data.getChildByPath("keydown") != null;
        // Some skills have old system, some new.
        final MapleData level = data.getChildByPath("common");
        if (level != null) {
            ret.maxLevel = MapleDataTool.getInt("maxLevel", level, 1); // 10 just a failsafe, shouldn't actually happens.
            ret.trueMax = ret.maxLevel + (ret.combatOrders ? 2 : 0);
            for (int i = 1; i <= ret.trueMax; i++) {
                ret.effects.add(MapleStatEffect.loadSkillEffectFromData(level, id, isBuff, i, "x"));
            }

        } else {
            for (final MapleData leve : data.getChildByPath("level")) {
                ret.effects.add(MapleStatEffect.loadSkillEffectFromData(leve, id, isBuff, Byte.parseByte(leve.getName()), null));
            }
            ret.maxLevel = ret.effects.size();
            ret.trueMax = ret.effects.size();
        }
        final MapleData level2 = data.getChildByPath("PVPcommon");
        if (level2 != null) {
            ret.pvpEffects = new ArrayList<MapleStatEffect>();
            for (int i = 1; i <= ret.trueMax; i++) {
                ret.pvpEffects.add(MapleStatEffect.loadSkillEffectFromData(level2, id, isBuff, i, "x"));
            }
        }
        final MapleData reqDataRoot = data.getChildByPath("req");
        if (reqDataRoot != null) {
            for (final MapleData reqData : reqDataRoot.getChildren()) {
                ret.requiredSkill.add(new Pair<Integer, Byte>(Integer.parseInt(reqData.getName()), (byte) MapleDataTool.getInt(reqData, 1)));
            }
        }
        ret.animationTime = 0;
        if (effect != null) {
            for (final MapleData effectEntry : effect) {
                ret.animationTime += MapleDataTool.getIntConvert("delay", effectEntry, 0);
            }
        }
        return ret;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public MapleStatEffect getEffect(final int level) {
        if (effects.size() < level) {
            if (effects.size() > 0) { // incAllskill
                return effects.get(effects.size() - 1);
            }
            return null;
        } else if (level <= 0) {
            return effects.get(0);
        }
        return effects.get(level - 1);
    }

    public MapleStatEffect getPVPEffect(final int level) {
        if (pvpEffects == null) {
            return getEffect(level);
        }
        if (pvpEffects.size() < level) {
            if (pvpEffects.size() > 0) { // incAllskill
                return pvpEffects.get(pvpEffects.size() - 1);
            }
            return null;
        } else if (level <= 0) {
            return pvpEffects.get(0);
        }
        return pvpEffects.get(level - 1);
    }

    public int getSkillType() {
        return skillType;
    }

    public List<Integer> getAllAnimation() {
        return animation;
    }

    public int getAnimation() {
        if (animation == null) {
            return -1;
        }
        return animation.get(Randomizer.nextInt(animation.size()));
    }

    public boolean isPVPDisabled() {
        return pvpDisabled;
    }

    public boolean isChargeSkill() {
        return chargeskill;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public boolean hasRequiredSkill() {
        return requiredSkill.size() > 0;
    }

    public List<Pair<Integer, Byte>> getRequiredSkills() {
        return requiredSkill;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getTrueMax() {
        return trueMax;
    }

    public boolean combatOrders() {
        return combatOrders;
    }

    public boolean canBeLearnedBy(int job) {
        int jid = job;
        int skillForJob = id / 10000;
        if (skillForJob == 2001) {
            return GameConstants.isEvan(job); // Special exception for beginner.
        } else if (skillForJob == 0) {
            return GameConstants.isAdventurer(job); // Special exception for beginner.
        } else if (skillForJob == 1000) {
            return GameConstants.isKOC(job); // Special exception for beginner.
        } else if (skillForJob == 2000) {
            return GameConstants.isAran(job); // Special exception for beginner.
        } else if (skillForJob == 3000) {
            return GameConstants.isResist(job); // Special exception for beginner.
        } else if (skillForJob == 1) {
            return GameConstants.isCannon(job); // Special exception for beginner.
        } else if (skillForJob == 3001) {
            return GameConstants.isDemon(job); // Special exception for beginner.
        } else if (skillForJob == 2002) {
            return GameConstants.isMercedes(job); // Special exception for beginner.
        } else if (skillForJob == 508) {
            return GameConstants.isJett(job); // Special exception for beginner.
        } else if (skillForJob == 2003) {
            return GameConstants.isPhantom(job); // Special exception for beginner.
        } else if (skillForJob == 5000) {
            return GameConstants.isMihile(job); // Special exception for beginner.
        } else if (skillForJob == 2004) {
            return GameConstants.isLuminous(job); // special exception for beginner.
        } else if (skillForJob == 6000) {
            return GameConstants.isKaiser(job); // Special exception for beginner.
        } else if (skillForJob == 6001) {
            return GameConstants.isAngelicBuster(job); // Special exception for beginner.
        } else if (jid / 100 != skillForJob / 100) { // Wrong job
            return false;
        } else if (jid / 1000 != skillForJob / 1000) { // Wrong job
            return false;
        } else if (GameConstants.isAngelicBuster(skillForJob) && !GameConstants.isAngelicBuster(job)) {
            return false;
        } else if (GameConstants.isKaiser(skillForJob) && !GameConstants.isKaiser(job)) {
            return false;
        } else if (GameConstants.isMihile(skillForJob) && !GameConstants.isMihile(job)) {
            return false;
        } else if (GameConstants.isLuminous(skillForJob) && !GameConstants.isLuminous(job)) {
            return false;
        } else if (GameConstants.isPhantom(skillForJob) && !GameConstants.isPhantom(job)) {
            return false;
        } else if (GameConstants.isJett(skillForJob) && !GameConstants.isJett(job)) {
            return false;
        } else if (GameConstants.isCannon(skillForJob) && !GameConstants.isCannon(job)) {
            return false;
        } else if (GameConstants.isDemon(skillForJob) && !GameConstants.isDemon(job)) {
            return false;
        } else if (GameConstants.isAdventurer(skillForJob) && !GameConstants.isAdventurer(job)) {
            return false;
        } else if (GameConstants.isKOC(skillForJob) && !GameConstants.isKOC(job)) {
            return false;
        } else if (GameConstants.isAran(skillForJob) && !GameConstants.isAran(job)) {
            return false;
        } else if (GameConstants.isEvan(skillForJob) && !GameConstants.isEvan(job)) {
            return false;
        } else if (GameConstants.isMercedes(skillForJob) && !GameConstants.isMercedes(job)) {
            return false;
        } else if (GameConstants.isResist(skillForJob) && !GameConstants.isResist(job)) {
            return false;
        } else if ((jid / 10) % 10 == 0 && (skillForJob / 10) % 10 > (jid / 10) % 10) { // Wrong 2nd job
            return false;
        } else if ((skillForJob / 10) % 10 != 0 && (skillForJob / 10) % 10 != (jid / 10) % 10) { // Wrong 2nd job
            return false;
        } else if (skillForJob % 10 > jid % 10) { // Wrong 3rd / 4th job
            return false;
        }
        return true;
    }

    public boolean isTimeLimited() {
        return timeLimited;
    }

    public boolean isFourthJobSkill(int skillid) {
        switch (skillid / 10000) {
            case 112:
            case 122:
            case 132:
            case 212:
            case 222:
            case 232:
            case 312:
            case 322:
            case 412:
            case 422:
            case 512:
            case 522:
                return true;
        }
        return false;
    }

    public boolean isThirdJobSkill(int skillid) {
        switch (skillid / 10000) {
            case 111:
            case 121:
            case 131:
            case 211:
            case 221:
            case 231:
            case 311:
            case 321:
            case 411:
            case 421:
            case 511:
            case 521:
                return true;
        }
        return false;
    }

    public boolean isSecondJobSkill(int skillid) {
        switch (skillid / 10000) {
            case 110:
            case 120:
            case 130:
            case 210:
            case 220:
            case 230:
            case 310:
            case 320:
            case 410:
            case 420:
            case 510:
            case 520:
                return true;
        }
        return false;
    }

    public boolean isFourthJob() {
        switch (id) { // I guess imma make an sql table to store these, so that we could max them all out.
            case 3220010:
            case 3120011:
            case 33120010:
            case 5220014:
            case 23120011:
            case 23121008:
            case 33121005:
                //case 32120009:
            case 5321006:
            case 21120011:
            case 22181004:
            case 4340010:
            case 4320005:
            case 5720008:
            case 5120012:
            case 5320007:
                //case 24121004:// Aria Phantom skill
                return false;
        }
        // Resurrection has master level while ult.strafe does not.. wtf, impossible to tell from WZ
        if ((id / 10000) == 2312) { // All 10 skills.
            return true;
        }
        if (id == 24121009 || id == 24121010) { // LOL WTF U LOSER
            return true;
        }
        if ((getMaxLevel() <= 15 && !invisible && getMasterLevel() <= 0)) {
            return false;
        }
        if (id / 10000 >= 2210 && id / 10000 < 3000) { // Evan skill
            return ((id / 10000) % 10) >= 7 || getMasterLevel() > 0;
        }
        if (id / 10000 >= 430 && id / 10000 <= 434) { // Dual blade skill
            return ((id / 10000) % 10) == 4 || getMasterLevel() > 0;
        }
        return ((id / 10000) % 10) == 2 && id < 90000000 && !isBeginnerSkill();
    }

    public Element getElement() {
        return element;
    }

    public int getAnimationTime() {
        return animationTime;
    }

    public int getMasterLevel() {
        return masterLevel;
    }

    public int getDelay() {
        return delay;
    }

    public int getTamingMob() {
        return eventTamingMob;
    }

    public int getSkillTamingMob() {
        return eventTamingMob;
    }

    public boolean isBeginnerSkill() {
        int jobId = id / 10000;
        return GameConstants.isBeginnerJob(jobId);
    }

    public boolean isMagic() {
        return magic;
    }

    public boolean isMovement() {
        return casterMove;
    }

    public boolean isPush() {
        return pushTarget;
    }

    public boolean isPull() {
        return pullTarget;
    }

    public boolean isSpecialSkill() {
        int jobId = id / 10000;
        return jobId == 900 || jobId == 800 || jobId == 9000 || jobId == 9200 || jobId == 9201 || jobId == 9202 || jobId == 9203 || jobId == 9204;
    }
}