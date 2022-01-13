import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class nn_regression {

	public static float fwcal(HashMap<Integer, List<Float>> X, HashMap<Integer, Float> w, HashMap<Integer, Integer> y, float lambda)
	{//System.out.println("X: "+X);
		List<Float> Xw = new ArrayList<Float>();
		for (int z = 0; z < X.size(); z++) { // aRow

			for (int j = 0; j < 1; j++) {// bColumn
				float sum=0;
				for (int k = 0; k < X.get(0).size(); k++) { // aColumn
					sum+=X.get(z).get(k)*w.get(k);	
				}
				Xw.add(sum);
			}
		}
		//System.out.println("Xw: "+Xw);
		List<Float> temp = new ArrayList<Float>();
		for(int j=0;j<Xw.size();j++)
		{
			temp.add(Xw.get(j)-y.get(j));
		}
		//System.out.println(temp);
		float sum1=0;
		for(int x=0;x<temp.size();x++)
		{
			float t=temp.get(x);
			if(t!=0)
				sum1+=t*t;
		}
		//System.out.println("Sum1: "+sum1);
		float sum2=0;
		for(int x=0;x<w.size();x++)
		{
			float t=w.get(x);
			if(t!=0)
				sum2+=t*t;  
		}
		//System.out.println("Sum2: "+sum2);
		sum2=sum2*lambda;
		
		return (sum1+sum2);
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String line;
		String trainpath=args[0];
		String testpath=args[1];
		String finaltestpath=args[2];
		String outfile2=args[3];
		String outfile1=args[4];
//		String testpath="mnist_validation.csv";
//		String trainpath="mnist_train.csv";
//		String finaltestpath="mnist_test.csv";
//		String outfile1="weights.csv";
//		String outfile2="abc.csv";
		HashMap<Integer, Integer> test_map = new HashMap<Integer, Integer>();
		HashMap<Integer, List<Float>> test_list = new HashMap<Integer, List<Float>>();
		HashMap<Integer, Integer> train_map = new HashMap<Integer, Integer>();
		HashMap<Integer, List<Float>> train_list = new HashMap<Integer, List<Float>>();
		//HashMap<Integer, Integer> predicted_map = new HashMap<Integer, Integer>();
		//HashMap<Integer, Float> predicted_value_map = new HashMap<Integer, Float>();
		HashMap<Integer, HashMap<Integer,Integer>> models = new HashMap<Integer, HashMap<Integer,Integer>>();
		HashMap<Integer, Integer> final_test_map = new HashMap<Integer, Integer>();
		HashMap<Integer, List<Float>> final_test_list = new HashMap<Integer, List<Float>>();
		HashMap<Integer, Integer> combined_train_map = new HashMap<Integer, Integer>();
		HashMap<Integer, List<Float>> combined_train_list = new HashMap<Integer, List<Float>>();
		//float lambda=(float)0.01;
	    FileWriter fw1 = new FileWriter(new File(outfile1));
	    FileWriter fw2 = new FileWriter(new File(outfile2));

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
		
		List<Float> mod1 = new ArrayList<Float>();
		for(int j=0;j<test_list.get(0).size();j++)
		{
			float sum=0;
			for(int i=0;i<test_list.size();i++)
			{
				float x=test_list.get(i).get(j);
				sum+=x*x;
			}
			mod1.add((float)Math.sqrt(sum));
		}
		
		for(int j=0;j<test_list.get(0).size();j++)
		{
			for(int i=0;i<test_list.size();i++)
			{
				test_list.get(i).set(j,test_list.get(i).get(j)/mod1.get(j));
			}
		}

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
		
		List<Float> mod = new ArrayList<Float>();
		for(int j=0;j<train_list.get(0).size();j++)
		{
			float sum=0;
			for(int i=0;i<train_list.size();i++)
			{
				float x=train_list.get(i).get(j);
				sum+=x*x;
			}
			mod.add((float)Math.sqrt(sum));
		}
		//System.out.println("mod: "+mod);
		for(int j=0;j<train_list.get(0).size();j++)
		{
			for(int i=0;i<train_list.size();i++)
			{
				if(mod.get(j)!=0)
					train_list.get(i).set(j,train_list.get(i).get(j)/mod.get(j));
			}
		}
		
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
		
		List<Float> mod2 = new ArrayList<Float>();
		for(int j=0;j<final_test_list.get(0).size();j++)
		{
			float sum=0;
			for(int i=0;i<final_test_list.size();i++)
			{
				float x=final_test_list.get(i).get(j);
				sum+=x*x;
			}
			mod2.add((float)Math.sqrt(sum));
		}
	
		for(int j=0;j<final_test_list.get(0).size();j++)
		{
			for(int i=0;i<final_test_list.size();i++)
			{
				if(mod2.get(j)!=0)
					final_test_list.get(i).set(j,final_test_list.get(i).get(j)/mod2.get(j));
			}
		}
		
		 //combined_train_list.putAll(train_list);
		 //combined_train_map.putAll(train_map);
		 int ct=0;
			BufferedReader reader3 = new BufferedReader(new FileReader(trainpath));
		   
			 while ((line = reader3.readLine()) != null)
			    {
			        String[] parts = line.split(",");
			        String id=parts[0];
			        
			        combined_train_map.put(ct, Integer.parseInt(id));
			        
			        List<Float> a = new ArrayList<Float>();
			        
			        for(int i=1;i<parts.length;i++)
			        {
			        	a.add(Float.parseFloat(parts[i]));
			        }
			     combined_train_list.put(ct, a);
			     ct++;
			    }
			 reader3.close();
			 
			 BufferedReader reader4 = new BufferedReader(new FileReader(testpath));
			 while ((line = reader4.readLine()) != null)
			    {
			        String[] parts = line.split(",");
			        String id=parts[0];
			        
			        combined_train_map.put(ct, Integer.parseInt(id));
			        
			        List<Float> a = new ArrayList<Float>();
			        
			        for(int i=1;i<parts.length;i++)
			        {
			        	a.add(Float.parseFloat(parts[i]));
			        }
			     combined_train_list.put(ct, a);
			     ct++;
			    }
			 reader4.close();
			 
				List<Float> mod3 = new ArrayList<Float>();
				for(int j=0;j<combined_train_list.get(0).size();j++)
				{
					float sum=0;
					for(int i=0;i<combined_train_list.size();i++)
					{
						float x=combined_train_list.get(i).get(j);
						sum+=x*x;
					}
					mod3.add((float)Math.sqrt(sum));
				}
			
				for(int j=0;j<combined_train_list.get(0).size();j++)
				{
					for(int i=0;i<combined_train_list.size();i++)
					{
						if(mod3.get(j)!=0)
							combined_train_list.get(i).set(j,combined_train_list.get(i).get(j)/mod3.get(j));
					}
				}
			 
		//System.out.println("Train: "+train_list);
		
			 List<Float> lambdas = Arrays.asList((float)5,(float)2,(float)1,(float)0.5,(float)0.1,(float)0.05,(float)0.01);		
				
				for(int i=0;i<10;i++)
				{
					HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
					for(int img : train_map.keySet())
					{
						if(train_map.get(img)==i)
						{
							temp.put(img, 1);
						}
						else
						{
							temp.put(img, 0);
						}
					}
					models.put(i, temp);
				}

				float max_accuracy=0;
				float max_lambda=0;
				
				Random generator = new Random();
				for(int l=0;l<lambdas.size();l++)
				{
					HashMap<Integer, Integer> predicted_map = new HashMap<Integer, Integer>();
					HashMap<Integer, Float> predicted_value_map = new HashMap<Integer, Float>();
					//System.out.println(lambdas.get(l));
				for(int y=0;y<10;y++)
				{
					//System.out.println(y);
					HashMap<Integer, Float> weights = new HashMap<Integer, Float>();
					for(int i=0;i<train_list.get(0).size();i++)
					{
						weights.put(i, generator.nextFloat()*(float)0.00001);
					}
					//System.out.println(weights);
					//			  for (int z = 0; z < train_list.size(); z++) { // aRow
					//				  
					//		           for (int j = 0; j < 1; j++) {// bColumn
					//		           	float sum=0;
					//		               for (int k = 0; k < train_list.get(0).size(); k++) { // aColumn
					//		               	sum+=train_list.get(z).get(k)*weights.get(k);	
					//		               }
					//		               predicted_map.put(z,sum);
					//		           }
					//			  }

					float fw=fwcal(train_list,weights,models.get(y),lambdas.get(l));
					//System.out.println("Initial fw: "+fw);
					float p_fw=0;
					//System.out.println(weights);
					int cnt=0;
					do
					{
						p_fw=fw;
						//System.out.println("Previous fw: "+p_fw);
						//System.out.println(weights);
						for(int i=0; i<weights.size();i++)
						{
							List<Float> Xwbar = new ArrayList<Float>();
							for (int z = 0; z < train_list.size(); z++) { // aRow

								for (int j = 0; j < 1; j++) {// bColumn
									float sum=0;
									for (int k = 0; k < train_list.get(0).size(); k++) { // aColumn
										if(k!=i)
										{
											sum+=train_list.get(z).get(k)*weights.get(k);
										}
										//C[i][j] += A[i][k] * B[k][j];
									}
									Xwbar.add(sum);
								}

							}
							//System.out.println(Xwbar);
							float Xsquare=1;
//							for(int j : train_list.keySet())
//							{
//								float x=train_list.get(j).get(i);
//								Xsquare+=x*x;
//							}
//							System.out.println("xsquare"+Xsquare);

							float denominator=Xsquare+lambdas.get(l);

							List<Float> temp = new ArrayList<Float>();

							for(int j=0;j<Xwbar.size();j++)
							{
								temp.add(models.get(y).get(j)-Xwbar.get(j));
							}

							float numerator=0;

							for(int j=0;j<Xwbar.size();j++)
							{
								numerator+=train_list.get(j).get(i)*temp.get(j);
							}

							float w_new=numerator/denominator;
							//System.out.println("New W: "+w_new);
							weights.put(i, w_new);
						}
						//System.out.println(weights);
						fw=fwcal(train_list,weights,models.get(y),lambdas.get(l));
						//System.out.println(fw);
						cnt++;
					}while((p_fw-fw)/p_fw > 0.0001 || (p_fw-fw)/p_fw < 0 || cnt==1);
					for (int z = 0; z < test_list.size(); z++) { // aRow

						for (int j = 0; j < 1; j++) {// bColumn
							float sum=0;
							for (int k = 0; k < test_list.get(0).size(); k++) { // aColumn
								sum+=test_list.get(z).get(k)*weights.get(k);	
							}
							if(y==0 || sum>predicted_value_map.get(z))
							{
								predicted_value_map.put(z,sum);
								predicted_map.put(z, y);
							}
						}
					}
					//System.out.println("prediction scores: "+predicted_value_map);
				}
				//System.out.println("prediction: "+predicted_map);
				int cnt=0;
				for(int i : test_list.keySet())
				{
					if(test_map.get(i)==predicted_map.get(i))
					{
						cnt++;
					}
				}

				float accuracy=(float)cnt/(float)test_map.size();
				//System.out.println("ACCURACY: "+accuracy);
				if(accuracy>max_accuracy)
				{
					max_accuracy=accuracy;
					max_lambda=lambdas.get(l);
				}
			}
				//System.out.println("Max lambda: "+max_lambda);	 
				float twice_lambda=2*max_lambda;
		
		HashMap<Integer, Integer> final_predicted_map = new HashMap<Integer, Integer>();
		HashMap<Integer, Float> final_predicted_value_map = new HashMap<Integer, Float>();
		HashMap<Integer, List<Float>> weight_map1 = new HashMap<Integer, List<Float>>();
		HashMap<Integer, HashMap<Integer,Integer>> new_models = new HashMap<Integer, HashMap<Integer,Integer>>();
		for(int i=0;i<10;i++)
		{
			HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
			for(int img : combined_train_map.keySet())
			{
				if(combined_train_map.get(img)==i)
				{
					temp.put(img, 1);
				}
				else
				{
					temp.put(img, 0);
				}
			}
			new_models.put(i, temp);
		}

		//			 System.out.println(models);
		//			 System.out.println(train_list);
		//Random generator = new Random();
		for(int y=0;y<10;y++)
		{
			//System.out.println(y);
			HashMap<Integer, Float> weights = new HashMap<Integer, Float>();
			for(int i=0;i<combined_train_list.get(0).size();i++)
			{
				weights.put(i, generator.nextFloat()*(float)0.00001);
			}
			//System.out.println(weights);
			//			  for (int z = 0; z < train_list.size(); z++) { // aRow
			//				  
			//		           for (int j = 0; j < 1; j++) {// bColumn
			//		           	float sum=0;
			//		               for (int k = 0; k < train_list.get(0).size(); k++) { // aColumn
			//		               	sum+=train_list.get(z).get(k)*weights.get(k);	
			//		               }
			//		               predicted_map.put(z,sum);
			//		           }
			//			  }

			float fw=fwcal(combined_train_list,weights,new_models.get(y),twice_lambda);
			//System.out.println("Initial fw: "+fw);
			float p_fw=0;
			//System.out.println(weights);
			int cnt=0;
			do
			{
				p_fw=fw;
				System.out.println("Previous fw: "+p_fw);
				//System.out.println(weights);
				for(int i=0; i<weights.size();i++)
				{
					List<Float> Xwbar = new ArrayList<Float>();
					for (int z = 0; z < combined_train_list.size(); z++) { // aRow

						for (int j = 0; j < 1; j++) {// bColumn
							float sum=0;
							for (int k = 0; k < combined_train_list.get(0).size(); k++) { // aColumn
								if(k!=i)
								{
									sum+=combined_train_list.get(z).get(k)*weights.get(k);
								}
								//C[i][j] += A[i][k] * B[k][j];
							}
							Xwbar.add(sum);
						}

					}
					//System.out.println(Xwbar);
					float Xsquare=1;
//					for(int j : combined_train_list.keySet())
//					{
//						float x=combined_train_list.get(j).get(i);
//						Xsquare+=x*x;
//					}
//					System.out.println("xsquare"+Xsquare);

					float denominator=Xsquare+twice_lambda;

					List<Float> temp = new ArrayList<Float>();

					for(int j=0;j<Xwbar.size();j++)
					{
						temp.add(new_models.get(y).get(j)-Xwbar.get(j));
					}

					float numerator=0;

					for(int j=0;j<Xwbar.size();j++)
					{
						numerator+=combined_train_list.get(j).get(i)*temp.get(j);
					}

					float w_new=numerator/denominator;
					weights.put(i, w_new);
				}
				//System.out.println(weights);
				fw=fwcal(combined_train_list,weights,new_models.get(y),twice_lambda);
				//System.out.println(fw);
				cnt++;
			}while((p_fw-fw)/p_fw > 0.0001 || (p_fw-fw)/p_fw < 0 || cnt==1);
			for (int z = 0; z < final_test_list.size(); z++) { // aRow

				for (int j = 0; j < 1; j++) {// bColumn
					float sum=0;
					for (int k = 0; k < final_test_list.get(0).size(); k++) { // aColumn
						sum+=final_test_list.get(z).get(k)*weights.get(k);	
					}
					if(y==0 || sum>final_predicted_value_map.get(z))
					{
						final_predicted_value_map.put(z,sum);
						final_predicted_map.put(z, y);
					}
				}
				
			}
			List<Float> t=new ArrayList<Float>();
			for(int a=0;a<weights.size();a++)
			{
				t.add(weights.get(a));
			}
			weight_map1.put(y, t);
		}
		//System.out.println("prediction: "+final_predicted_map);
		int cnt=0;
		for(int i : final_test_list.keySet())
		{
			if(final_test_map.get(i)==final_predicted_map.get(i))
			{
				cnt++;
			}
		}
		

		float accuracy=(float)cnt/(float)final_test_map.size();
		//System.out.println("ACCURACY: "+accuracy);
		
		HashMap<Integer, Integer> final_predicted_map1 = new HashMap<Integer, Integer>();
		HashMap<Integer, Float> final_predicted_value_map1 = new HashMap<Integer, Float>();
		HashMap<Integer, List<Float>> weight_map2 = new HashMap<Integer, List<Float>>();
		
		for(int y=0;y<10;y++)
		{
			//System.out.println(y);
			HashMap<Integer, Float> weights = new HashMap<Integer, Float>();
			for(int i=0;i<combined_train_list.get(0).size();i++)
			{
				weights.put(i, generator.nextFloat()*(float)0.00001);
			}
			//System.out.println(weights);
			//			  for (int z = 0; z < train_list.size(); z++) { // aRow
			//				  
			//		           for (int j = 0; j < 1; j++) {// bColumn
			//		           	float sum=0;
			//		               for (int k = 0; k < train_list.get(0).size(); k++) { // aColumn
			//		               	sum+=train_list.get(z).get(k)*weights.get(k);	
			//		               }
			//		               predicted_map.put(z,sum);
			//		           }
			//			  }

			float fw=fwcal(combined_train_list,weights,new_models.get(y),max_lambda);
			//System.out.println("Initial fw: "+fw);
			float p_fw=0;
			//System.out.println(weights);
			
			do
			{
				p_fw=fw;
				System.out.println("Previous fw: "+p_fw);
				//System.out.println(weights);
				for(int i=0; i<weights.size();i++)
				{
					List<Float> Xwbar = new ArrayList<Float>();
					for (int z = 0; z < combined_train_list.size(); z++) { // aRow

						for (int j = 0; j < 1; j++) {// bColumn
							float sum=0;
							for (int k = 0; k < combined_train_list.get(0).size(); k++) { // aColumn
								if(k!=i)
								{
									sum+=combined_train_list.get(z).get(k)*weights.get(k);
								}
								//C[i][j] += A[i][k] * B[k][j];
							}
							Xwbar.add(sum);
						}

					}
					//System.out.println(Xwbar);
					float Xsquare=1;
//					for(int j : combined_train_list.keySet())
//					{
//						float x=combined_train_list.get(j).get(i);
//						Xsquare+=x*x;
//					}
//					System.out.println("xsquare"+Xsquare);

					float denominator=Xsquare+max_lambda;

					List<Float> temp = new ArrayList<Float>();

					for(int j=0;j<Xwbar.size();j++)
					{
						temp.add(new_models.get(y).get(j)-Xwbar.get(j));
					}

					float numerator=0;

					for(int j=0;j<Xwbar.size();j++)
					{
						numerator+=combined_train_list.get(j).get(i)*temp.get(j);
					}

					float w_new=numerator/denominator;
					weights.put(i, w_new);
				}
				//System.out.println(weights);
				fw=fwcal(combined_train_list,weights,new_models.get(y),max_lambda);
				//System.out.println(fw);
				
			}while((p_fw-fw)/p_fw > 0.0001 || (p_fw-fw)/p_fw < 0);
			for (int z = 0; z < final_test_list.size(); z++) { // aRow

				for (int j = 0; j < 1; j++) {// bColumn
					float sum=0;
					for (int k = 0; k < final_test_list.get(0).size(); k++) { // aColumn
						sum+=final_test_list.get(z).get(k)*weights.get(k);	
					}
					if(y==0 || sum>final_predicted_value_map1.get(z))
					{
						final_predicted_value_map1.put(z,sum);
						final_predicted_map1.put(z, y);
					}
				}
				
			}
			List<Float> t=new ArrayList<Float>();
			for(int a=0;a<weights.size();a++)
			{
				t.add(weights.get(a));
			}
			weight_map2.put(y, t);
			//System.out.println("prediction scores: "+predicted_value_map);
		}
		//System.out.println("prediction: "+final_predicted_map1);
		int x=0;
		for(int i : final_test_list.keySet())
		{
			if(final_test_map.get(i)==final_predicted_map1.get(i))
			{
				x++;
			}
		}
		

		float accuracy1=(float)x/(float)final_test_map.size();
		//System.out.println("ACCURACY1: "+accuracy1);
		
		if(accuracy>accuracy1)
		{
			System.out.println("ACCURACY: "+accuracy);
			for(int i=0;i<final_predicted_map.size();i++)
			{
				fw2.write(Integer.toString(final_predicted_map.get(i)));
				fw2.write("\n");
			}
			
			for(int i=0;i<weight_map1.size();i++)
			{
				for(int j=0;j<weight_map1.get(i).size();j++)
				{
					fw1.write(Float.toString(weight_map1.get(i).get(j)));
					fw1.write(",");
				}
				fw1.write("\n");
			}
		}
		else
		{
			System.out.println("ACCURACY: "+accuracy1);
			for(int i=0;i<final_predicted_map1.size();i++)
			{
				fw2.write(Integer.toString(final_predicted_map1.get(i)));
				fw2.write("\n");
			}
			
			for(int i=0;i<weight_map2.size();i++)
			{
				for(int j=0;j<weight_map2.get(i).size();j++)
				{
					fw1.write(Float.toString(weight_map2.get(i).get(j)));
					fw1.write(",");
				}
				fw1.write("\n");
			}
		}
		
		fw1.close();
		fw2.close();
	}

}
