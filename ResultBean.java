package beans;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * An application level bean that holds the cats data and implements the voting algorithm
 * 
 * @author Ahmad Wabbi
 *
 */
public class ResultBean {

	private int totalVotesCounter = 0; 
	private ArrayList<Cat> catList;
	private HashMap<String, Cat> catMap; //we also store the same cats objects in a hash map to accelerate direct access by id 
	private Random random=new Random(); //used to pick two cats randomly
	private int[][] votes; //votes[i][j] is the number of times the cat i was voted over the cat j. i and j are indices in catList.
	private String[] rankedCats; //the urls of the cats ranked according to the algorithm. This is updated in "calculateRanks";
	
	
	public ResultBean() {
		catList = new ArrayList<Cat>();
		catMap = new HashMap<String, Cat>();
		String jsonData = executeGet("https://latelier.co/data/cats.json"); //get the list of cats
		try { //parse the response and fill "catList"
			JsonObject response = new JsonParser().parse(jsonData).getAsJsonObject();
			JsonArray cats = response.get("images").getAsJsonArray();
			rankedCats=new String[cats.size()];
			for(int i=0; i<cats.size(); i++) {
				JsonObject jsonCat = cats.get(i).getAsJsonObject();
				Cat cat=new Cat(jsonCat.get("id").getAsString(), jsonCat.get("url").getAsString(), i);
				catList.add(cat);
				catMap.put(cat.getId(), cat);
				rankedCats[i]=cat.getUrl();
			}
			votes=new int[cats.size()][cats.size()];
		}
		catch(Exception ex) {
			System.err.println("Exception in reading and decoding cats list: "+ex.getMessage());
		}
		
	}
	
	/**
	 * Called when the user votes (clicks on a cat's image). 
	 * @param vote A string that has the format: "id1;;id2". The user votes for "id1" over "id2".
	 */
	public synchronized void setVote(String vote) { //this is synchronized as this is an application level bean. This method can be called by different users for the same singleton object at the same time  
		if(vote==null || vote.trim().equals(""))
			return;
		
		String[] votesSplit = vote.split(";;");
		if(votesSplit.length!=2 || votesSplit[0]==null || votesSplit[0].trim().equals("") || votesSplit[1]==null || votesSplit[1].trim().equals(""))
			return;
		
		//All good, do the vote
		totalVotesCounter++;
		Cat c1=catMap.get(votesSplit[0]), c2=catMap.get(votesSplit[1]); //c1 is voted over c2
		votes[c2.getMyIndex()][c1.getMyIndex()]++;
		
		//Update rankedCats list according to this vote by applying the algorithm
		calculateRanks();
	}
	
	public String[] getRankedCats() {
		return rankedCats;
	}
	
	/**
	 * A function that picks to cats randomly from the list. The cats are always different. This is called from the CatMashBean bean.
	 * @return A CatPair that contains the two cat's ids
	 */
	public CatPair getRandomCatPair() {
		int catCount = catList.size();
		if(catCount<2)
			return null;
		int i1 = random.nextInt(catCount);
		int i2;
		do {
			i2 = random.nextInt(catCount);
		} while(i1==i2);
		
		return new CatPair(catList.get(i1), catList.get(i2));
	}
	
	public int getTotalVotesCounter() {
		return this.totalVotesCounter;
	}
	
	/**
	 * A utility function to execute a GET request
	 * @param targetURL The URL to GET (contains eventual request parameters
	 * @return The request result. This is (null) if an exception occurs. 
	 */
	private String executeGet(String targetURL) {
		  HttpURLConnection connection = null;
		  try {
		    //Create connection			
		    URL url = new URL(targetURL);
		    connection = (HttpURLConnection) url.openConnection();
		    connection.setRequestMethod("GET");
		    connection.setRequestProperty("Content-Language", "en-US");  
		    connection.setUseCaches(false);

		    //Get Response  
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuffer response = new StringBuffer(); 
		    String line;
		    while ((line = rd.readLine()) != null) {
		      response.append(line);
		      response.append('\r');
		    }
		    rd.close();
		    return response.toString();
		  } catch (Exception e) {
		    e.printStackTrace();
		    return null;
		  } finally {
		    if (connection != null) {
		      connection.disconnect();
		    }
		  }
		}

	/**
	 * Updates the "rankedCats" array according to the "votes" array by applying the algorithm described here: https://stackoverflow.com/questions/3848004/facemash-algorithm/13223303#13223303
	 */
	private void calculateRanks () {
		double[][] scores = new double[votes.length][votes.length];
		for(int j=0; j<votes.length; j++) {
			for(int i=0; i<votes.length; i++) {
				if(i==j) // ignore diagonal
					continue;
				scores[i][j] = totalScore(j, i);
			}
			normalizeColumns(scores);
			calculateLinesAverages(scores); //the averages are stored inside the "Cat" objects of "catList"
			fillRankedCats();
		}
	}
	
	/**
	 * Calculates direct+indirect score between two cats
	 * 
	 * @param from The index of the first cat
	 * @param to The index of the second cat
	 * @return The calculated score
	 */
	private double totalScore(int from, int to) {
		int size=catList.size();
		int count=0;
		double sum=0;
		for(int i=0; i<size; i++) {
			if(i==from || i==to)
				continue;
			if(votes[from][i]==0 && votes[i][from]==0) //no votes between from and i
				continue;
			count++;
			sum+=directScore(from, i)+directScore(i, to);
		}
		return directScore(from, to) + ((count==0)? 0 : sum/count); //sum of direct and average indirect score
	}
	
	/**
	 * Calculates direct score between two cats
	 * 
	 * @param from The index of the first cat
	 * @param to The index of the second cat
	 * @return The calculated score
	 */
	private double directScore(int from, int to) {
		int total=votes[from][to] + votes[to][from];
		if(total==0)
			return 0.0;
		return votes[from][to]*2.0/total - 1;
	}

	private void normalizeColumns(double[][] scores) {
		int size=scores.length;
		for(int j=0; j<size; j++) {
			double max=Double.NEGATIVE_INFINITY, min=Double.POSITIVE_INFINITY;
			for(int i=0; i<size; i++) {
				if(scores[i][j]<min)
					min=scores[i][j];
				if(scores[i][j]>max)
					max=scores[i][j];
			}
			
			min*=-1;
			
			for(int i=0; i<size; i++) {
				if(scores[i][j]>0)
					scores[i][j]/=max;
				else if (scores[i][j]<0)
					scores[i][j]/= min;
			}
		}
	}
	
	private void calculateLinesAverages(double[][] scores) {
		int size=scores.length;
		for(int i=0; i<size; i++) {
			double sum=0;
			for(int j=0; j<size; j++) {
				sum+=scores[i][j];
			}
			catList.get(i).setMyScore(sum/(size-1));
		} 
		
	}
	
	private void fillRankedCats() {
		int size=votes.length;
		boolean[] visited = new boolean[size];
		for(int i=0; i<size; i++) {
			int originalLocation=0;
			double max=Double.NEGATIVE_INFINITY;
			for(int j=0; j<size; j++) {
				if(visited[j])
					continue;
				double score=catList.get(j).getMyScore();
				if(score>max) {
					max=score;
					originalLocation = j;
				}
			}
			
			visited[originalLocation] = true;
			rankedCats[i]=catList.get(originalLocation).getUrl();
		}
	}
	
}
