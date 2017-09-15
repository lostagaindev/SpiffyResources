package lostagain.nl.spiffyresources.interfaces;

public interface HasForumType {
		    
	public static class ForumTypeConstant {
		private String forumTypesString;
	
		private ForumTypeConstant(String forumTypesString) {
	      this.forumTypesString = forumTypesString;
	    }
		  public String getForumType() {
		      return forumTypesString;
		    }
		  }
	
		ForumTypeConstant vBulletin = new ForumTypeConstant("vBulletin");

		ForumTypeConstant phpBB = new ForumTypeConstant("phpBB");

		ForumTypeConstant extraForumType = new ForumTypeConstant("extraForumType");
	
	void setForumType(ForumTypeConstant align);
	  
}
