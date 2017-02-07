var Message = new Array(
    "Use the command @help to view a list of available commands.",
    "Vote for Project Nexus to get rewards.",
    "If you have found any bugs or glitches, please report it to the Forums.",
	"If you have found any hackers in game, please report it to the Forums.",
    "Use the command @dispose if your character is stucked.",
    "Use the command @shop to buy common items.",
	"Be sure to check out the Froums for the latest news and updates.",
	"Did you know that you can earn NX from achievements?",
    "Challenge yourself in Mu Lung Dojo and exchange for rewards.",
    "Use the command @check to check your information.",
    "Event Prizes are Event Trophy.",
	"You can buy NX item by using the command @nx",
	"You can buy NX by using the command @buynx",
	"Feeling Strong? Then try the Boss Party Quest by using the command @boss",
	"Get your daily 7th Anniversary Coin reward by using the command @daily.",
    "You can craft item by using the command @craft",
    "You can get between 1 to 7 NX at a 1% chance by killing some of the boss.");

var setupTask;

function init() {
    scheduleNew();
}

function scheduleNew() {
    setupTask = em.schedule("start", 900000);
}

function cancelSchedule() {
	setupTask.cancel(false);
}

function start() {
    scheduleNew();
    em.broadcastYellowMsg("[" + em.getChannelServer().getServerName() + " Tip] " + Message[Math.floor(Math.random() * Message.length)]);
}