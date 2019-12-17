import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.*;


public class StartTester {

	HashMap <RnaNode, Integer> CheckMap = new HashMap();

	public String StartTester(File bpseq, Constants setting, int trials, double fitness) throws IOException{
		
		sequenceAnalysis report = new sequenceAnalysis();


		parseFile test = new parseFile(bpseq); 
		test.removeNonCanonicals();
		setting.setLength(test.length());
		setting.setSequenceV(test.sequences());
		setting.setPositionV(test.positions());
		setting.setFitness(fitness);

		double iterationRandom = Math.random(); //used to keep .txt file names from being overwritten

		String name = "RD_batch"+LocalDateTime.now();

		name = name.substring(0, 27);
		name = name.replace(':', '_');
		String FileName = name+Double.toString(iterationRandom)+"("+trials+").txt";
		File newFile = new File(FileName);
		
		BufferedWriter details = new BufferedWriter(new FileWriter(newFile));
					
		treeBuilder builder = new treeBuilder(setting, details, CheckMap);
		RnaNode<String> result = builder.buildTree();

		if(setting.iterations()){
			SequenceBuilder iterationsSampler = new SequenceBuilder(result, setting);
			iterationsSampler.build();
			iterationsSampler.buildBPSEQ(setting.positionV());
			setting.setStarter(result);
		}

		details.close();
		
		if(!setting.details()){
			newFile.delete();
		}

		int cellCount = builder.cellCountGet();
		this.CheckMap = builder.check.CheckMap;

		return report.analysis(result, trials, cellCount, CheckMap);

	}
	
//doesn't remove non-canonicals at the start	
public String iStartTester(File bpseq, Constants setting, int trials, double fitness, HashMap <RnaNode, Integer> CheckMap) throws IOException{
		
		sequenceAnalysis report = new sequenceAnalysis();
		
		setting.setType(false);
		
		parseFile test = new parseFile(bpseq); 
		setting.setLength(test.length());
		setting.setSequenceV(test.sequences());
		setting.setPositionV(test.positions());
		setting.setFitness(fitness);

		double iterationRandom = Math.random();
				
		String name = "RD_batch"+LocalDateTime.now();
		name = name.substring(0, 27);
		name = name.replace(':', '_');
		String FileName = name+Double.toString(iterationRandom)+"("+trials+").txt";
		File newFile = new File(FileName);
		
		BufferedWriter details = new BufferedWriter(new FileWriter(newFile));
					
		treeBuilder builder = new treeBuilder(setting, details, CheckMap);
		RnaNode<String> result = builder.buildTree();
		
		if(setting.iterations()){
			SequenceBuilder iterationsSampler = new SequenceBuilder(result, setting);
			iterationsSampler.build();
			iterationsSampler.buildBPSEQ(setting.positionV());
			
			setting.setStarter(result);
			setting.setType(false);
		}
	
		
		if(setting.iterations()){
			SequenceBuilder iterationsSampler = new SequenceBuilder(result, setting);
			ArrayList<String> hold = iterationsSampler.build();
			
			Checker check = new Checker(setting);

			int count = 0;
			for(int i = 0; i < hold.size(); i++){
				
				RnaNode<String> tester = new RnaNode<String>();
				tester.setData(hold.get(i), i);
				
				if(setting.modelType()==1){
					if(!check.simpleMutation(tester)){
						count++;
					}
				}else if(setting.modelType()==2){
					if(!check.complexMutation(tester)){
						count++;
					}
				}else{
					if(!check.unpairedCheck(tester)){
						count++;
					}
				}
			}
			
			File bp = null; 
			
			if(count != 0){
				bp = iterationsSampler.buildBPSEQ(setting.positionV());
			}else{
				bp.delete();
			}
		}

		details.close();
		
		if(!setting.details()){
			newFile.delete();
		}
		int cellCount = builder.cellCountGet();
		HashMap <RnaNode, Integer> iterationsMapping = builder.check.CheckMap;
		return report.analysis(result, trials, cellCount, iterationsMapping);
	}
	
}
