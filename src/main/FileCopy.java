package main;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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

	// Háttér szálon lesz végrehajtva
	@Override
	protected Void doInBackground() {
		// FILEMÁSOLÁS CUCC IDE
		try {

			if (!to.exists()) {
				System.out.println("filehozas");
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
				counter += read;
				setProgress((int) (Math.floor((1.0 * counter / length) * 100)));
				out.write(buffer, 0, read);
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
		// MI TÖRTÉNJEN HA KÉSZ
		super.done();
		Toolkit.getDefaultToolkit().beep();
		System.out.println("kész lett ez a szar");
	}
}
