import re

classes = []
classes.append([False] * 100)   # Class 0
classes.append([False] * 50)    # Class 1
classes.append([False] * 100)   # Class 2
classes.append([False] * 100)   # Class 3
classes.append([False] * 71)    # Class 4

file = open('output.txt', 'r')
lines = file.readlines()
for line in lines:
    matches = re.findall("([0-9]+-[0-9]+)\n", line)
    if len(matches) > 0:
        class_id = int(re.findall("([0-9]+)-", line)[0])
        covered = int(re.findall("-([0-9]+)", line)[0])
        classes[class_id][covered] = True
print(classes[2])