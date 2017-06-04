package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

public class CopyProgress extends JPanel implements ActionListener, PropertyChangeListener {

	private JButton copyButton;
	private JButton exitButton;
	private JTextField fromTextField;
	private JTextField toTextField;
	private JLabel fromLabel;
	private JLabel toLabel;
	private JProgressBar progressBar;

	private JFileChooser fChooser;
	private JButton fromChooserButton;
	private JButton toChooserButton;
	
	private JPanel mainContainer;
	private JPanel fromPanel;
	private JPanel toPanel;
	private JPanel progressBarPanel;
	private JPanel progressPanel;
	private JPanel buttonPanel;
	
	private File fromFile;
	private File toFile;

	FileCopy fileCopy;

	public CopyProgress() {
		
		this.fChooser = new JFileChooser();
		this.fromChooserButton = new JButton("...");
		this.toChooserButton = new JButton("...");
		
		this.copyButton = new JButton("Copy");
		this.exitButton = new JButton("Exit");
		this.fromTextField = new JTextField();
		this.toTextField = new JTextField();
		this.fromLabel = new JLabel("from: ");
		this.toLabel = new JLabel("to: ");
		this.progressBar = new JProgressBar(0, 100);
		this.mainContainer = new JPanel();
		this.fromPanel = new JPanel();
		this.toPanel = new JPanel();
		this.progressBarPanel = new JPanel();
		this.progressPanel = new JPanel();
		this.buttonPanel = new JPanel();

		// main container
		BoxLayout mainLayout = new BoxLayout(mainContainer, BoxLayout.Y_AXIS);
		mainContainer.setLayout(mainLayout);
		
		// text fields
		Dimension textFieldDim = new Dimension(200, 30);
		fromTextField.setMinimumSize(textFieldDim);
		fromTextField.setPreferredSize(textFieldDim);
		toTextField.setMinimumSize(textFieldDim);
		toTextField.setPreferredSize(textFieldDim);
		

		// from row:
		BoxLayout fromRowLayout = new BoxLayout(fromPanel, BoxLayout.X_AXIS);
		fromPanel.add(fromLabel);
		fromPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		fromPanel.add(fromTextField);
		fromPanel.add(fromChooserButton);
		fromChooserButton.setActionCommand("chooseFrom");
		fromChooserButton.addActionListener(this);
		fromPanel.setLayout(fromRowLayout);
		mainContainer.add(fromPanel);

		// to row:
		BoxLayout toRowLayout = new BoxLayout(toPanel, BoxLayout.X_AXIS);
		toPanel.add(toLabel);
		toPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		toPanel.add(toTextField);
		toPanel.add(toChooserButton);
		toChooserButton.setActionCommand("chooseTo");
		toChooserButton.addActionListener(this);
		toPanel.setLayout(toRowLayout);
		mainContainer.add(toPanel);
		
		//progress bar
		FlowLayout progressBarLayout = new FlowLayout();
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(220, 30));
		progressBarPanel.add(progressBar);
		progressBarPanel.setLayout(progressBarLayout);
		progressBarLayout.setVgap(30);
		mainContainer.add(progressBarPanel);
		
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
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension windowSize = new Dimension(360, 280);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//centering the window
		frame.setLocation((int)(screen.getWidth() / 2) - (int)(windowSize.getWidth() / 2), (int)(screen.getHeight() / 2) - (int)(windowSize.getHeight() / 2));
		frame.setSize(320, 240);
		frame.setTitle("File Copy");
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);
		frame.setVisible(true);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName() == "progress"){
			Integer progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
			System.out.println(progress + "%");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("copy")){
			if(!fromTextField.getText().equals("") && !toTextField.getText().equals("")){
				copyButton.setEnabled(false);
				exitButton.setEnabled(true);
				fileCopy = new FileCopy(fromFile, toFile);
				fileCopy.addPropertyChangeListener(this);
				fileCopy.execute();
			}
		}
		
		if(e.getActionCommand().equals("chooseFrom")){
			int ret = fChooser.showOpenDialog(CopyProgress.this);
			
			if(ret == JFileChooser.APPROVE_OPTION){
				fromFile = fChooser.getSelectedFile();
				fromTextField.setText(fromFile.getAbsolutePath());
				System.out.println(fromFile.length());
			}
		}
		
		if(e.getActionCommand().equals("chooseTo")){
			int ret = fChooser.showSaveDialog(CopyProgress.this);
			
			if(ret == JFileChooser.APPROVE_OPTION){
				toFile = fChooser.getSelectedFile();
				toTextField.setText(toFile.getAbsolutePath());
			}
		}
		
		if(e.getActionCommand().equals("exit")){
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				createGui();
			}
		});
	}
}

