
public class Song {
  
  private final String name;
  private final String unlockLocation;
  private boolean unlocked;
  private int lineId;
  private int id;
  
  public Song(String name, int id, String unlockLocation, boolean unlocked) {
    this.name = name;
    this.id = id;
    this.unlockLocation = unlockLocation;
    this.unlocked = unlocked;
    this.lineId = -1;
  }
  
  public Song(String name, int id, String unlockLocation) {
    this(name, id, unlockLocation, false);
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getUnlockLocation() {
    return this.unlockLocation;
  }
  
  public boolean isUnlocked() {
    return this.unlocked;
  }
  
  public void setUnlocked(boolean unlocked) {
    this.unlocked = unlocked;
  }
  
  public int getLineId() {
    return this.lineId;
  }
  
  public void setLineId(int lineId) {
    this.lineId = lineId;
  }
  
  public int getId() {
    return this.id;
  }
  
}
