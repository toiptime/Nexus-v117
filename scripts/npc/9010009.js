rewards = [["Ryko", 1902018, 5], ["Wolf Saddle", 1912011, 5], ["Spectrum Goggles", 1022082, 5], ["Raccoon Mask", 1022058, 5], ["White Raccoon Mask", 1022060, 5], ["Archeologist Glasses", 1022089, 5], ["Silver Deputy Star", 1122014, 5], ["Blizzard Stick", 1702211, 10], ["Patriot Seraphim", 1702187, 10], ["Seraphim Cape", 1102222, 10], ["Timeless MoonLight", 1102172, 5], ["BlackFist Cloak", 1102206, 5], ["Angry Mask", 1012110, 7], ["Sad Mask", 1012111, 7], ["Crying Mask", 1012109, 7], ["Happy Mask", 1012108, 7], ["Strawberry Popsicle", 1012070, 10],
    ["Chocolate Popsicle", 1012071, 10]];

maps = [["#gForest of Tenacity : Stage 1 - 2 [Easy]#k", 910530000], ["#bForest of Tenacity : Stage 3 - 4 [Normal]#k", 910530100], ["#rForest of Tenacity : Stage 5 - 7 [Hard]#k", 910530200], ["#bForest of Endurance : Stage 1 - 2 [Normal]#k", 910130000], ["#rForest of Endurance : Stage 3 - 5 [Hard]#k", 910130100]];

var option = null;
var status = 0;

function start() {
    if (cm.getPlayer().getClient().getChannel() != 1) {
        cm.sendOk("Jumping Quest may only be attempted on channel 1.");
        cm.dispose();
        return;
    }
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status == 0 && mode == 0) {
            cm.dispose();
            return;
        }
    }

    if (mode == 1)
        status++;

    else
        status--;
    if (status == 0) {
        cm.sendSimple("Hello #b#h ##k,\r\n\r\nYou currently have #g" + cm.getJQPoints() + "#k Jumping Quest Points.\r\n#b#L100#I would like to participate in Jumping Quest#l");
    } else if (status == 1) {
        if (selection == 100) {
            var talk = "Choose your destination.\r\n\r\n#g[Easy] 1 Jumping Quest Point#k\r\n#b[Normal] 2 Jumping Quest Points#k\r\n#r[Hard] 3 Jumping Quest Points#k\r\n\r\n#rThere will be an entrance fee of 10 NX.#b";
            for (var j = 0; j < maps.length; j++)
                talk += "\r\n#L" + j + "#" + maps[j][0] + "#l";
            cm.sendSimple(talk);
            option = true;
        } else if (selection == 101) {
            var talk = "Choose your destination.\r\n\r\n#g[Easy] 1 Jumping Quest Point#k\r\n#b[Normal] 2 Jumping Quest Point)#k\r\n#d[Hard] 3 Jumping Quest Points#k\r\nYou have #r" + cm.getJQPoints() + "#k JQ Points#b";
            for (var i = 0; i < rewards.length; i++)
                talk += "\r\n#L" + i + "#" + rewards[i][0] + " #i" + rewards[i][1] + ":# - " + rewards[i][2] + " JQ Points#l";
            cm.sendSimple(talk);
            option = true;
        }
    } else if (status == 2) {
        if (option == true) {//Map Warper
            if (cm.checkNX() >= 1000) {
                cm.gainNXCredit(-10)
                cm.warp(maps[selection][1], 0);
				cm.gainItem(4032024, 1);
                cm.sendOk("Make it to the end of your destination to receive your #gJumping Quest Points#k!");
                cm.dispose();
            } else {
                cm.sendOk("You don't have enough NX to enter.");
                cm.dispose();
            }
        } else if (option == true) {//Exchanger
            if (cm.getJQPoints() >= rewards[selection][2]) {
                cm.sendOk("Here you go!, enjoy your prize #h #.");
                cm.gainItem(rewards[selection][1], 1);
                cm.gainJQPoints(-rewards[selection][2]);
                cm.dispose();
            } else {
                cm.sendOk("You don't have enough JQ Points");
                cm.dispose();
            }
        }
    }
}