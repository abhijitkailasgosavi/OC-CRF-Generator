package ocg.connUtils;

public interface ConnUtils {
	void createStudy(String title, String studyId);

	void createSite(String title, String siteId, String studyId);
}