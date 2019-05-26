package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import customExceptions.AttemptedToRemoveCurrentPlayListException;
import customExceptions.AttemptedToRemoveDemoLibraryException;
import customExceptions.FolderWithoutMP3ContentException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicPlayer {
	public final static String MUSIC_FOLDERS_PATH = "data"+File.separator+"mscfldrs.got";

	private Media currentAudio;
	public Media getCurrentAudio() {
		return currentAudio;
	}

	private MediaPlayer mediaPlayer;
	private MusicFolder firstMusicFolder;
	private SimpleStringProperty currentSongTitle;
	private SimpleStringProperty currentSongArtist;
	private SimpleStringProperty currentSongAlbum;
	private ArrayList<Song> currentPlaylist;
	private SimpleIntegerProperty songLoaded;
	private byte[] currentCoverArt;

	private Song currentSong;
	
	/**
	 * Constructor MusicPlayer method that starts the entire current song metadata and the media player. 
	 * @throws ClassNotFoundException if the class definition is not there due to the library witch contains it 
	 * is not in the application class path.
	 * @throws IOException if the file has not been found, deleted or moved to another location.
	 * @throws FolderWithoutMP3ContentException if the selected folder does not have music files with mp3 extension.
	 */
	public MusicPlayer() throws ClassNotFoundException, IOException, FolderWithoutMP3ContentException {
		songLoaded = new SimpleIntegerProperty(Integer.MIN_VALUE);

		currentSongAlbum = new SimpleStringProperty();
		currentSongArtist = new SimpleStringProperty();
		currentSongTitle = new SimpleStringProperty();

		firstMusicFolder = new MusicFolder(new File("music"));
		currentPlaylist = firstMusicFolder.getSongs();
		currentSong = currentPlaylist.get(0);

		File file = new File(MUSIC_FOLDERS_PATH);
		if(file.exists()) {
			loadMusicFolders(file);
		}
		chargeMedia();
	}
	/**
	 * This method charges the entire metadata to the Media and the Media to the MediaPlayer.
	 */
	private void chargeMedia() {
		if(mediaPlayer != null) {
			mediaPlayer.stop();
		}
		currentAudio = new Media(currentSong.getSongPath());
		mediaPlayer = new MediaPlayer(currentAudio);
		mediaPlayer.stop();

		currentCoverArt = currentSong.getImage();

		currentSongAlbum.setValue(currentSong.getAlbum());
		currentSongArtist.setValue(currentSong.getArtist());
		currentSongTitle.setValue(currentSong.getTitle());

		songLoaded.set(songLoaded.get()+1);
	}
	/**
	 * Method that deserializes the folder with music when the application is started again.
	 * @param mf A File that represents the folder with music to deserialize when the application is started again.  
	 * @throws IOException if the file has not been found, deleted or moved to another location.
	 * @throws ClassNotFoundException if the class definition is not there due to the library witch contains it is not
	 * in the application class path.
	 */
	private void loadMusicFolders(File mf) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(mf);
		ObjectInputStream ois = new ObjectInputStream(fis);

		firstMusicFolder = (MusicFolder)ois.readObject();

		ois.close();
		fis.close();
	}
	/**
	 * Method to get the current Song sounding in the media player.
	 * @return A Song that represents the current Song sounding in the media player.
	 */
	public Song getCurrentSong() {
		return currentSong;
	}
	/**
	 * This method allows to set the Media through an index that represents the song position inside
	 * its respective music folder and charges its media.
	 * @param index the song position inside its respective music folder.
	 */
	public void setMedia(int index) {
		this.currentSong = currentPlaylist.get(index);
		mediaPlayer.stop();
		chargeMedia();
	}
	/**
	 * This method allows to get the actual media player used to play the music.
	 * @return A MediaPlayer that represents the actual media player used to play the music.
	 */
	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
	/**
	 * This method allows to set the actual media player used to play the music.
	 * @param mediaPlayer A MediaPlayer that represents the actual media player used to play the music.
	 */
	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}
	/**
	 * This method allows to obtain the first music folder in the list.
	 * @return A MusicFolder that represents the first music folder in the list.
	 */
	public MusicFolder getFirstMusicFolder() {
		return firstMusicFolder;
	}
	/**
	 * This method allows to set the first music folder in the list.
	 * @param firstMusicFolder A MusicFolder that represents the first music folder in the list.
	 */
	public void setFirstMusicFolder(MusicFolder firstMusicFolder) {
		this.firstMusicFolder = firstMusicFolder;
	}
	/**
	 * This method allows to obtain a simple string representation of the current song title to use it in the interface.
	 * @return A SimpleStringProperty that represents a simple string representation of the current song title to use it in the interface
	 */
	public SimpleStringProperty getCurrentSongTitle() {
		return currentSongTitle;
	}
	/**
	 * This method allows to obtain a simple string representation of the current song artist to use it in the interface.
	 * @return A SimpleStringProperty that represents a simple string representation of the current song artist to use it in the interface
	 */
	public SimpleStringProperty getCurrentSongArtist() {
		return currentSongArtist;
	}
	/**
	 * This method allows to obtain a simple string representation of the current song album to use it in the interface.
	 * @return A SimpleStringProperty that represents a simple string representation of the current song album to use it in the interface
	 */
	public SimpleStringProperty getCurrentSongAlbum() {
		return currentSongAlbum;
	}
	/**
	 * This method allows to obtain the current song cover art to use it in the interface as a byte array.
	 * @return An byte array that represents the current song cover art to use it in the interface.
	 */
	public byte[] getCurrentCoverArt() {
		return currentCoverArt;
	}
	/**
	 * This method allows to add a music folder into the linked list of music folders in the last place. 
	 * @param dir A File that represents the music folder to be added in the linked list of music folders.
	 * @throws IOException if the file has not been found, deleted or moved to another location.
	 * @throws FolderWithoutMP3ContentException if the selected folder does not have music files with mp3 extension.
	 */
	public void addMusicFolder(File dir) throws IOException, FolderWithoutMP3ContentException {
		if(dir != null) {
			MusicFolder toAdd = new MusicFolder(dir);
			MusicFolder current = firstMusicFolder;
			boolean duplicated = false;
			while(current != null && !duplicated) {
				if(current.equals(toAdd)) {
					duplicated = true;
				} else if(current.getNextMusicFolder() == null) {
					current.setNextMusicFolder(toAdd);
					toAdd.setPrevMusicFolder(current);
				}
				current = current.getNextMusicFolder();
			}
		}
	}
	/**
	 * This method allow to obtain a observable list of music folders adding them as a linked list and thus be able 
	 * to display them in the in the interface. 
	 * @return A ObservableList of MusicFolder that represents the linked list to display it in the interface. 
	 */
	public ObservableList<MusicFolder> getMusicFolders() {
		ObservableList<MusicFolder> folders = FXCollections.observableArrayList();
		MusicFolder current = firstMusicFolder;
		while(current != null) {
			folders.add(current);
			current = current.getNextMusicFolder();
		}
		return folders;
	}
	/**
	 * This method allows to save the music folders added when the application is running.
	 * @throws IOException if the file has not been found, deleted or moved to another location.
	 */
	public void save() throws IOException {
		File file = new File(MUSIC_FOLDERS_PATH);
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);

		oos.writeObject(firstMusicFolder);

		oos.close();
		fos.close();
	}
	/**
	 * This method allows to obtain the song loaded position as a simple integer property.
	 * @return A SimpleIntegerProperty that represents the song loaded position.
	 */
	public SimpleIntegerProperty getSongLoaded() {
		return songLoaded;
	}

	public void setCurrentPlayList(MusicFolder current) {
		currentPlaylist = current.getSongs();
	}

	public ArrayList<Song> getCurrentPlayList() {
		return currentPlaylist;
	}

	public void removeMusicFolderFromLibraries(MusicFolder toremove) throws AttemptedToRemoveDemoLibraryException {
		if(toremove == firstMusicFolder) {
			throw new AttemptedToRemoveDemoLibraryException();
		}
		if(toremove.getSongs().contains(currentSong)) {
			throw new AttemptedToRemoveCurrentPlayListException(currentSong.getParentFolderPath());
		}

		MusicFolder prev = toremove.getPrevMusicFolder();
		MusicFolder next = toremove.getNextMusicFolder();
		if(prev != null) {
			prev.setNextMusicFolder(next);
		} else {
			firstMusicFolder = next;
		}
		if(next != null) {
			next.setPrevMusicFolder(prev);
		}
	}
}
