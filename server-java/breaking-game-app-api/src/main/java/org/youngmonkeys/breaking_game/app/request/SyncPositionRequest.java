package org.youngmonkeys.breaking_game.app.request;

import com.tvd12.ezyfox.binding.annotation.EzyArrayBinding;
import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;

import lombok.Data;

@Data
@EzyArrayBinding
public class SyncPositionRequest {
	private String objectType;
	private int objectId;
	private double x;
	private double y;
	private double z;
}
