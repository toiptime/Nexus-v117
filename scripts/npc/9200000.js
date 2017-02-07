/* 	Item Exchange NPC
	Coded by Quiet_Crystal
*/

var status = 0;
var selectedItem = -1;
var totalMesoCost = 0;
var canBuy = true;

var onSale = [
	[1002553, [ //   === Genesis Bandana ===
		[1002419, 1], //     Mark of the Beta x1
		[4001359, 1], // Small Snail Shell x1
		[-1, 100000]]], // 100,000 Mesos
	[1000040, [ //   === Blizzard Helmet ===
		[4220148, 1], //   Portrait of an Orange Mushroom x1
		[-1, 300000]]], // 300,000 Mesos
	[1001060, [ //   === Snow Ice's Fur Hat ===
		[4220148, 1], //        Portrait of an Orange Mushroom  x1
		[-1, 300000]]], // 300,000 Mesos
	[1102246, [ //   === Blizzard Cape ===
		[4220148, 1], //      Portrait of an Orange Mushroom  x1
		[-1, 300000]]], // 300,000 Mesos
	[1082276, [ //   === Blizzard Gloves ===
	    [4220148, 1], //      Portrait of an Orange Mushroom  x1
		[-1, 300000]]], // 300,000 Mesos
		[1050169, [ //   === Blizzard Armour (Male) ===
	    [4220148, 1], //      Portrait of an Orange Mushroom  x1
		[-1, 300000]]], // 300,000 Mesos
		[1051210, [ //   === Blizzard Armour (Female) ===
	    [4220148, 1], //      Portrait of an Orange Mushroom  x1
		[-1, 300000]]], // 300,000 Mesos
		[1072447, [ //   === Blizzard Boots ===
	    [4220148, 1], //      Portrait of an Orange Mushroom  x1
		[-1, 300000]]], // 300,000 Mesos
		[1442106, [ //   === Blizzard Polearm ===
	    [4220148, 1], //      Portrait of an Orange Mushroom  x1
		[-1, 500000]]], // 500,000 Mesos
		[1003361, [ //   === Super Zakum Helmet ===
		[1002357, 1], //      Zakum Helmet x1
		[1003112, 1], // Chaos Zakum Helmet x1
		[1003439, 1], //      Pink Zakum Helmet x1
	    [4032133, 1], //      Zakum Diamond x1
		[4310020, 30], // Monster Park Commemorative Coin x30
		[-1, 5000000]]], // 5,000,000 Mesos
		[1122151, [ //   === Chaos Horntail Necklace (+2) ===
		[1122000, 1], //     Horntail Necklace x1
		[1122076, 1], // Chaos Horntail Necklace x1
		[4161049, 1], //     Dragon Types and Characteristics (Vol.I) x1
	    [4161050, 1], //     Dragon Types and Characteristics (Vol.II)
		[4310020, 40], // Monster Park Commemorative Coin x60
		[-1, 10000000]]], // 10,000,000 Mesos
		[1003450, [ //   === Pink Bean Hat ===
		[4032308, 1], //    Pink Candle x1
		[4310020, 50], // Monster Park Commemorative Coin x100
		[-1, 20000000]]], // 20,000,000 Mesos
		[1112585, [ //   === Angelic Blessing ===
		[2511106, 1], // Angelic Blessing Recipe
		[4011000, 20], //    Bronze Plate x20
		[4011003, 20], // Adamantium Plate x20
		[4011008, 10], // Lidium
		[-1, 5000000]]], // 5,000,000 Mesos
		[1112663, [ //   === White Angelic Blessing ===
		[2511123, 1], // White Angelic Blessing Recipe
		[1112585, 1], //    Angelic Blessing x1
		[4011001, 50], // Steel Plate x50
		[4021005, 50], // Sapphire x50
		[4021007, 20], // Diamond x20
		[-1, 10000000]]], // 10,000,000 Mesos
		[1122057, [ //   === Awakening Mind of Maple Necklace ===
		[4001249, 1], // Maple Necklace Gem
		[-1, 5000000]]], // 5,000,000 Mesos
		[1012070, [ //   === Strawberry Popsicle ===
		[2001001, 1000], // Ice Cream Pop
		[2022000, 1000], // Pure Water
		[4031124, 1], // Strawberry
		[-1, 100000]]], // 100,000 Mesos
		[1182005, [ //   === Spiegelmann's Gold Badge ===
		[4011001, 1], // Steel Plate x1
		[4011006, 1], // Gold Plate x1
		[4310020, 50], // Monster Park Commemorative Coin x100
		[-1, 5000000]]] // 5,000,000 Mesos
];

