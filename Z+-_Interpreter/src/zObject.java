
public class zObject {

	private String name;
	private String value;
	private String dataType;
	
	public zObject(String dataType, String name, String value) {
		this.name = name;
		this.dataType = dataType;
		
		if (!value.equalsIgnoreCase("")) {
			this.value = value;
		}
		else {
			value = "";
		}
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getName() {
		return name;
	}
	public String getDataType() {
		return dataType;
	}
	
}
