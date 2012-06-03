package models;

public enum EntityType {

	/**
	 * DO NOT change the order as the ordinals are already stored in db and interpreted
	 * once defined.
	 */
	Classifieds,
	Events,
	Deals,
	YellowPages,
	Undefined;
	
	public static EntityType fromOrdinal(int i) {
		try {
			return values()[i];
		}catch(Exception e) {
			return Undefined;
		}
	}
	
	public static int Classifieds() {
		return Classifieds.ordinal();
	}
	
	public static int Events() {
		return Events.ordinal();
	}
	
	public static int Deals() {
		return Deals.ordinal();
	}
	
	public static int YellowPages() {
		return YellowPages.ordinal();
	}
	
}
