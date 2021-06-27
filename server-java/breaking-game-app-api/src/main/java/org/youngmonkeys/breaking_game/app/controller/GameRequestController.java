package org.youngmonkeys.breaking_game.app.controller;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.core.annotation.EzyDoHandle;
import com.tvd12.ezyfox.core.annotation.EzyRequestController;
import com.tvd12.ezyfox.exception.BadRequestException;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfox.io.EzyLists;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import com.tvd12.gamebox.constant.PlayerRole;
import com.tvd12.gamebox.entity.LocatedPlayer;
import com.tvd12.gamebox.entity.LocatedRoom;
import org.youngmonkeys.breaking_game.app.constant.Commands;
import org.youngmonkeys.breaking_game.app.constant.Errors;
import org.youngmonkeys.breaking_game.app.request.SyncDataRequest;
import org.youngmonkeys.breaking_game.app.request.SyncPositionRequest;
import org.youngmonkeys.breaking_game.app.service.GameService;

import java.util.Collections;
import java.util.List;

@EzyRequestController
public class GameRequestController {

	@EzyAutoBind
	private GameService gameService;

	@EzyAutoBind
	private EzyResponseFactory responseFactory;

	@EzyDoHandle(Commands.ACCESS_GAME)
	public void accessGame(EzyUser user) {
		LocatedRoom room;
		LocatedPlayer player;
		synchronized (gameService) {
			room = gameService.findAvailableRoom(user);
			player = gameService.getPlayer(user.getName());
		}
		List<String> playerNames;
		synchronized (room) {
			playerNames = room.getPlayerManager().getPlayerNames();
		}
		responseFactory.newObjectResponse()
			.command(Commands.ACCESS_GAME)
			.param("roomId", room.getId())
			.param("master", player.getRole() == PlayerRole.MASTER)
			.param("playerNames", playerNames)
			.user(user)
			.execute();
		responseFactory.newObjectResponse()
			.command(Commands.PLAYER_ACCESS_GAME)
			.param("playerName", user.getName())
			.usernames(EzyLists.filter(playerNames, it -> !it.equals(user.getName())))
			.execute();
	}

	@EzyDoHandle(Commands.SYNC_POSITION)
	public void synPosition(EzyUser user, SyncPositionRequest request) {
		LocatedPlayer player;
		synchronized (gameService) {
			player = gameService.getPlayer(user.getName());
		}
		if(player == null) {
			throw new BadRequestException(Errors.UNKNOWN, "you're a hacker");
		}
		LocatedRoom room;
		synchronized (gameService) {
			room = gameService.getRoom(player.getCurrentRoomId());
		}
		List<String> playerNames;
		synchronized (room) {
			playerNames = room.getPlayerManager().getPlayerNames();
		}
		responseFactory.newArrayResponse()
			.command(Commands.SYNC_POSITION)
			.data(request)
			.usernames(EzyLists.filter(playerNames, it -> !it.equals(user.getName())))
			.execute();
	}

	@EzyDoHandle(Commands.SYNC_DATA)
	public void synData(EzyUser user, SyncDataRequest request) {
		LocatedPlayer player;
		synchronized (gameService) {
			player = gameService.getPlayer(user.getName());
		}
		if(player == null) {
			throw new BadRequestException(Errors.UNKNOWN, "you're a hacker");
		}
		List<String> playerNames;
		if(request.getTo() != null) {
			playerNames = Collections.singletonList(request.getTo());
		}
		else {
			LocatedRoom room;
			synchronized (gameService) {
				room = gameService.getRoom(player.getCurrentRoomId());
			}
			synchronized (room) {
				playerNames = room.getPlayerManager().getPlayerNames();
			}
		}
		responseFactory.newObjectResponse()
			.command(Commands.SYNC_DATA)
			.data(
				EzyEntityFactory.newObjectBuilder()
					.append("command", request.getCommand())
					.append("data", request.getData())
					.build()
			)
			.usernames(EzyLists.filter(playerNames, it -> !it.equals(user.getName())))
			.execute();
	}
}
