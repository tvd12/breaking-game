package org.youngmonkeys.breaking_game.app.service;


import org.youngmonkeys.breaking_game.app.config.AppConfig;
import org.youngmonkeys.breaking_game.common.service.CommonService;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;

@EzySingleton
public class GreetingService {

	@EzyAutoBind
	private AppConfig appConfig;
	
	@EzyAutoBind
	private CommonService commonService;
	
	public String hello(String nickName) {
		return appConfig.getHelloPrefix() + " " + nickName + "!";
	}
	
	public String go(String nickName) {
		return appConfig.getGoPrefix() + " " + nickName + "!";
	}
	
}
