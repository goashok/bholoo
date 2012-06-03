package models;

public enum EntityState {
	
	Inactive,
	Active,
	PendingApproval,
	Undefined;
	
	public static EntityState fromOrdinal(int i) {
		try {
			return values()[i];
		}catch(Exception e) {
			return Undefined;
		}
	}
	
	
	public static int Inactive() {
		return Inactive.ordinal();
	}
	
	public static int Active() {
		return Active.ordinal();
	}
	
	public static int PendingApproval() {
		return  PendingApproval.ordinal();
	}
	
	public static int Undefined() {
		return Undefined.ordinal();
	}
}
