from bs4 import BeautifulSoup
import os
import operator
from itertools import chain
import re
from nltk import PorterStemmer
import math
import numpy as np

#ab=PorterStemmer().stem('factionally')

#remove non_ascii
def remove_non_ascii(string):
    ''' Returns the string without non ASCII characters'''
    s = (i for i in string if ord(i) < 128)
    return ''.join(s)

stoplist=open('reuters21578/stoplist.txt').read().replace('\n', '')

root="reuters21578"
#directory = os.fsencode(root)
#fw=open("C:\\Users\\jishn\\Downloads\\folder\\testfile.txt",'w')

count={}
token_count={}
article_list=[]
c=0

class article:
    def __init__(self, id, topic, body):
        self.id = id
        self.topic=topic
        self.body=body

for file in os.listdir(root):
    
    if file.endswith(".sgm"):
        filepath=os.path.join(root,file)
        f=open(str(filepath),'r').read()
        soup=BeautifulSoup(f,"html.parser")
        articles=soup.findAll('reuters')
        for each in articles:
            topics=each.findChildren()
            t=each.findChildren('topics')
            d=t[0].findChildren()
            if(len(d)==1):
                c+=1
                #fw.write(d[0].text + '\n')
                if(d[0].text in count):
                    count[d[0].text]+=1
                else:
                    count[d[0].text]=1
                #fw.write(each['newid'] + '\n')
                #b=each.findChildren("body")
                #print(b[0].text)
                #fw.write(b[0].text)
#print(c)                              
#print(count) 

sorted_count=sorted(count.items(),key=operator.itemgetter(1),reverse=True)
sorted_count=sorted_count[0:20]
#print(sorted_count)
#example_list = [['aaa'], ['fff', 'gg'], ['ff'], ['', 'gg']]
#'' in chain.from_iterable(example_list)

for file in os.listdir(root):
    
    if file.endswith(".sgm"):
        filepath1=os.path.join(root,file)
        f1=open(str(filepath1),'r').read()
        soup1=BeautifulSoup(f1,"html.parser")
        articles1=soup1.findAll('reuters')
        for each1 in articles1:
            topics1=each1.findChildren()
            t1=each1.findChildren('topics')
            d1=t1[0].findChildren()
           # print(d)
            if(len(d1)==1 and d1[0].text in chain.from_iterable(sorted_count)):
                #fw.write(d1[0].text + '\n')
                #fw.write(each1['newid'] + '\n')
                b1=each1.findChildren("body")
                #fw.write(b1[0].text)
                if(len(b1)>0):
                    s=b1[0].text
                    
                    #remove non_ascii
                    s = remove_non_ascii(s)
                
                    #convert to lower case
                    s = s.lower()
                    
                    #replace non-alphanumeric with space and split into tokens
                    s = re.sub('[^0-9a-zA-Z]+', ' ', s)
                    token_bag=s.split(' ')
                    
                    #remove digit tokens, stoplist and stem
                    i=0
                    while(1):
                        if(token_bag[i].isdigit() or token_bag[i] in stoplist):
                            token_bag.remove(token_bag[i])
                        else:
                            token_bag[i]=PorterStemmer().stem(token_bag[i])
                            i+=1
                        try:
                            token_bag[i+1]
                        except IndexError:
                            break  
                        
                    if('' in token_bag):
                        token_bag.remove('')
                    x=article(each1['newid'], d1[0].text, token_bag)
                    article_list.append(x)

#print(len(article_list)) 

for article in article_list:
    for token in article.body:
        if(token in token_count):
            token_count[token]+=1
        else:
            token_count[token]=1

#print(len(token_count))        

final_token_count={}
labelfile = open('reuters21578.clabel', "w")

for token in token_count:
        if(token_count[token]>=5):
            final_token_count[token]=token_count[token]
            labelfile.write(token)
            labelfile.write('\n')

#print(len(final_token_count))

#generating vectors
csv1 = open('freq.csv', "w") 
csv2 = open('sqrtfreq.csv', "w") 
csv3 = open('log2freq.csv', "w") 
classfile = open('reuters21578.class', "w")

v1={}
v2={}
v3={}

for article in article_list:
    a1=[]
    a2=[]
    a3=[]
    
    classfile.write(article.id)
    classfile.write(',')
    classfile.write(article.topic)
    classfile.write('\n')
    
    for token in final_token_count:
        
        a1.append(article.body.count(token))
        
        if(article.body.count(token)==0):
           
            a2.append(article.body.count(token))
            
            a3.append(article.body.count(token))
        else:
            
            a2.append(1 + math.sqrt(article.body.count(token)))
           
            a3.append(1 + math.log(article.body.count(token),2))
        
    v1[article.id]=a1/np.linalg.norm(a1)
    v2[article.id]=a2/np.linalg.norm(a2)
    v3[article.id]=a3/np.linalg.norm(a3)
    
        
for key, value in v1.items():
    for idx, val in enumerate(v1[key]):
        if(val != 0):
            csv1.write(key)
            csv1.write(',')
            csv1.write(str(idx+1))
            csv1.write(',')
            csv1.write(str(val))
            csv1.write('\n')
        
for key, value in v2.items():
    for idx, val in enumerate(v2[key]):
        if(val != 0):
            csv2.write(key)
            csv2.write(',')
            csv2.write(str(idx+1))
            csv2.write(',')
            csv2.write(str(val))
            csv2.write('\n')    
        
for key, value in v3.items():
    for idx, val in enumerate(v3[key]):
        if(val != 0):
            csv3.write(key)
            csv3.write(',')
            csv3.write(str(idx+1))
            csv3.write(',')
            csv3.write(str(val))
            csv3.write('\n')    
csv1.close()
csv2.close()
csv3.close() 
labelfile.close()
classfile.close()    
#fw.close() 

#def removeNonAscii(s):
#    return "".join(i for i in s if ord(i)<128)

#replace non-alphanumeric with space and split into tokens
#x="9 9 . "
#x = re.sub('[^0-9a-zA-Z]+', ' ', x)
#a=x.split(' ')
#
##remove only number tokens
#if(x.isdigit()):
#    print("digit")
#
##eliminate tokens in stoplist
#if 'theres' not in open('C:\\Users\\jishn\\Downloads\\reuters21578\\stoplist.txt').read():
#    print("true")
#else:
#    print("false")
#
#z="ABc"
##convert to lower case
#z=z.lower()
#print(z)
#print(remove_non_ascii(x))
#
#a=[0,1,3,4]
#i=0
#while(1):
#    print('hi')
#    if(a[i]%2!=0):
#        print('yes')
#        a.remove(a[i])
#    else:
#        print('no')
#        i+=1
#    try:
#        a[i+1]
#    except IndexError:
#        break  
