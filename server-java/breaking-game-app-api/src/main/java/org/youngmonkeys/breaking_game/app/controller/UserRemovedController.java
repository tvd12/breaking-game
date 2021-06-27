package org.youngmonkeys.breaking_game.app.controller;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.core.annotation.EzyEventHandler;
import com.tvd12.ezyfoxserver.context.EzyAppContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractAppEventController;
import com.tvd12.ezyfoxserver.event.EzyUserRemovedEvent;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import com.tvd12.gamebox.entity.LocatedRoom;
import org.youngmonkeys.breaking_game.app.constant.Commands;
import org.youngmonkeys.breaking_game.app.service.GameService;

import java.util.List;

import static com.tvd12.ezyfoxserver.constant.EzyEventNames.USER_REMOVED;

@EzySingleton
@EzyEventHandler(USER_REMOVED) // refer EzyEventType
public class UserRemovedController
		extends EzyAbstractAppEventController<EzyUserRemovedEvent> {

	@EzyAutoBind
	private GameService gameService;

	@EzyAutoBind
	private EzyResponseFactory responseFactory;

	@Override
	public void handle(EzyAppContext ctx, EzyUserRemovedEvent event) {
		logger.info("breaking-game app: user {} removed", event.getUser());
		LocatedRoom room;
		List<String> playerNames;
		String playerName = event.getUser().getName();
		synchronized (gameService) {
			room = gameService.removePlayer(playerName);
		}
		if(room == null) {
			return;
		}
		synchronized (room) {
			playerNames = room.getPlayerManager().getPlayerNames();
		}
		responseFactory.newObjectResponse()
			.command(Commands.PLAYER_EXIT_GAME)
			.param("playerName", playerName)
			.usernames(playerNames)
			.execute();
	}
	
}
