import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Iterator;

public class project2 {

    public static void main(String[] args) throws IOException {
        FileWriter filewriter = new FileWriter(args[2],true);

        Table<Branch> company = new Table<>(Table.DEFAULT_TABLE_SIZE);//company initialized
        try{
            File file = new File(args[0]);
            Scanner scanner = new Scanner(file);

            while(scanner.hasNextLine()){
                String nextLine = scanner.nextLine();
                String[] lineArray=nextLine.split(", ");
                Branch branch= new Branch(lineArray[0],lineArray[1]);//open a new branch wrt name of the city and district
                company.insert(branch);
                branch = company.getelement(branch,branch.getName());//get the element inside the company map that has the same name -used for equalizing the address of interested branch
                Member member = new Member(lineArray[2],lineArray[3]);//create the member obj wrt name and employee type
                branch.addtoBranch(member);

            }
            scanner.close();


       }catch(FileNotFoundException e){
           System.out.println("File not found "+e.getMessage());
       }
        try{
            File file = new File(args[1]);
            Scanner scanner = new Scanner(file);
            String disposedLine = scanner.nextLine();

            while(scanner.hasNextLine()){
                String nextLine= scanner.nextLine();
                String[] lineArray = nextLine.split(" ");
                if(lineArray[0].equals("LEAVE:")){//any case that might happen after a leave action of an employee
                    String line = nextLine.replace("LEAVE: ","");
                    String[] linear = line.split(", ");
                    String str1 = linear[0];
                    String str2 = linear[1];
                    String str3 = linear[2];
                    Branch branch= new Branch(str1,str2);
                    Branch branchOfRemoved =company.getelement(branch,str1+str2);//select the branch to update
                    Member memberToRemove = branchOfRemoved.findMember(str3);//select the member to leave
                    if(memberToRemove == null){//if selected branch do not contain given employee to remove
                        filewriter.write("There is no such employee."+"\n");
                    }else{//selected branch has that employee to leave
                        String decreasedEmployeeType = memberToRemove.memberType;
                        if(decreasedEmployeeType.equals("COOK")){//if it is a cook
                            if(branchOfRemoved.cookCount==1){//cook won't be dismissed, there is only him/her at that branch
                                if(!branchOfRemoved.shouldBeDismissed.contains(memberToRemove)){//only if he/she has normal status, not waiting to leave
                                    branchOfRemoved.monthly_bonuses += 200;//a bonus given
                                    branchOfRemoved.overall_bonuses += 200;
                                }

                            }else{//cook will be dismissed
                                branchOfRemoved.cookCount--;
                                branchOfRemoved.getEmployeeArray().remove(memberToRemove);
                                filewriter.write(linear[2]+" is leaving from branch: "+linear[1]+"."+"\n");
                                branchOfRemoved.shouldBeDismissed.remove(memberToRemove);// may or may not in dismissal list remove for guarantee
                                branchOfRemoved.cookQueue.remove(memberToRemove);//may or may not waiting for a promotion remove for guarantee

                            }
                        }


                        if(decreasedEmployeeType.equals("CASHIER")){//if it is a cashier
                            if(branchOfRemoved.cashierCount==1){//cashier won't be dismissed, there is only him/her at that branch
                                if(!branchOfRemoved.shouldBeDismissed.contains(memberToRemove)){//only if he/she has normal status,not waiting to leave
                                    branchOfRemoved.monthly_bonuses += 200;// a bonus given
                                    branchOfRemoved.overall_bonuses += 200;
                                }
                            }else{//cook will be dismissed
                                branchOfRemoved.cashierCount--;
                                branchOfRemoved.getEmployeeArray().remove(memberToRemove);
                                filewriter.write(linear[2]+" is leaving from branch: "+linear[1]+"."+"\n");
                                branchOfRemoved.shouldBeDismissed.remove(memberToRemove);// may or may not in dismissal list remove for guarantee
                                branchOfRemoved.cashierQueue.remove(memberToRemove);//may or may not waiting for a promotion remove for guarantee

                            }
                        }


                        if(decreasedEmployeeType.equals("MANAGER")){//if it is a manager
                            if(branchOfRemoved.cookQueue.isEmpty()){//there is no one deserves to be the next manager
                                if(!branchOfRemoved.shouldBeDismissed.contains(memberToRemove)){//manager is not in dismissal list
                                    branchOfRemoved.monthly_bonuses += 200;//a bonus given
                                    branchOfRemoved.overall_bonuses += 200;
                                }

                            }else{//manager can leave someone will take his/her place
                                branchOfRemoved.shouldBeDismissed.remove(memberToRemove);
                                branchOfRemoved.getEmployeeArray().remove(memberToRemove);
                                filewriter.write(linear[2]+" is leaving from branch: "+linear[1]+"."+"\n");
                                Member willBeNextManager = branchOfRemoved.cookQueue.poll();
                                willBeNextManager.promotion_point-=10;
                                willBeNextManager.memberType="MANAGER";
                                branchOfRemoved.manager=willBeNextManager;
                                branchOfRemoved.cookCount--;
                                filewriter.write(willBeNextManager+" is promoted from Cook to Manager."+"\n");

                            }

                        }

                        if(decreasedEmployeeType.equals("COURIER")){//if it's a courier
                            if(branchOfRemoved.courierCount==1){//courier won't be dismissed, there is only him/her at that branch
                                if(!branchOfRemoved.shouldBeDismissed.contains(memberToRemove)){//courier is not in dismissal list
                                    branchOfRemoved.monthly_bonuses += 200;//a bonus given
                                    branchOfRemoved.overall_bonuses += 200;
                                }

                            }else{//he/her may leave
                                branchOfRemoved.shouldBeDismissed.remove(memberToRemove);
                                branchOfRemoved.courierCount--;
                                branchOfRemoved.getEmployeeArray().remove(memberToRemove);
                                filewriter.write(linear[2]+" is leaving from branch: "+linear[1]+"."+"\n");
                            }

                        }


                    }

                }
                if(lineArray[0].equals("PERFORMANCE_UPDATE:")){//any case that might happen after a performance update of an employee
                    String line = nextLine.replace("PERFORMANCE_UPDATE: ","");
                    String[] linear = line.split(", ");
                    String str1 = linear[0];
                    String str2 = linear[1];
                    String str3 = linear[2];
                    Branch branch= new Branch(str1,str2);
                    Branch branchOfUpdate =company.getelement(branch,str1+str2);
                    Member memberToUpdate = branchOfUpdate.findMember(str3);
                    if(memberToUpdate==null){
                        filewriter.write("There is no such employee."+"\n");
                    }
                    else{
                        memberToUpdate.promotion_point+=Integer.parseInt(linear[3]) / 200; //give a member its promotion point and add it to the existing points
                        if(Integer.parseInt(linear[3])>0){ //if performance update given is positive give the remaining from promotion point as bonus
                            branchOfUpdate.monthly_bonuses+= Integer.parseInt(linear[3]) % 200; //give as bonus
                            branchOfUpdate.overall_bonuses+= Integer.parseInt(linear[3]) % 200; //give as bonus
                        }
                        //no longer deserves to be promoted
                        if(memberToUpdate.memberType.equals("CASHIER") && branchOfUpdate.cashierQueue.contains(memberToUpdate)&&memberToUpdate.promotion_point<3){// updated member was a cashier waiting to be promoted but no longer deserves promotion
                            branchOfUpdate.cashierQueue.remove(memberToUpdate);//get it outside promotion  queue
                        }
                        if(memberToUpdate.memberType.equals("COOK") && branchOfUpdate.cookQueue.contains(memberToUpdate)&&memberToUpdate.promotion_point<10){//updated member was a cook waiting to be promoted but no longer deserves promotion
                            branchOfUpdate.cookQueue.remove(memberToUpdate);//get it outside promotion queue
                        }
                        //no longer deserves to be dismissed
                        if(branchOfUpdate.shouldBeDismissed.contains(memberToUpdate)&&memberToUpdate.promotion_point>-5){//member was to be dismissed but has a higher point now
                            branchOfUpdate.shouldBeDismissed.remove(memberToUpdate);//get it outside to be dismissed list
                        }
                        //possible promotions
                        if(memberToUpdate.memberType.equals("CASHIER") && memberToUpdate.promotion_point >=3){//a cashier's promotion point was updated, and now it deserves to be a cook
                            if(branchOfUpdate.CashierNumEnough()){//it can be a cook ,he/she is not the only cashier in the branch
                                branchOfUpdate.cashierQueue.remove(memberToUpdate);//to be sure it's no longer in the promotion queue
                                memberToUpdate.memberType="COOK";
                                memberToUpdate.promotion_point-=3;
                                branchOfUpdate.cashierCount--;
                                branchOfUpdate.cookCount++;
                                filewriter.write(memberToUpdate.name+" is promoted from Cashier to Cook."+"\n");

                                if(memberToUpdate.promotion_point>= 10&& !branchOfUpdate.cookQueue.contains(memberToUpdate)){//he/she is not in the cook queue but promotion point is enough to be in cook queue just after promoted to a cook and lost 3 points
                                    branchOfUpdate.cookQueue.add(memberToUpdate);
                                }
                                for(Member m:branchOfUpdate.shouldBeDismissed){//there may be a cook waiting to be dismissed but can't because he/she is the only cook.
                                    if(m.memberType.equals("COOK")&&branchOfUpdate.CookNumEnough()){//cook num is now enough for prior cook to be dismissed
                                        branchOfUpdate.cookCount--;
                                        branchOfUpdate.getEmployeeArray().remove(m);
                                        branchOfUpdate.shouldBeDismissed.remove(m);
                                        filewriter.write(m.name+" is dismissed from branch: "+branch.getDistrict()+"."+"\n");
                                    }
                                }

                            }else{//it can't be a cook he/she is the only cashier existing
                                if(!branchOfUpdate.cashierQueue.contains(memberToUpdate)){
                                    branchOfUpdate.cashierQueue.add(memberToUpdate);
                                }
                            }

                        }
                        if(memberToUpdate.memberType.equals("COOK") && memberToUpdate.promotion_point>=10){
                            if(branchOfUpdate.shouldBeDismissed.contains(branchOfUpdate.manager)){//manager is waiting to be dismissed -then priorly no cook was in the queue - no need to put memberToUpdate in the queue do a direct promotion
                                branchOfUpdate.cookQueue.remove(memberToUpdate);//to be sure it's no longer in the promotion queue we know not but to guarantee
                                branchOfUpdate.shouldBeDismissed.remove(branchOfUpdate.manager);
                                branchOfUpdate.getEmployeeArray().remove(branchOfUpdate.manager);
                                memberToUpdate.memberType="MANAGER";
                                memberToUpdate.promotion_point-=10;//lost 10 of his/her points
                                branchOfUpdate.cookCount--;
                                filewriter.write(branchOfUpdate.manager.name+" is dismissed from branch: "+branchOfUpdate.getDistrict()+"."+"\n");
                                branchOfUpdate.manager=memberToUpdate;
                                filewriter.write(memberToUpdate.name+" is promoted from Cook to Manager."+"\n");


                            }else{//cannot be manager directly branch has a sufficient manager
                                if(!branchOfUpdate.cookQueue.contains(memberToUpdate)){//if cook queue already does not contain
                                    branchOfUpdate.cookQueue.add(memberToUpdate);//add it there
                                }
                            }

                        }
                        //possible dismissals
                        if(memberToUpdate.memberType.equals("COURIER") && memberToUpdate.promotion_point <= -5){//a courier should be dismissed
                            if(branchOfUpdate.CourierNumEnough()){//there is enough courier in the branch, can be dismissed
                                branchOfUpdate.getEmployeeArray().remove(memberToUpdate);
                                branchOfUpdate.courierCount--;
                                filewriter.write(memberToUpdate.name +" is dismissed from branch: "+linear[1]+"."+"\n");
                            }else{//he/she is the only courier cannot be dismissed right now
                                if(!branchOfUpdate.shouldBeDismissed.contains(memberToUpdate))
                                    branchOfUpdate.shouldBeDismissed.add(memberToUpdate);// add to should be dismissed if not priorly added
                            }
                        }
                        if(memberToUpdate.memberType.equals("CASHIER") && memberToUpdate.promotion_point <= -5){//a cashier should be dismissed
                            if(branchOfUpdate.CashierNumEnough()){//there is enough cashier in the branch,can be dismissed
                                branchOfUpdate.getEmployeeArray().remove(memberToUpdate);
                                branchOfUpdate.cashierCount--;
                                branch.cashierQueue.remove(memberToUpdate);//already was removed in prior if bodies but to be sure
                                filewriter.write(memberToUpdate.name +" is dismissed from branch: "+linear[1]+"."+"\n");
                            }else{//he/she is the only cashier, cannot be dismissed right now
                                if(!branchOfUpdate.shouldBeDismissed.contains(memberToUpdate)){
                                    branchOfUpdate.shouldBeDismissed.add(memberToUpdate);//add to should be dismissed if not priorly added
                                }

                            }

                        }
                        if(memberToUpdate.memberType.equals("COOK")&&memberToUpdate.promotion_point <=-5){// a cook should be dismissed
                            if(branchOfUpdate.CookNumEnough()){//there is enough cook in the branch,can be dismissed
                                branchOfUpdate.getEmployeeArray().remove(memberToUpdate);
                                branchOfUpdate.cookCount--;
                                branchOfUpdate.cookQueue.remove(memberToUpdate);//already was removed in prior if bodies but to be sure
                                filewriter.write(memberToUpdate.name+" is dismissed from branch: "+linear[1]+"."+"\n");
                            }else{//he/she is the only cook, cannot be dismissed right now
                                if(!branchOfUpdate.shouldBeDismissed.contains(memberToUpdate)){
                                    branchOfUpdate.shouldBeDismissed.add(memberToUpdate);//add to should be dismissed if not priorly added
                                }

                            }

                        }
                        if(memberToUpdate.memberType.equals("MANAGER")&& memberToUpdate.promotion_point <=-5){// the manager should be dismissed
                            if(branchOfUpdate.cookCount!=1){//branch has currently more than one cook
                                if(!branchOfUpdate.cookQueue.isEmpty()){//branch has at least one cook that deserves to be promoted
                                    branchOfUpdate.getEmployeeArray().remove(memberToUpdate);//manager dismissed
                                    Member tobePromoted = branchOfUpdate.cookQueue.poll();//first cook waiting in line determined
                                    tobePromoted.memberType="MANAGER";
                                    tobePromoted.promotion_point-=10;
                                    branchOfUpdate.manager= tobePromoted;
                                    branchOfUpdate.cookCount--;
                                    filewriter.write(memberToUpdate.name+" is dismissed from branch: "+linear[1]+"."+"\n");
                                    filewriter.write(branchOfUpdate.manager + " is promoted from Cook to Manager."+"\n");
                                }else{//branch has no cook that deserves to be promoted - for sure manager won't be dismissed right now
                                    if(!branchOfUpdate.shouldBeDismissed.contains(memberToUpdate)){
                                        branchOfUpdate.shouldBeDismissed.add(memberToUpdate);//if not already in to be dismissed list, add it
                                    }
                                }
                            }else{//branch has only one cook-for sure the manager won't be dismissed right now
                                if(!branchOfUpdate.shouldBeDismissed.contains(memberToUpdate)){
                                    branchOfUpdate.shouldBeDismissed.add(memberToUpdate);// if not already in to be dismissed list, add it
                                }
                            }
                        }

                    }

                }
                if(lineArray[0].equals("ADD:")){//any case that might happen after an addition of an employee
                    String line = nextLine.replace("ADD: ","");
                    String[] linear = line.split(", ");
                    Branch branch= new Branch(linear[0],linear[1]);
                    company.insert(branch);
                    branch = company.getelement(branch,branch.getName());
                    Member member = new Member(linear[2],linear[3]);
                    if(branch.findMember(member.name)==null){
                        branch.addtoBranch(member);
                        if(branch.CashierNumEnough()&&!branch.cashierQueue.isEmpty()){// a cashier was added and now the cashier who is waiting for promotion can be promoted
                            Member cashierToGetPromoted = branch.cashierQueue.poll();
                            cashierToGetPromoted.memberType="COOK";
                            cashierToGetPromoted.promotion_point-=3;
                            branch.cookCount++;
                            branch.cashierCount--;
                            if(cashierToGetPromoted.promotion_point>=10){// that cashier which was added also may have initialized the process of promotion of a cashier which has more than 13 promotion points-will be obtaining 10 more points after becoming a cook
                                if(!branch.cookQueue.contains(cashierToGetPromoted)){
                                    branch.cookQueue.add(cashierToGetPromoted);//add it into the cook queue if not already there
                                }
                            }
                            filewriter.write(cashierToGetPromoted.name+" is promoted from Cashier to Cook."+"\n");

                        }

                        if(branch.CookNumEnough()&&!branch.cookQueue.isEmpty()&&branch.shouldBeDismissed.contains(branch.manager)){// a cook was added and now the cook who is waiting for promotion while manager should be dismissed can be promoted
                            Member tobeManager = branch.cookQueue.poll();
                            branch.shouldBeDismissed.remove(branch.manager);
                            branch.getEmployeeArray().remove(branch.manager);
                            branch.cookCount--;
                            tobeManager.memberType="MANAGER";
                            tobeManager.promotion_point-=10;
                            filewriter.write(branch.manager.name+" is dismissed from branch: "+branch.getDistrict()+"."+"\n");
                            filewriter.write(tobeManager.name + " is promoted from Cook to Manager."+"\n");

                        }

                        Iterator<Member> iterator = branch.shouldBeDismissed.iterator();

                        while(iterator.hasNext()){//go through dismissal list, addition of a member may satisfy the conditions of(enough num) to be dismissed process
                            Member m = iterator.next();
                            if(m.memberType.equals("COURIER")&&branch.CourierNumEnough()){
                                iterator.remove();//removed from to be dismissed
                                branch.getEmployeeArray().remove(m);//removed from the branch's employee list
                                branch.courierCount--;
                                filewriter.write(m.name+" is dismissed from branch: "+branch.getDistrict()+"."+"\n");
                            }
                            if(m.memberType.equals("CASHIER")&&branch.CashierNumEnough()){
                                iterator.remove();//removed from to be dismissed
                                branch.getEmployeeArray().remove(m);//removed from the branch's employee list
                                branch.cashierCount--;
                                filewriter.write(m.name+" is dismissed from branch: "+branch.getDistrict()+"."+"\n");
                            }
                            if(m.memberType.equals("COOK")&&branch.CookNumEnough()){
                                iterator.remove();//removed from to be dismissed
                                branch.getEmployeeArray().remove(m);//removed from the branch's employee list
                                branch.cookCount--;
                                filewriter.write(m.name+" is dismissed from branch: "+branch.getDistrict()+"\n"+".");
                            }

                        }

                    }else{//member may already in the branch
                        filewriter.write("Existing employee cannot be added again."+"\n");
                    }

                }
                if(lineArray[0].equals("PRINT_MONTHLY_BONUSES:")){// print monthly bonus of given branch
                    String line = nextLine.replace("PRINT_MONTHLY_BONUSES: ","");
                    String[] linear = line.split(", ");
                    String str1 = linear[0];
                    String str2 = linear[1];
                    Branch branch= new Branch(str1,str2);
                    Branch branchToLook =company.getelement(branch,str1+str2);
                    filewriter.write("Total bonuses for the "+ str2 +" branch this month are: " + branchToLook.monthly_bonuses+"\n");


                }
                if(lineArray[0].equals("PRINT_OVERALL_BONUSES:")){//print overall bonus of given branch
                    String line = nextLine.replace("PRINT_OVERALL_BONUSES: ","");
                    String[] linear = line.split(", ");
                    String str1 = linear[0];
                    String str2 = linear[1];
                    Branch branch= new Branch(str1,str2);
                    Branch branchToLook =company.getelement(branch,str1+str2);
                    filewriter.write("Total bonuses for the "+ str2 +" branch are: " + branchToLook.overall_bonuses+"\n");

                }
                if(lineArray[0].equals("PRINT_MANAGER:")){//print manager of given branch
                    String line = nextLine.replace("PRINT_MANAGER: ","");
                    String[] linear = line.split(", ");
                    String str1 = linear[0];
                    String str2 = linear[1];
                    Branch branch= new Branch(str1,str2);
                    Branch branchToLook =company.getelement(branch,str1+str2);
                    filewriter.write("Manager of the "+linear[1]+" branch is "+branchToLook.manager+"."+"\n");
                }
                if(nextLine.equals("")){//if a month has ended
                    for(LinkedList<Branch> branches : company.getArray()){//go through each branch and make each monthly bonus count zero
                        for(Branch branch : branches){
                            branch.monthly_bonuses=0;
                        }
                    }

                }
            }
            filewriter.close();


        }catch(FileNotFoundException e){
            System.out.println("File not found "+e.getMessage());

        }

    }

}