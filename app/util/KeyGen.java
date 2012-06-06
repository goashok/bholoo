package util;

public class KeyGen {
	
	

	public static String  key(String... args) {
		return args.toString();
	}
	
	public static String userActivation(String... args) {
		StringBuilder sb = new StringBuilder(KeySpace.UserActivation.keySpace());
		for(String arg : args) {
			sb.append(arg);
		}
		return sb.toString();
	}
	
}
	

