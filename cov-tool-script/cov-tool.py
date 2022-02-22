import re

classes = []
classes.append([False] * 24)   # Class 0
classes.append([False] * 50)    # Class 1
classes.append([False] * 63)   # Class 2
classes.append([False] * 35)   # Class 3
classes.append([False] * 72)    # Class 4

file = open('output.txt', 'r')
lines = file.readlines()
for line in lines:
    matches = re.findall("([0-9]+-[0-9]+)\n", line)
    if len(matches) > 0:
        class_id = int(re.findall("([0-9]+)-", line)[0])
        branch = int(re.findall("-([0-9]+)", line)[0])
        classes[class_id][branch] = True # Flag that this branch was taken

for i in range(len(classes)):
    cvg = sum(classes[i]) /len(classes[i])
    print("Class " + str(i) + " coverage: " + str(cvg))