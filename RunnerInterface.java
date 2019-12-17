import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.HashMap;
import java.util.Map;

public class RunnerInterface extends JFrame{
	
	private static File targetBPSEQ; 
	private static File bpDICT; 
	private static File sbpDICT; 
	private static RnaNode startingNode; 
	
	public static Container createContent(Container pane) throws IOException{
		
		final ButtonGroup groupA = new ButtonGroup(); 
		final ButtonGroup groupB = new ButtonGroup(); 
		final ButtonGroup groupC = new ButtonGroup(); 
		final ButtonGroup groupD = new ButtonGroup(); 
		
		Constants settings = new Constants();
				
		GroupLayout layout = new GroupLayout(pane); 
		pane.setLayout(layout);
		
		//LABELS
		JLabel modelTypeL = new JLabel("Model Type:"); 
		JLabel selectedFileL = new JLabel("Selected bpseq File:");
		JLabel fitnessL = new JLabel("Desired Fitness (0-1.0):");
		JLabel generationsL = new JLabel("Generation Limit:");
		JLabel runL = new JLabel("Run to:");
		JLabel mutationL = new JLabel ("First Mutation:"); 
		JLabel trialsL = new JLabel("How many trials to run?"); 
		JLabel detailsL = new JLabel("Should run details be printed to a .txt file?");
		JLabel iterationsL = new JLabel("Run iterations?");
		
		//TEXTFIELDS
		JTextField selectedFileTF = new JTextField(); 
		JTextField fitnessTF = new JTextField(); 
		JTextField generationsTF = new JTextField(); 
		JTextField trialsTF = new JTextField(); 
		
		//BUTTONS
		JButton uploadB = new JButton("Upload"); 
		uploadB.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){

						JFileChooser selector = new JFileChooser();
						FileNameExtensionFilter filter = new FileNameExtensionFilter("BPseq files", "bpseq"); 
						selector.setFileFilter(filter); 
						int returnValue = selector.showOpenDialog(null); 
						if(returnValue == JFileChooser.APPROVE_OPTION){
							targetBPSEQ = selector.getSelectedFile(); 

							selectedFileTF.setText(targetBPSEQ.getAbsolutePath());
							
						}
					}
				});
		
		JButton runB = new JButton("Run"); 
		runB.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
												
						int trials = Integer.parseInt(trialsTF.getText());
						Double fitness = Double.parseDouble(fitnessTF.getText());
						int generations = Integer.parseInt(generationsTF.getText()); 
						
						settings.setMaxLevel(generations);
						settings.setFitness(fitness);					
						
						int counter = 1;
						
						String name = "Result_batch"+LocalDateTime.now();
						name = name.substring(0, 31);
						name = name.replace(':', '_');
						String FileName = name+".csv";
						
						try {
							PrintWriter report = new PrintWriter(new File(FileName));

							if (settings.iterations()) {
								report.write("Iteration,Trial,Compensatory Generation,First Mutation,Mutation Position,Noncanonical Mutations,Cells Sampled,Dead Lineages,Single-base Mutations,Mutation Sequence\n");
							} else {
								report.write("Trial,Compensatory Generation,First Mutation,Mutation Position,Noncanonical Mutations,Cells Sampled,Dead Lineages,Single-base Mutations,Mutation Sequence\n");
							}

							if (!settings.iterations()){
								while (counter <= trials) {
									StartTester executor_1 = new StartTester();
									String analysis_1 = executor_1.StartTester(targetBPSEQ, settings, counter, fitness);
									if (settings.iterations()) {
										report.print("0,");
									}
									report.println(analysis_1);
									counter++;
								}
							}
							if(settings.iterations()){
								int iterationsCount = 0;
								while(new File("Temporary_bpseq.txt").exists() && iterationsCount < settings.iterationCount()){
									counter = 1;
									while(counter <= trials){

										StartTester executor_1 = new StartTester();
										String analysis_1 = executor_1.StartTester(targetBPSEQ, settings, counter, fitness);

										StartTester executor_2 = new StartTester();

										String analysis_2 = executor_2.iStartTester(targetBPSEQ, settings, counter, fitness, executor_1.CheckMap);
										report.println(iterationsCount+","+analysis_2);
										counter++;
									}
									iterationsCount++;
								}
							}
							
							report.close();
							
						}catch(IOException a){
							a.printStackTrace();
						}

					}
				});
		
		//RADIO BUTTONS
		
		//GROUP 1
		JRadioButton btnA1 = new JRadioButton("Simple"); 
		btnA1.setSelected(true);
		settings.setModelType(1); 
		
		btnA1.addItemListener(
				new ItemListener(){
					public void itemStateChanged(ItemEvent e){
						if(e.getStateChange() == ItemEvent.SELECTED){
							settings.setModelType(1);
						}
					}
				});
		
		JRadioButton btnA2 = new JRadioButton("Complex"); 
		btnA2.addItemListener(
				new ItemListener(){
					public void itemStateChanged(ItemEvent e){
						if(e.getStateChange() == ItemEvent.SELECTED){
							
							JOptionPane.showMessageDialog(new JFrame(), "Please upload a base paired region dictionary.");
							
							JFileChooser selector = new JFileChooser();
							FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt"); 
							selector.setFileFilter(filter); 
							int returnValue = selector.showOpenDialog(null); 
							if(returnValue == JFileChooser.APPROVE_OPTION){
								bpDICT = selector.getSelectedFile(); 
								settings.setModelType(2);
								try {
									settings.bpDictionary(bpDICT);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} 
							}
							else{
								
								btnA1.setSelected(true);
							}
						}
					}
				});
		
		JRadioButton btnA3 = new JRadioButton("Paired/Unpaired"); 
		btnA3.addItemListener(
				new ItemListener(){
					public void itemStateChanged(ItemEvent e){
						if(e.getStateChange() == ItemEvent.SELECTED){
							
							JOptionPane.showMessageDialog(new JFrame(), "Please upload a base paired region dictionary.");
							
							JFileChooser selector = new JFileChooser();
							FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT files", "txt"); 
							selector.setFileFilter(filter); 
							int returnValue = selector.showOpenDialog(null); 
							if(returnValue == JFileChooser.APPROVE_OPTION){
								bpDICT = selector.getSelectedFile(); 
								try {
									settings.bpDictionary(bpDICT);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();	
								}
							}
							
							JOptionPane.showMessageDialog(new JFrame(), "Please upload an unpaired region dictionary");
							
							JFileChooser selector2 = new JFileChooser();
							selector2.setFileFilter(filter); 
							int returnValue2 = selector2.showOpenDialog(null); 
							if(returnValue2 == JFileChooser.APPROVE_OPTION){
								sbpDICT = selector2.getSelectedFile(); 
								try {
									settings.sbpDictionary(sbpDICT);
								} catch (FileNotFoundException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							
							if(returnValue == JFileChooser.APPROVE_OPTION && returnValue2 == JFileChooser.APPROVE_OPTION){
								btnA3.setSelected(true);
								btnA3.setSelected(true);
								settings.setModelType(3);
							}
							else{
								btnA1.setSelected(true);
							}
							
						}
					}
				});
		
		groupA.add(btnA1);
		groupA.add(btnA2);
		groupA.add(btnA3);
		
		//GROUP 2
		JRadioButton btnB1 = new JRadioButton("Random"); 
		settings.setType(true);
		btnB1.setSelected(true);
		btnB1.addItemListener(
				new ItemListener(){
					public void itemStateChanged(ItemEvent e){
						if(e.getStateChange() == ItemEvent.SELECTED){
							settings.setType(true); 
						}
					}
				});
		
		JRadioButton btnB2 = new JRadioButton("Defined"); 
		btnB2.addItemListener(
				new ItemListener(){
					public void itemStateChanged(ItemEvent e){

						if(e.getStateChange() == ItemEvent.SELECTED){
							String returnValue = JOptionPane.showInputDialog(new JFrame(), "Please choose a location (EX: 500-A)"); 
							
							try{

								int position = Integer.parseInt(returnValue.substring(0,returnValue.lastIndexOf('-'))); 
								String nucleotide = returnValue.substring(returnValue.lastIndexOf('-')+1, returnValue.lastIndexOf('-')+2);

								if(nucleotide.equals("A") || nucleotide.equals("G") || nucleotide.equals("C") || nucleotide.equals("U")){
									startingNode = new RnaNode<String>();
									startingNode.setData(nucleotide, position);
									settings.setType(false);
									settings.setStarter(startingNode);
								}else{
									btnB1.setSelected(true); 
									JOptionPane.showMessageDialog(new JFrame(), "Not recognized nucleotide entered. Please enter A, G, C, or U", "Error", JOptionPane.ERROR_MESSAGE);
								}

							}catch(Exception a){
								btnB1.setSelected(true); 
								JOptionPane.showMessageDialog(new JFrame(), "Incorrect format used. Please re-enter desired position-nucleotide", "Error", JOptionPane.ERROR_MESSAGE);
							}
							
							if(returnValue == null || returnValue.equals("")){
								btnB1.setSelected(true);
							}
						}
					}
				});
		
		groupB.add(btnB1);
		groupB.add(btnB2);
		
		//Group 3
		
		JRadioButton btnC1 = new JRadioButton("Yes");
		btnC1.addItemListener(
				new ItemListener(){
						public void itemStateChanged(ItemEvent e){
							
							if(e.getStateChange() == ItemEvent.SELECTED){
								settings.setDetails(true);
								btnC1.setSelected(true);
							}
							
							}
						});
		
		JRadioButton btnC2 = new JRadioButton("No");
		btnC2.setSelected(true);
		settings.setDetails(false);
		btnC2.addItemListener(
				new ItemListener(){
						public void itemStateChanged(ItemEvent e){
							if(e.getStateChange() == ItemEvent.SELECTED){
								settings.setDetails(false);
								btnC2.setSelected(true);
							}
							}
						});
		
		groupC.add(btnC1);
		groupC.add(btnC2);
		
		//GROUP D
		JRadioButton btnD1 = new JRadioButton("Yes");
		btnD1.addItemListener(
				new ItemListener(){
						public void itemStateChanged(ItemEvent e){
							
							if(e.getStateChange() == ItemEvent.SELECTED){
								settings.setIterations(true);
								btnD1.setSelected(true);
								
								String returnValue = JOptionPane.showInputDialog(new JFrame(), "How many iterations should be run?");

								settings.setIterationCount(Integer.parseInt(returnValue));
								settings.setIterationCountB(Integer.parseInt(returnValue));
							}
							
							}
						});
		
		JRadioButton btnD2 = new JRadioButton("No");
		btnD2.setSelected(true);
		settings.setIterations(false);
		btnD2.addItemListener(
				new ItemListener(){
						public void itemStateChanged(ItemEvent e){
							if(e.getStateChange() == ItemEvent.SELECTED){
								settings.setIterations(false);
								btnD2.setSelected(true);
							}
							}
						});
		
		groupD.add(btnD1);
		groupD.add(btnD2);
		
		//DROP-DOWN MENU
		String[] levels = {"First", "End"}; 
		JComboBox boxA = new JComboBox(levels); 
		boxA.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(((String)boxA.getSelectedItem()).equals("First")){
						settings.setEndRun(true);
					}else{
						settings.setEndRun(false);
					}
				}
			});
		boxA.setSelectedIndex(0);
		
		layout.setAutoCreateGaps(true); 
		layout.setAutoCreateContainerGaps(true); 
		
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(modelTypeL)
						.addComponent(selectedFileL)
						.addComponent(fitnessL)
						.addComponent(generationsL)
						.addComponent(trialsL))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addComponent(btnA1)
								.addComponent(btnA2)
								.addComponent(btnA3))
						.addComponent(selectedFileTF)
						.addComponent(fitnessTF)
						.addComponent(generationsTF)
						.addGroup(layout.createSequentialGroup()
								.addComponent(trialsTF)
								.addComponent(detailsL)
								.addComponent(btnC1)
								.addComponent(btnC2))
						.addGroup(layout.createSequentialGroup()
								.addComponent(runL)
								.addComponent(boxA)
								.addComponent(mutationL)
								.addComponent(btnB1)
								.addComponent(btnB2))
						.addGroup(layout.createSequentialGroup()
								.addComponent(iterationsL)
								.addComponent(btnD1)
								.addComponent(btnD2)))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(uploadB)
						.addComponent(runB)));
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(modelTypeL)
						.addComponent(btnA1)
						.addComponent(btnA2)
						.addComponent(btnA3))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(selectedFileL)
						.addComponent(selectedFileTF)
						.addComponent(uploadB))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(fitnessL)
						.addComponent(fitnessTF))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(generationsL)
						.addComponent(generationsTF))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(trialsL)
						.addComponent(trialsTF)
						.addComponent(detailsL)
						.addComponent(btnC1)
						.addComponent(btnC2))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(runL)
						.addComponent(boxA)
						.addComponent(mutationL)
						.addComponent(btnB1)
						.addComponent(btnB2))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(iterationsL)
						.addComponent(btnD1)
						.addComponent(btnD2)
						.addComponent(runB)));
				
		return pane; 		
	}
	
	public static void createWindow() throws IOException{
		
		JFrame frame = new JFrame("EVO PROJECT MULTIPLE TRIAL GENERATOR"); 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container pane = frame.getContentPane(); 
		frame.setContentPane(createContent(pane));
		
		frame.setSize(700, 250);
		frame.setVisible(true);
	}
	
	public static void main(String[] args){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
					createWindow();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
	}	
	
}
