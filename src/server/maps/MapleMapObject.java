
package server.maps;

import client.MapleClient;
import constants.GameConstants;

import java.awt.*;

public abstract class MapleMapObject {

    private Point position = new Point();
    private int objectId;

    public Point getPosition() {
        return new Point(position);
    }

    public void setPosition(Point position) {
        this.position.x = position.x;
        this.position.y = position.y;
    }

    public Point getTruePosition() {
        return position;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int id) {
        this.objectId = id;
    }

    public int getRange() {
        return GameConstants.maxViewRangeSq();
    }

    public abstract MapleMapObjectType getType();

    public abstract void sendSpawnData(final MapleClient client);

    public abstract void sendDestroyData(final MapleClient client);
}