import java.util.*;

public class Branch {
    private String city;
    private String district;
    public int cookCount;
    public int courierCount;
    public int cashierCount;

    public Member manager;
    private Table<Member> employeeArray;
    public Queue<Member> cookQueue = new LinkedList<>();//if a cook in the branch deserves to be promoted put it in the queue
    public Queue<Member> cashierQueue= new LinkedList<>();//if a cashier in the branch deserves to be promoted put it in the queue
    public ArrayList<Member> shouldBeDismissed = new ArrayList<>();
    public int monthly_bonuses;//until saw an empty line increase iy when an empty line make it 0
    public int overall_bonuses;//will increase at each promotion

    public boolean CashierNumEnough(){
        return cashierCount > 1;
    }
    public boolean CookNumEnough(){
        return cookCount > 1;
    }

    public String getDistrict() {
        return district;
    }

    public boolean CourierNumEnough(){
        return courierCount >1;
    }
    public String getName(){
        return city+district;
    }


    public Table<Member> getEmployeeArray() {
        return employeeArray;
    }



    public Branch(String city,String district){
        this.city=city;
        this.district=district;
        employeeArray = new Table<Member>(Table.DEFAULT_TABLE_SIZE);

    }
    public Branch addtoBranch(Member m){
        employeeArray.insert(m);
        String type= m.memberType;
        if(type.equals("COOK"))
            cookCount++;
        if(type.equals("CASHIER"))
            cashierCount++;
        if(type.equals("MANAGER"))
            this.manager=m;
        if(type.equals("COURIER"))
            courierCount++;

        return this;

    }
    public Member findMember(String name){
        int index= name.hashCode();
        index = index % employeeArray.getArray().length;
        if(index < 0) {
            index += employeeArray.getArray().length;
        }

        LinkedList<Member> listtosearch= employeeArray.getArray()[index];
        for(Member value: listtosearch){
            if(value.name.equals(name)){
                return value;
            }

        }
        return null;

    }
    public int hash() {
        return (city+district).hashCode(); // Use hashCode of the district string
    }
    public String toString(){
        return city+district;
    }


    public boolean equals(Object o){
         Branch BranchChecked = (Branch) o;
         return ((this.city+this.district).equals(((Branch) o).city + ((Branch) o).district));
    }


}
