import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class knn {
	
	public static float cosine(List<Float> a, List<Float> b)
	{
		float Sum = 0;
	    float s1 = 0;
	    float s2 = 0;
	    
		for (int i=0;i<a.size();i++)
	    {
			float A=a.get(i);
			float B=b.get(i);
			if(A!=0 && B!=0)
			{
				Sum=Sum + (A*B);
				s1=s1 + A*A;
				s2=s2 + B*B;
			}
			else if(A!=0 || B!=0)
			{
			s1=s1 + A*A;
			s2=s2 + B*B;
			}
	    }
	    
	    return (float)(Sum/(Math.sqrt(s1)*Math.sqrt(s2)));
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String line;
		String trainpath=args[0];
		String testpath=args[1];
		String finaltestpath=args[2];
		String outfile=args[3];
		//String testpath="mnist_validation.csv";
		//String trainpath="mnist_train.csv";
		//String finaltestpath="mnist_test.csv";
		//String outfile="abc.csv";
		HashMap<Integer, Integer> test_map = new HashMap<Integer, Integer>();
		HashMap<Integer, List<Float>> test_list = new HashMap<Integer, List<Float>>();
		HashMap<Integer, Integer> train_map = new HashMap<Integer, Integer>();
		HashMap<Integer, List<Float>> train_list = new HashMap<Integer, List<Float>>();
		HashMap<Integer, Integer> predicted_map = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> combined_train_map = new HashMap<Integer, Integer>();
		HashMap<Integer, List<Float>> combined_train_list = new HashMap<Integer, List<Float>>();
		HashMap<Integer, Integer> final_test_map = new HashMap<Integer, Integer>();
		HashMap<Integer, List<Float>> final_test_list = new HashMap<Integer, List<Float>>();
		//int k=2;
		FileWriter fw = new FileWriter(new File(outfile));
		BufferedReader reader = new BufferedReader(new FileReader(testpath));
	    int c=0;
		 while ((line = reader.readLine()) != null)
		    {
		        String[] parts = line.split(",");
		        String id=parts[0];
		        
		        test_map.put(c, Integer.parseInt(id));
		        
		        List<Float> a = new ArrayList<Float>();
		        
		        for(int i=1;i<parts.length;i++)
		        {
		        	a.add(Float.parseFloat(parts[i]));
		        }
		     test_list.put(c, a);
		     c++;
		    }
		 reader.close();
		 
			BufferedReader reader1 = new BufferedReader(new FileReader(trainpath));
		    int count=0;
			 while ((line = reader1.readLine()) != null)
			    {
			        String[] parts = line.split(",");
			        String id=parts[0];
			        
			        train_map.put(count, Integer.parseInt(id));
			        
			        List<Float> a = new ArrayList<Float>();
			        
			        for(int i=1;i<parts.length;i++)
			        {
			        	a.add(Float.parseFloat(parts[i]));
			        }
			     train_list.put(count, a);
			     count++;
			    }
			 reader1.close();
			 
			 BufferedReader reader2 = new BufferedReader(new FileReader(finaltestpath));
			    int cn=0;
				 while ((line = reader2.readLine()) != null)
				    {
				        String[] parts = line.split(",");
				        String id=parts[0];
				        
				        final_test_map.put(cn, Integer.parseInt(id));
				        
				        List<Float> a = new ArrayList<Float>();
				        
				        for(int i=1;i<parts.length;i++)
				        {
				        	a.add(Float.parseFloat(parts[i]));
				        }
				     final_test_list.put(cn, a);
				     cn++;
				    }
				 reader2.close();
		
		 combined_train_list.putAll(train_list);
		 combined_train_map.putAll(train_map);
		 
			BufferedReader reader3 = new BufferedReader(new FileReader(testpath));
		   
			 while ((line = reader3.readLine()) != null)
			    {
			        String[] parts = line.split(",");
			        String id=parts[0];
			        
			        combined_train_map.put(count, Integer.parseInt(id));
			        
			        List<Float> a = new ArrayList<Float>();
			        
			        for(int i=1;i<parts.length;i++)
			        {
			        	a.add(Float.parseFloat(parts[i]));
			        }
			     combined_train_list.put(count, a);
			     count++;
			    }
			 reader3.close();
	
		 
//		 System.out.println(test_list);
//		 System.out.println(test_map);
//		 
//		 System.out.println(train_list);
//		 System.out.println(train_map);
		 
		 float max_accuracy=0;
		 int max_k=0;
		 
		 for(int k=1;k<=20;k++)
		 {
		 for(int i : test_list.keySet())
		 {
			 List<Float> a = new ArrayList<Float>();
			 a=test_list.get(i);
			 HashMap<Integer, Float> knn = new HashMap<Integer, Float>();
			 for(int j=0;j<k;j++)
			 {
				 knn.put(j, cosine(train_list.get(j), a));
			 }
			 
			 for(int x=k;x<train_list.size();x++)
			 {
				 int min_idx=0;
				 float min_dist=999999999;
				 
				 for(int y : knn.keySet())
				 {
					 float d=knn.get(y);
					 if(d<min_dist)
					 {
						 min_dist=d;
						 min_idx=y;
					 }
				 }
				 float nd=cosine(train_list.get(x), a);
				 if(nd>min_dist)
				 {
					 knn.remove(min_idx);
					 knn.put(x, nd);
				 }
			 }
//			 System.out.println(i);
//			 System.out.println(knn);
			 float max=0;
			 int p_class=-1;
			 for(int j=0;j<10;j++)
			 {
				 float s=0;
				 for(int x : knn.keySet())
				 {
					 if(train_map.get(x)==j)
					 {
						 float z=1-knn.get(x);
						 s=s+1/(z*z);
					 }
				 }
				 
				 if(s>max)
				 {
					 max=s;
					 p_class=j;
				 }
			 }
			 
			 predicted_map.put(i, p_class);
		 }
		 
		 //System.out.println(predicted_map);
		 
		 int cnt=0;
		 
		 for(int i : test_list.keySet())
		 {
			 if(test_map.get(i)==predicted_map.get(i))
			 {
				 cnt++;
			 }
		 }
		 float accuracy=(float)cnt/(float)test_map.size();
		 //System.out.println(k);
		 //System.out.println("ACCURACY: "+accuracy);
		 if(accuracy>max_accuracy)
		 {
			 max_accuracy=accuracy;
			 max_k=k;
		 }
	}
		 //System.out.println("Best Accuracy: "+max_accuracy);
		 //System.out.println("Best K: "+max_k);
		 
HashMap<Integer, Integer> final_predicted_map = new HashMap<Integer, Integer>();
		 
		 for(int i : final_test_list.keySet())
		 {
			 List<Float> a = new ArrayList<Float>();
			 a=final_test_list.get(i);
			 HashMap<Integer, Float> knn = new HashMap<Integer, Float>();
			 for(int j=0;j<max_k;j++)
			 {
				 knn.put(j, cosine(combined_train_list.get(j), a));
			 }
			 
			 for(int x=max_k;x<combined_train_list.size();x++)
			 {
				 int min_idx=0;
				 float min_dist=999999999;
				 
				 for(int y : knn.keySet())
				 {
					 float d=knn.get(y);
					 if(d<min_dist)
					 {
						 min_dist=d;
						 min_idx=y;
					 }
				 }
				 float nd=cosine(combined_train_list.get(x), a);
				 if(nd>min_dist)
				 {
					 knn.remove(min_idx);
					 knn.put(x, nd);
				 }
			 }
//			 System.out.println(i);
//			 System.out.println(knn);
			 float max=0;
			 int p_class=-1;
			 for(int j=0;j<10;j++)
			 {
				 float s=0;
				 for(int x : knn.keySet())
				 {
					 if(combined_train_map.get(x)==j)
					 {
						 float z=1-knn.get(x);
						 s=s+1/(z*z);
					 }
				 }
				 
				 if(s>max)
				 {
					 max=s;
					 p_class=j;
				 }
			 }
			 
			 final_predicted_map.put(i, p_class);
			 fw.write(Integer.toString(p_class));
			 fw.write("\n");
		 }
		 fw.close();
		 int cnt=0;
		 
		 for(int i : final_test_list.keySet())
		 {
			 if(test_map.get(i)==predicted_map.get(i))
			 {
				 cnt++;
			 }
		 }
		 float accuracy=(float)cnt/(float)final_test_map.size();
		 System.out.println("ACCURACY: "+accuracy);
	}
	
}
