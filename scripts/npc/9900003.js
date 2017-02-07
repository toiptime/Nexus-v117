//Starting Npc - Sera
function start() {
    cm.sendSimple("I'm a fagot");
}

function action(mode, type, selection) {
    cm.dispose();
    if (selection == 0) {
        cm.gainItem(1002419, 1); // Mark of the Beta
        cm.gainItem(2000005, 300); // Power Elixir
        cm.gainItem(5450000, 1); // Miu Miu
        cm.warp(800000000, 3);
        // cm.maxAllSkills();
        cm.sendOk("Enjoy your Starter Pack.");
        cm.dispose();
    }
}