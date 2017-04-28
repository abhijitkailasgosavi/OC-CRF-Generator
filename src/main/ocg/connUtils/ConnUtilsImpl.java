package ocg.connUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.util.IOUtils;

import ocg.crfGenerator.CRFGeneratorImpl;

public class ConnUtilsImpl implements ConnUtils {
	private Map<String, String> studyUId = new HashMap<String, String>();

	public String createStudy(String title, String studyId) {
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

			String apiKey = "c8a82545104e4fd89938650fa70dbd23";
			apiKey += ":";
			String basicAuth = "Basic " + new String(Base64.getEncoder().encode(apiKey.getBytes()));
			conn.setRequestProperty ("Authorization", basicAuth);

			String studyTitle = title;
			uniqueProtocolID = title.substring(0, Math.min(title.length(), 15)) /*+ " " + studyId*/;
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
				CRFGeneratorImpl.logger.error("Failed to create study" + title +
						" : HTTP error code :" + conn.getResponseCode());
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
			CRFGeneratorImpl.logger.error("Error in create study" + title + " "+e.getMessage());
		} finally {
			IOUtils.closeQuietly(outputStream);
			IOUtils.closeQuietly(bufferedReader);
			conn.disconnect();
		}
		return uniqueProtocolID;
	}

	public String createSite(String title, String siteId, String studyId) {
		HttpURLConnection conn = null;
		OutputStream outputStream = null;
		BufferedReader bufferedReader = null;
		String uniqueProtocolID = null;
		try {
			String studyUniqueId = studyUId.get(studyId);
			String siteUrl =new String("http://localhost:8080/OpenClinica/pages/auth/api/v1/studies/"+studyUniqueId+"/sites");
			URL url = new URL(siteUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type","application/json;");

			String apiKey = "c8a82545104e4fd89938650fa70dbd23";
			apiKey += ":";
			String basicAuth = "Basic " + new String(Base64.getEncoder().encode(apiKey.getBytes()));
			conn.setRequestProperty ("Authorization", basicAuth);

			String siteTitle = title.replaceAll("[^a-zA-Z0-9]", "_");
			//uniqueProtocolID = title.substring(0, Math.min(title.length(), 10)) 
			//		+ "_" + studyId.substring(0, Math.min(studyId.length(), 5)) ;
			uniqueProtocolID = title.substring(0, Math.min(title.length(), 10)) 
							+ "_" + siteId ;
			uniqueProtocolID = uniqueProtocolID.replaceAll("[^a-zA-Z0-9]", "_");
			String input ="{\"briefTitle\": \""+siteTitle+"\"," +
					"\"principalInvestigator\": \"userz\", "+
					"\"expectedTotalEnrollment\": \"10\","+
					"\"assignUserRoles\": [{ \"username\" : \"user1\", \"role\" : \"Investigator\"}," +
					"{ \"username\" : \"usera\", \"role\" : \"Clinical Research Coordinator\"}]," +
					"\"uniqueProtocolID\": \""+uniqueProtocolID+"\"," +
					"\"startDate\": \"2017-06-11\"," +
					"\"secondaryProtocolID\" : \""+uniqueProtocolID+"_2\" ," +
					"\"protocolDateVerification\" : \"2017-05-14\"}";

			outputStream = conn.getOutputStream();
			outputStream.write(input.getBytes());
			outputStream.flush();

			if (conn.getResponseCode() != 200 ) {
				CRFGeneratorImpl.logger.error("Failed : HTTP error code :" + conn.getResponseMessage());
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;
				CRFGeneratorImpl.logger.info("Output from Server ....");
				while ((output = bufferedReader.readLine()) != null) {
					CRFGeneratorImpl.logger.info(output);
				}
			}
		} catch (Exception e) {
			CRFGeneratorImpl.logger.error("Error in create site" + title + " "+e.getMessage());
		} finally {
			IOUtils.closeQuietly(outputStream);
			IOUtils.closeQuietly(bufferedReader);
			conn.disconnect();
		}
		return uniqueProtocolID;
	}
}