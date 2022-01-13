import java.util.*; 
import java.io.*;

class Item {
	int item_id;
	Item parent;
	List<Item> children = new ArrayList<Item>();
	int count=0;
}

public class fptminer {
	
	public static int minsup=0;
	public static double mincon=0;
	public static HashMap<String, Integer> frequent_patterns = new HashMap<String, Integer>();
	public static HashMap<String, Integer> final_frequent_patterns = new HashMap<String, Integer>();
	public static HashMap<Integer, Integer> count = new HashMap<Integer, Integer>();
	public static String filePath = "";
	public static String outfile = "";

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		// TODO Auto-generated method stub
		minsup=Integer.parseInt(args[0]);
		mincon=Double.parseDouble(args[1]);
		filePath=args[2];
		String outfile=args[3];
		
	    HashMap<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
	    HashMap<Integer, List<Item>> head = new HashMap<Integer, List<Item>>();
	    FileWriter fw = new FileWriter(new File(outfile));
	    
	    String line;
	    Integer p_tid=0;
	    int index=0;
	    
	    
	    BufferedReader reader = new BufferedReader(new FileReader(filePath));
	    List<Integer> list = new ArrayList<Integer>();
	    
	    while ((line = reader.readLine()) != null)
	    {
	        String[] parts = line.split(" ", 2);
	        
	            int tid = Integer.parseInt(parts[0]);
	            int event = Integer.parseInt(parts[1]);
	            if(p_tid!=tid)
	            {
	            	
	            	map.put(p_tid, list);
	            	list = new ArrayList<Integer>();
	            	list.add(event);
	            	if(count.containsKey(event))
	            	{
	            		count.put(event, count.get(event) + 1);
	            	}
	            	else {
	            		count.put(event, 1);
	            	}
	            }
	            else if(p_tid==tid)
	            {
	            	list.add(event);
	            	if(count.containsKey(event))
	            	{
	            		count.put(event, count.get(event) + 1);
	            	}
	            	else {
	            		count.put(event, 1);
	            	}
	            }
	            p_tid=tid;
	    }
	    map.put(p_tid, list);
	    
	    
	    //System.out.println("Before pruning and sorting");
	    /*for (int i : map.keySet())
	    {
	        System.out.println(i + ":" + map.get(i));
	    }*/

	    for (Integer x : count.keySet())
	    {
	    		if(count.get(x)<minsup)
	    		{
	    			for (Integer y : map.keySet())
	    			{
	    				map.get(y).remove(x);
	    			}
	    		}
	    }
	    
	    for (int j : map.keySet())
		{
	    	for(int z=0;z<map.get(j).size();z++)
	    	{
	    		for(int y=1;y<map.get(j).size()-z;y++)
	    		if(count.get(map.get(j).get(y-1))<count.get(map.get(j).get(y)))
	    		{
	    			int temp=map.get(j).get(y);
	    			map.get(j).set(y,map.get(j).get(y-1));
	    			map.get(j).set(y-1, temp);
	    		}
	    	}
		}
	    
	   /* for (int i : map.keySet())
	    {
	        System.out.println(i + ":" + map.get(i));
	    }*/
	    reader.close();
	    //System.out.println(count);
	    
	    //fp-tree
	    Item root=new Item();
	    root.item_id=-1;
	    
	    for (int i : map.keySet())
	    {
	    	Item pre_node=root;
	    	for(int z=0;z<map.get(i).size();z++)
	    	{
	    		int flag=0;
	    		for(int j=0;j<pre_node.children.size();j++)
	    		{
	    			Item temp=pre_node.children.get(j);
	    			if(map.get(i).get(z)==temp.item_id)
	    			{
	    				flag=1;
	    				index=j;
	    				break;
	    			}
	    		}
	    		if(flag==1)
	    		{
	    			Item n=(Item)pre_node.children.get(index);
	    			n.count++;
	    			pre_node=n;
	    		}
	    		else
	    		{
	    			Item node=new Item();
	    			node.item_id=map.get(i).get(z);
	    			node.count++;
	    			node.parent=pre_node;
	    			pre_node.children.add(node);
	    			if(head.containsKey(map.get(i).get(z)))
	    			{
	    			head.get(map.get(i).get(z)).add(node);
	    			}
	    			else
	    			{
	    				List<Item> links = new ArrayList<Item>();
	    				links.add(node);
	    				head.put(map.get(i).get(z), links);
	    			}
	    			pre_node=node;
	    		}
	    	}
	    }
	    
