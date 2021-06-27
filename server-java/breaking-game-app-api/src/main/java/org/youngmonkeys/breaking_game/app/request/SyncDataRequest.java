package org.youngmonkeys.breaking_game.app.request;

import com.tvd12.ezyfox.binding.annotation.EzyArrayBinding;
import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import com.tvd12.ezyfox.entity.EzyObject;
import lombok.Data;

@Data
@EzyObjectBinding
public class SyncDataRequest {
	private String to;
	private String command;
	private Object data;
}
