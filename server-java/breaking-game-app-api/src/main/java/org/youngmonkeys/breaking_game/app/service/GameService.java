package org.youngmonkeys.breaking_game.app.service;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import com.tvd12.gamebox.constant.PlayerRole;
import com.tvd12.gamebox.entity.LocatedPlayer;
import com.tvd12.gamebox.entity.LocatedRoom;
import com.tvd12.gamebox.manager.*;

@EzySingleton
public class GameService {
    private final RoomManager<LocatedRoom> roomManager =
        new DefaultRoomManager<>();
    private final PlayerManager<LocatedPlayer> playerManager =
        new DefaultPlayerManager<>();

    public LocatedRoom findAvailableRoom(EzyUser user) {
        LocatedPlayer player = new LocatedPlayer(user.getName());

        LocatedRoom room = roomManager.getRoom(it ->
            it.getPlayerManager().getPlayerCount() < 2
        );
        if(room == null) {
            room = LocatedRoom.builder()
                .defaultUserManager(2)
                .build();
            room.addUser(user, player);
            room.getPlayerManager().setMaster(player);
            roomManager.addRoom(room);
            player.setRole(PlayerRole.MASTER);
        }
        else {
            room.addUser(user, player);
            player.setRole(PlayerRole.PLAYER);
        }
        player.setCurrentRoomId(room.getId());
        playerManager.addPlayer(player);
        return room;
    }

    public LocatedRoom getRoom(long roomId) {
        return roomManager.getRoom(roomId);
    }

    public LocatedPlayer getPlayer(String username) {
        return playerManager.getPlayer(username);
    }

    public LocatedRoom removePlayer(String username) {
        LocatedPlayer player = playerManager.getPlayer(username);
        if(player == null)
            return null;
        LocatedRoom room = roomManager.getRoom(player.getCurrentRoomId());
        if(room != null) {
            synchronized (room) {
                room.removePlayer(player);
                if (room.getPlayerManager().isEmpty()) {
                    roomManager.removeRoom(room);
                } else {
                    LocatedPlayer master = room.getPlayerManager().setNewMaster();
                    master.setRole(PlayerRole.MASTER);
                }
            }
        }
        return room;
    }
}
