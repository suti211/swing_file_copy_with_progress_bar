package main;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

public class CopyProgress extends JPanel implements ActionListener, PropertyChangeListener {

	private static final long serialVersionUID = 5429504162901231281L;

	private JScrollPane copyListScrollPane;
	private JButton addNewFileOpration;
	private JButton copyButton;

	private JButton exitButton;
	private JFileChooser fChooser;

	private JPanel mainContainer;
	private JPanel buttonPanel;
	private JPanel addOpPanel;
	private JPanel copyListPanel;

	private File fromFile;
	private File toFile;
	private Map<FileCopy, JProgressBar> fileOperations;
	private Map<JButton, FileCopy> operationButtons;

	public CopyProgress() {

		this.fileOperations = new HashMap<FileCopy, JProgressBar>();
		this.operationButtons = new HashMap<JButton, FileCopy>();
		this.fChooser = new JFileChooser();
		this.addNewFileOpration = new JButton("New File");

		this.copyButton = new JButton("Copy");
		this.exitButton = new JButton("Exit");
		this.mainContainer = new JPanel();
		this.buttonPanel = new JPanel();
		this.addOpPanel = new JPanel();

		this.copyListPanel = new JPanel();
		this.copyListScrollPane = new JScrollPane(copyListPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// main container
		BoxLayout mainLayout = new BoxLayout(mainContainer, BoxLayout.Y_AXIS);
		BoxLayout listLayout = new BoxLayout(copyListPanel, BoxLayout.Y_AXIS);
		mainContainer.setLayout(mainLayout);
		copyListPanel.setLayout(listLayout);

		// addOpbutton
		addNewFileOpration.setActionCommand("newOp");
		addNewFileOpration.addActionListener(this);
		addOpPanel.add(addNewFileOpration);

		// copyList
//		copyListPanel.setPreferredSize(new Dimension(800, 600));
		copyListScrollPane.setPreferredSize(new Dimension(750, 450));

		mainContainer.add(copyListScrollPane);
		mainContainer.add(addOpPanel);

		// button row
		FlowLayout buttonRowLayout = new FlowLayout();
		buttonPanel.add(copyButton);
		copyButton.setActionCommand("copy");
		exitButton.setActionCommand("exit");
		copyButton.addActionListener(this);
		exitButton.addActionListener(this);
		exitButton.setEnabled(false);
		buttonPanel.add(exitButton);
		buttonRowLayout.setHgap(20);
		buttonRowLayout.setVgap(10);
		buttonPanel.setLayout(buttonRowLayout);
		mainContainer.add(buttonPanel);

		add(mainContainer);
	}

	private static void createGui() {
		JFrame frame = new JFrame("Copy, right?");
		JComponent newContentPane = new CopyProgress();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// centering the window
		frame.setLocation( 300, 100);
		frame.setSize(800, 600);
		frame.setTitle("File Copy");
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);
		frame.pack();
		
		frame.setVisible(true);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		FileCopy fc = (FileCopy) evt.getSource();

		if (evt.getPropertyName() == "progress") {
			Integer progress = (Integer) evt.getNewValue();
			fileOperations.get(fc).setValue(progress);
		}
		
		if(fc.isDone()){
			for(Entry<JButton, FileCopy> entry : operationButtons.entrySet()){
				if(entry.getValue().equals(fc)){
					entry.getKey().setEnabled(false);
					entry.getKey().setText("Done");
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("copy")) {
			if(fileOperations.size() > 0){
				copyButton.setEnabled(false);
				exitButton.setEnabled(true);

				for (FileCopy fc : fileOperations.keySet()) {
					fc.execute();
				}
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}

		if (e.getActionCommand().equals("newOp")) {
			int ret = fChooser.showOpenDialog(CopyProgress.this);

			if (ret == JFileChooser.APPROVE_OPTION) {
				fromFile = fChooser.getSelectedFile();
			}

			ret = fChooser.showSaveDialog(CopyProgress.this);

			if (ret == JFileChooser.APPROVE_OPTION) {
				toFile = fChooser.getSelectedFile();
			}

			if (toFile != null && fromFile != null) {
				FileCopy operation = new FileCopy(fromFile, toFile);
				operation.addPropertyChangeListener(this);

				JPanel opPanel = new JPanel();
				JPanel fromPanel = new JPanel();
				JPanel toPanel = new JPanel();
				JPanel progressPanel = new JPanel();
				JLabel fromLabel = new JLabel("From : " + fromFile.getAbsolutePath());
				JLabel toLabel = new JLabel("To : " + toFile.getAbsolutePath());
				JButton stopButton = new JButton("Stop");
				stopButton.setActionCommand("stop");
				stopButton.addActionListener(this);
				JProgressBar bar = new JProgressBar(0, 100);
				bar.setStringPainted(true);
				bar.setValue(0);

				fromPanel.add(fromLabel);
				toPanel.add(toLabel);
				progressPanel.add(bar);
				progressPanel.add(stopButton);

				BoxLayout bl = new BoxLayout(opPanel, BoxLayout.Y_AXIS);
				opPanel.setLayout(bl);

				opPanel.add(fromPanel);
				opPanel.add(toPanel);
				opPanel.add(progressPanel);
				opPanel.setSize(100, 100);
				opPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
				opPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

				fileOperations.put(operation, bar);
				operationButtons.put(stopButton, operation);
				copyListPanel.add(opPanel);
				copyListPanel.revalidate();
				copyListPanel.repaint();
			}
			
		}
		
		if(e.getActionCommand().equals("stop")){
			JButton butt = ((JButton)e.getSource());
			butt.setText("Interrupted");
			butt.setEnabled(false);
			operationButtons.get(butt).cancel(true);
			System.out.println("stop");
		}

		if (e.getActionCommand().equals("exit")) {
			for(FileCopy fc : fileOperations.keySet()){
				fc.cancel(true);
			}
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createGui();
			}
		});
	}
}
