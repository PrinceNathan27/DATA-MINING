import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class kcluster {
	
	public static double distance(HashMap<Integer, Double> a, HashMap<Integer, Double> b)
    {
		List<Integer> keylist = new ArrayList<Integer>(a.keySet());
		
		//keylist.addAll(a.keySet());
		keylist.addAll(b.keySet());
		Set<Integer> hs = new HashSet<>();
		hs.addAll(keylist);
		keylist = new ArrayList<Integer>(hs);
		//keylist.addAll(hs);
		
        double Sum = 0.0;
        double val1=0.0;
        double val2=0.0;
        for(int i=0;i<keylist.size();i++) {
        	int idx=keylist.get(i);
        	if(a.containsKey(idx))
        		val1=a.get(idx);
        	else
        		val1=0.0;
        	if(b.containsKey(idx))
    			val2=b.get(idx);
        	else
        		val2=0.0;
        	
           Sum = Sum + (val1-val2)*(val1-val2);
        }
        return Math.sqrt(Sum);
    }
	
	public static double cosine(HashMap<Integer, Double> a, HashMap<Integer, Double> c)
    {
		double Sum = 0.0;
        double s = 0.0;
        
		for (int i : c.keySet())
	    {
			double cval=c.get(i);
			if(a.containsKey(i))   
				Sum=Sum + (a.get(i)*cval);
			s=s + (cval*cval);
	    }
        
        return Sum/Math.sqrt(s);
    }
	
	public static double cosine_centroids(HashMap<Integer, Double> a, HashMap<Integer, Double> b)
    {
		double Sum = 0.0;
        double s1 = 0.0;
        
		for (int i : b.keySet())
	    {
			double bval=b.get(i);
	
			if(a.containsKey(i))   
				Sum=Sum + (a.get(i)*bval);
			s1=s1 + (bval*bval);
	    }
		
        
        return Sum/Math.sqrt(s1);
    }
	
	public static double mod(HashMap<Integer, Double> a)
    {
		double Sum = 0.0;
        
		for (int i : a.keySet())
	    {
			double aval=a.get(i);
			Sum=Sum + aval*aval;
			
	    }
        
        return Sum/Math.sqrt(Sum);
    }

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String filepath=args[0];
		String criterion=args[1];
		String classfile=args[2];
		int k=Integer.parseInt(args[3]);
		int trials=Integer.parseInt(args[4]);
		String outfile=args[5];
		//int k=20;
		//String criterion="E1";
		String line;
		//String filepath="sqrtfreq.csv";
		HashMap<Integer, HashMap<Integer, Double>> list = new HashMap<Integer, HashMap<Integer, Double>>();
		//String outfile = "clusters";
		String matrix = "matrix";
		System.out.println(criterion);
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
	    
		String p_id="";
		
		long stime = System.currentTimeMillis();
		
		 while ((line = reader.readLine()) != null)
		    {
		        String[] parts = line.split(",", 3);
		        String id=parts[0];
		        
		        if(id.equals(p_id))
		        {
		        	list.get(Integer.parseInt(id)).put(Integer.parseInt(parts[1]), Double.parseDouble(parts[2]));
		        }
		        
		        else 
		        {
		        	HashMap<Integer, Double> t = new HashMap<Integer, Double>();
		        	t.put(Integer.parseInt(parts[1]), Double.parseDouble(parts[2]));
		        	list.put(Integer.parseInt(parts[0]), t);
		        }
		        
		        p_id=id;
		    }
		 reader.close();
		 
		 BufferedReader reader1 = new BufferedReader(new FileReader(classfile));
		 HashMap<Integer, String> classmap = new HashMap<Integer, String>();
		 HashMap<String, Integer> uniqueclass = new HashMap<String, Integer>();
		 
		 int index=0;
		 while ((line = reader1.readLine()) != null)
		    {
		        String[] parts = line.split(",", 2);
		        int id=Integer.parseInt(parts[0]);
		        String topic=parts[1];
		        classmap.put(id, topic);
		        if(!uniqueclass.containsKey(topic))
		        {
		        	uniqueclass.put(topic,index);
		        	index++;
		        }
		    }   
		 reader1.close();

		 
		List<Integer> seeds = Arrays.asList(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35, 37, 39);
		double SSE;
		double best_purity=0.0;
		double best_entropy=0.0;
		double min_SSE=999999999999.0;
		double max_SSE=0.0;
		int seed=0;
		
		FileWriter out = new FileWriter(new File(outfile));
		FileWriter mat = new FileWriter(new File(matrix));
		
		if(criterion.equals("I2"))
		{
		for(int s=0;s<trials;s++)
		{	
			double p_SSE=0.0;
			int trial=s+1;
			System.out.println("Trial "+trial);
			SSE=0.0;
			HashMap<Integer, List<Integer>> cluster1 = new HashMap<Integer, List<Integer>>();
			HashMap<Integer, Integer> map1 = new HashMap<Integer, Integer>();
			HashMap<Integer, HashMap<Integer, Double>> centroid1 = new HashMap<Integer, HashMap<Integer, Double>>();
			
			Random generator = new Random(seeds.get(s));
	
			for (int i : list.keySet())
		    {
				int num = generator.nextInt(k);
			    map1.put(i, num);
			    if(cluster1.containsKey(num))
			    {
			    	cluster1.get(num).add(i);
			    }
			    else
			    {
			    	List<Integer> a = new ArrayList<Integer>();
			    	a.add(i);
			    	cluster1.put(num, a);
			    }
		    }
			
			
			HashMap<Integer, Double> ctd = new HashMap<Integer, Double>();
			
			
			for (int i : cluster1.keySet())
		    {
				List<Integer> keylist = new ArrayList<Integer>();
				
					for(int x=0;x<cluster1.get(i).size();x++)
					{
						keylist.addAll(list.get(cluster1.get(i).get(x)).keySet());
					}
					Set<Integer> hs = new HashSet<>();
					hs.addAll(keylist);
					keylist.clear();
					keylist.addAll(hs);
					ctd = new HashMap<Integer, Double>();
					for(int j=0;j<keylist.size();j++)
					{
						int kval=keylist.get(j);
						double sum=0.0;
						
						for(int y=0;y<cluster1.get(i).size();y++)
						{
							int lval=cluster1.get(i).get(y);
							if(list.get(lval).containsKey(kval))
							{
								sum=sum+list.get(lval).get(kval);
							}

						}
						ctd.put(kval, sum);
						//ctd.put(keylist.get(j), sum/cluster1.get(i).size());
					}
					centroid1.put(i, ctd);
		    }
			
			
			
			int c=0;
			while(true)
			{
				SSE=0.0;
				c++;
				System.out.println("Iteration "+c);
				
				int centre1=0;
				int flag1=0;
				for(int i : list.keySet())
				{
					double mindist=0.0;
					for (int x : centroid1.keySet())
				    {
						double dist=cosine(list.get(i),centroid1.get(x));
						if(dist>mindist)
								{
									mindist=dist;
									centre1=x;
								}
				    }
					if(centre1!=map1.get(i))
					{
					cluster1.get(map1.get(i)).remove((Integer)i);
					map1.put(i,centre1);
					cluster1.get(centre1).add(i);
					flag1=1;
					}
				}
				if(flag1==0)
				{
					System.out.println("Converged");
					break;
				}
				
				HashMap<Integer, Double> temp_ctd = new HashMap<Integer, Double>();
				
				
				for (int i : cluster1.keySet())
			    {
					List<Integer> keylist = new ArrayList<Integer>();
					
						for(int x=0;x<cluster1.get(i).size();x++)
						{
							keylist.addAll(list.get(cluster1.get(i).get(x)).keySet());
						}
						Set<Integer> hs = new HashSet<>();
						hs.addAll(keylist);
						keylist.clear();
						keylist.addAll(hs);
						
						temp_ctd = new HashMap<Integer, Double>();
						for(int j=0;j<keylist.size();j++)
						{
							int kval=keylist.get(j);
							double sum=0;
							
							for(int y=0;y<cluster1.get(i).size();y++)
							{
								int lval=cluster1.get(i).get(y);
								if(list.get(lval).containsKey(kval))
								{
									sum=sum+list.get(lval).get(kval);
								}					
							}
							temp_ctd.put(kval, sum);
							//temp_ctd.put(keylist.get(j), sum/cluster1.get(i).size());
						}
						
					centroid1.put(i, temp_ctd);
			    }
				
//				double cluster_SSE;
//				for(int i : cluster1.keySet())
//				{
//					cluster_SSE=0.0;
//					for(int j=0;j<cluster1.get(i).size();j++)
//					{
//						cluster_SSE=cluster_SSE + Math.pow(distance(centroid1.get(i), list.get(cluster1.get(i).get(j))),2);
//					}
//					SSE=SSE + cluster_SSE;
//				}
//				if(SSE<p_SSE && p_SSE-SSE<1)
//				{
//					System.out.println("SSE "+SSE);
//					System.out.println("p_SSE "+p_SSE);
//					System.out.println("SSE Converged");
//					break;
//				}
				for(int i : centroid1.keySet())
				{
					SSE=SSE+mod(centroid1.get(i));
				}
				
//				SSE=SSE*100;
//				SSE=Math.floor(SSE);
//				SSE=SSE/100;
//				p_SSE=p_SSE*100;
//				p_SSE=Math.floor(p_SSE);
//				p_SSE=p_SSE/100;
				if(SSE-p_SSE<1)
				{
//					System.out.println(SSE);
//					System.out.println(p_SSE);
//					System.out.println("I2 Converged");
					break;
				}
				p_SSE=SSE;
		}
			String[] topicorder= new String[uniqueclass.size()];
			
			if(SSE>max_SSE)
			{
				for(int i : map1.keySet())
				{
					out.write(Integer.toString(i));
					out.write(",");
					out.write(Integer.toString(map1.get(i)));
					out.write("\n");
				}
				
				
				int[][] confusion_matrix = new int[k][uniqueclass.size()];
				int z=-1;
				for (int i : cluster1.keySet())
			    {
					z++;
					for(int j=0;j<cluster1.get(i).size();j++)
					{
						String topic=classmap.get(cluster1.get(i).get(j));
						int idx=uniqueclass.get(topic);
						confusion_matrix[z][idx]++;
						topicorder[idx]=topic;
					}
			    }
				
				double sum=0.0;
				double entropy=0.0;
				double row_entropy=0.0;
				
				z=-1;
				for (int i : cluster1.keySet())
			    {
					z++;
					int max=0;
					row_entropy=0.0;
					for(int j=0;j<uniqueclass.size();j++)
					{
						if(confusion_matrix[z][j]>max)
							max=confusion_matrix[z][j];
						if(confusion_matrix[z][j]!=0)
						{
							row_entropy=row_entropy - ((double)confusion_matrix[z][j]/cluster1.get(i).size())*(Math.log((double)confusion_matrix[z][j]/cluster1.get(i).size())/Math.log(2));
						}
					}
					sum=sum+max;
					entropy=entropy+(double)cluster1.get(i).size()/(double)classmap.size()*row_entropy;
			    }
				
				double purity=sum/classmap.size();
				
				best_purity=purity;
				best_entropy=entropy;
				max_SSE=SSE;
				seed=seeds.get(s);
				
				for(int y=0;y<topicorder.length;y++)
				{
					mat.write(topicorder[y]);
					mat.write(",");
				}
				
				mat.write("\n");
				
				for(int i=0;i<k;i++)
				{
					mat.write(Integer.toString(i));
					mat.write(",");
					for(int j=0;j<uniqueclass.size();j++)
					{
						String cell=Integer.toString(confusion_matrix[i][j]);
						mat.write(cell);
						if(j!=uniqueclass.size()-1)
							mat.write(",");
					}
					mat.write("\n");
				}
				
			}
	}
		}
		
		else if(criterion.equals("SSE"))
		{
		for(int s=0;s<trials;s++)
		{	
			double p_SSE=0.0;
			int trial=s+1;
			System.out.println("Trial "+trial);
			SSE=0.0;
			HashMap<Integer, List<Integer>> cluster1 = new HashMap<Integer, List<Integer>>();
			HashMap<Integer, Integer> map1 = new HashMap<Integer, Integer>();
			HashMap<Integer, HashMap<Integer, Double>> centroid1 = new HashMap<Integer, HashMap<Integer, Double>>();
			
			Random generator = new Random(seeds.get(s));
	
			for (int i : list.keySet())
		    {
				int num = generator.nextInt(k);
			    map1.put(i, num);
			    if(cluster1.containsKey(num))
			    {
			    	cluster1.get(num).add(i);
			    }
			    else
			    {
			    	List<Integer> a = new ArrayList<Integer>();
			    	a.add(i);
			    	cluster1.put(num, a);
			    }
		    }
			
			
			HashMap<Integer, Double> ctd = new HashMap<Integer, Double>();
			
			
			for (int i : cluster1.keySet())
		    {
				List<Integer> keylist = new ArrayList<Integer>();
				
					for(int x=0;x<cluster1.get(i).size();x++)
					{
						keylist.addAll(list.get(cluster1.get(i).get(x)).keySet());
					}
					Set<Integer> hs = new HashSet<>();
					hs.addAll(keylist);
					keylist.clear();
					keylist.addAll(hs);
					ctd = new HashMap<Integer, Double>();
					for(int j=0;j<keylist.size();j++)
					{
						int kval=keylist.get(j);
						double sum=0.0;
						
						for(int y=0;y<cluster1.get(i).size();y++)
						{
							int lval=cluster1.get(i).get(y);
							if(list.get(lval).containsKey(kval))
							{
								sum=sum+list.get(lval).get(kval);
							}

						}
						
						ctd.put(kval, sum/cluster1.get(i).size());
					}
					centroid1.put(i, ctd);
		    }
			
			
			
			int c=0;
			while(true)
			{
				SSE=0.0;
				c++;
				System.out.println("Iteration "+c);
				
				int centre1=0;
				int flag1=0;
				for(int i : list.keySet())
				{
					double mindist=99999999999.0;
					for (int x : centroid1.keySet())
				    {
						double dist=distance(list.get(i),centroid1.get(x));
						if(dist<mindist)
								{
									mindist=dist;
									centre1=x;
								}
				    }
					if(centre1!=map1.get(i))
					{
					cluster1.get(map1.get(i)).remove((Integer)i);
					map1.put(i,centre1);
					cluster1.get(centre1).add(i);
					flag1=1;
					}
				}
				if(flag1==0)
				{
					System.out.println("Converged");
					break;
				}
				
				HashMap<Integer, Double> temp_ctd = new HashMap<Integer, Double>();
				
				
				for (int i : cluster1.keySet())
			    {
					List<Integer> keylist = new ArrayList<Integer>();
					
						for(int x=0;x<cluster1.get(i).size();x++)
						{
							keylist.addAll(list.get(cluster1.get(i).get(x)).keySet());
						}
						Set<Integer> hs = new HashSet<>();
						hs.addAll(keylist);
						keylist.clear();
						keylist.addAll(hs);
						
						temp_ctd = new HashMap<Integer, Double>();
						for(int j=0;j<keylist.size();j++)
						{
							int kval=keylist.get(j);
							double sum=0;
							
							for(int y=0;y<cluster1.get(i).size();y++)
							{
								int lval=cluster1.get(i).get(y);
								if(list.get(lval).containsKey(kval))
								{
									sum=sum+list.get(lval).get(kval);
								}					
							}
							
							temp_ctd.put(kval, sum/cluster1.get(i).size());
						}
						
					centroid1.put(i, temp_ctd);
			    }
				
				double cluster_SSE;
				for(int i : cluster1.keySet())
				{
					cluster_SSE=0.0;
					for(int j=0;j<cluster1.get(i).size();j++)
					{
						cluster_SSE=cluster_SSE + (distance(centroid1.get(i), list.get(cluster1.get(i).get(j))))*(distance(centroid1.get(i), list.get(cluster1.get(i).get(j))));
					}
					SSE=SSE + cluster_SSE;
				}
				if(SSE<p_SSE && p_SSE-SSE<1)
				{
//					System.out.println("SSE "+SSE);
//					System.out.println("p_SSE "+p_SSE);
//					System.out.println("SSE Converged");
					break;
				}

				p_SSE=SSE;
		}
			String[] topicorder= new String[uniqueclass.size()];
			if(SSE<min_SSE)
			{
				for(int i : map1.keySet())
				{
					out.write(Integer.toString(i));
					out.write(",");
					out.write(Integer.toString(map1.get(i)));
					out.write("\n");
				}
				
				int[][] confusion_matrix = new int[k][uniqueclass.size()];
				int z=-1;
				for (int i : cluster1.keySet())
			    {
					z++;
					for(int j=0;j<cluster1.get(i).size();j++)
					{
						String topic=classmap.get(cluster1.get(i).get(j));
						int idx=uniqueclass.get(topic);
						confusion_matrix[z][idx]++;
						topicorder[idx]=topic;
					}
			    }
				
				double sum=0.0;
				double entropy=0.0;
				double row_entropy=0.0;
				
				z=-1;
				for (int i : cluster1.keySet())
			    {
					z++;
					int max=0;
					row_entropy=0.0;
					for(int j=0;j<uniqueclass.size();j++)
					{
						if(confusion_matrix[z][j]>max)
							max=confusion_matrix[z][j];
						if(confusion_matrix[z][j]!=0)
						{
							row_entropy=row_entropy - ((double)confusion_matrix[z][j]/cluster1.get(i).size())*(Math.log((double)confusion_matrix[z][j]/cluster1.get(i).size())/Math.log(2));
						}
					}
					sum=sum+max;
					entropy=entropy+(double)cluster1.get(i).size()/(double)classmap.size()*row_entropy;
			    }
				
				double purity=sum/classmap.size();
				
				best_purity=purity;
				best_entropy=entropy;
				min_SSE=SSE;
				seed=seeds.get(s);
				
				for(int y=0;y<topicorder.length;y++)
				{
					mat.write(topicorder[y]);
					mat.write(",");
				}
				
				mat.write("\n");
				
				for(int i=0;i<k;i++)
				{
					mat.write(Integer.toString(i));
					mat.write(",");
					for(int j=0;j<uniqueclass.size();j++)
					{
						String cell=Integer.toString(confusion_matrix[i][j]);
						mat.write(cell);
						if(j!=uniqueclass.size()-1)
							mat.write(",");
					}
					mat.write("\n");
				}
			}
	}
		}
		
		else if(criterion.equals("E1"))
		{
		for(int s=0;s<trials;s++)
		{	
			double p_SSE=0.0;
			int trial=s+1;
			System.out.println("Trial "+trial);
			SSE=0.0;
			HashMap<Integer, List<Integer>> cluster1 = new HashMap<Integer, List<Integer>>();
			HashMap<Integer, Integer> map1 = new HashMap<Integer, Integer>();
			HashMap<Integer, HashMap<Integer, Double>> centroid1 = new HashMap<Integer, HashMap<Integer, Double>>();
			
			Random generator = new Random(seeds.get(s));
			
			List<Integer> klist = new ArrayList<Integer>();
			HashMap<Integer, Double> octd = new HashMap<Integer, Double>();
			
			for (int i : list.keySet())
		    {
				int num = generator.nextInt(k);
			    map1.put(i, num);
			    if(cluster1.containsKey(num))
			    {
			    	cluster1.get(num).add(i);
			    }
			    else
			    {
			    	List<Integer> a = new ArrayList<Integer>();
			    	a.add(i);
			    	cluster1.put(num, a);
			    }
			    
			    klist.addAll(list.get(i).keySet());
		    }
			
			Set<Integer> h = new HashSet<>();
			h.addAll(klist);
			klist.clear();
			klist.addAll(h);
			
			for(int i=0;i<klist.size();i++)
			{
				int kval=klist.get(i);
				double sum=0.0;
				for(int j : list.keySet())
				{
					if(list.get(j).containsKey(kval))
					{
						sum=sum+list.get(j).get(kval);
					}
				}
				octd.put(i,sum);
			}
		
			HashMap<Integer, Double> ctd = new HashMap<Integer, Double>();
			
			
			for (int i : cluster1.keySet())
		    {
				List<Integer> keylist = new ArrayList<Integer>();
				
					for(int x=0;x<cluster1.get(i).size();x++)
					{
						keylist.addAll(list.get(cluster1.get(i).get(x)).keySet());
					}
					Set<Integer> hs = new HashSet<>();
					hs.addAll(keylist);
					keylist.clear();
					keylist.addAll(hs);
					ctd = new HashMap<Integer, Double>();
					for(int j=0;j<keylist.size();j++)
					{
						int kval=keylist.get(j);
						double sum=0.0;
						
						for(int y=0;y<cluster1.get(i).size();y++)
						{
							int lval=cluster1.get(i).get(y);
							if(list.get(lval).containsKey(kval))
							{
								sum=sum+list.get(lval).get(kval);
							}

						}
						ctd.put(kval, sum);
				
					}
					centroid1.put(i, ctd);
		    }
			
			
			
			int c=0;
			while(true)
			{
				SSE=0.0;
				c++;
				System.out.println("Iteration "+c);
				
				int centre1=0;
				int flag1=0;
				for(int i : list.keySet())
				{
					double mindist=999999999999999999.0;
					for (int x : centroid1.keySet())
				    {
						HashMap<Integer, Double> t_ctd = new HashMap<Integer, Double>();
						if(map1.get(i)!=x)
						{
						List<Integer> clist = new ArrayList<Integer>();
						
						clist.addAll(list.get(i).keySet());
						clist.addAll(centroid1.get(x).keySet());
						Set<Integer> hs = new HashSet<>();
						hs.addAll(clist);
						clist.clear();
						clist.addAll(hs);
						for(int j=0;j<clist.size();j++)
						{
							int cval=clist.get(j);
							double v1=0.0;
							double v2=0.0;
							if(centroid1.get(x).containsKey(cval))
								v1=centroid1.get(x).get(cval);
							
							if(list.get(i).containsKey(cval))
								v2=list.get(i).get(cval);
							
							t_ctd.put(cval,v1+v2);
						}
						}
						else
						{
							t_ctd=centroid1.get(x);
						}
						double dist=cosine_centroids(octd,t_ctd);
						
						if(dist<mindist)
								{
									mindist=dist;
									centre1=x;
								}
				    }
					if(centre1!=map1.get(i))
					{
					cluster1.get(map1.get(i)).remove((Integer)i);
					map1.put(i,centre1);
					cluster1.get(centre1).add(i);
					flag1=1;
					}
				}
				if(flag1==0)
				{
					System.out.println("Converged");
					break;
				}
				
				HashMap<Integer, Double> temp_ctd = new HashMap<Integer, Double>();
				
				
				for (int i : cluster1.keySet())
			    {
					if(cluster1.get(i).size()!=0)
					{
					List<Integer> keylist = new ArrayList<Integer>();
					
						for(int x=0;x<cluster1.get(i).size();x++)
						{
							keylist.addAll(list.get(cluster1.get(i).get(x)).keySet());
						}
						Set<Integer> hs = new HashSet<>();
						hs.addAll(keylist);
						keylist.clear();
						keylist.addAll(hs);
						
						temp_ctd = new HashMap<Integer, Double>();
						for(int j=0;j<keylist.size();j++)
						{
							int kval=keylist.get(j);
							double sum=0;
							
							for(int y=0;y<cluster1.get(i).size();y++)
							{
								int lval=cluster1.get(i).get(y);
								if(list.get(lval).containsKey(kval))
								{
									sum=sum+list.get(lval).get(kval);
								}					
							}
							temp_ctd.put(kval, sum);
							
						}
						
					centroid1.put(i, temp_ctd);
					}
			    }
				
				for(int i : centroid1.keySet())
				{
					System.out.println(centroid1.get(i).size());
					System.out.println(cluster1.get(i).size());
					SSE=SSE+cluster1.get(i).size()*cosine_centroids(octd,centroid1.get(i));
				}
				System.out.println(SSE);
				if(p_SSE-SSE<1)
				{
//					System.out.println(SSE);
//					System.out.println(p_SSE);
//					System.out.println("E1 Converged");
					break;
				}
				p_SSE=SSE;
		}
			
			String[] topicorder= new String[uniqueclass.size()];
			
			if(SSE<min_SSE)
			{
				for(int i : map1.keySet())
				{
					out.write(Integer.toString(i));
					out.write(",");
					out.write(Integer.toString(map1.get(i)));
					out.write("\n");
				}
				
				int[][] confusion_matrix = new int[k][uniqueclass.size()];
				int z=-1;
				for (int i : cluster1.keySet())
			    {
					z++;
					for(int j=0;j<cluster1.get(i).size();j++)
					{
						String topic=classmap.get(cluster1.get(i).get(j));
						int idx=uniqueclass.get(topic);
						confusion_matrix[z][idx]++;
						topicorder[idx]=topic;
					}
			    }
				
				double sum=0.0;
				double entropy=0.0;
				double row_entropy=0.0;
				
				z=-1;
				for (int i : cluster1.keySet())
			    {
					z++;
					int max=0;
					row_entropy=0.0;
					for(int j=0;j<uniqueclass.size();j++)
					{
						if(confusion_matrix[z][j]>max)
							max=confusion_matrix[z][j];
						if(confusion_matrix[z][j]!=0)
						{
							row_entropy=row_entropy - ((double)confusion_matrix[z][j]/cluster1.get(i).size())*(Math.log((double)confusion_matrix[z][j]/cluster1.get(i).size())/Math.log(2));
						}
					}
					sum=sum+max;
					entropy=entropy+(double)cluster1.get(i).size()/(double)classmap.size()*row_entropy;
			    }
				
				double purity=sum/classmap.size();
				
				best_purity=purity;
				best_entropy=entropy;
				min_SSE=SSE;
				seed=seeds.get(s);
				
				for(int y=0;y<topicorder.length;y++)
				{
					mat.write(topicorder[y]);
					mat.write(",");
				}
				mat.write("\n");
				
				for(int i=0;i<k;i++)
				{
					mat.write(Integer.toString(i));
					mat.write(",");
					for(int j=0;j<uniqueclass.size();j++)
					{
						String cell=Integer.toString(confusion_matrix[i][j]);
						mat.write(cell);
						if(j!=uniqueclass.size()-1)
							mat.write(",");
					}
					mat.write("\n");
				}
				
			}
	}
		}
		
		out.close();
		mat.close();
		
		System.out.println("Best Purity = "+best_purity);
		System.out.println("Best Entropy = "+best_entropy);
		//System.out.println("Seed = "+seed);
		if(criterion=="I1")
			System.out.println("Criterion function SSE = "+min_SSE);
		else if(criterion=="I2")
			System.out.println("Criterion function I2 = "+max_SSE);
		else if(criterion=="E1")
			System.out.println("Criterion function E1 = "+min_SSE);
		long etime = System.currentTimeMillis();
		long t=etime-stime;
		System.out.println("Total time: "+(double)t/1000);
	}
}
