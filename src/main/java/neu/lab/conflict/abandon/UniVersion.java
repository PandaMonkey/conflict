package neu.lab.conflict.abandon;

public class UniVersion {
	private String version;
	private String classfier;

	public UniVersion(String version, String classifier) {
		this.version = version;
		this.classfier = classifier;
	}

	public String getVersion() {
		return version;
	}

	public String getClassfier() {
		return classfier;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UniVersion) {
			UniVersion uniVersion = (UniVersion) obj;
			if (null == classfier) {
				return null == uniVersion.getClassfier() && version.equals(uniVersion.getVersion());
			} else {
				return classfier.equals(uniVersion.getClassfier()) && version.equals(uniVersion.getVersion());
			}

		}
		return false;
	}

	@Override
	public int hashCode() {
		if (null == classfier)
			return version.hashCode();
		return version.hashCode() + classfier.hashCode() * 31;
	}

}
