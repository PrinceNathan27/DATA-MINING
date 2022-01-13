#from PIL import image
import matplotlib.pyplot as plt
import matplotlib.image as mpimg
import numpy as np
import csv
#import Image
from decimal import Decimal
data=[]

with open('weights.csv', 'r') as f:
    reader = csv.reader(f)
    ctr=0
    hashmap={}
    img_count=0
    for row in reader:
        temprow=[]
        t_count=0
        temp_pixel=[]
        for entry in row[:-1]:  
            if(t_count==27):
                temprow.append(temp_pixel)
                t_count=0
                temp_pixel=[]
            else:
                #print("current t count ",t_count)
                t_count+=1
                if Decimal(entry)<Decimal(0.0) :
                    temp_pixel.append(abs(float(entry)))
                else:
                    temp_pixel.append(float(entry))
                #print(temp_pixel)
        hashmap[img_count] =np.array(temprow)
        img_count+=1
    #print(hashmap[2])
    for key in hashmap:
        plt.imshow(hashmap[key])
        #fig = plt.figure()
        plt.savefig("img_"+str(key)+'.png')
    #print(hashmap[0].shape)
               
        
            
            
"""            
            
            
            
        hashmap[img_count] =np.array(temprow)
        img_count+=1
    plt.imshow(hashmap[0])
    print(hashmap[1].shape)
"""        
