import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;


public class QueryProcess {

	static Map<Double, String> questionsPattern = loadQuestionsPattern();
//	static NaiveBayesModel sameModel =  loadClassifierModel();
	static Map<String , Integer> vocabulary = loadVocabulary(); 
	Map<String, String> abstractMap;
	
	public static void main(String[] agrs) throws Exception{
//		String sentence = "但丁密码中饰演罗伯特兰登的人是谁";
//		QueryProcess.process(sentence);
		trainclassifier();
	}
	
//	public static void process(String sentence){
//		QueryProcess qp = new QueryProcess();
//		String abstractQuery = qp.queryAbstract(sentence);
//		String queryPattern = "";
//		try {
//			queryPattern = qp.queryClassify(abstractQuery);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String orgin_query = qp.queryExtenstion(queryPattern);
//		System.out.println(orgin_query);
//	}
	
	public static NaiveBayesModel  loadClassifierModel(){
		SparkConf conf = new SparkConf();
		conf.set("spark.testing.memory", "2147480000");
		JavaSparkContext sc = new JavaSparkContext("local[*]", "spark", conf);
		NaiveBayesModel nb_Model = NaiveBayesModel.load(sc.sc(),"./myNaiveBayesModel");
		return nb_Model;
	}
	public String queryAbstract(String querySentence){
		Segment segment = HanLP.newSegment();
		List<Term> terms = segment.seg(querySentence);
		
		//句子抽象化
		String abstractQuery  = "";
		abstractMap = new HashMap<String, String>();
		for(Term term: terms){
			String word = term.word;
			String termStr = term.toString();
			if(termStr.contains("nm")){
				abstractQuery += "nm ";
				abstractMap .put("nm", word);
			}
			else if(termStr.contains("nnt")){
				abstractQuery += "nnt ";
				abstractMap .put("nnt", word);
			}
			else{
				abstractQuery += word + " ";
			}
		}
		return abstractQuery;
	}
		
	public String queryExtenstion(String queryPattern){
		//句子还原
		Set<String> set = abstractMap.keySet();
		for(String key : set){
			if(queryPattern.contains(key)){
				String value = abstractMap.get(key);
				queryPattern = queryPattern.replace(key, value);
			}
		}
		String extendedQuery = queryPattern;
		abstractMap.clear();
		abstractMap = null;
		return extendedQuery;
	}
	
	
	public static Map<String , Integer> loadVocabulary(){
		Map<String, Integer> vocabulary =  new HashMap<String, Integer>();
		File file = new File("./question/vocabulary.txt");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line;
		try {
			while((line = br.readLine())!= null){
				String[] tokens = line.split(":");
				int index = Integer.parseInt(tokens[0]);
				String word = tokens[1];
				vocabulary.put(word, index);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vocabulary;
	}
	
	public static String loadFile(String filename) throws IOException{
		File file = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String content = "";
		String line;
		while( ( line = br.readLine()) != null){
			content += line +"`";
		}
		System.out.println(content);
		return content;
	}
	
	public static double[] sentenceToArrays(String sentence) throws Exception{
		double[] vector = new double[vocabulary.size()];
		for(int i=0 ; i < vocabulary.size(); i++){
			vector[i] = 0;
		}
		Segment segment = HanLP.newSegment();
		List<Term> terms = segment.seg(sentence);
		for(Term term: terms){
			String word = term.word;
			if(vocabulary.containsKey(word)){
				int index = vocabulary.get(word);
				vector[index] = 1;
			}
		}
		return vector;
	}
	public static void trainclassifier() throws Exception{
		//生成spark对象
		SparkConf conf = new SparkConf();
		conf.set("spark.testing.memory", "2147480000");
		JavaSparkContext sc = new JavaSparkContext("local[*]", "spark", conf);
		
		//训练集生成
		List<LabeledPoint> train_list = new LinkedList<LabeledPoint>();
		
		String lengthQuestions = loadFile("./question/片长.txt");
		String[] sentences = lengthQuestions.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(0.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String scoreQuestions = loadFile("./question/评分.txt");
		sentences = scoreQuestions.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(1.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String timeQuestions = loadFile("./question/上映.txt");
		sentences = timeQuestions.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(2.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String boxOfficeQuestions = loadFile("./question/票房.txt");
		sentences = boxOfficeQuestions.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(3.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String areaQuestions = loadFile("./question/地区.txt");
		sentences = areaQuestions.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(4.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String styleQuestions = loadFile("./question/风格.txt");
		sentences = styleQuestions.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(5.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String themeQuestions = loadFile("./question/题材.txt");
		sentences = themeQuestions.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(6.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String similarMovieQuestions = loadFile("./question/相关电影.txt");
		sentences = similarMovieQuestions.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(7.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String storyQuestions = loadFile("./question/剧情.txt");
		sentences = storyQuestions.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(8.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String commentQuestions = loadFile("./question/评论.txt");
		sentences = commentQuestions.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(9.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String prizeQuestions = loadFile("./question/获奖.txt");
		sentences = prizeQuestions.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(10.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String roleListQuestions = loadFile("./question/角色列表.txt");
		sentences = roleListQuestions.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(11.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String roleQuestions = loadFile("./question/角色简介.txt");
		sentences = roleQuestions.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(12.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String actorQuestion = loadFile("./question/演员名字.txt");
		sentences = actorQuestion.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(13.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String actorCountryQuestion = loadFile("./question/演员国籍.txt");
		sentences =actorCountryQuestion.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(14.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String actorBirthdayQuestion = loadFile("./question/演员出生日期.txt");
		sentences = actorBirthdayQuestion.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(15.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String actorPopQuestion = loadFile("./question/演员受欢迎程度.txt");
		sentences = actorPopQuestion.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(16.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String actorRepresent = loadFile("./question/演员受欢迎程度.txt");
		sentences = actorRepresent.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(17.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String director = loadFile("./question/导演名字.txt");
		sentences = director.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(18.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String directorRepresent = loadFile("./question/导演的代表作品.txt");
		sentences = directorRepresent.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(19.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String company = loadFile("./question/出品公司.txt");
		sentences = company.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(20.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String companyRepresent = loadFile("./question/出品公司的过去作品.txt");
		sentences = companyRepresent.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(21.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String companyFuture = loadFile("./question/出品公司的未来作品.txt");
		sentences = companyFuture.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(22.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String scriptWriter = loadFile("./question/编剧名字.txt");
		sentences = scriptWriter.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(23.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		String scriptWriterRepresent = loadFile("./question/编剧的代表作品.txt");
		sentences =  scriptWriterRepresent.split("`");
		for(String sentence: sentences){
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(24.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		
		
		JavaRDD<LabeledPoint> trainingRDD = sc.parallelize(train_list);
		NaiveBayesModel nb_model = NaiveBayes.train(trainingRDD.rdd());
		nb_model.save(sc.sc(), "./myNaiveBayesModel");
//		
		}
	
	public static Map<Double, String> loadQuestionsPattern() {
		Map<Double, String> questionsPattern = new HashMap<Double, String>();
		File file = new File("./question/question_classification.txt");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String line;
		try {
			while((line = br.readLine()) != null){
				String[]  tokens = line.split(":");
				double index = Double.valueOf(tokens[0]);
				String pattern = tokens[1];
				questionsPattern.put(index, pattern);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return questionsPattern;
	}
	
//	public static String queryClassify(String sentence) throws Exception{
//		double[] testArray = sentenceToArrays(sentence);
//		Vector v = Vectors.dense(testArray);
//		double index = sameModel.predict(v);
//		return questionsPattern.get(index);
//	}
}
