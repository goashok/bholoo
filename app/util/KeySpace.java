package util;

public enum KeySpace {
	
	UserActivation("user.activation"),
	LocationPref("location.pref");
	
	private KeySpace(String keySpace) {
		this.keySpace = keySpace;
	}
	private String keySpace;
	
	public String keySpace() {
		return this.keySpace;
	}
}