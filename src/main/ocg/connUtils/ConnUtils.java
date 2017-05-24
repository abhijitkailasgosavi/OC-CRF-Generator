package ocg.connUtils;

public interface ConnUtils {
	public String createStudy(String title, String studyId, String apiKey);

	public String createSite(String title, String siteId, String studyId, String apiKey, String username);

	public String getUserApiKey(String username, String password);
}