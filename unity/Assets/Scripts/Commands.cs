using System;

public sealed class Commands
{

    public const String ACCESS_GAME = "accessGame";
	public const String PLAYER_ACCESS_GAME = "playerAccessGame";
	public const String PLAYER_EXIT_GAME = "playerExitGame";
	public const String SYNC_POSITION = "s";
	public const String SYNC_DATA = "syncData";

    private Commands()
    {
    }
}
