/*
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MapleAchievements {

    private static MapleAchievements instance = new MapleAchievements();
    private Map<Integer, MapleAchievement> achievements = new LinkedHashMap<Integer, MapleAchievement>();

    protected MapleAchievements() {
        achievements.put(1, new MapleAchievement("have purchased Donor Point [Cashback]", 2000, false));
        achievements.put(2, new MapleAchievement("have reached 1 Reborn [Reborner] (Brown)", 40, false));
        achievements.put(3, new MapleAchievement("have reached 5 Reborns [Reborner] (Grey)", 200, false));
        achievements.put(4, new MapleAchievement("have reached 10 Reborns [Reborner] (Magenta)", 400, false));
        achievements.put(5, new MapleAchievement("have reached 20 Reborns [Reborner] (Cyan)", 800, false));
        achievements.put(7, new MapleAchievement("received 1 Fame [Eminence] (Brown)", 20, false));
        /*achievements.put(9, new MapleAchievement("equipped a Reverse Item", 100, false));
        achievements.put(10, new MapleAchievement("equipped a Timeless Item", 100, false));*/
        achievements.put(11, new MapleAchievement("said 'I like Project Nexus' [Overrun] (Purple)", 90, false));
        achievements.put(12, new MapleAchievement("killed a Female Boss [Legend] (Magenta)", 1800, false));
        achievements.put(13, new MapleAchievement("killed a Papulatus [Legend] (Grey)", 1400, false));
        achievements.put(14, new MapleAchievement("killed a Pianus [Legend] (Brown)", 1000, false));
        achievements.put(15, new MapleAchievement("killed the Almighty Zakum [Legend] (Purple)", 6000, false));
        achievements.put(16, new MapleAchievement("killed the Horntail [Legend] (Red)", 12000, false));
        achievements.put(17, new MapleAchievement("killed the Pink Bean [Legend] (Red+++++)", 30000, false));
        achievements.put(18, new MapleAchievement("killed a Boss [Skirmisher]", 40, false));
        achievements.put(19, new MapleAchievement("won the event 'OX Quiz' [I See]", 200, false));
        achievements.put(20, new MapleAchievement("won the event 'MapleFitness' [Energetic]", 200, false));
        achievements.put(21, new MapleAchievement("won the event 'Ola Ola' [Risk Taker]", 200, false));
        achievements.put(22, new MapleAchievement("cleared the Boss Party Quest in HELL mode [Destroyer] (Cyan)", 8000, false));
        achievements.put(23, new MapleAchievement("killed an Almighty Chaos Zakum [Legend] (Green)", 14000, false));
        achievements.put(24, new MapleAchievement("killed a Chaos Horntail [Legend] (Red+++)", 26000, false));
        achievements.put(25, new MapleAchievement("won the event 'Survival Challenge' [Enduring]", 200, false));
        //achievements.put(26, new MapleAchievement("dealt over 10000 Damage [That pain?]", 20, false));
        //achievements.put(27, new MapleAchievement("dealt over 50000 Damage [Pain killer]", 30, false));
        //achievements.put(28, new MapleAchievement("dealt over 100000 Damage [Here]", 40, false));
        //achievements.put(29, new MapleAchievement("dealt over 500000 Damage [Here]", 50, false));
        //achievements.put(30, new MapleAchievement("dealt 999999 Damage [No pain no gain]", 10, false));
        achievements.put(31, new MapleAchievement("received 1 Meso [Prudent] (Brown)", 10, false));
        achievements.put(32, new MapleAchievement("received 10 Mesos [Prudent] (Grey)", 20, false));
        achievements.put(33, new MapleAchievement("received 100 Mesos [Prudent] (Magenta)", 30, false));
        achievements.put(34, new MapleAchievement("received 1,000 Mesos [Prudent] (Cyan)", 60, false));
        achievements.put(35, new MapleAchievement("made a Guild [Master]", 200, false));
        //achievements.put(36, new MapleAchievement("made a Family [Senior]", 250, false));
        achievements.put(37, new MapleAchievement("cleared Crimsonwood Party Quest [Jamboree] (White)", 300, false));
        achievements.put(38, new MapleAchievement("defeated Von Leon [Legend] (Yellow)", 8000, false));
        achievements.put(39, new MapleAchievement("defeated Empress Cygnus [Legend] (Red++)", 24000, false));
        achievements.put(40, new MapleAchievement("equipped an item that is Level 10 [Equipped] (Brown)", 10, false));
        achievements.put(41, new MapleAchievement("equipped an item that is Level 20 [Equipped] (Grey)", 20, false));
        achievements.put(42, new MapleAchievement("received 10,000 Mesos [Prudent] (Orange) ", 120, false));
        achievements.put(43, new MapleAchievement("received 100,000 Mesos [Prudent] (White) ", 240, false));
        achievements.put(44, new MapleAchievement("received 1,000,000 Mesos [Prudent] (Blue) ", 480, false));
        achievements.put(45, new MapleAchievement("received 10,000,000 Mesos [Prudent] (Purple) ", 960, false));
        achievements.put(46, new MapleAchievement("received 100,000,000 Mesos [Prudent] (Yellow) ", 1920, false));
        achievements.put(47, new MapleAchievement("received 1,000,000,000 Mesos [Prudent] (Green) ", 3840, false));
        achievements.put(48, new MapleAchievement("received 2,147,483,647 Mesos [Prudent] (Red) ", 7680, false));
        achievements.put(49, new MapleAchievement("received 5 Fames [Eminence] (Brown)", 100, false));
        achievements.put(50, new MapleAchievement("received 10 Fames [Eminence] (Grey)", 200, false));
        achievements.put(51, new MapleAchievement("received 25 Fames [Eminence] (Magenta)", 500, false));
        achievements.put(52, new MapleAchievement("received 50 Fames [Eminence] (Cyan)", 1000, false));
        achievements.put(53, new MapleAchievement("received 100 Fames [Eminence] (Orange)", 2000, false));
        achievements.put(54, new MapleAchievement("said 'JudoNX' [Overrun] (Green)", 90, false));
        achievements.put(55, new MapleAchievement("said 'Jud0' [Overrun] (Blue)", 80, false));
        achievements.put(56, new MapleAchievement("said '01032014' [Overrun] (White)", 80, false));
        achievements.put(57, new MapleAchievement("said '1172' [Overrun] (Orange)", 80, false));
        achievements.put(58, new MapleAchievement("said '@dm1n157r470r' [Overrun] (Cyan)", 80, false));
        achievements.put(59, new MapleAchievement("said '94m3 M4573r' [Overrun] (Magenta)", 70, false));
        achievements.put(60, new MapleAchievement("said 'in73rn' [Overrun] (Grey)", 70, false));
        achievements.put(61, new MapleAchievement("said 'D0n0r' [Overrun] (Brown)", 70, false));
        achievements.put(62, new MapleAchievement("said 'i h473 h4(k3r!' [Overrun] (Red)", 100, false));
        achievements.put(63, new MapleAchievement("said 'i 11k3 Jud0M$' [Overrun] (Yellow)", 90, false));
        achievements.put(64, new MapleAchievement("said 'v0734nX' [Overrun]", 100, false));
        achievements.put(65, new MapleAchievement("killed a The Boss [Legend] (Cyan)", 4000, false));
        achievements.put(66, new MapleAchievement("killed the Targa Boss [Legend] (White)", 5000, false));
        achievements.put(67, new MapleAchievement("killed the Scarlion Boss [Legend] (Blue)", 5000, false));
        achievements.put(68, new MapleAchievement("killed an Arkarium [Legend] (Red+)", 22000, false));
        achievements.put(69, new MapleAchievement("killed a Level 120 Hilla [Legend] (Orange)", 4000, false));
        achievements.put(70, new MapleAchievement("killed a Level 190 Hilla [Legend] (Red++++)", 28000, false));
        achievements.put(71, new MapleAchievement("equipped an item that is Level 30 [Equipped] (Magenta)", 30, false));
        achievements.put(72, new MapleAchievement("equipped an item that is Level 40 [Equipped] (Cyan)", 40, false));
        achievements.put(73, new MapleAchievement("equipped an item that is Level 50 [Equipped] (Orange)", 50, false));
        achievements.put(74, new MapleAchievement("equipped an item that is Level 60 [Equipped] (White)", 60, false));
        achievements.put(75, new MapleAchievement("equipped an item that is Level 70 [Equipped] (Blue)", 70, false));
        achievements.put(76, new MapleAchievement("equipped an item that is Level 80 [Equipped] (Purple)", 80, false));
        achievements.put(77, new MapleAchievement("equipped an item that is Level 90 [Equipped] (Yellow)", 90, false));
        achievements.put(78, new MapleAchievement("equipped an item that is Level 100 [Equipped] (Green)", 100, false));
        achievements.put(79, new MapleAchievement("equipped an item that is Level 110 [Equipped] (Red)", 110, false));
        achievements.put(80, new MapleAchievement("equipped an item that is Level 120 [Equipped] (Red+)", 120, false));
        achievements.put(81, new MapleAchievement("equipped an item that is Level 130 [Equipped] (Red++)", 130, false));
        achievements.put(82, new MapleAchievement("equipped an item that is Level 140 [Equipped] (Red+++)", 140, false));
        achievements.put(83, new MapleAchievement("have reached 40 Reborns [Reborner] (Orange)", 1600, false));
        achievements.put(84, new MapleAchievement("have reached 70 Reborns [Reborner] (White)", 2800, false));
        achievements.put(85, new MapleAchievement("have reached 120 Reborns [Reborner] (Blue)", 4800, false));
        achievements.put(86, new MapleAchievement("have reached 190 Reborns [Reborner] (Purple)", 7600, false));
        achievements.put(87, new MapleAchievement("have reached 300 Reborns [Reborner] (Yellow)", 12000, false));
        achievements.put(88, new MapleAchievement("have reached 500 Reborns [Reborner] (Green)", 20000, false));
        achievements.put(89, new MapleAchievement("have reached 800 Reborns [Reborner] (Red)", 32000, false));
        achievements.put(90, new MapleAchievement("have reached Level 30 [Be Prepared] (Brown)", 20, false));
        achievements.put(91, new MapleAchievement("have reached Level 70 [Be Prepared] (Grey)", 40, false));
        achievements.put(92, new MapleAchievement("have reached Level 120 [Be Prepared] (Magenta)", 60, false));
        achievements.put(93, new MapleAchievement("have reached Level 200 [Be Prepared] (Cyan)", 80, false));
        achievements.put(94, new MapleAchievement("have begin adding ability points to STR [Buttress] (Brown)", 10, false));
        achievements.put(95, new MapleAchievement("have added a total of 100 ability points to STR [Buttress] (Grey)", 30, false));
        achievements.put(96, new MapleAchievement("have added a total of 1000 ability points to STR [Buttress] (Magenta)", 60, false));
        achievements.put(97, new MapleAchievement("have added a total of 10000 ability points to STR [Buttress] (Cyan)", 200, false));
        achievements.put(98, new MapleAchievement("have added a total of 30000 ability points to STR [Buttress] (Orange)", 600, false));
        achievements.put(99, new MapleAchievement("have begin adding ability points to DEX [Nimbleness] (Brown)", 10, false));
        achievements.put(100, new MapleAchievement("have added a total of 100 ability points to DEX [Nimbleness] (Grey)", 30, false));
        achievements.put(101, new MapleAchievement("have added a total of 1000 ability points to DEX [Nimbleness] (Magenta)", 60, false));
        achievements.put(102, new MapleAchievement("have added a total of 10000 ability points to DEX [Nimbleness] (Cyan)", 200, false));
        achievements.put(103, new MapleAchievement("have added a total of 30000 ability points to DEX [Nimbleness] (Orange)", 600, false));
        achievements.put(104, new MapleAchievement("have begin adding ability points to INT [Perception] (Brown)", 10, false));
        achievements.put(105, new MapleAchievement("have added a total of 100 ability points to INT [Perception] (Grey)", 30, false));
        achievements.put(106, new MapleAchievement("have added a total of 1000 ability points to INT [Perception] (Magenta)", 60, false));
        achievements.put(107, new MapleAchievement("have added a total of 10000 ability points to INT [Perception] (Cyan)", 200, false));
        achievements.put(108, new MapleAchievement("have added a total of 30000 ability points to INT [Perception] (Orange)", 600, false));
        achievements.put(109, new MapleAchievement("have begin adding ability points to LUK [Fortunate] (Brown)", 10, false));
        achievements.put(110, new MapleAchievement("have added a total of 100 ability points to LUK [Fortunate] (Grey)", 30, false));
        achievements.put(111, new MapleAchievement("have added a total of 1000 ability points to LUK [Fortunate] (Magenta)", 60, false));
        achievements.put(112, new MapleAchievement("have added a total of 10000 ability points to LUK [Fortunate] (Cyan)", 200, false));
        achievements.put(113, new MapleAchievement("have added a total of 30000 ability points to LUK [Fortunate] (Orange)", 600, false));
        achievements.put(114, new MapleAchievement("have reached 99,999 HP in stats [Master In Health]", 200, false));
        achievements.put(115, new MapleAchievement("have reached 99,999 MP in stats [Master In Mana]", 200, false));
        achievements.put(116, new MapleAchievement("have purchased Game Master Skills [Foremost]", 1000, false));
        //achievements.put(117, new MapleAchievement("have purchased the Donor Pack [Philanthropist]", 1500, true));
        achievements.put(118, new MapleAchievement("have reached Level 250 [Let The Journey Ends]", 200, false));
        achievements.put(119, new MapleAchievement("have expanded your Buddy Slots to 100 [More Buddy]", 200, false));
        achievements.put(120, new MapleAchievement("have killed a Castellan Toad [?]", 30000, false));
        achievements.put(121, new MapleAchievement("have crafted and equipped Genesis Bandana [Crafter] (Brown)", 200, false));
        achievements.put(122, new MapleAchievement("have crafted and equipped Blizzard Helmet [Crafter] (Magenta)", 600, false));
        achievements.put(123, new MapleAchievement("have crafted and equipped Snow Ice's Fur Hat [Crafter] (Cyan)", 600, false));
        //achievements.put(124, new MapleAchievement("have crafted and equipped the Blizzard Cape [Crafter] (Orange)", 600, false));
        achievements.put(125, new MapleAchievement("have crafted and equipped Blizzard Gloves [Crafter] (Orange)", 600, false));
        achievements.put(126, new MapleAchievement("have crafted and equipped Blizzard Armour (Male) [Crafter] (White)", 600, false));
        achievements.put(127, new MapleAchievement("have crafted and equipped Blizzard Armour (Female) [Crafter] (Blue)", 600, false));
        achievements.put(128, new MapleAchievement("have crafted and equipped Blizzard Boots [Crafter] (Purple)", 600, false));
        achievements.put(129, new MapleAchievement("have crafted and equipped Blizzard Polearm [Crafter] (Yellow)", 1000, false));
        //achievements.put(130, new MapleAchievement("have crafted and equipped Flaming Katana [Crafter] (Green)", 4000, false));
        //achievements.put(131, new MapleAchievement("have crafted and equipped Super Zakum Helmet [Crafter] (Red+++++) ", 10000, false));
        //achievements.put(132, new MapleAchievement("have crafted and equipped Chaos Horntail Necklace (+2) [Crafter] (Red++++++)", 20000, false));
        achievements.put(133, new MapleAchievement("have crafted and equipped Pink Bean Hat [Crafter] (Green)", 40000, false));
        //achievements.put(134, new MapleAchievement("have crafted and equipped Angelic Blessing [Crafter] (Red+++)", 10000, false));
        //achievements.put(135, new MapleAchievement("have crafted and equipped White Angelic Blessing [Crafter] (Red++++)", 20000, false));
        //achievements.put(136, new MapleAchievement("have crafted and equipped Awakening Mind of Maple Necklace [Crafter] (Red++)", 10000, false));
        //achievements.put(137, new MapleAchievement("have crafted and equipped Strawberry Popsicle [Crafter] (Red)", 7500, false));
        //achievements.put(138, new MapleAchievement("have crafted and equipped Spiegelmann's Gold Badge [Crafter] (Red+)", 10000, false));
        achievements.put(139, new MapleAchievement("have equipped the Pendant of the Spirit [Spirit Really Exist]", 100, false));
        achievements.put(140, new MapleAchievement("have begin contributing Guild Points to your Guild [Advocate] (Brown)", 20, false));
        achievements.put(141, new MapleAchievement("have contributed a total of 480 Guild Points to your Guild [Advocate] (Grey)", 100, false));
        achievements.put(142, new MapleAchievement("have contributed a total of 3360 Guild Points to your Guild [Advocate] (Magenta)", 1500, false));
        achievements.put(143, new MapleAchievement("have contributed a total of 14400 Guild Points to your Guild [Advocate] (Cyan)", 5000, false));
        achievements.put(144, new MapleAchievement("have contributed a total of 28800 Guild Points to your Guild [Advocate] (Orange)", 11500, false));
        achievements.put(145, new MapleAchievement("have contributed a total of 57600 Guild Points to your Guild [Advocate] (White)", 24000, false));
        achievements.put(146, new MapleAchievement("have contributed a total of 115200 Guild Points to your Guild [Advocate] (Blue)", 50000, false));
        achievements.put(147, new MapleAchievement("have received and equipped the Mark of the Beta [Getting Started]", 20, false));
        //achievements.put(148, new MapleAchievement("have bought and equipped the OTP User's Bluff Medal [OTP]", 10, false));
        achievements.put(149, new MapleAchievement("cleared Boss Party Quest in EASY mode [Destroyer] (Brown)", 400, false));
        achievements.put(150, new MapleAchievement("cleared Boss Party Quest in NORMAL mode [Destroyer] (Grey)", 2000, false));
        achievements.put(151, new MapleAchievement("cleared Boss Party Quest in HARD mode [Destroyer] (Magenta)", 4400, false));
        achievements.put(152, new MapleAchievement("cleared Romeo and Juliet Party Quest [Jamboree] (Magenta)", 200, false));
        achievements.put(153, new MapleAchievement("cleared Juliet and Romeo Party Quest [Jamboree] (Cyan)", 200, false));
        achievements.put(154, new MapleAchievement("cleared Forest of Poison Haze Party Quest [Jamboree] (Brown)", 300, false));
        achievements.put(155, new MapleAchievement("cleared Lord Pirate Party Quest [Jamboree] (Grey)", 200, false));
        achievements.put(156, new MapleAchievement("cleared Kenta in Danger Party Quest [Jamboree] (Orange)", 200, false));
        achievements.put(157, new MapleAchievement("cleared a Stage in Monster Park [Behemoth]", 200, false));
        achievements.put(158, new MapleAchievement("have exchanged and equipped White Belt [Dojo] (Brown)", 100, false));
        achievements.put(159, new MapleAchievement("have exchanged and equipped Yellow Belt [Dojo] (Grey)", 300, false));
        achievements.put(160, new MapleAchievement("have exchanged and equipped Blue Belt [Dojo] (Magenta)", 600, false));
        //achievements.put(161, new MapleAchievement("have exchanged and equipped Red Belt [Dojo] (Cyan)", 900, false));
        achievements.put(162, new MapleAchievement("have exchanged and equipped Black Belt [Dojo] (Cyan)", 1200, false));
        //achievements.put(163, new MapleAchievement("have exchanged and equipped So Gong's Gloves [Dojo] (Orange)", 1800, false));
        //achievements.put(164, new MapleAchievement("have exchanged and equipped Mu Gong's Gloves [Dojo] (White)", 2200, false));
        //achievements.put(165, new MapleAchievement("have exchanged and equipped Hero's Gloves [Dojo] (Blue)", 3000, false));
        //achievements.put(166, new MapleAchievement("have exchanged and equipped Pink Zakum Helmet [As Pinky As Pie]", 400, false));
        achievements.put(167, new MapleAchievement("cleared Mu Lung Dojo [Vanquisher]", 300, false));
        achievements.put(168, new MapleAchievement("earned your first Jumping Quest Point [Agility] (Brown)", 50, false));
        achievements.put(169, new MapleAchievement("earned a total of 10 Jumping Quest Point [Agility] (Grey)", 180, false));
        achievements.put(170, new MapleAchievement("earned a total of 30 Jumping Quest Point [Agility] (Magenta)", 500, false));
        achievements.put(171, new MapleAchievement("earned a total of 70 Jumping Quest Point [Agility] (Cyan)", 1200, false));
        achievements.put(172, new MapleAchievement("earned a total of 120 Jumping Quest Point [Agility] (Orange)", 2300, false));
        achievements.put(173, new MapleAchievement("earned a total of 200 Jumping Quest Point [Agility] (White)", 4000, false));
        achievements.put(174, new MapleAchievement("you have a total of 1 meso your bank [Coinage] (Brown)", 2, false));
        achievements.put(175, new MapleAchievement("you have a total of 1,000 mesos in your bank [Coinage] (Grey)", 10, false));
        achievements.put(176, new MapleAchievement("you have a total of 100,000 mesos in your bank [Coinage] (Magenta)", 20, false));
        achievements.put(177, new MapleAchievement("you have a total of 1,000,000 mesos in your bank [Coinage] (Cyan)", 30, false));
        achievements.put(178, new MapleAchievement("you have a total of 10,000,000 mesos in your bank [Coinage] (Orange)", 50, false));
        achievements.put(179, new MapleAchievement("you have a total of 100,000,000 mesos in your bank [Coinage] (White)", 120, false));
        achievements.put(180, new MapleAchievement("you have a total of 1,000,000,000 mesos in your bank [Coinage] (Blue)", 300, false));
        achievements.put(181, new MapleAchievement("you have a total of 2,147,483,647 mesos in your bank [Coinage] (Purple)", 750, false));
        achievements.put(182, new MapleAchievement("you have a total of 1 ability point in your ability point bank [Saver] (Brown)", 2, false));
        achievements.put(183, new MapleAchievement("you have a total of 100 ability points in your ability point bank [Saver] (Grey)", 100, false));
        achievements.put(184, new MapleAchievement("you have a total of 1000 ability points in your ability point bank [Saver] (Magenta)", 1000, false));
        achievements.put(185, new MapleAchievement("you have a total of 30000 ability points in your ability point bank [Saver] (Cyan)", 3000, false));
        achievements.put(186, new MapleAchievement("you have a total of 100000 ability points in your ability point bank [Saver] (Orange)", 10000, false));
        achievements.put(187, new MapleAchievement("you have reached 200 Fatigue [Overclocked]", 600, false)); // 164 Achievements
    }

    public static MapleAchievements getInstance() {
        return instance;
    }

    public MapleAchievement getById(int id) {
        return achievements.get(id);
    }

    public Integer getByMapleAchievement(MapleAchievement ma) {
        for (Entry<Integer, MapleAchievement> achievement : this.achievements.entrySet()) {
            if (achievement.getValue() == ma) {
                return achievement.getKey();
            }
        }
        return null;
    }
}