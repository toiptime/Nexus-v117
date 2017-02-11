package client.messages.commands;

import client.MapleClient;
import constants.ServerConstants.CommandType;

public class CommandObject {
    private int gmLevelReq;
    private CommandExecute exe;

    public CommandObject(CommandExecute c, int gmLevel) {
        exe = c;
        gmLevelReq = gmLevel;
    }

    public int execute(MapleClient c, String[] splitted) {
        return exe.execute(c, splitted);
    }

    public CommandType getType() {
        return exe.getType();
    }

    public int getReqGMLevel() {
        return gmLevelReq;
    }
}