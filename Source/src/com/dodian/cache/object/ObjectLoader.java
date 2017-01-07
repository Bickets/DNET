package com.dodian.cache.object;

import java.io.IOException;
import java.util.logging.Logger;

import com.dodian.cache.Cache;
import com.dodian.cache.InvalidCacheException;
import com.dodian.cache.index.impl.MapIndex;
import com.dodian.cache.index.impl.StandardIndex;
import com.dodian.cache.map.LandscapeListener;
import com.dodian.cache.map.LandscapeParser;
import com.dodian.cache.obj.ObjectDefinitionListener;
import com.dodian.cache.obj.ObjectDefinitionParser;
import com.dodian.cache.region.RegionManager;
import com.dodian.cache.region.Regions;
import com.dodian.uber.game.model.Position;

/**
 * Manages all of the in-game objects.
 * 
 * @author Graham Edgecombe
 * 
 */
public class ObjectLoader implements LandscapeListener, ObjectDefinitionListener {

  /**
   * Logger instance.
   */
  private static final Logger logger = Logger.getLogger(ObjectLoader.class.getName());

  /**
   * The number of definitions loaded.
   */
  private int definitionCount = 0;

  /**
   * The count of objects loaded.
   */
  private int objectCount = 0;

  /**
   * Loads the objects in the map.
   * 
   * @throws java.io.IOException
   *           if an I/O error occurs.
   * @throws com.rs2.cache.InvalidCacheException
   *           if the cache is invalid.
   */
  public void load() throws IOException, InvalidCacheException {
    Cache cache = Cache.getSingleton();
    try {
      logger.info("Loading definitions...");
      StandardIndex[] defIndices = cache.getIndexTable().getObjectDefinitionIndices();
      new ObjectDefinitionParser(cache, defIndices, this).parse();
      logger.info("Loaded " + definitionCount + " object definitions.");
      logger.info("Loading map...");
      MapIndex[] mapIndices = cache.getIndexTable().getMapIndices();
      for (MapIndex index : mapIndices) {
        new LandscapeParser(cache, index.getIdentifier(), this).parse();
      }
      logger.info("Loaded " + objectCount + " objects.");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      cache.close();
    }

  }

  public static void removeUnnecessaryClipping() {
    /*
     * ObjectHandler.getInstance().removeClip(3363, 9646, 2, 10, 0);
     * ObjectHandler.getInstance().removeClip(3366, 9646, 2, 10, 0);
     * ObjectHandler.getInstance().removeClip(3363, 9630, 2, 10, 0);
     * ObjectHandler.getInstance().removeClip(3366, 9630, 2, 10, 0);
     * 
     * ObjectHandler.getInstance().removeClip(3360, 9643, 0, 10, 0);
     * ObjectHandler.getInstance().removeClip(3359, 9644, 0, 10, 0);
     * ObjectHandler.getInstance().removeClip(3366, 9643, 0, 10, 0);
     * ObjectHandler.getInstance().removeClip(3367, 9644, 0, 10, 0);
     * ObjectHandler.getInstance().removeClip(3366, 9637, 0, 10, 0);
     * ObjectHandler.getInstance().removeClip(3367, 9636, 0, 10, 0);
     * ObjectHandler.getInstance().removeClip(3360, 9637, 0, 10, 0);
     * ObjectHandler.getInstance().removeClip(3359, 9636, 0, 10, 0);
     */
  }

  @Override
  public void objectParsed(CacheObject obj) {
    objectCount++;
    RegionManager.getRegion(obj.getLocation().getX(), obj.getLocation().getY()).getGameObjects().add(obj);
  }

  @Override
  public void objectDefinitionParsed(GameObjectData def) {
    definitionCount++;
    GameObjectData.addDefinition(def);
  }

  public static CacheObject object(int x, int y, int z) {
    final Position loc = new Position(x, y, z);
    Regions r = RegionManager.getRegion(x, y);
    for (CacheObject go : r.getGameObjects()) {
      if (go.getLocation().equals(loc)) {
        return go;
      }
    }
    return null;
  }

  public static CacheObject object(int id, int x, int y, int z) {
    final Position loc = new Position(x, y, z);
    Regions r = RegionManager.getRegion(x, y);
    for (CacheObject go : r.getGameObjects()) {
      if (go.getDef().getId() == id && go.getLocation().equals(loc)) {
        return go;
      }
    }
    return null;
  }

  public static CacheObject object(String name, int x, int y, int z) {
    final Position loc = new Position(x, y, z);
    Regions r = RegionManager.getRegion(x, y);
    for (CacheObject go : r.getGameObjects()) {
      final String objectName = GameObjectData.forId(go.getDef().getId()) != null
          ? GameObjectData.forId(go.getDef().getId()).getName().toLowerCase() : "";
      if (objectName.contains(name.toLowerCase()) && go.getLocation().equals(loc)) {
        return go;
      }
    }
    return null;
  }

}
