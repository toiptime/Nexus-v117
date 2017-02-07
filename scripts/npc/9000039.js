var status = -1;
var cat;
var prizes = [
[[2213042, 1, 24], [2213032, 1, 24], [2213043, 1, 24], [2213017, 1, 24], [2213019, 1, 24], [2003518, 1, 18], [2003519, 1, 12]], // Transform potions
[[2022282, 1, 3], [5680021, 2, 1], [1662002, 10, 1], [1662003, 10, 1], [1672007, 10, 1]], // Others
[[5570000, 1, 1], [5640000, 1, 1], [5534000, 1, 1], [5530068, 15, 1], [5530141, 70, 1], [5530158, 130, 1], [5590000, 15, 1], [2450059, 5, 1], [2450041, 8, 1], [2450043, 15, 1]], // Rares
[[4161050, 5, 1], [4031989, 10, 1]] // Crafting Items
];

function start() {
if (cm.haveItem(4000038, 1)) {
	cm.sendYesNo("Hello #b#h ##k,\r\nYou currently have #g#c4000038##k #i4000038#.\r\ Would you like to exchange your #gEvent Trophy#k for rewards?");
	//cm.sendOk("#rThis NPC is disabled until we can find all the event trophies");
	//cm.dispose();
	}else {
	cm.sendOk("You don't have an #r#t4000038# #i4000038##k.\r\nCome back to me when you have one.");
	cm.dispose();
}
}
function action(m, t, s) {
	if (m != 1) {
		cm.dispose();
	} else {
		status++;
		if (status == 0) {
			cm.sendSimple("What would you like to exchange for?\r\n#L1##bTransformation Potions#l\r\n#L2##dOthers#l\r\n#L3##gRares#k#l\r\n#L4##bCrafting Items#k#l");
		} else if (status == 1) {
			cat = s;
              if (cat == 1) {
				cm.sendSimple("Please choose the item that you wish to exchange.\r\n#L0##i2213042# 24 Von Leon Transformation Potions: #i4000038#\r\n#L1##i2213032# 24 Balrog Transformation Potions: #i4000038#\r\n#L2##i2213043# 24 Papulatus Transformations Potions: #i4000038#\r\n#L3##i2213017# 24 King Clang Transformations Potions: #i4000038#\r\n#L4##i2213019# 24 Muscle Stone Transformation Potions: #i4000038#\r\n#L5##i2003518# 18 Triple Snowdrop Giant Potions: #i4000038#\r\n#L6##i2003519# 12 Quintuple Snowdrop Giant Potions: #i4000038#");
			} else if (cat == 2) {
			    cm.sendSimple("Please choose the item that you wish to exchange.\r\n#L0##i2022282# 3 Naricain's Demon Elixir: #i4000038#\r\n#L1##i5680021# Chair Gachapon Ticket: #d2#k #i4000038#\r\n#L2##i1662002# Deluxe Android (M): #d10#k #i4000038#\r\n#L3##i1662003# Deluxe Android (F): #d10#k #i4000038#\r\n#L4##i1672007# Lidium Heart: #d10#k #i4000038#");
			} else if (cat == 3) {
			    cm.sendSimple("Please choose the item that you wish to exchange.\r\n#L0##i5570000# Vicious' Hammer: #d1#k #i4000038#\r\n#L1##i5640000# Pam's Song: #d1#k #i4000038#\r\n#L2##i5534000# Tim's Secret lab: #d1#k #i4000038#\r\n#L3##i5530068# Pendant of the Spirit Coupon (3-day): #d15#k #i4000038#\r\n#L4##i5530141# Pendant of the Spirit Coupon (15 Days): #d70#k #i4000038#\r\n#L5##i5530158# Pendant of the Spirit Coupon (30 Days): #d130#k #i4000038#\r\n#L6##i5590000# High-Five Stamp: #d15#k #i4000038#\r\n#L7##i2450059# 1.3X EXP Coupon (1 Hour): #d5#k #i4000038#\r\n#L8##i2450041# 1.5X EXP Coupon (1 Hour): #d8#k #i4000038#\r\n#L9##i2450043# 2x EXP Coupon (1 Hour): #d15#k #i4000038#");
			} else if (cat == 4) {
			    cm.sendSimple("Please choose the item that you wish to exchange.\r\n#L0##i4161050# Dragon Types and Characteristics (Vol.II): #d5#k #i4000038#\r\n#L1##i4031989# Pink Water Drop: #d10#k #i4000038#");
			}
		} else if (status == 2) {
			if (cat == 0) {
				if (s == 0) {
					if (cm.haveItem(4000038, 1)) {
						cm.gainItem(4000038, -1);
						cm.gainNXCredit(20000);
						cm.sendOk("You have exchanged for 10,000 NX.");
					} else {
						cm.sendOk("You don't have enough #i4000038#.");
					}
				} else if (s == 1) {
					if (cm.haveItem(4000038, 2)) {
						cm.gainItem(4000038, -2);
						cm.gainNXCredit(40000);
						cm.sendOk("You have exchanged for 20,000 NX.");
					} else {
						cm.sendOk("You don't have enough #i4000038#.");
					}
				} else if (s == 2) {
					if (cm.haveItem(4000038, 3)) {
						cm.gainItem(4000038, -3);
						cm.gainNXCredit(60000);
						cm.sendOk("You have exchanged for 30,000 NX.");
					} else {
						cm.sendOk("You don't have enough #i4000038#.");
					}
				}
				cm.dispose();
			} else {
				var prizeInfo = prizes[cat - 1][s];
				if (!cm.canHold(prizeInfo[0])) {
					cm.sendOk("You don't have enough inventory space.");
					cm.dispose();
					return;
				}
				if (cm.haveItem(4000038, prizeInfo[1])) {
					cm.gainItem(4000038, -prizeInfo[1]);
					cm.gainItem(prizeInfo[0], prizeInfo[2]);
					cm.sendOk("You have exchanged for the item.");
				} else {
					cm.sendOk("You don't have enough #i4000038#.");
				}
				cm.dispose();
			}
		}
	}
}