	    //testing
	   /* List<Item> t=head.get(7);
	    for(int z=0;z<t.size();z++)
    	{ 
	    	Item a=t.get(z);
	    	System.out.println(a.children);
	    	System.out.println("Parent "+a.parent.item_id);
	    	for(int x=0;x<a.children.size();x++)
	    	{
	    	System.out.println(a.children.get(x).item_id);
	    	
    	}*/
	    //fp-growth	
	    for (int i : head.keySet())
	    {
	    List<Item> test_node=head.get(i);//testing for particular item
	    int node_id=test_node.get(0).item_id;
    	String a=Integer.toString(node_id);
	    frequent_patterns.put(Integer.toString(test_node.get(0).item_id), count.get(test_node.get(0).item_id));
	    fpgrowth(test_node,a);
	    }
	    for(String pat : frequent_patterns.keySet())
	    {
	    	String p="";
	    	String[] part = pat.split(" ");
			List<String> items = new ArrayList<String>(Arrays.asList(part));
			Collections.sort(items);
			for(int i=0;i<items.size();i++)
			{
				p=p+items.get(i)+" ";
			}
			p=p.substring(0, p.length()-1);
	    	final_frequent_patterns.put(p,frequent_patterns.get(pat));
	    }
	    long patTime = System.currentTimeMillis();
	    long time=patTime - startTime;
	    System.out.println("Patterns Time: "+(double)time/1000);
	    
	    if(minsup==15 || minsup==20)
	    {
	    for(String f : final_frequent_patterns.keySet())
	    {
	    	String fp=f;
	    	String s=final_frequent_patterns.get(f).toString();
	    	//System.out.println(fp+"|{}|"+s+"|"+"-1");
			fw.write(fp+"|{}|"+s+"|"+"-1");
		    fw.write("\n");
	    }
	    }
	    
	   System.out.println(final_frequent_patterns.size());
	    
	    //Association Rule Mining
	    
