package ocg.connUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;

import ocg.crfGenerator.CRFGeneratorImpl;

public class ConnUtilsImpl implements ConnUtils {
	private Map<String, String> studyUId = new HashMap<String, String>();

	public String createStudy(String studyTitle, String studyId, String apiKey) {
		HttpURLConnection conn = null;
		OutputStream outputStream = null;
		BufferedReader bufferedReader = null;
		String uniqueProtocolID = null;
		try {
			URL url = new URL("http://localhost:8080/OpenClinica/pages/auth/api/v1/studies/");
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type","application/json;");
			conn.setRequestProperty ("Authorization", "Basic " + apiKey);

			studyTitle = studyTitle.replaceAll("[\"\']", "");
			uniqueProtocolID = studyTitle.substring(0, Math.min(studyTitle.length(), 10)) +
					"_" + studyId ;
			uniqueProtocolID = uniqueProtocolID.replaceAll("[^a-zA-Z0-9]", "_");
			String input = "{\"briefTitle\": \"" + studyTitle + "\"," +
					"\"principalInvestigator\": \"default\"," +
					"\"expectedTotalEnrollment\": \"50\"," +
					"\"sponsor\": \"n_a\"," +
					"\"protocolType\": \"Interventional\"," +
					"\"status\": \"available\"," +
					"\"assignUserRoles\": ["+
					"{ \"username\": \"userz\", \"role\": \"Data Manager\" }]," +
					"\"uniqueProtocolID\": \"" + uniqueProtocolID + "\"," +
					"\"briefSummary\": \"Study for post api\"," +
					"\"startDate\": \"2017-06-12\"}";

			outputStream = conn.getOutputStream();
			outputStream.write(input.getBytes());
			outputStream.flush();

			if (conn.getResponseCode() != 200 ) {
				CRFGeneratorImpl.logger.error("Failed to create "+studyTitle+" study" +
						" : HTTP error code :"+ conn.getResponseCode() +" " + conn.getResponseMessage());
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;
				CRFGeneratorImpl.logger.info("Output from Server ....");
				while ((output = bufferedReader.readLine()) != null) {
					CRFGeneratorImpl.logger.info(output);
				}
			}
			studyUId.put(studyId, uniqueProtocolID);
		} catch (Exception e) {
			CRFGeneratorImpl.logger.error("Error in create study" + studyTitle + " "+e.getMessage());
		} finally {
			IOUtils.closeQuietly(outputStream);
			IOUtils.closeQuietly(bufferedReader);
			conn.disconnect();
		}
		return uniqueProtocolID;
	}

	public String createSite(String siteTitle, String siteId, String studyId, String apiKey, String username) {
		HttpURLConnection conn = null;
		OutputStream outputStream = null;
		BufferedReader bufferedReader = null;
		String uniqueProtocolID = null;
		try {
			String studyUniqueId = studyUId.get(studyId);
			String siteUrl = new String("http://localhost:8080/OpenClinica/pages/auth/api/v1/studies/"+
					studyUniqueId +"/sites");

			URL url = new URL(siteUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type","application/json;");
			conn.setRequestProperty ("Authorization", "Basic " + apiKey);

			siteTitle = siteTitle.replaceAll("[\"\']", "");
			uniqueProtocolID = siteTitle.substring(0, Math.min(siteTitle.length(), 8)) 
					+ "_" + siteId + "_" + studyId ;
			uniqueProtocolID = uniqueProtocolID.replaceAll("[^a-zA-Z0-9]", "_");
			String input ="{\"briefTitle\": \""+ siteTitle +"\"," +
					"\"principalInvestigator\": \"userz\", "+
					"\"expectedTotalEnrollment\": \"10\","+
					"\"assignUserRoles\": [{ \"username\" : \""+ username +
					"\", \"role\" : \"Clinical Research Coordinator\"}]," +
					"\"uniqueProtocolID\": \""+ uniqueProtocolID +"\"," +
					"\"startDate\": \"2017-06-11\"," +
					"\"secondaryProtocolID\" : \""+ uniqueProtocolID +"_2\" ," +
					"\"protocolDateVerification\" : \"2017-05-14\"}";

			outputStream = conn.getOutputStream();
			outputStream.write(input.getBytes());
			outputStream.flush();

			if (conn.getResponseCode() != 200 ) {
				CRFGeneratorImpl.logger.error("Failed to create "+ siteTitle +
						" site HTTP error code :"+conn.getResponseCode() +" " + conn.getResponseMessage());
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;
				CRFGeneratorImpl.logger.info("Output from Server ....");
				while ((output = bufferedReader.readLine()) != null) {
					CRFGeneratorImpl.logger.info(output);
				}
			}
		} catch (Exception e) {
			CRFGeneratorImpl.logger.error("Error in create site" + siteTitle + " "+e.getMessage());
		} finally {
			IOUtils.closeQuietly(outputStream);
			IOUtils.closeQuietly(bufferedReader);
			conn.disconnect();
		}
		return uniqueProtocolID;
	}

	public String getUserApiKey(String username, String password) {
		String apiKey = null;
		HttpURLConnection conn = null;
		OutputStream os = null;
		BufferedReader br = null;
		try {
			URL url = new URL("http://localhost:8080/OpenClinica/pages/accounts/login");
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json;");
			String input = "{\"username\":\""+username+"\",\"password\":\""+password+"\"}";

			os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != 200) {
				CRFGeneratorImpl.logger.error("Error in getting user apiKey " + conn.getResponseCode() +
						" " + conn.getResponseCode() );
			}

			br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String reader;
			String userDetails = null;
			while ((reader = br.readLine()) != null) {
				userDetails = reader;
			}
			userDetails = userDetails.replaceAll("[^a-zA-Z0-9]", " ");
			userDetails = userDetails.replaceAll("\\s+", " ");
			String[] userdetailsArray = userDetails.split(" ");
			for (int i = 0; i<userdetailsArray.length; i++) {
				if (userdetailsArray[i].equals("apiKey")) {
					apiKey = userdetailsArray[i + 1];
				}
			}
			apiKey += ":";
			apiKey =  new String(Base64.getEncoder().encode(apiKey.getBytes()));
		} catch (Exception e) {
			CRFGeneratorImpl.logger.error("Error in getting user apiKey "+e.getMessage());
		} finally {
			conn.disconnect();
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(os);
		}
		if (StringUtils.isBlank(apiKey)) {
			CRFGeneratorImpl.logger.error("apiKey is null may be username or password are wrong");
		}
		return apiKey;
	}
}