function start() {
    var selStr = "Hello #b#h ##k,\r\n\r\nWhat would you like me to craft?\r\n\r\n#bGenesis Bandana (21 Stats)\r\nBlizzard Item (42 Stats)\r\nBlizzard Polearm (100 Weapon Attack)\r\nSuper Zakum Helmet (500 Stats And 50 Attack)\r\n\Chaos Horntail Necklace (+2) (1000 Stats And 200 Attack)\r\nPink Bean Hat (3000 Stats And 300 Attack)\r\nAngelic Blessing (50 Attack)\r\nWhite Angelic Blessing (130 Attack)\r\nAwakening Mind of Maple Necklace (80 Attack)\r\nStrawberry Popsicle (30 Attack)\r\nSpiegelmann's Gold Badge (50 Attack)\r\n\r\nStats = STR / DEX / INT / LUK\r\nAttack = WA / MA\r\n\r\nAll items has 100 upgrade slots where applicable.#k";
    for (var i = 0; i < onSale.length; i++){
        selStr += "\r\n#L" + i + "# #i" + onSale[i][0] + "# - #t" + onSale[i][0] + "# #l\r\n";
    }
    cm.sendSimple(selStr);
}

function action(mode, type, selection) {
    if (mode > 0) {
        status++;
	} else if (mode == 0) {
		cm.sendOk("Come back to me if you wish to craft an item.");
		cm.dispose();
        return;
	} else {
        cm.dispose();
        return;
    }
    if (status == 1) {
        selectedItem = selection;
        var selStr = "I can craft you a #b#t" + onSale[selectedItem] + "##k if you provide me with the following items:\r\n";
		for (var i = 0; i < onSale[selectedItem][1].length; i++){
			if (onSale[selectedItem][1][i][0] == -1) {
				selStr += "\r\n#d" + onSale[selectedItem][1][i][1] + " Mesos#k";
			} else {
				selStr += "\r\n" + onSale[selectedItem][1][i][1] + "x #i" + onSale[selectedItem][1][i][0] + "# - #d#t" + onSale[selectedItem][1][i][0] + "##k\r\n";
			}
		}
		selStr += "\r\n\r\nWould you like me to craft now?";
    	cm.sendYesNo(selStr);
    } else if (status == 2) {
		for (var i = 0; i < onSale[selectedItem][1].length && canBuy; i++){
			if (onSale[selectedItem][1][i][0] == -1) {
				totalMesoCost += onSale[selectedItem][1][i][1]; // In case some retards attempt to put 2 entries of meso cost for the same object.
				canBuy = canBuy && cm.getMeso() >= totalMesoCost;
			} else {
				canBuy = canBuy && cm.haveItem(onSale[selectedItem][1][i][0], onSale[selectedItem][1][i][1]);
			}
		}
		if (canBuy) {
			for (var i = 0; i < onSale[selectedItem][1].length; i++){
				if (onSale[selectedItem][1][i][0] == -1) {
					cm.gainMeso(-onSale[selectedItem][1][i][1]);
				} else {
					cm.gainItem(onSale[selectedItem][1][i][0], -onSale[selectedItem][1][i][1]);
				}
			}
			cm.gainItem(onSale[selectedItem][0], 1);
			cm.sendOk("Enjoy your new item.");
			cm.dispose();
        	return;
		} else {
			cm.sendOk("Please come back to me if you have the items.");
			cm.dispose();
        	return;
		}
    }
}