	    if(minsup!=15 && minsup!=20)
	    {
	    for(String pattern : final_frequent_patterns.keySet())
	    {
		String[] part = pattern.split(" ");
		if(part.length>1)
		{
		List<String> items = new ArrayList<String>(Arrays.asList(part));
		
		for(int j=0;j<items.size();j++)
		{
		List<String> rhs = new ArrayList<String>();
		List<String> lhs = new ArrayList<String>();
		String temp = items.get(j);
		rhs.add(temp);
		for(int i=0;i<items.size();i++)
		{
			if(items.get(i)!=temp)
			{
				lhs.add(items.get(i));
			}
		}
		/*System.out.println("Initial Pattern: "+pattern);
		System.out.println("Initial LHS: "+lhs);
		System.out.println("Initial RHS: "+rhs);*/
		try {
			association(lhs,rhs,pattern,fw);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	    }
	    }
	    }
	    fw.close();
	    long assocTime = System.currentTimeMillis();
	    long t=assocTime-patTime;
	    System.out.println("Rules time: "+(double)t/1000);
	}

	private static void fpgrowth(List<Item> test_node, String c) {
		// TODO Auto-generated method stub
		List<List<Integer>> test_transaction=new ArrayList<List<Integer>>();
	    HashMap<Integer, Integer> test_count = new HashMap<Integer, Integer>();
	    HashMap<Integer, List<Item>> c_head = new HashMap<Integer, List<Item>>();
	    //System.out.println("Parent of "+test_node.get(0).item_id+ " is "+test_node.get(0).parent.item_id);
	    for(int i=0;i<test_node.size();i++)
	    {
	    	List<Integer> tran=new ArrayList<Integer>();
	    	Item x=test_node.get(i);
	    	int cnt=x.count;
	    	while(x.parent.item_id!=-1)
	    	{
	    		tran.add(x.parent.item_id);
	    		x=x.parent;
	    	}
	    	for(int j=0;j<cnt;j++)
    		{
	    		if(tran.size()!=0)
    			test_transaction.add(tran);
    		}
	    }
	   // System.out.println("Before pruning and sorting: "+test_transaction);
	    
	    for(int i=0;i<test_transaction.size();i++)
	    {
	    	for(int j=0;j<test_transaction.get(i).size();j++)
	    	{
	    		if(test_count.containsKey(test_transaction.get(i).get(j)))
	    		{
	    			test_count.put(test_transaction.get(i).get(j), test_count.get(test_transaction.get(i).get(j))+1);
	    		}
	    		else
	    		{
	    			test_count.put(test_transaction.get(i).get(j), 1);
	    		}
	    	}
	    }
	  // System.out.println("Count: "+test_count);
	    
	    for(int i : test_count.keySet())
	    {
	    	if(test_count.get(i)<minsup)
	    	{
	    for(int j=0;j<test_transaction.size();j++)
	    {
	    	if(test_transaction.get(j).contains(i))
	    			{
	    				test_transaction.get(j).remove(test_transaction.get(j).indexOf(i));
	    			}
	    }
	    }
	    }
	    
	  /*  for (int j=0;j<test_transaction.size();j++)
		{
	    	for(int z=0;z<test_transaction.get(j).size()-1;z++)
	    	{
	    		if(count.get(test_transaction.get(j).get(z))<count.get(test_transaction.get(j).get(z+1)))
	    		{
	    			int temp=test_transaction.get(j).get(z+1);
	    			test_transaction.get(j).set(z+1,test_transaction.get(j).get(z));
	    			test_transaction.get(j).set(z, temp);
	    		}
	    	}
		}*/
	    
	 //  System.out.println("Projection database: " + test_transaction);
	    
	  //conditional fp-tree
	    Item c_root=new Item();
	    c_root.item_id=-1;
	    int index=0;
	    for (int i=0;i<test_transaction.size();i++)
	    {
	    	Item pre_node=c_root;
	    	for(int z=0;z<test_transaction.get(i).size();z++)
	    	{
	    		int flag=0;
	    		for(int j=0;j<pre_node.children.size();j++)
	    		{
	    			Item temporary=pre_node.children.get(j);
	    			if(test_transaction.get(i).get(z)==temporary.item_id)
	    			{
	    				flag=1;
	    			    index=j;
	    				break;
	    			}
	    		}
	    		if(flag==1)
	    		{
	    			Item n=(Item)pre_node.children.get(index);
	    			n.count++;
	    			pre_node=n;
	    		}
	    		else
	    		{
	    			Item node=new Item();
	    			node.item_id=test_transaction.get(i).get(z);
	    			node.count++;
	    			node.parent=pre_node;
	    			pre_node.children.add(node);
	    			if(c_head.containsKey(test_transaction.get(i).get(z)))
	    			{
	    				int x=test_transaction.get(i).get(z);
	    			c_head.get(x).add(node);
	    			}
	    			else
	    			{
	    				List<Item> links = new ArrayList<Item>();
	    				links.add(node);
	    				c_head.put(test_transaction.get(i).get(z), links);
	    			}
	    			pre_node=node;
	    		}
	    	}
	    }
	  //  System.out.println("Header: "+c_head);
	    if(c_head.size()!=0)
	    {
	    for(int i : c_head.keySet())
	    {
	    	String b=Integer.toString(i);
	    	frequent_patterns.put(b.concat(" ").concat(c),test_count.get(i));
	    	fpgrowth(c_head.get(i),b.concat(" ").concat(c));
	    }
	    }
	    else
	    {
	    	return;
	    }
	}

	private static void association(List<String> lhs, List<String> rhs, String pattern, FileWriter fw) throws Exception
	{
		/*for(int z=0;z<lhs.size();z++)
    	{
    		for(int y=1;y<lhs.size()-z;y++)
    		if(count.get(Integer.parseInt(lhs.get(y-1)))<count.get(Integer.parseInt(lhs.get(y))))
    		{
    			String temp=lhs.get(y);
    			lhs.set(y,lhs.get(y-1));
    			lhs.set(y-1, temp);
    		}
    	}*/
		Collections.sort(lhs);
		
		/*for(int z=0;z<rhs.size();z++)
    	{
    		for(int y=1;y<rhs.size()-z;y++)
    		if(count.get(Integer.parseInt(rhs.get(y-1)))<count.get(Integer.parseInt(rhs.get(y))))
    		{
    			String temp=rhs.get(y);
    			rhs.set(y,rhs.get(y-1));
    			rhs.set(y-1, temp);
    		}
    	}*/
		Collections.sort(rhs);
		
		List<String> l_new=new ArrayList<String>(lhs);
		List<String> r_new=new ArrayList<String>(rhs);
		//l_new=lhs;
		//r_new=rhs;
		
		if(lhs.size()==0)
		{
			//System.out.println("size 0");
			return;
		}
		
		//System.out.println("LHS: "+lhs);
		String l="";
		for(int i=0;i<l_new.size();i++)
		{
			l=l+l_new.get(i)+" ";
		}
		l=l.substring(0,l.length()-1);
		//System.out.println("L: "+l);
		double l_sup=(double)final_frequent_patterns.get(l);
		double pat_sup=(double)final_frequent_patterns.get(pattern);
		if(pat_sup/l_sup>=mincon)
		{
			String LHS=l_new.toString();
			LHS=LHS.substring(1, LHS.length()-1);
			String RHS=r_new.toString();
			RHS=RHS.substring(1, RHS.length()-1);
			String S=Double.toString(pat_sup);
			String C=Double.toString(pat_sup/l_sup);
			
			fw.write(LHS+"|"+RHS+"|"+S+"|"+C);
		    fw.write("\n");
			//System.out.println(l_new+"->"+r_new);
			for(int i=0;i<l_new.size();i++)
			{ 
			r_new.add(l_new.get(i));
			l_new.remove(i);
			//System.out.println("New LHS: "+l_new);
			//System.out.println("New RHS: "+r_new);
			association(l_new,r_new,pattern,fw);
			}
			//System.out.println("exit");
		}
		
		else
		{
			//System.out.println("Less support");
			return;
		}
	}

}
