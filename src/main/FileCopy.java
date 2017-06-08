package main;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class FileCopy extends SwingWorker<Void, Void> {

	private File from;
	private File to;
	private FileInputStream in;
	private FileOutputStream out;

	public FileCopy(File from, File to) {

		this.from = from;
		this.to = to;
	}

	// H�tt�r sz�lon lesz v�grehajtva
	@Override
	protected Void doInBackground() {
		// FILEM�SOL�S CUCC IDE
		try {

			if (!to.exists()) {
				//System.out.println("filehozas");
				to.createNewFile();
			}

			setProgress(0);
			in = new FileInputStream(from);
			out = new FileOutputStream(to);
			long length = from.length();
			long counter = 0;
			int read = 0;
			byte[] buffer = new byte[1024];

			while ((read = in.read(buffer)) != -1) {
				if(!this.isCancelled()){
					counter += read;
					setProgress((int) (Math.floor((1.0 * counter / length) * 100)));
					out.write(buffer, 0, read);
				} else {
					break;
				}
			}
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("failed to close files!");
			}
		}
		return null;
	}
	@Override
	protected void done() {
		// MI T�RT�NJEN HA K�SZ
		super.done();
		Toolkit.getDefaultToolkit().beep();
		System.out.println("k�sz lett ez a szar");
	}

}
