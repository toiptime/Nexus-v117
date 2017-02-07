var status = 0;

function start() {
    cm.sendNext("Hello #b#h ##k,\r\n\r\nDo you wish to collect your daily reward?");
}

function action(mode, type, selection) {
    if (mode == 0) {
        cm.sendOk("Fine, I'll give to other players if you don't want it..")
        cm.dispose();
    }else {
        if(mode > 0)
            status++;
        else if(mode < 0)
            cm.dispose();
        if (status == 1) {
            if (cm.getGiftLog('FreeGift') >= 1) {
                cm.sendOk("You have already collected your #i4310025#.\r\nPlease come back to me in the next 24 hours from your last collected time.");
                cm.dispose();
            }else
                cm.sendYesNo("You are eligible to collect your #i4310025#.\r\nDo you wish to collect now?");
        }else if (status == 2) {
            cm.gainItem(4310025, 1);
            cm.setBossLog('FreeGift');
            cm.sendOk("You have collected your #i4310025#.\r\nCome back to me in the next 24 hours to collect again.");
            cm.dispose();
        } else
            cm.sendOk("Come back to me if you wish to collect.")
    }
}