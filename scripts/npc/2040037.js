/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy 
Matthias Butz 
Jan Christian Meyer 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation version 3 as published by
the Free Software Foundation. You may not use, modify or distribute
this program under any other version of the GNU Affero General Public
License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see .
*/

/* JukeBox NPC
Computer - 1052013
*/

var status = 0;

function start() {
status = -1;
action(1, 0, 0);
}

function action(mode, type, selection) {
if (mode == -1) {
cm.dispose();
} else {
if (mode == 0 && status == 0) {
cm.dispose();
return;
}
if (mode == 1)
status++;
else
status--;
if (status == 0) {
cm.sendSimple("I can play some songs.#b\r\n#L0#FloralLife#l\r\n#L1#BadGuys#l\r\n#L2#Nightmare#l\r\n#L3#MissingYou#l\r\n#L4#PlayWithMe#l\r\n#L5#WhiteChristmas#l\r\n#L6#UponTheSky#l\r\n#L7#Shinin'Harbor#l\r\n#L8#Ariant#l\r\n#L9#ComeWithMe#l\r\n#L10#Fantasia#l\r\n#L11#Aquarium#l\r\n#L12#CokeTown#l\r\n#L13#Leafre#l\r\n#L14#Amoria#l\r\n#L15#Chapel#l\r\n#L16#FirstStepMaster#l\r\n ");
} else if (status == 1) {
if (selection == 0) {
cm.playMusic("Bgm00/FloralLife");
cm.dispose();
} else if (selection == 1) {
cm.playMusic("Bgm01/BadGuys");
cm.dispose();
} else if (selection == 2) {
cm.playMusic("Bgm00/Nightmare");
cm.dispose();
} else if (selection == 3) {
cm.playMusic("Bgm02/MissingYou");
cm.dispose();
} else if (selection == 4) {
cm.playMusic("Bgm04/PlayWithMe");
cm.dispose();
} else if (selection == 5) {
cm.playMusic("Bgm04/WhiteChristmas");
cm.dispose();
} else if (selection == 6) {
cm.playMusic("Bgm04/UponTheSky");
cm.dispose();
} else if (selection == 7) {
cm.playMusic("Bgm04/Shinin'Harbor");
cm.dispose();
} else if (selection == 8) {
cm.playMusic("Bgm14/Ariant");
cm.dispose();
} else if (selection == 9) {
cm.playMusic("Bgm06/ComeWithMe");
cm.dispose();
} else if (selection == 10) {
cm.playMusic("Bgm07/Fantasia");
cm.dispose();
} else if (selection == 11) {
cm.playMusic("Bgm11/Aquarium");
cm.dispose();
} else if (selection == 12) {
cm.playMusic("Bgm13/CokeTown");
cm.dispose();
} else if (selection == 13) {
cm.playMusic("Bgm13/Leafre");
cm.dispose();
} else if (selection == 14) {
cm.playMusic("BgmGL/amoria");
cm.dispose();
} else if (selection == 15) {
cm.playMusic("BgmGL/chapel");
cm.dispose();
} else if (selection == 16) {
cm.playMusic("BgmJp/FirstStepMaster");
cm.dispose();
}
}
}
}