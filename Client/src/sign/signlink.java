// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   signlink.java

package sign;

import java.applet.Applet;
import java.io.*;
import java.net.*;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

public final class signlink implements Runnable {

	public static void startpriv(InetAddress inetaddress) {
		threadliveid = (int) (Math.random() * 99999999D);
		if (active) {
			try {
				Thread.sleep(500L);
			} catch (Exception _ex) {
			}
			active = false;
		}
		socketreq = 0;
		threadreq = null;
		dnsreq = null;
		savereq = null;
		urlreq = null;
		socketip = inetaddress;
		Thread thread = new Thread(new signlink());
		thread.setDaemon(true);
		thread.start();
		while (!active)
			try {
				Thread.sleep(50L);
			} catch (Exception _ex) {
			}
	}
	
  enum Position {
    LEFT, RIGHT, NORMAL
  };

  public void run() {
		active = true;
		String s = findcachedir();
		uid = getuid(s);
		try {
			File file = new File(s + "main_file_cache.dat");
			if (file.exists() && file.length() > 0x3200000L)
				file.delete();
			cache_dat = new RandomAccessFile(s + "main_file_cache.dat", "rw");
			for (int j = 0; j < 5; j++)
				cache_idx[j] = new RandomAccessFile(s + "main_file_cache.idx" + j, "rw");

		} catch (Exception exception) {
			exception.printStackTrace();
		}
    for (int i = threadliveid; threadliveid == i;) {
      if (socketreq != 0) {
        try {
          socket = new Socket(socketip, socketreq);
        } catch (Exception _ex) {
          socket = null;
        }
        socketreq = 0;
      } else if (threadreq != null) {
        Thread thread = new Thread(threadreq);
        thread.setDaemon(true);
        thread.start();
        thread.setPriority(threadreqpri);
        threadreq = null;
      } else if (dnsreq != null) {
        try {
          dns = InetAddress.getByName(dnsreq).getHostName();
        } catch (Exception _ex) {
          dns = "unknown";
        }
        dnsreq = null;
      } else if (savereq != null) {
        if (savebuf != null)
          try {
            FileOutputStream fileoutputstream = new FileOutputStream(s + savereq);
            fileoutputstream.write(savebuf, 0, savelen);
            fileoutputstream.close();
          } catch (Exception _ex) {
          }
        if (midiplay) {
          midi = s + savereq;
          try {
            if (music != null) {
              music.stop();
              music.close();
            }
            playMidi(midi);
          } catch (Exception ex) {
            ex.printStackTrace();
          }
          midiplay = false;
        }
        savereq = null;
      } else if (urlreq != null) {
        try {
          System.out.println("urlstream");
          urlstream = new DataInputStream((new URL(mainapp.getCodeBase(), urlreq)).openStream());
        } catch (Exception _ex) {
          urlstream = null;
        }
        urlreq = null;
      }
      try {
        Thread.sleep(50L);
      } catch (Exception _ex) {
      }
    }
  }

//  public static final String findcachedir() {
//	  return "./DodianDebug-Cache/";
//  }
  
	public static final String findcachedir() {
		String s;
		String s1;
		File file;
		s = System.getProperty("user.home") + "/.dodian_netV2/";
		s1 = "";
		file = new File((new StringBuilder()).append(s1).append(s).toString());
		if (file.exists() || file.mkdir()) {
			return (new StringBuilder()).append(s1).append(s).append("/").toString();
		}
		return "./DodianDebug-Cache/";
	}
	
	 /**
   * Plays the specified midi sequence.
   * @param location
   */
  private void playMidi(String location) {
    music = null;
    synthesizer = null;
    sequence = null;
    {
      return;
    }
  }

