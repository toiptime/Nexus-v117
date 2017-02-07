/*
All Time Temple portal
*/

var quest;
var tomap;
var uncompletedmap;

function enter(pi) {
    switch (pi.getMapId()) {
	// Green area
        case 270010100:
            quest = 3501;
            tomap = 270010110;
            uncompletedmap = 270010000;
			pi.warp(tomap, "out00");
            break;
        case 270010200:
            quest = 3502;
            tomap = 270010210;
            uncompletedmap = 270010110;
			pi.warp(tomap, "out00");
            break;
        case 270010300:
            quest = 3503;
            tomap = 270010310;
            uncompletedmap = 270010210;
			pi.warp(tomap, "out00");
            break;
        case 270010400:
            quest = 3504;
            tomap = 270010410;
            uncompletedmap = 270010310;
			pi.warp(tomap, "out00");
            break;
        case 270010500:
            quest = 3507;
            tomap = 270020000;
            uncompletedmap = 270010410;
			pi.warp(tomap, "out00");
            break;

        // Blue area
        case 270020100:
            quest = 3508;
            tomap = 270020110;
            uncompletedmap = 270020000;
			pi.warp(tomap, "out00");
            break;
        case 270020200:
            quest = 3509;
            tomap = 270020210;
            uncompletedmap = 270020110;
			pi.warp(tomap, "out00");
            break;
        case 270020300:
            quest = 3510;
            tomap = 270020310;
            uncompletedmap = 270020210;
			pi.warp(tomap, "out00");
            break;
        case 270020400:
            quest = 3511;
            tomap = 270020410;
            uncompletedmap = 270020310;
			pi.warp(tomap, "out00");
            break;
        case 270020500:
            quest = 3514;
            tomap = 270030000;
            uncompletedmap = 270020410;
			pi.warp(tomap, "out00");
            break;

        // Red zone
        case 270030100:
            quest = 3515;
            tomap = 270030110;
            uncompletedmap = 270030000;
			pi.warp(tomap, "out00");
            break;
        case 270030200:
            quest = 3516;
            tomap = 270030210;
            uncompletedmap = 270030110;
			pi.warp(tomap, "out00");
            break;
        case 270030300:
            quest = 3517;
            tomap = 270030310;
            uncompletedmap = 270030210;
			pi.warp(tomap, "out00");
            break;
        case 270030400:
            quest = 3518;
            tomap = 270030410;
            uncompletedmap = 270030310;
			pi.warp(tomap, "out00");
            break;
        case 270030500:
            quest = 3521;
            tomap = 270040000;
            uncompletedmap = 270030410;
			pi.warp(tomap, "out00");
            break;

        case 270040000:
            if (pi.haveItem(4032002)) {
				pi.playPortalSE();
                pi.warp(270040100, "out00");
                pi.playerMessage("Now moving to a deep part of the temple.");
                return true;
            } else {
			if (pi.haveItem(4000451, 10) && pi.haveItem(4000446, 10) && pi.haveItem(4000456, 10) && pi.haveItem(4000460, 1) && pi.haveItem(4000461, 1) && pi.haveItem(4000462, 1)) {
                                //Masks
				pi.gainItem(4000451, -10);
				pi.gainItem(4000446, -10);
				pi.gainItem(4000456, -10);
				//Helmets
				pi.gainItem(4000460, -1);
				pi.gainItem(4000461, -1);
				pi.gainItem(4000462, -1);
				//Marble of chaos
				pi.gainItem(4032002, 1);
				pi.playerMessage("Here you go, take your Marble of Chaos");
                return false;
            } else {
			pi.playerMessage("You do not have the required items. You need 10 Frowny,Happy,Neutral Masks and 1 Guardian's Horn,Whale's Helmet,Knight's Mask");
			}
    }
	    break;
    return true;
}
}