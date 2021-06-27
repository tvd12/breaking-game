using System.Collections.Generic;
using UnityEngine;

public class GameManager
{
    private IDictionary<int, GameObject> gameObjectById;
    private IDictionary<GameObject, int> gameObjectTypes;
    private IDictionary<GameObject, Vector3> gameObjectPositions;
    private IDictionary<GameObject, Vector3> gameObjectPositionsChanged;
    private static readonly GameManager INSTANCE = new GameManager();
    private GameManager()
    {
        gameObjectById = new Dictionary<int, GameObject>();
        gameObjectPositions = new Dictionary<GameObject, Vector3>();
        gameObjectPositionsChanged = new Dictionary<GameObject, Vector3>();
        gameObjectTypes = new Dictionary<GameObject, int>();
    }

    public static GameManager getInstance()
    {
        return INSTANCE;
    }

    public void startGame()
    {
        SocketClientProxy.getInstance().startGame();
    }

    public void finishGame()
    {
        SocketClientProxy.getInstance().finishGame();
    }

    public void addGameObject(int type, GameObject gameObject)
    {
        var instanceId = gameObject.GetInstanceID();
        gameObjectById[instanceId] = gameObject;
        gameObjectPositions[gameObject] = Vector3.zero;
        gameObjectTypes[gameObject] = type;
    }

    public GameObject getGameObject(int id)
    {
        return gameObjectById[id];
    }

    public void syncGameObjectPositions()
    {
        var socketClientProxy = SocketClientProxy.getInstance();
        foreach (var obj in gameObjectPositions.Keys)
        {
            var position = gameObjectPositions[obj];
            if(obj == null || !obj.activeSelf)
            {
                gameObjectPositionsChanged[obj] = position;
                continue;
            }
            var objectPosition = obj.transform.position;
            if (!objectPosition.Equals(position))
            {
                gameObjectPositionsChanged[obj] = objectPosition;
            }
        }
        foreach (var obj in gameObjectPositionsChanged.Keys)
        {
            if (obj == null || !obj.activeSelf)
            {
                gameObjectById.Remove(obj.GetInstanceID());
                gameObjectPositions.Remove(obj);
                gameObjectTypes.Remove(obj);
                continue;
            }
            var position = gameObjectPositionsChanged[obj];
            gameObjectPositions[obj] = position;
            socketClientProxy.syncPosition(
                obj.name,
                gameObjectTypes[obj],
                obj.GetInstanceID(),
                true,
                position.x,
                position.y,
                position.z
            );
        }
        gameObjectPositionsChanged.Clear();
    }

    public void clear()
    {
        gameObjectById.Clear();
        gameObjectPositions.Clear();
    }
}