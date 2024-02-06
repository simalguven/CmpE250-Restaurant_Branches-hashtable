public class Member {
    String name;
    String memberType;
    int promotion_point;
    public Member(String name,String type){
        this.name=name;
        this.memberType=type;
        this.promotion_point=0;
    }
    public int hash() {
        return name.hashCode(); // Use hashCode of the district string
    }
    public String toString(){
        return name;
    }
    public boolean equals(Object o){
        Member MemberChecked = (Member) o;
        return (this.name).equals(MemberChecked.name);
    }


}
