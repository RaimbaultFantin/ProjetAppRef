package client;

public class Client {
	private String mdp;
	private String url;
	public Client(String mdp) {
		super();
		this.mdp = mdp;
		this.url = null;
	}
	
	@Override
	public String toString() {
		return mdp;
	}
	
	public void setUrl(String url) {
		this.url = url.replaceAll("\\s+","");
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getMdp() {
		return mdp;
	}
	
	public boolean hasUrl() {
		return url != null;
	}
	
}
