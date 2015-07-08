package com.revimedia.log.model;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;

/**
 * A log file tailer is designed to monitor a log file and send notifications
 * when new lines are added to the log file. This class has a notification
 * strategy similar to a SAX parser: implement the FileTailerListener
 * interface, create a FileTailer to tail your log file, add yourself as a
 * listener, and start the FileTailer. It is your job to interpret the
 * results, build meaningful sets of data, etc. This tailer simply fires
 * notifications containing new log file lines, one at a time.
 */
public class FileTailer implements Runnable {
	/**
	 * How frequently to check for file changes; defaults to 5 seconds
	 */
	private long sampleInterval = 5000;

	/**
	 * The log file to tail
	 */
	private File logfile;

	/**
	 * Defines whether the log file tailer should include the entire contents of
	 * the existing log file or tail from the end of the file when the tailer
	 * starts
	 */
	private boolean startAtBeginning = true;

	/**
	 * Is the tailer currently tailing?
	 */
	private boolean tailing = false;

	/**
	 * Set of listeners
	 */
	private Set<IFileTailerListener> listeners = new HashSet<>();

	/**
	 * Creates a new log file tailer that tails an existing file and checks the
	 * file for updates every 5000ms
	 */
	public FileTailer(File file) {
		this.logfile = file;
	}

	/**
	 * Creates a new log file tailer
	 * 
	 * @param file
	 *            The file to tail
	 * @param sampleInterval
	 *            How often to check for updates to the log file (default =
	 *            5000ms)
	 * @param startAtBeginning
	 *            Should the tailer simply tail or should it process the entire
	 *            file and continue tailing (true) or simply start tailing from
	 *            the end of the file
	 */
	public FileTailer(File file, long sampleInterval, boolean startAtBeginning) {
		this.logfile = file;
		this.sampleInterval = sampleInterval;
	}

	public void addLogFileTailerListener(IFileTailerListener l) {
		this.listeners.add(l);
	}

	public void removeLogFileTailerListener(IFileTailerListener l) {
		this.listeners.remove(l);
	}

	protected void fireNewLogFileLine(String line) {
		for (IFileTailerListener l: this.listeners) {
			l.onFileUpdate(line);
		}
	}

	public void stopTailing() {
		System.out.println("Stop process the file");
		this.tailing = false;
	}

	public void run() {
		System.out.println("FileTailer thread started for file: " + logfile.getName());
		// The file pointer keeps track of where we are in the file
		long filePointer = 0;

		// Determine start point
		if (this.startAtBeginning) {
			filePointer = 0;
		} else {
			filePointer = this.logfile.length();
		}

		try {
			// Start tailing
			this.tailing = true;
			RandomAccessFile file = new RandomAccessFile(logfile, "r");
			while (this.tailing) {
				try {
					// Compare the length of the file to the file pointer
					long fileLength = this.logfile.length();
					if (fileLength < filePointer) {
						// Log file must have been rotated or deleted;
						// reopen the file and reset the file pointer
						file = new RandomAccessFile(logfile, "r");
						filePointer = 0;
					}

					if (fileLength > filePointer) {
						// There is data to read
						file.seek(filePointer);
						String line = file.readLine();
						while (line != null) {
							if(!line.isEmpty()) {
								this.fireNewLogFileLine(line);
							}
							line = file.readLine();
						}
						filePointer = file.getFilePointer();
					}

					// Sleep for the specified interval
					Thread.sleep(this.sampleInterval);
				} catch (Exception e) {
				}
			}

			// Close the file that we are tailing
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}