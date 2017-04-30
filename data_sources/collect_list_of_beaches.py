import json
from bs4 import BeautifulSoup
from pymongo import *
import requests
import pdb
import LatLon

req = requests.get("https://en.wikipedia.org/wiki/List_of_beaches")
soup = BeautifulSoup(req.text,'html.parser')
ul = soup.find_all('ul')[3:94]
all_links = []
for u in ul:
        links = u.find_all('a')
        for i,a in enumerate(links):
                print i
                try:
                        if(a.has_attr("class")):
                                if(a["class"][0]=="new"):
                                        continue
                        all_links.append(a['href'])
                except Exception as e:
                        print str(e)
                        continue

big_countries = soup.find_all("div",{"role": "note"})[1:-1]

for country in big_countries:
        url = "http://www.wikipedia.com"+country.find('a')["href"]
        soup1 = BeautifulSoup(requests.get(url).text,'html.parser')
        uls = soup1.find_all('ul')
        uls = uls[2:]
        uls = uls[:18]
        for u in uls:
                for i,a in enumerate(u.find_all('a')):
                        print i
                        if(a.has_attr("class")):
                                if(a["class"][0]=="new"):
                                        continue
                        try:
                                all_links.append(a['href'])
                        except Exception as e:
				if(a["class"][1]=='selflink'):
					continue	
				continue

lines = []
skip = 0
filtered_list = []
for a in all_links:
	if 'wikimedia' not in a and ':' not in a and '=' not in a and 'cite_note' not in a and 'wiki' in a and 'List' not in a:
		filtered_list.append(a)

for i,link in enumerate(all_links):
	print i
        try:
        
		s = BeautifulSoup(requests.get("http://www.wikipedia.com"+link).text,'html.parser')
                latlong = s.find('span',{'class': 'geo-dec'}).text
		lat = float(latlong.split(' ')[0].split(u'\xb0')[0])
		lon = float(latlong.split(' ')[1].split(u'\xb0')[0])
        except Exception as e:
                continue
        name = s.find('title').text.split('-')[0]
	req = requests.get("https://maps.googleapis.com/maps/api/geocode/json?latlng={},{}&key={}".format(lat,lon,"AIzaSyCa-e3nbLmkMeczwrZ5U3-bhSyzEl4_d_g"))
	try:
		address =  json.loads(req.text)["results"][0]["formatted_address"]
		lines.append("Beach.create(name:'{}' ,latitude:{} ,longitude:{} , address:'{}')\n".format(name.encode('utf-8'),lat,lon,address.encode('utf-8')))
	except Exception as e:
		skip+=1
		print "Skip"
		continue

with open("seed.rb","w") as fp:
	for line in lines:
		try:
			fp.write(line)	
		except Exception as e:
			pdb.set_trace()

print skip
