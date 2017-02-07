var status = 0;
var choice = ["Check My Bank Balance", "Withdraw Mesos From My Bank", "Deposit Mesos To My Bank"];
var sel;

function start() {
    var msg = "Hello #b#h ##k,\r\n\r\nWelcome to #dProject Nexus#k Bank, how may I serve you today?\r\n\r\n#r* The bank requires you to deposit 1 meso to your bank as a one-time deposit fee. If you have 2 mesos saved in your bank, you can only withdraw 1 meso.#k";
    for (var i = 0; i < choice.length; i++) 
        msg += "\r\n\t#L"+i+"#"+choice[i]+"#l";
    cm.sendSimple(msg);
}

function action(m, t, s) {
    if (m < 1) {
        cm.dispose();
        return;
    } else {
        status++;
    }
    if (status == 1) {
           sel = s;
        if (s == 0) {
            cm.sendOk("You have a total of #g" + cm.getMesosInBank() + "#k mesos in your bank currently.");
            cm.dispose();
        } else if (s == 1) {
            cm.sendGetNumber("How many mesos would you like to withdraw from your bank?\r\nYou currently have #g" + cm.getMesosInBank() + "#k mesos in your bank.", 1, 1, 2147483647);
        } else if (s == 2) {
            cm.sendGetNumber("How many mesos would you like to deposit to your bank?\r\nYou currently have #g"+ cm.getMesosInBank() + "#k mesos in your bank.", 1, 1, 2147483647);
        }
    } else if (status == 2) {
        if (sel == 1) {
            cm.withdrawMesosFromBank(s);
            cm.sendOk("You have successfully withdrawn #g" + s + "#k mesos from your bank.");
            cm.dispose();
        } else if (sel == 2) {
            cm.depositMesosToBank(s);
            cm.sendOk("You now have #g" + cm.getMesosInBank() + "#k mesos in your bank account.");
            cm.dispose();
        }
    }     
}  