  /**
   * Sets the volume for the midi synthesizer.
   * @param value
   */
  public static void setVolume(int value) {
    int CHANGE_VOLUME = 7;
    midiVolume = value;
    if (synthesizer.getDefaultSoundbank() == null) {
      try {
        ShortMessage volumeMessage = new ShortMessage();
        for (int i = 0; i < 16; i++) {
          volumeMessage.setMessage(ShortMessage.CONTROL_CHANGE, i, CHANGE_VOLUME, midiVolume);
          volumeMessage.setMessage(ShortMessage.CONTROL_CHANGE, i, 39, midiVolume);
          MidiSystem.getReceiver().send(volumeMessage, -1);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      MidiChannel[] channels = synthesizer.getChannels();
      for (int c = 0; channels != null && c < channels.length; c++) {
        channels[c].controlChange(CHANGE_VOLUME, midiVolume);
        channels[c].controlChange(39, midiVolume);
      }
    }
  }

  public static Sequencer music = null;
  public static Sequence sequence = null;
  public static Synthesizer synthesizer = null;

	public static String findcachedirORIG() {
		String as[] = { "c:/windows/", "c:/winnt/", "d:/windows/", "d:/winnt/", "e:/windows/", "e:/winnt/",
				"f:/windows/", "f:/winnt/", "c:/", "~/", "/tmp/", "", "c:/rscache", "/rscache" };
		if (storeid < 32 || storeid > 34)
			storeid = 32;
		String s = ".file_store_" + storeid;
		for (int i = 0; i < as.length; i++)
			try {
				String s1 = as[i];
				if (s1.length() > 0) {
					File file = new File(s1);
					if (!file.exists())
						continue;
				}
				File file1 = new File(s1 + s);
				if (file1.exists() || file1.mkdir())
					return s1 + s + "/";
			} catch (Exception _ex) {
			}

		return null;

	}

	private static int getuid(String s) {
		try {
			File file = new File(s + "uid.dat");
			if (!file.exists() || file.length() < 4L) {
				DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(s + "uid.dat"));
				dataoutputstream.writeInt((int) (Math.random() * 99999999D));
				dataoutputstream.close();
			}
		} catch (Exception _ex) {
		}
		try {
			DataInputStream datainputstream = new DataInputStream(new FileInputStream(s + "uid.dat"));
			int i = datainputstream.readInt();
			datainputstream.close();
			return i + 1;
		} catch (Exception _ex) {
			return 0;
		}
	}

	public static synchronized Socket opensocket(int i) throws IOException {
		for (socketreq = i; socketreq != 0;)
			try {
				Thread.sleep(50L);
			} catch (Exception _ex) {
			}

		if (socket == null)
			throw new IOException("could not open socket");
		else
			return socket;
	}

	public static synchronized DataInputStream openurl(String s) throws IOException {
		for (urlreq = s; urlreq != null;)
			try {
				Thread.sleep(50L);
			} catch (Exception _ex) {
			}

		if (urlstream == null)
			throw new IOException("could not open: " + s);
		else
			return urlstream;
	}

	public static synchronized void dnslookup(String s) {
		dns = s;
		dnsreq = s;
	}

	public static synchronized void startthread(Runnable runnable, int i) {
		threadreqpri = i;
		threadreq = runnable;
	}

	public static synchronized boolean wavesave(byte abyte0[], int i) {
		if (i > 0x1e8480)
			return false;
		if (savereq != null) {
			return false;
		} else {
			wavepos = (wavepos + 1) % 5;
			savelen = i;
			savebuf = abyte0;
			savereq = "sound" + wavepos + ".wav";
			return true;
		}
	}

	public static synchronized boolean wavereplay() {
		if (savereq != null) {
			return false;
		} else {
			savebuf = null;
			savereq = "sound" + wavepos + ".wav";
			return true;
		}
	}

	public static synchronized void midisave(byte abyte0[], int i) {
		if (i > 0x1e8480)
			return;
		if (savereq != null) {
		} else {
			midipos = (midipos + 1) % 5;
			savelen = i;
			savebuf = abyte0;
			midiplay = true;
			savereq = "jingle" + midipos + ".mid";
		}
	}

  
	public static void reporterror(String s) {
		System.out.println("Error: " + s);
	}

	private signlink() {
	}

	public static final int clientversion = 289;
	public static int uid;
	public static int storeid = 32;
	public static RandomAccessFile cache_dat = null;
	public static final RandomAccessFile[] cache_idx = new RandomAccessFile[5];
	public static boolean sunjava;
	public static final Applet mainapp = null;
	private static boolean active;
	private static int threadliveid;
	private static InetAddress socketip;
	private static int socketreq;
	private static Socket socket = null;
	private static int threadreqpri = 1;
	private static Runnable threadreq = null;
	private static String dnsreq = null;
	public static String dns = null;
	private static String urlreq = null;
	private static DataInputStream urlstream = null;
	private static int savelen;
	private static String savereq = null;
	private static byte[] savebuf = null;
	private static boolean midiplay;
	private static int midipos;
	public static String midi = null;
	public static int midiVolume;
	public static int midifade;
	private static int wavepos;
	public static int wavevol;
	public static boolean reporterror = true;
	public static String errorname = "";

}
