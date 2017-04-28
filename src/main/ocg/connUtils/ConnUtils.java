package ocg.connUtils;

public interface ConnUtils {
	public String createStudy(String title, String studyId);

    public String createSite(String title, String siteId, String studyId);
}