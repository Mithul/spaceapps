import csv

with open("HAB.csv","rb") as fp:
	reader = csv.DictReader(fp)
	lines = []
	for row in reader:
		lines.append("Hab.create(name:'{}',latitude:{},longitude:{},depth:{})\n".format(row["LOCATION"].replace("'",""),float(row["LATITUDE"]),float(row["LONGITUDE"]),float(row["DEPTH"])))

with open("hab_seed.rb","w") as fp:
	[fp.write(line) for line in lines]
