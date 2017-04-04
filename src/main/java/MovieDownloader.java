import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * A class for downloading movie data from the internet.
 * Code adapted from Google.
 *
 * YOUR TASK: Add comments explaining how this code works!
 * 
 * @author Joel Ross & Kyungmin Lee
 */
public class MovieDownloader {

	public static String[] downloadMovieData(String movie) {

		//construct the url for the omdbapi API
		String urlString = "";
		try {
			urlString = "http://www.omdbapi.com/?s=" + URLEncoder.encode(movie, "UTF-8") + "&type=movie";
		}catch(UnsupportedEncodingException uee){ // catch error if the encoding is not successful
			return null;
		}

		// create java HTTP connection and bufferreader for file I/O
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;

		String[] movies = null;

		try {
			// create url
			URL url = new URL(urlString);

			// get the server connected for http requests
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

			// for reading the response from IMDB API 
			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer();
			if (inputStream == null) {
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String line = reader.readLine();
			while (line != null) {
				buffer.append(line + "\n");
				line = reader.readLine();
			}

			if (buffer.length() == 0) {
				return null;
			}
			// tokenlize response to json for displaying to the user 
			String results = buffer.toString();
			results = results.replace("{\"Search\":[","");
			results = results.replace("]}","");
			results = results.replace("},", "},\n");

			movies = results.split("\n");
		} 
		catch (IOException e) { // catch I/O error
			return null;
		} 
		finally { // disconnect the server and close file I/O
			if (urlConnection != null) {
				urlConnection.disconnect(); 
			}
			if (reader != null) {
				try {
					reader.close();
				} 
				catch (IOException e) {
				}
			}
		}
		// return json
		return movies;
	}


	public static void main(String[] args) 
	{
		Scanner sc = new Scanner(System.in);

		boolean searching = true;

		// keeps prompting user until user enters "q" to quit
		while(searching) {					
			System.out.print("Enter a movie name to search for or type 'q' to quit: ");
			String searchTerm = sc.nextLine().trim();
			if(searchTerm.toLowerCase().equals("q")){
				searching = false;
			}
			else {
				String[] movies = downloadMovieData(searchTerm);
				for(String movie : movies) {
					System.out.println(movie);
				}
			}
		}
		sc.close();
	}
}
