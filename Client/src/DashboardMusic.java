import java.io.File;
import java.util.ArrayList;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import sign.signlink;

/**
 * 
 * @author Dashboard
 *
 */
public class DashboardMusic {

  public static final int MUSIC_TAB = 962;
  public static final int SCROLL_PANE = 4262;
  public static final int[] MUSIC_TEXT_ALLOWED = { 963, 8934, 6272, 6271, 9926, 5450, 4439, 3206, 5449, 11941, 4287,
      4288, 4289, 4290, 11134, 4291, 4292, 4293, 4294, 4295, 4296, 8935, 4297, 4298 };

  public static final int[] MUSIC_LINES = { 11941, 4287, 4288, 4289, 4290, 11134, 4291, 4292, 4293, 4294, 4295, 4296,
      8935, 4297, 4298 };

  private static ArrayList<Song> songList;

  private static boolean enabled = true;
  private static boolean isManual = false;
  private static boolean isPlaying = false;
  private static int currentSong = -1;

  private static Sequencer sequencer;

  public static boolean textAllowed(int id) {
    for (int text : MUSIC_TEXT_ALLOWED) {
      if (text == id)
        return true;
    }
    return false;
  }

  public static void buildMusicTab() {
    songList = new ArrayList<Song>();
    songList.add(new Song("Victorious Days", 0, "the Legend's Guild"));
    songList.add(new Song("In Reverence", 1, "the Legend's Guild Dungeon"));
    songList.add(new Song("The Town of Witchwoode", 2, "Taverley"));
    songList.add(new Song("Under The Bards Tree", 3, "Seers Village"));
    songList.add(new Song("In The Cave", 4, "the Ancient Dungeon"));
    songList.add(new Song("The Heroes Return", 5, "Yanille"));
    songList.add(new Song("Proud Warriors", 6, "the Yanille Dungeon"));
    songList.add(new Song("Medieval Banquet", 7, "Ardougne"));
    songList.add(new Song("The Long Journey Home", 8, "the Outerworld", true));
    songList.add(new Song("A Jetty On The Lake", 9, "Catherby"));
    songList.add(new Song("Valley Of The Clouds", 10, "the Fishing Guild"));
    songList.add(new Song("Lottyr Lady Of The Hells", 11, "the Dragon Cave"));
    songList.add(new Song("The Fairy Woods", 12, "the Gnome Stronghold"));
    songList.add(new Song("12th Warrior", 13, "the Ice Queen's lair"));
    songList.add(new Song("The Chaos Warrior", 14, "the Ardougne Slayer Area"));

    updateMusicTab();
  }

  public static void updateMusicTab() {
    for (int index = 0; index < songList.size(); index++) {
      Song song = songList.get(index);
      int lineId = MUSIC_LINES[index];
      song.setLineId(lineId);

      RSInterface line = RSInterface.interfaceCache[lineId];
      line.message = song.getName();
      line.aString228 = "";
      line.textColor = song.isUnlocked() ? 65280 : 0xff0000;
      line.width = line.textDrawingAreas.getTextWidth(song.getName()) + 4;
      line.tooltip = "@gre@Play Song @whi@" + song.getName();
    }
  }
  
  public static void enableSong(Client client, int songId, boolean enabled, boolean unlocked) {
    for (Song song : songList) {
      if (song.getId() == songId) {
        song.setUnlocked(enabled);
        updateMusicTab();
        if (unlocked) {
          client.pushMessage("<col=65280>Congratulations! You've unlocked the music track " + song.getName() + ".", 0, "");
        }
        break;
      }
    }
  }

  public static void clickSong(Client client, int buttonId) {
    Song clickedSong = null;
    for (Song song : songList) {
      if (song.getLineId() == buttonId) {
        clickedSong = song;
        break;
      }
    }

    if (clickedSong != null) {
      if (clickedSong.isUnlocked()) {
        client.musicToggle = 0;
        setEnabled(client.musicToggle);
        playSong(client, clickedSong, true, false);
      } else {
        client.pushMessage("<col=FF0000>Hint: This music track can be unlocked in " + clickedSong.getUnlockLocation() + ".", 0, "");
      }
    }
  }
  
  public static void regionMusic(Client client, int songId) {
    if (isManual && isPlaying) {
      return;
    }
    if (currentSong == songId && isPlaying) {
      return;
    }
    
    for (Song song : songList) {
      if (song.getId() == songId) {
        playSong(client, song, false, true);
        break;
      }
    }
  }

  public static void playSong(final Client client, Song song, boolean manual, boolean loop) {
    if (!enabled)
      return;
    isManual = manual;
    currentSong = song.getId();
    try {
      String musicLocation = signlink.findcachedir() + "/music/" + song.getName() + ".mid";
      Sequence sequence = MidiSystem.getSequence(new File(musicLocation));

      if (sequencer == null) {
        sequencer = MidiSystem.getSequencer();
      } else {
        sequencer.stop();
      }

      sequencer.open();
      sequencer.setSequence(sequence);
      if (loop)
        sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
      else
        sequencer.setLoopCount(0);

      // Start playing
      sequencer.start();
      isPlaying = true;

      sequencer.addMetaEventListener(new MetaEventListener() {
        public void meta(MetaMessage event) {
          if (event.getType() == 47) {
            isPlaying = false;
          }
        }
      });

    } catch (Exception e) {
      client.pushMessage("<col=FF0000>Unfortunately, the client cannot play this music track.", 0, "");
    }
  }

  public static void setEnabled(int value) {
    if (value == 0) {
      enabled = true;
    } else {
      enabled = false;
      sequencer.stop();
      isPlaying = false;
    }
  }

}