/*  SharedX: An RPG
 Copyright (C) 2011-2012 Eric Ahnell

 Any questions should be directed to the author via email at: realmzxfamily@worldwizard.net
 */
package com.puttysoftware.lasertank.resourcemanagers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.retropipes.diane.asset.music.DianeMusicPlayer;
import org.retropipes.diane.gui.dialog.CommonDialogs;

import com.puttysoftware.lasertank.LaserTank;
import com.puttysoftware.lasertank.arena.AbstractArena;
import com.puttysoftware.lasertank.editor.ExternalMusic;

public class MusicManager {
    // Fields
    private static String EXTERNAL_LOAD_PATH = null;
    private static ExternalMusic gameExternalMusic;
    private static Class<?> LOAD_CLASS = MusicManager.class;

    // Constructors
    private MusicManager() {
        // Do nothing
    }

    public static boolean isMusicPlaying() {
	return DianeMusicPlayer.isPlaying();
    }

    public static void playMusic() {
	try {
            if (MusicManager.EXTERNAL_LOAD_PATH == null) {
                MusicManager.EXTERNAL_LOAD_PATH = LaserTank.getApplication()
                        .getArenaManager().getArena().getArenaTempMusicFolder();
            }
            try (final InputStream is = MusicManager.LOAD_CLASS
			.getResourceAsStream(MusicManager.EXTERNAL_LOAD_PATH + LaserTank.getApplication().getArenaManager()
		                .getArena().getMusicFilename())) {
		    DianeMusicPlayer.playStream(is);
		}
        } catch (final IOException io) {
            LaserTank.logError(io);
        }
    }

    public static void loadPlayMusic(final String filename) {
	try {
            if (MusicManager.EXTERNAL_LOAD_PATH == null) {
                MusicManager.EXTERNAL_LOAD_PATH = LaserTank.getApplication()
                        .getArenaManager().getArena().getArenaTempMusicFolder();
            }
            try (final InputStream is = MusicManager.LOAD_CLASS
			.getResourceAsStream(MusicManager.EXTERNAL_LOAD_PATH + filename)) {
		    DianeMusicPlayer.playStream(is);
		}
        } catch (final IOException io) {
            LaserTank.logError(io);
        }
    }

    public static void stopMusic() {
	DianeMusicPlayer.stopPlaying();
    }

    public static void arenaChanged() {
        MusicManager.EXTERNAL_LOAD_PATH = null;
    }

    // Methods
    public static ExternalMusic getExternalMusic() {
        if (MusicManager.gameExternalMusic == null) {
            MusicManager.loadExternalMusic();
        }
        return MusicManager.gameExternalMusic;
    }

    public static void setExternalMusic(final ExternalMusic newExternalMusic) {
        MusicManager.gameExternalMusic = newExternalMusic;
    }

    public static void loadExternalMusic() {
        final AbstractArena a = LaserTank.getApplication().getArenaManager()
                .getArena();
        final ExternalMusicLoadTask ellt = new ExternalMusicLoadTask(
                a.getArenaTempMusicFolder() + a.getMusicFilename());
        ellt.start();
        // Wait
        if (ellt.isAlive()) {
            boolean waiting = true;
            while (waiting) {
                try {
                    ellt.join();
                    waiting = false;
                } catch (final InterruptedException ie) {
                    // Ignore
                }
            }
        }
    }

    public static void deleteExternalMusicFile() {
        final AbstractArena a = LaserTank.getApplication().getArenaManager()
                .getArena();
        final File file = new File(
                a.getArenaTempMusicFolder() + a.getMusicFilename());
        file.delete();
    }

    public static void saveExternalMusic() {
        // Write external music
        final File extMusicDir = new File(LaserTank.getApplication()
                .getArenaManager().getArena().getArenaTempMusicFolder());
        if (!extMusicDir.exists()) {
            final boolean res = extMusicDir.mkdirs();
            if (!res) {
                CommonDialogs.showErrorDialog("Save External Music Failed!",
                        "External Music Editor");
                return;
            }
        }
        final String filename = MusicManager.gameExternalMusic.getName();
        final String filepath = MusicManager.gameExternalMusic.getPath();
        final ExternalMusicSaveTask esst = new ExternalMusicSaveTask(filepath,
                filename);
        esst.start();
    }

    public static String getExtension(final File f) {
        String ext = null;
        final String s = f.getName();
        final int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}