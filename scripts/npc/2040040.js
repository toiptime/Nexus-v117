function start() {
    cm.sendSimple("\r\n#g#L0##i4021007##l#k");
}

function action(mode, type, selection) {
    if (mode < 1) {
        cm.dispose();
        return;
    } else if (selection == 0) {
        cm.gainItem(4021007, 1);
		cm.gainItem(4032712, 1);
		cm.gainGP(50);
		cm.warp(100000000);
		cm.dispose();
}
}